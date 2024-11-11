package com.edteam.reservations.dto;

import java.time.LocalDate;
import java.util.List;

public class ReservationDTO {

    private String id;

    private List<PassengerDTO> passengers;

    private ItineraryDTO itinerary;

    private LocalDate creationDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PassengerDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerDTO> passengers) {
        this.passengers = passengers;
    }

    public ItineraryDTO getItinerary() {
        return itinerary;
    }

    public void setItinerary(ItineraryDTO itinerary) {
        this.itinerary = itinerary;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
}
