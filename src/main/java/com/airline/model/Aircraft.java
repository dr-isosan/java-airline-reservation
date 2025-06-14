package com.airline.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents an aircraft with a seat layout.
 *
 * @author İshak Duran
 * @version 1.0
 */
public class Aircraft {
  private String aircraftId;
  private String model;
  private final Map<String, Seat> seats;
  private final int rows;
  private final String[] columns;

  public Aircraft(String aircraftId, String model, int rows, String[] columns) {
    this.aircraftId = aircraftId;
    this.model = model;
    this.rows = rows;
    this.columns = columns;
    this.seats = new ConcurrentHashMap<>();
    initializeSeats();
  }

  /**
   * Initializes the aircraft with seats based on the configuration.
   */
  private void initializeSeats() {
    for (int row = 1; row <= rows; row++) {
      for (String column : columns) {
        String seatNumber = row + column;
        Seat.SeatClass seatClass = determineSeatClass(row);
        seats.put(seatNumber, new Seat(seatNumber, seatClass));
      }
    }
  }

  /**
   * Determines the seat class based on the row number.
   * 
   * @param row The row number
   * @return The seat class
   */
  private Seat.SeatClass determineSeatClass(int row) {
    if (row <= 3) {
      return Seat.SeatClass.FIRST;
    } else if (row <= 10) {
      return Seat.SeatClass.BUSINESS;
    } else {
      return Seat.SeatClass.ECONOMY;
    }
  }

  /**
   * Gets a seat by its number.
   * 
   * @param seatNumber The seat number
   * @return The seat, or null if not found
   */
  public Seat getSeat(String seatNumber) {
    return seats.get(seatNumber);
  }

  /**
   * Gets all seats in the aircraft.
   * 
   * @return List of all seats
   */
  public List<Seat> getAllSeats() {
    return new ArrayList<>(seats.values());
  }

  /**
   * Gets all available seats.
   * 
   * @return List of available seats
   */
  public List<Seat> getAvailableSeats() {
    return seats.values().stream()
        .filter(Seat::isAvailable)
        .collect(Collectors.toList());
  }

  /**
   * Gets all reserved seats.
   * 
   * @return List of reserved seats
   */
  public List<Seat> getReservedSeats() {
    return seats.values().stream()
        .filter(Seat::isReserved)
        .collect(Collectors.toList());
  }

  /**
   * Gets available seats by class.
   * 
   * @param seatClass The seat class
   * @return List of available seats in the specified class
   */
  public List<Seat> getAvailableSeatsByClass(Seat.SeatClass seatClass) {
    return seats.values().stream()
        .filter(seat -> seat.getSeatClass() == seatClass && seat.isAvailable())
        .collect(Collectors.toList());
  }

  /**
   * Gets the total number of seats.
   * 
   * @return Total number of seats
   */
  public int getTotalSeats() {
    return seats.size();
  }

  /**
   * Gets the number of available seats.
   * 
   * @return Number of available seats
   */
  public int getAvailableSeatsCount() {
    return (int) seats.values().stream().filter(Seat::isAvailable).count();
  }

  /**
   * Gets the number of reserved seats.
   * 
   * @return Number of reserved seats
   */
  public int getReservedSeatsCount() {
    return (int) seats.values().stream().filter(Seat::isReserved).count();
  }

  /**
   * Gets the seat layout as a 2D representation.
   * 
   * @return 2D array representing the seat layout
   */
  public Seat[][] getSeatLayout() {
    Seat[][] layout = new Seat[rows][columns.length];
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < columns.length; col++) {
        String seatNumber = (row + 1) + columns[col];
        layout[row][col] = seats.get(seatNumber);
      }
    }
    return layout;
  }

  // Getters
  public String getAircraftId() {
    return aircraftId;
  }

  public String getModel() {
    return model;
  }

  public int getRows() {
    return rows;
  }

  public String[] getColumns() {
    return columns;
  }

  @Override
  public String toString() {
    return String.format("Aircraft{id='%s', model='%s', capacity=%d}",
        aircraftId, model, getTotalSeats());
  }
}
