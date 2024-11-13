package com.edteam.reservations.dto;

import java.util.List;
import java.util.Objects;

public class ItineraryDTO {

    private Long id;

    Long version;

    private List<SegmentDTO> segment;

    private PriceDTO price;

    public ItineraryDTO() {
    }

    public ItineraryDTO(Long id, Long version, List<SegmentDTO> segment, PriceDTO price) {
        this.id = id;
        this.version = version;
        this.segment = segment;
        this.price = price;
    }

    public List<SegmentDTO> getSegment() {
        return segment;
    }

    public void setSegment(List<SegmentDTO> segment) {
        this.segment = segment;
    }

    public PriceDTO getPrice() {
        return price;
    }

    public void setPrice(PriceDTO price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        ItineraryDTO that = (ItineraryDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(version, that.version)
                && Objects.equals(segment, that.segment) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, segment, price);
    }

    @Override
    public String toString() {
        return "ItineraryDTO{" + "id=" + id + ", version=" + version + ", segment=" + segment + ", price=" + price
                + '}';
    }
}
