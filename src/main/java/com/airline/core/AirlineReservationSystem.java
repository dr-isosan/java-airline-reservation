package com.airline.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import com.airline.model.Flight;
import com.airline.model.Passenger;
import com.airline.model.Seat;
import com.airline.util.ReservationLogger;

/**
 * Enhanced thread-safe airline reservation system.
 * Handles multiple flights and provides comprehensive booking functionality.
 *
 * @author İshak Duran
 * @version 2.0
 */
public class AirlineReservationSystem {

  private final Map<String, Flight> flights;
  private final Map<String, Reservation> reservations;
  private final ReentrantReadWriteLock lock;
  private final ReservationLogger logger;

  public AirlineReservationSystem() {
    this.flights = new ConcurrentHashMap<>();
    this.reservations = new ConcurrentHashMap<>();
    this.lock = new ReentrantReadWriteLock();
    this.logger = new ReservationLogger();
  }

  /**
   * Adds a flight to the system.
   * 
   * @param flight The flight to add
   */
  public void addFlight(Flight flight) {
    lock.writeLock().lock();
    try {
      flights.put(flight.getFlightNumber(), flight);
      logger.logInfo("Flight added: " + flight.getFlightNumber());
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Makes a reservation for a passenger on a specific flight and seat.
   * 
   * @param flightNumber The flight number
   * @param seatNumber   The seat number
   * @param passenger    The passenger
   * @return The reservation if successful, null otherwise
   */
  public Reservation makeReservation(String flightNumber, String seatNumber, Passenger passenger) {
    lock.writeLock().lock();
    try {
      String threadName = Thread.currentThread().getName();
      logger.logInfo(String.format("%s attempting to reserve seat %s on flight %s for passenger %s",
          threadName, seatNumber, flightNumber, passenger.getFullName()));

      Flight flight = flights.get(flightNumber);
      if (flight == null) {
        logger.logWarning(String.format("%s - Flight %s not found", threadName, flightNumber));
        return null;
      }

      Seat seat = flight.getAircraft().getSeat(seatNumber);
      if (seat == null) {
        logger.logWarning(String.format("%s - Seat %s not found on flight %s",
            threadName, seatNumber, flightNumber));
        return null;
      }

      if (seat.reserve(passenger)) {
        Reservation reservation = new Reservation(
            generateReservationId(), flight, seat, passenger, LocalDateTime.now());
        reservations.put(reservation.getReservationId(), reservation);

        logger.logInfo(String.format("%s successfully reserved seat %s on flight %s for %s (Reservation: %s)",
            threadName, seatNumber, flightNumber, passenger.getFullName(), reservation.getReservationId()));
        return reservation;
      } else {
        logger.logWarning(String.format("%s failed to reserve seat %s on flight %s - seat already reserved",
            threadName, seatNumber, flightNumber));
        return null;
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Cancels a reservation.
   * 
   * @param reservationId The reservation ID to cancel
   * @return true if cancellation was successful, false otherwise
   */
  public boolean cancelReservation(String reservationId) {
    lock.writeLock().lock();
    try {
      String threadName = Thread.currentThread().getName();
      logger.logInfo(String.format("%s attempting to cancel reservation %s", threadName, reservationId));

      Reservation reservation = reservations.get(reservationId);
      if (reservation == null) {
        logger.logWarning(String.format("%s - Reservation %s not found", threadName, reservationId));
        return false;
      }

      if (reservation.getSeat().cancelReservation()) {
        reservations.remove(reservationId);
        logger.logInfo(String.format("%s successfully cancelled reservation %s for seat %s",
            threadName, reservationId, reservation.getSeat().getSeatNumber()));
        return true;
      } else {
        logger.logWarning(String.format("%s failed to cancel reservation %s - seat not reserved",
            threadName, reservationId));
        return false;
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * Queries all flight information and seat availability.
   */
  public void queryFlightInformation() {
    lock.readLock().lock();
    try {
      String threadName = Thread.currentThread().getName();
      logger.logInfo(String.format("%s querying flight information", threadName));

      StringBuilder info = new StringBuilder();
      info.append("\n=== FLIGHT INFORMATION ===\n");

      for (Flight flight : flights.values()) {
        info.append(String.format("Flight: %s | Route: %s | Status: %s\n",
            flight.getFlightNumber(), flight.getRoute(), flight.getStatus().getDisplayName()));
        info.append(String.format("  Available Seats: %d/%d | Reserved: %d\n",
            flight.getAvailableSeatsCount(), flight.getTotalSeats(), flight.getReservedSeatsCount()));
        info.append(String.format("  Departure: %s | Aircraft: %s\n",
            flight.getDepartureTime(), flight.getAircraft().getModel()));
        info.append("  ---\n");
      }

      System.out.println(info.toString());
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Queries detailed seat map for a specific flight.
   * 
   * @param flightNumber The flight number
   */
  public void querySeatMap(String flightNumber) {
    lock.readLock().lock();
    try {
      String threadName = Thread.currentThread().getName();
      logger.logInfo(String.format("%s querying seat map for flight %s", threadName, flightNumber));

      Flight flight = flights.get(flightNumber);
      if (flight == null) {
        System.out.println("Flight not found: " + flightNumber);
        return;
      }

      Seat[][] seatLayout = flight.getAircraft().getSeatLayout();
      String[] columns = flight.getAircraft().getColumns();

      System.out.printf("\n=== SEAT MAP FOR FLIGHT %s ===\n", flightNumber);
      System.out.printf("Aircraft: %s | Route: %s\n",
          flight.getAircraft().getModel(), flight.getRoute());

      // Print column headers
      System.out.print("Row ");
      for (String col : columns) {
        System.out.printf("%3s", col);
      }
      System.out.println();

      // Print seat map
      for (int row = 0; row < seatLayout.length; row++) {
        System.out.printf("%3d ", row + 1);
        for (int col = 0; col < seatLayout[row].length; col++) {
          Seat seat = seatLayout[row][col];
          String status = seat.isReserved() ? " X " : " O ";
          System.out.print(status);
        }
        System.out.println();
      }

      System.out.println("Legend: O = Available, X = Reserved");
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Gets all flights in the system.
   * 
   * @return List of all flights
   */
  public List<Flight> getAllFlights() {
    lock.readLock().lock();
    try {
      return flights.values().stream().collect(Collectors.toList());
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Gets a specific flight by number.
   * 
   * @param flightNumber The flight number
   * @return The flight, or null if not found
   */
  public Flight getFlight(String flightNumber) {
    lock.readLock().lock();
    try {
      return flights.get(flightNumber);
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Gets all reservations.
   * 
   * @return List of all reservations
   */
  public List<Reservation> getAllReservations() {
    lock.readLock().lock();
    try {
      return reservations.values().stream().collect(Collectors.toList());
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Gets a specific reservation by ID.
   * 
   * @param reservationId The reservation ID
   * @return The reservation, or null if not found
   */
  public Reservation getReservation(String reservationId) {
    lock.readLock().lock();
    try {
      return reservations.get(reservationId);
    } finally {
      lock.readLock().unlock();
    }
  }

  private String generateReservationId() {
    return "RSV" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
  }

  /**
   * Gets system statistics.
   * 
   * @return SystemStats object with current statistics
   */
  public SystemStats getSystemStats() {
    lock.readLock().lock();
    try {
      int totalFlights = flights.size();
      int totalReservations = reservations.size();
      int totalSeats = flights.values().stream()
          .mapToInt(Flight::getTotalSeats)
          .sum();
      int availableSeats = flights.values().stream()
          .mapToInt(Flight::getAvailableSeatsCount)
          .sum();

      return new SystemStats(totalFlights, totalReservations, totalSeats, availableSeats);
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Searches for flights on a specific date.
   * 
   * @param date The date to search for
   * @return List of flights on the specified date
   */
  public List<Flight> getFlightsByDate(LocalDate date) {
    lock.readLock().lock();
    try {
      return flights.values().stream()
          .filter(flight -> flight.getDepartureTime().toLocalDate().equals(date))
          .collect(Collectors.toList());
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Searches for flights between two dates.
   * 
   * @param startDate Start date (inclusive)
   * @param endDate   End date (inclusive)
   * @return List of flights in the date range
   */
  public List<Flight> getFlightsByDateRange(LocalDate startDate, LocalDate endDate) {
    lock.readLock().lock();
    try {
      return flights.values().stream()
          .filter(flight -> {
            LocalDate flightDate = flight.getDepartureTime().toLocalDate();
            return !flightDate.isBefore(startDate) && !flightDate.isAfter(endDate);
          })
          .collect(Collectors.toList());
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Searches for flights on a specific route and date.
   * 
   * @param origin      Origin airport code
   * @param destination Destination airport code
   * @param date        Travel date
   * @return List of matching flights
   */
  public List<Flight> searchFlights(String origin, String destination, LocalDate date) {
    lock.readLock().lock();
    try {
      return flights.values().stream()
          .filter(flight -> flight.getOrigin().equalsIgnoreCase(origin))
          .filter(flight -> flight.getDestination().equalsIgnoreCase(destination))
          .filter(flight -> flight.getDepartureTime().toLocalDate().equals(date))
          .collect(Collectors.toList());
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Gets upcoming flights (departing from now onwards).
   * 
   * @return List of upcoming flights
   */
  public List<Flight> getUpcomingFlights() {
    lock.readLock().lock();
    try {
      LocalDateTime now = LocalDateTime.now();
      return flights.values().stream()
          .filter(flight -> flight.getDepartureTime().isAfter(now))
          .sorted((f1, f2) -> f1.getDepartureTime().compareTo(f2.getDepartureTime()))
          .collect(Collectors.toList());
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Gets flights departing today.
   * 
   * @return List of today's flights
   */
  public List<Flight> getTodaysFlights() {
    return getFlightsByDate(LocalDate.now());
  }

  /**
   * Gets flights departing tomorrow.
   * 
   * @return List of tomorrow's flights
   */
  public List<Flight> getTomorrowsFlights() {
    return getFlightsByDate(LocalDate.now().plusDays(1));
  }

  /**
   * Gets flights for the next week.
   * 
   * @return List of flights for the next 7 days
   */
  public List<Flight> getWeeklyFlights() {
    LocalDate today = LocalDate.now();
    return getFlightsByDateRange(today, today.plusDays(7));
  }

  /**
   * System statistics container.
   */
  public static class SystemStats {
    private final int totalFlights;
    private final int totalReservations;
    private final int totalSeats;
    private final int availableSeats;

    public SystemStats(int totalFlights, int totalReservations, int totalSeats, int availableSeats) {
      this.totalFlights = totalFlights;
      this.totalReservations = totalReservations;
      this.totalSeats = totalSeats;
      this.availableSeats = availableSeats;
    }

    public int getTotalFlights() {
      return totalFlights;
    }

    public int getTotalReservations() {
      return totalReservations;
    }

    public int getTotalSeats() {
      return totalSeats;
    }

    public int getAvailableSeats() {
      return availableSeats;
    }

    public int getReservedSeats() {
      return totalSeats - availableSeats;
    }

    public double getOccupancyRate() {
      return totalSeats > 0 ? (double) getReservedSeats() / totalSeats * 100 : 0;
    }
  }
}
