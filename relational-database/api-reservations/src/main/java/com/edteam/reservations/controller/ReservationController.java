package com.edteam.reservations.controller;

import com.edteam.reservations.controller.resource.ReservationResource;
import com.edteam.reservations.dto.ReservationDTO;
import com.edteam.reservations.dto.SearchReservationCriteriaDTO;
import com.edteam.reservations.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reservation")
@Validated
public class ReservationController implements ReservationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService service;

    @Autowired
    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<ReservationDTO> getReservations(SearchReservationCriteriaDTO criteria) {
        LOGGER.info("Obtain all the reservations");
        return service.getReservations(criteria);
    }

    @GetMapping("/{id}")
    public Mono<ReservationDTO> getReservationById(@PathVariable Long id) {
        LOGGER.info("Obtain information from a reservation with {}", id);
        return service.getReservationById(id);
    }

    @PostMapping
    public Mono<ReservationDTO> save(@RequestBody ReservationDTO reservation) {
        LOGGER.info("Saving new reservation");
        return service.save(reservation);
    }

    @PutMapping("/{id}")
    public Mono<ReservationDTO> update(@PathVariable Long id, @RequestBody ReservationDTO reservation) {
        LOGGER.info("Updating a reservation with {}", id);
        return service.update(id, reservation);

    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        LOGGER.info("Deleting a reservation with {}", id);
        return service.delete(id);
    }

}