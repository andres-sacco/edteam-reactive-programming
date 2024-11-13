package com.edteam.reservations.service;

import com.edteam.reservations.connector.CatalogConnector;
import com.edteam.reservations.connector.response.CityDTO;
import com.edteam.reservations.dto.*;
import com.edteam.reservations.enums.APIError;
import com.edteam.reservations.exception.EdteamException;
import com.edteam.reservations.model.Reservation;
import com.edteam.reservations.repository.ReservationRepository;
import com.edteam.reservations.specification.ReservationSpecification;
import jakarta.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import reactor.core.scheduler.Schedulers;

@Service
public class ReservationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);

    private ReservationRepository repository;

    private ConversionService conversionService;

    private CatalogConnector catalogConnector;

    @Autowired
    public ReservationService(ReservationRepository repository, ConversionService conversionService,
                              CatalogConnector catalogConnector) {
        this.repository = repository;
        this.conversionService = conversionService;
        this.catalogConnector = catalogConnector;
    }

    public Flux<ReservationDTO> getReservations(SearchReservationCriteriaDTO criteria) {
        Pageable pageable = PageRequest.of(criteria.getPageActual(), criteria.getPageSize());

        List<Reservation> reservations = repository.findAll(ReservationSpecification.withSearchCriteria(criteria), pageable);

        return Flux.fromIterable(reservations)
                // Filtrado: solo reservas creadas en el último mes
                .filter(reservation -> reservation.getCreationDate() != null &&
                        reservation.getCreationDate().isAfter(LocalDate.now().minusMonths(1)))

                // Transformación: convertir cada Reservation a ReservationDTO
                .map(reservation -> conversionService.convert(reservation, ReservationDTO.class))

                // Validación: asegura que el resultado no sea nulo y tenga campos obligatorios
                .filter(dto -> dto != null && dto.getId() != null && dto.getItinerary() != null)

                // Combinación: concatenar con otro flujo (ej., reservas con descuentos especiales)
                .concatWith(getDiscountedReservations(criteria))

                // Otra Transformación: establecer valores predeterminados si ciertos campos son nulos
                .map(dto -> {
                    if (dto.getPassengers() == null) dto.setPassengers(new ArrayList<>());
                    return dto;
                })
                // Condicional: establecer un valor por defecto en caso de no tener resultado
                .defaultIfEmpty(new ReservationDTO())
                // Primer repeat: repite cada 5 minutos, útil para solicitudes recurrentes
                //.repeatWhen(flux -> flux.delayElements(Duration.ofSeconds(1)));

                // Ejecuta en un scheduler que permite operaciones bloqueantes
                .publishOn(Schedulers.boundedElastic())

                // Limita la cantidad de elementos procesados a la vez
                .limitRate(100)
                .onBackpressureBuffer(500);
    }

    public Mono<ReservationDTO> getReservationById(Long id) {
        Optional<Reservation> result = repository.findById(id);
        if (result.isEmpty()) {
            LOGGER.debug("Not exist reservation with the id {}", id);
            throw new EdteamException(APIError.RESERVATION_NOT_FOUND);
        }
        return Mono.justOrEmpty(conversionService.convert(result.get(), ReservationDTO.class))
                .map(dto -> {
                    if (dto.getPassengers() == null) dto.setPassengers(new ArrayList<>());
                    return dto;
                });
    }

    public Mono<ReservationDTO> save(ReservationDTO reservation) {
        if (Objects.nonNull(reservation.getId())) {
            throw new EdteamException(APIError.RESERVATION_WITH_SAME_ID);
        }
        checkCity(reservation);

        Reservation transformed = conversionService.convert(reservation, Reservation.class);
        validateEntity(transformed);

        Reservation result = repository.save(Objects.requireNonNull(transformed));
        return Mono.justOrEmpty(conversionService.convert(result, ReservationDTO.class))
                .map(dto -> {
                    if (dto.getPassengers() == null) dto.setPassengers(new ArrayList<>());
                    return dto;
                });
    }

    public Mono<ReservationDTO> update(Long id, ReservationDTO reservation) {
        if (!repository.existsById(id)) {
            LOGGER.debug("Not exist reservation with the id {}", id);
            throw new EdteamException(APIError.RESERVATION_NOT_FOUND);
        }
        checkCity(reservation);

        Reservation transformed = conversionService.convert(reservation, Reservation.class);
        validateEntity(transformed);
        Reservation result = repository.save(Objects.requireNonNull(transformed));

        return Mono.justOrEmpty(conversionService.convert(result, ReservationDTO.class))
                .map(dto -> {
                    if (dto.getPassengers() == null) dto.setPassengers(new ArrayList<>());
                    return dto;
                });
    }

    public Mono<Void> delete(Long id) {
        if (!repository.existsById(id)) {
            LOGGER.debug("Not exist reservation with the id {}", id);
            throw new EdteamException(APIError.RESERVATION_NOT_FOUND);
        }

        repository.deleteById(id);

        return Mono.empty();
    }

    private void checkCity(ReservationDTO reservationDTO) {
        for (SegmentDTO segmentDTO : reservationDTO.getItinerary().getSegment()) {
            Mono<CityDTO> origin = catalogConnector.getCity(segmentDTO.getOrigin());
            Mono<CityDTO> destination = catalogConnector.getCity(segmentDTO.getDestination());

            if (origin.block() == null || destination.block() == null) {
                throw new EdteamException(APIError.VALIDATION_ERROR);
            }
        }
    }

    private void validateEntity(Reservation transformed) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Reservation>> violations = validator.validate(transformed);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    // Metodo auxiliar para simular un flujo de "reservas con descuento" adicionales
    private Flux<ReservationDTO> getDiscountedReservations(SearchReservationCriteriaDTO criteria) {
        // Crear datos de ejemplo para "PassengerDTO"
        PassengerDTO passenger1 = new PassengerDTO("Juan", "Perez", "12345678", "DNI",LocalDate.now().minusMonths(1));
        PassengerDTO passenger2 = new PassengerDTO("Ana", "Gomez", "87654321", "DNI",LocalDate.now().minusMonths(1));

        // Crear datos de ejemplo para "ItineraryDTO"
        ItineraryDTO itinerary = new ItineraryDTO(1L, 2L, List.of(new SegmentDTO("DEF", "DEF", "2024-01-01", "2024-01-01", "XX")), new PriceDTO(BigDecimal.valueOf(10L), BigDecimal.valueOf(5L), BigDecimal.valueOf(5L)));

        // Crear "ReservationDTO" con datos específicos para el flujo simulado
        List<ReservationDTO> discountedReservations = List.of(
                new ReservationDTO(
                        1001L, // id
                        1L, // versión
                        List.of(passenger1, passenger2), // pasajeros
                        itinerary, // itinerario
                        LocalDate.now().minusDays(10) // fecha de creación
                ),
                new ReservationDTO(
                        1002L, // id
                        1L, // versión
                        null, // pasajeros
                        itinerary, // itinerario
                        LocalDate.now().minusDays(5) // fecha de creación
                )
        );
        return Flux.fromIterable(discountedReservations);
    }
}
