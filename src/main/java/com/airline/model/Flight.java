package com.airline.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Represents a flight in the airline reservation system.
 *
 * @author İshak Duran
 * @version 1.0
 */
public class Flight {
  private String flightNumber;
  private String origin;
  private String destination;
  private LocalDateTime departureTime;
  private LocalDateTime arrivalTime;
  private Aircraft aircraft;
  private double basePrice;
  private FlightStatus status;

  public enum FlightStatus {
    SCHEDULED("Scheduled"),
    BOARDING("Boarding"),
    DEPARTED("Departed"),
    ARRIVED("Arrived"),
    CANCELLED("Cancelled"),
    DELAYED("Delayed");

    private final String displayName;

    FlightStatus(String displayName) {
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }
  }

  public Flight(String flightNumber, String origin, String destination,
      LocalDateTime departureTime, LocalDateTime arrivalTime,
      Aircraft aircraft, double basePrice) {
    this.flightNumber = flightNumber;
    this.origin = origin;
    this.destination = destination;
    this.departureTime = departureTime;
    this.arrivalTime = arrivalTime;
    this.aircraft = aircraft;
    this.basePrice = basePrice;
    this.status = FlightStatus.SCHEDULED;
  }

  /**
   * Gets all available seats on this flight.
   * 
   * @return List of available seats
   */
  public List<Seat> getAvailableSeats() {
    return aircraft.getAvailableSeats();
  }

  /**
   * Gets all reserved seats on this flight.
   * 
   * @return List of reserved seats
   */
  public List<Seat> getReservedSeats() {
    return aircraft.getReservedSeats();
  }

  /**
   * Gets the total number of seats on this flight.
   * 
   * @return Total number of seats
   */
  public int getTotalSeats() {
    return aircraft.getTotalSeats();
  }

  /**
   * Gets the number of available seats.
   * 
   * @return Number of available seats
   */
  public int getAvailableSeatsCount() {
    return aircraft.getAvailableSeatsCount();
  }

  /**
   * Gets the number of reserved seats.
   * 
   * @return Number of reserved seats
   */
  public int getReservedSeatsCount() {
    return aircraft.getReservedSeatsCount();
  }

  /**
   * Calculates the price for a specific seat class.
   * 
   * @param seatClass The seat class
   * @return The price for the seat class
   */
  public double getPriceForSeatClass(Seat.SeatClass seatClass) {
    return basePrice * seatClass.getPriceMultiplier();
  }

  // Getters and Setters
  public String getFlightNumber() {
    return flightNumber;
  }

  public void setFlightNumber(String flightNumber) {
    this.flightNumber = flightNumber;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public LocalDateTime getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(LocalDateTime departureTime) {
    this.departureTime = departureTime;
  }

  public LocalDateTime getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(LocalDateTime arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  public Aircraft getAircraft() {
    return aircraft;
  }

  public void setAircraft(Aircraft aircraft) {
    this.aircraft = aircraft;
  }

  public double getBasePrice() {
    return basePrice;
  }

  public void setBasePrice(double basePrice) {
    this.basePrice = basePrice;
  }

  public FlightStatus getStatus() {
    return status;
  }

  public void setStatus(FlightStatus status) {
    this.status = status;
  }

  public String getRoute() {
    return origin + " → " + destination;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Flight flight = (Flight) obj;
    return Objects.equals(flightNumber, flight.flightNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(flightNumber);
  }

  @Override
  public String toString() {
    return String.format("Flight{number='%s', route='%s', departure=%s, status='%s'}",
        flightNumber, getRoute(), departureTime, status.getDisplayName());
  }
}
