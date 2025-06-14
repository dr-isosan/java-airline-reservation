package com.airline.core;

import com.airline.model.Flight;
import com.airline.model.Passenger;
import com.airline.model.Seat;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a reservation in the airline system.
 *
 * @author İshak Duran
 * @version 1.0
 */
public class Reservation {
  private String reservationId;
  private Flight flight;
  private Seat seat;
  private Passenger passenger;
  private LocalDateTime reservationTime;
  private ReservationStatus status;
  private double totalPrice;

  public enum ReservationStatus {
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    CHECKED_IN("Checked In"),
    NO_SHOW("No Show");

    private final String displayName;

    ReservationStatus(String displayName) {
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }
  }

  public Reservation(String reservationId, Flight flight, Seat seat,
      Passenger passenger, LocalDateTime reservationTime) {
    this.reservationId = reservationId;
    this.flight = flight;
    this.seat = seat;
    this.passenger = passenger;
    this.reservationTime = reservationTime;
    this.status = ReservationStatus.CONFIRMED;
    this.totalPrice = calculateTotalPrice();
  }

  private double calculateTotalPrice() {
    return flight.getPriceForSeatClass(seat.getSeatClass());
  }

  /**
   * Cancels this reservation.
   * 
   * @return true if cancellation was successful
   */
  public boolean cancel() {
    if (status == ReservationStatus.CONFIRMED) {
      status = ReservationStatus.CANCELLED;
      return seat.cancelReservation();
    }
    return false;
  }

  /**
   * Checks in the passenger.
   * 
   * @return true if check-in was successful
   */
  public boolean checkIn() {
    if (status == ReservationStatus.CONFIRMED) {
      status = ReservationStatus.CHECKED_IN;
      return true;
    }
    return false;
  }

  // Getters and Setters
  public String getReservationId() {
    return reservationId;
  }

  public Flight getFlight() {
    return flight;
  }

  public Seat getSeat() {
    return seat;
  }

  public Passenger getPassenger() {
    return passenger;
  }

  public LocalDateTime getReservationTime() {
    return reservationTime;
  }

  public ReservationStatus getStatus() {
    return status;
  }

  public void setStatus(ReservationStatus status) {
    this.status = status;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public String getReservationSummary() {
    return String.format("Reservation %s: %s on Flight %s, Seat %s (%s) - $%.2f",
        reservationId, passenger.getFullName(), flight.getFlightNumber(),
        seat.getSeatNumber(), seat.getSeatClass().getDisplayName(), totalPrice);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Reservation that = (Reservation) obj;
    return Objects.equals(reservationId, that.reservationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reservationId);
  }

  @Override
  public String toString() {
    return String.format("Reservation{id='%s', flight='%s', seat='%s', passenger='%s', status='%s'}",
        reservationId, flight.getFlightNumber(), seat.getSeatNumber(),
        passenger.getFullName(), status.getDisplayName());
  }
}
