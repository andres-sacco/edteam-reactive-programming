package com.edteam.reservations.repository;

import com.edteam.reservations.model.Reservation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReservationRepository extends ReactiveCrudRepository<Reservation, String> {

}