package com.airline.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a seat in an aircraft.
 *
 * @author İshak Duran
 * @version 1.0
 */
public class Seat {
  private String seatNumber;
  private SeatClass seatClass;
  private boolean isReserved;
  private Passenger passenger;
  private LocalDateTime reservationTime;
  private String reservationId;

  public enum SeatClass {
    ECONOMY("Economy", 1.0),
    BUSINESS("Business", 2.5),
    FIRST("First Class", 4.0);

    private final String displayName;
    private final double priceMultiplier;

    SeatClass(String displayName, double priceMultiplier) {
      this.displayName = displayName;
      this.priceMultiplier = priceMultiplier;
    }

    public String getDisplayName() {
      return displayName;
    }

    public double getPriceMultiplier() {
      return priceMultiplier;
    }
  }

  public Seat(String seatNumber, SeatClass seatClass) {
    this.seatNumber = seatNumber;
    this.seatClass = seatClass;
    this.isReserved = false;
  }

  /**
   * Reserves this seat for a passenger.
   * 
   * @param passenger The passenger to reserve the seat for
   * @return true if reservation was successful, false if seat is already reserved
   */
  public synchronized boolean reserve(Passenger passenger) {
    if (isReserved) {
      return false;
    }
    this.passenger = passenger;
    this.isReserved = true;
    this.reservationTime = LocalDateTime.now();
    this.reservationId = generateReservationId();
    return true;
  }

  /**
   * Cancels the reservation for this seat.
   * 
   * @return true if cancellation was successful, false if seat was not reserved
   */
  public synchronized boolean cancelReservation() {
    if (!isReserved) {
      return false;
    }
    this.passenger = null;
    this.isReserved = false;
    this.reservationTime = null;
    this.reservationId = null;
    return true;
  }

  private String generateReservationId() {
    return "RES" + System.currentTimeMillis();
  }

  // Getters
  public String getSeatNumber() {
    return seatNumber;
  }

  public SeatClass getSeatClass() {
    return seatClass;
  }

  public boolean isReserved() {
    return isReserved;
  }

  public Passenger getPassenger() {
    return passenger;
  }

  public LocalDateTime getReservationTime() {
    return reservationTime;
  }

  public String getReservationId() {
    return reservationId;
  }

  public boolean isAvailable() {
    return !isReserved;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Seat seat = (Seat) obj;
    return Objects.equals(seatNumber, seat.seatNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(seatNumber);
  }

  @Override
  public String toString() {
    return String.format("Seat{number='%s', class='%s', reserved=%s}",
        seatNumber, seatClass.getDisplayName(), isReserved);
  }
}
