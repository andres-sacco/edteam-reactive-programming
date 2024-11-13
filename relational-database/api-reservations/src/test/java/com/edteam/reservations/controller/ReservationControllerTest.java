package com.edteam.reservations.controller;

import com.edteam.reservations.dto.*;
import com.edteam.reservations.util.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

class ReservationControllerTest extends BaseTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getReservations_should_return_a_list_of_reservations() {

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
        ReservationDTO reservation = new ReservationDTO(2L, 1L, Lists.newArrayList(passenger1, passenger2), itinerary,
                LocalDate.of(2023, 11, 12));

        webTestClient.get().uri("/reservation/2").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(ReservationDTO.class)
                .isEqualTo(reservation);
    }

    @Test
    void delete_should_return_okay() {
        webTestClient.delete().uri("/reservation/1").exchange().expectStatus().isOk();
    }
}
