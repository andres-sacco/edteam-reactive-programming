package com.edteam.reservations.service;

import com.edteam.reservations.connector.CatalogConnector;
import com.edteam.reservations.connector.response.CityDTO;
import com.edteam.reservations.dto.SearchReservationCriteriaDTO;
import com.edteam.reservations.dto.SegmentDTO;
import com.edteam.reservations.enums.APIError;
import com.edteam.reservations.exception.EdteamException;
import com.edteam.reservations.dto.ReservationDTO;
import com.edteam.reservations.model.Reservation;
import com.edteam.reservations.repository.PassengerRepository;
import com.edteam.reservations.repository.ReservationRepository;
import jakarta.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Set;

@Service
public class ReservationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);

    private PassengerRepository passengerRepository;

    private ReservationRepository repository;

    private ConversionService conversionService;

    private CatalogConnector catalogConnector;

    @Autowired
    public ReservationService(ReservationRepository repository, PassengerRepository passengerRepository,
            ConversionService conversionService, CatalogConnector catalogConnector) {
        this.passengerRepository = passengerRepository;
        this.repository = repository;
        this.conversionService = conversionService;
        this.catalogConnector = catalogConnector;
    }

    public Flux<ReservationDTO> getReservations(SearchReservationCriteriaDTO criteria) {
        Flux<Reservation> reservations = repository.findAll();

        return reservations.flatMap(reservation -> passengerRepository.findAllByReservationId(reservation.getId())
                .collectList().map(passengers -> {
                    reservation.setPassengers(passengers);
                    return reservation;
                })).mapNotNull(reservation -> conversionService.convert(reservation, ReservationDTO.class));
        // .zipWith(Flux.interval(Duration.ofSeconds(1)), (reservation, interval) -> reservation);
        // .concatMap(reservation -> Mono.just(conversionService.convert(reservation, ReservationDTO.class))
        // .delayElement(Duration.ofMillis(1500))); // Retardo de 1500 ms por cada elemento
        // .delayElements(Duration.ofSeconds(2));
    }

    public Mono<ReservationDTO> getReservationById(Long id) {
        return repository.findById(id).flatMap(reservation -> passengerRepository
                .findAllByReservationId(reservation.getId()).collectList().map(passengers -> {
                    reservation.setPassengers(passengers);
                    return reservation;
                })).switchIfEmpty(Mono.error(new EdteamException(APIError.RESERVATION_NOT_FOUND)))
                .mapNotNull(reservation -> conversionService.convert(reservation, ReservationDTO.class));
    }

    public Mono<ReservationDTO> save(ReservationDTO reservation) {
        if (Objects.nonNull(reservation.getId())) {
            throw new EdteamException(APIError.RESERVATION_WITH_SAME_ID);
        }
        checkCity(reservation);

        Reservation transformed = conversionService.convert(reservation, Reservation.class);
        validateEntity(transformed);

        Mono<Reservation> result = repository.save(Objects.requireNonNull(transformed));
        return result.mapNotNull(r -> conversionService.convert(r, ReservationDTO.class));
    }

    public Mono<ReservationDTO> update(Long id, ReservationDTO reservation) {
        if (!repository.existsById(id).block()) {
            LOGGER.debug("Not exist reservation with the id {}", id);
            throw new EdteamException(APIError.RESERVATION_NOT_FOUND);
        }
        checkCity(reservation);

        Reservation transformed = conversionService.convert(reservation, Reservation.class);
        validateEntity(transformed);
        Mono<Reservation> result = repository.save(Objects.requireNonNull(transformed));

        return result.mapNotNull(r -> conversionService.convert(r, ReservationDTO.class));
    }

    public Mono<Void> delete(Long id) {
        if (!repository.existsById(id).block()) {
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
}
