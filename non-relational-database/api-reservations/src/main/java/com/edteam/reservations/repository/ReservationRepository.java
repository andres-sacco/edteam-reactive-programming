package com.edteam.reservations.repository;

import com.edteam.reservations.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ReservationRepository extends MongoRepository<Reservation, String> {

}