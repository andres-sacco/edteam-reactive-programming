package com.edteam.reservations.repository;

import com.edteam.reservations.model.Passenger;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface PassengerRepository extends R2dbcRepository<Passenger, Long> {

    Flux<Passenger> findAllByReservationId(Long reservationId);
}