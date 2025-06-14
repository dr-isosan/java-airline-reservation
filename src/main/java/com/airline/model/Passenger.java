package com.airline.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a passenger in the airline reservation system.
 *
 * @author İshak Duran
 * @version 1.0
 */
public class Passenger {
  private String passengerId;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String passportNumber;
  private LocalDateTime registrationTime;

  public Passenger() {
    this.registrationTime = LocalDateTime.now();
  }

  public Passenger(String firstName, String lastName, String email, String phoneNumber, String passportNumber) {
    this();
    this.passengerId = generatePassengerId();
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.passportNumber = passportNumber;
  }

  private String generatePassengerId() {
    return "PAX" + System.currentTimeMillis();
  }

  // Getters and Setters
  public String getPassengerId() {
    return passengerId;
  }

  public void setPassengerId(String passengerId) {
    this.passengerId = passengerId;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPassportNumber() {
    return passportNumber;
  }

  public void setPassportNumber(String passportNumber) {
    this.passportNumber = passportNumber;
  }

  public LocalDateTime getRegistrationTime() {
    return registrationTime;
  }

  public void setRegistrationTime(LocalDateTime registrationTime) {
    this.registrationTime = registrationTime;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Passenger passenger = (Passenger) obj;
    return Objects.equals(passengerId, passenger.passengerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(passengerId);
  }

  @Override
  public String toString() {
    return String.format("Passenger{id='%s', name='%s', email='%s'}",
        passengerId, getFullName(), email);
  }
}
