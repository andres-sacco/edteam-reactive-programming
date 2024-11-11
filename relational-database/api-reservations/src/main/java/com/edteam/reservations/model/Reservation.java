package com.edteam.reservations.model;

import com.edteam.reservations.listener.ReservationEntityListener;
import jakarta.persistence.EntityListeners;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Table("reservation")
@EntityListeners(ReservationEntityListener.class)
public class Reservation extends Base {

    @Valid
    @NotEmpty(message = "You need at least one passenger")
    private List<Passenger> passengers;

    @Valid
    private Itinerary itinerary;

    @Column(value = "creation_date")
    private LocalDate creationDate;

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Reservation that = (Reservation) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(passengers, that.passengers)
                && Objects.equals(itinerary, that.itinerary) && Objects.equals(creationDate, that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), passengers, itinerary, creationDate);
    }

    @Override
    public String toString() {
        return "Reservation{" + "id=" + getId() + ", creationDate=" + creationDate + '}';
    }
}
