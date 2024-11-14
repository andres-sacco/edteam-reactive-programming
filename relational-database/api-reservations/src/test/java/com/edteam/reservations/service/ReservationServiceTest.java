package com.edteam.reservations.service;

import com.edteam.reservations.dto.*;
import com.edteam.reservations.util.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.google.common.collect.Lists;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

class ReservationServiceTest extends BaseTest {

    @Autowired
    private ReservationService service;

    @Test
    void getReservations_should_return_a_list_of_reservations() {
        // Define search criteria
        SearchReservationCriteriaDTO criteria = new SearchReservationCriteriaDTO();
        criteria.setPageActual(0);
        criteria.setPageSize(10);

        // Execute method
        Flux<ReservationDTO> result = service.getReservations(criteria).subscribeOn(Schedulers.boundedElastic())
                .limitRate(100).onBackpressureBuffer(500);

        // Verify the result
        StepVerifier.create(result).expectNextCount(2).verifyComplete();

    }

    @Test
    void getReservation_should_return_a_reservation() {
        // Execute method
        Mono<ReservationDTO> result = service.getReservationById(2L);

            // Verify the result
            StepVerifier.create(result).expectNextMatches(reservation -> reservation.equals(getReservation()))
                .expectComplete().verify();
    }

    private ReservationDTO getReservation() {
        // Passenger details
        PassengerDTO passenger1 = new PassengerDTO("Alberto", "Sacco", "AB554714", "PASSPORT",
                LocalDate.of(1985, 1, 1));
        PassengerDTO passenger2 = new PassengerDTO("Horacio", "Sacco", "AB554715", "PASSPORT",
                LocalDate.of(1985, 1, 1));

        // Segment details
        SegmentDTO segment = new SegmentDTO("BUE", "MIA", "2023-12-31", "2024-01-01", "AA");

        // Price details
        PriceDTO price = new PriceDTO(BigDecimal.valueOf(30.00).setScale(2), BigDecimal.valueOf(20.00).setScale(2),
                BigDecimal.valueOf(10.00).setScale(2));

        // Itinerary details
        ItineraryDTO itinerary = new ItineraryDTO(2L, 1L, List.of(segment), price);

        // Reservation details
        return new ReservationDTO(2L, 1L, Lists.newArrayList(passenger1, passenger2), itinerary,
                LocalDate.of(2023, 11, 12));
    }
}
