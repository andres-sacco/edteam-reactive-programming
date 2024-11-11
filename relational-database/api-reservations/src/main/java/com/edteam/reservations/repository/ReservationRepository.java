package com.edteam.reservations.repository;

import com.edteam.reservations.model.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ReservationRepository extends R2dbcRepository<Reservation, Long> {

    Flux<Reservation> findAllBy(Specification s, Pageable pageable);
}