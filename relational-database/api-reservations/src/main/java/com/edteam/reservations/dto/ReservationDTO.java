package com.edteam.reservations.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ReservationDTO {

    private Long id;

    Long version;

    private List<PassengerDTO> passengers;

    private ItineraryDTO itinerary;

    private LocalDate creationDate;

    public ReservationDTO() {
    }

    public ReservationDTO(Long id, Long version, List<PassengerDTO> passengers, ItineraryDTO itinerary,
            LocalDate creationDate) {
        this.id = id;
        this.version = version;
        this.passengers = passengers;
        this.itinerary = itinerary;
        this.creationDate = creationDate;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ReservationDTO that = (ReservationDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(version, that.version)
                && Objects.equals(passengers, that.passengers) && Objects.equals(itinerary, that.itinerary)
                && Objects.equals(creationDate, that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, passengers, itinerary, creationDate);
    }

    @Override
    public String toString() {
        return "ReservationDTO{" + "id=" + id + ", version=" + version + ", passengers=" + passengers + ", itinerary="
                + itinerary + ", creationDate=" + creationDate + '}';
    }
}
