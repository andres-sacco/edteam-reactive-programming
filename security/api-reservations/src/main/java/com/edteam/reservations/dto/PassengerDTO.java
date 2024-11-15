package com.edteam.reservations.dto;

import java.time.LocalDate;
import java.util.Objects;

public class PassengerDTO {

    private String firstName;

    private String lastName;

    private String documentNumber;

    private String documentType;

    private LocalDate birthday;

    public PassengerDTO() {
    }

    public PassengerDTO(String firstName, String lastName, String documentNumber, String documentType,
            LocalDate birthday) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.documentType = documentType;
        this.birthday = birthday;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PassengerDTO that = (PassengerDTO) o;
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName)
                && Objects.equals(documentNumber, that.documentNumber)
                && Objects.equals(documentType, that.documentType) && Objects.equals(birthday, that.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, documentNumber, documentType, birthday);
    }

    @Override
    public String toString() {
        return "PassengerDTO{" + "firstName='" + firstName + '\'' + ", lastName='" + lastName + '\''
                + ", documentNumber='" + documentNumber + '\'' + ", documentType='" + documentType + '\''
                + ", birthday=" + birthday + '}';
    }
}
