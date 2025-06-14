package com.airline.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.airline.core.AirlineReservationSystem;
import com.airline.core.Reservation;
import com.airline.model.Aircraft;
import com.airline.model.Flight;
import com.airline.model.Passenger;

/**
 * Comprehensive test suite for the enhanced airline reservation system.
 * Tests both basic functionality and new date features.
 *
 * @author İshak Duran
 * @version 2.0
 */
public class AirlineReservationSystemTest {

  private static AirlineReservationSystem system;
  private static int testCounter = 0;
  private static int passedTests = 0;

  public static void main(String[] args) {
    System.out.println("🧪 AIRLINE RESERVATION SYSTEM TEST SUITE");
    System.out.println("==========================================");

    system = new AirlineReservationSystem();
    setupTestData();

    // Run all tests
    testBasicReservation();
    testConcurrentReservations();
    testDateBasedSearch();
    testFlightsByDateRange();
    testRouteAndDateSearch();
    testTodaysTomorrowsFlights();
    testWeeklyFlights();
    testUpcomingFlights();
    testCancellation();
    testSystemStats();

    System.out.println("\n📊 TEST RESULTS");
    System.out.println("================");
    System.out.printf("✅ Passed: %d/%d tests%n", passedTests, testCounter);
    System.out.printf("📈 Success Rate: %.1f%%%n", (double) passedTests / testCounter * 100);

    if (passedTests == testCounter) {
      System.out.println("🎉 ALL TESTS PASSED! The system is ready for production.");
    } else {
      System.out.println("❌ Some tests failed. Please review the implementation.");
    }
  }

  private static void setupTestData() {
    System.out.println("📦 Setting up test data...");

    Aircraft boeing737 = new Aircraft("B737", "Boeing 737-800", 30, new String[] { "A", "B", "C", "D", "E", "F" });
    Aircraft airbusA320 = new Aircraft("A320", "Airbus A320", 28, new String[] { "A", "B", "C", "D", "E", "F" });
    Aircraft boeing777 = new Aircraft("B777", "Boeing 777-300ER", 42,
        new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "J", "K" });

    LocalDateTime now = LocalDateTime.now();

    // Today's flights
    system.addFlight(new Flight("TK001", "IST", "JFK",
        now.withHour(10).withMinute(30),
        now.withHour(18).withMinute(45),
        boeing777, 899.99));

    system.addFlight(new Flight("LH440", "FRA", "JFK",
        now.withHour(14).withMinute(15),
        now.withHour(22).withMinute(30),
        boeing777, 750.00));

    // Tomorrow's flights
    LocalDateTime tomorrow = now.plusDays(1);
    system.addFlight(new Flight("TK003", "IST", "LHR",
        tomorrow.withHour(8).withMinute(0),
        tomorrow.withHour(11).withMinute(30),
        boeing737, 299.99));

    system.addFlight(new Flight("BA100", "LHR", "JFK",
        tomorrow.withHour(12).withMinute(45),
        tomorrow.withHour(20).withMinute(15),
        boeing777, 950.00));

    // Day after tomorrow
    LocalDateTime dayAfter = now.plusDays(2);
    system.addFlight(new Flight("AF007", "CDG", "JFK",
        dayAfter.withHour(9).withMinute(30),
        dayAfter.withHour(17).withMinute(45),
        boeing777, 825.50));

    // Various future dates
    for (int i = 3; i <= 8; i++) {
      LocalDateTime futureDate = now.plusDays(i);
      system.addFlight(new Flight("TK" + String.format("%03d", i), "IST", "DXB",
          futureDate.withHour(6 + i).withMinute(0),
          futureDate.withHour(10 + i).withMinute(30),
          airbusA320, 199.99 + (i * 50)));
    }

    System.out.println("✅ Test data setup complete");
  }

  private static void testBasicReservation() {
    test("Basic Reservation", () -> {
      Passenger passenger = new Passenger("John", "Doe", "john@example.com", "+1234567890", "P123456");
      Reservation reservation = system.makeReservation("TK001", "1A", passenger);

      assert reservation != null : "Reservation should not be null";
      assert reservation.getPassenger().equals(passenger) : "Passenger should match";
      assert reservation.getFlight().getFlightNumber().equals("TK001") : "Flight number should match";
      assert reservation.getSeat().getSeatNumber().equals("1A") : "Seat number should match";

      return true;
    });
  }

  private static void testConcurrentReservations() {
    test("Concurrent Reservations", () -> {
      int numberOfThreads = 10;
      ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
      CountDownLatch latch = new CountDownLatch(numberOfThreads);

      boolean[] results = new boolean[numberOfThreads];

      for (int i = 0; i < numberOfThreads; i++) {
        final int threadIndex = i;
        executor.submit(() -> {
          try {
            Passenger passenger = new Passenger("User" + threadIndex, "Test",
                "user" + threadIndex + "@example.com", "+123456789" + threadIndex, "P" + threadIndex);
            Reservation reservation = system.makeReservation("LH440", (threadIndex + 1) + "A", passenger);
            results[threadIndex] = (reservation != null);
          } finally {
            latch.countDown();
          }
        });
      }

      try {
        latch.await();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return false;
      }

      executor.shutdown();

      // Count successful reservations
      int successCount = 0;
      for (boolean result : results) {
        if (result)
          successCount++;
      }

      System.out.printf("   📊 Concurrent reservations: %d/%d successful%n", successCount, numberOfThreads);
      return successCount > 0; // At least some should succeed
    });
  }

  private static void testDateBasedSearch() {
    test("Date-Based Flight Search", () -> {
      LocalDate today = LocalDate.now();
      List<Flight> todayFlights = system.getFlightsByDate(today);

      assert !todayFlights.isEmpty() : "Should find today's flights";

      for (Flight flight : todayFlights) {
        assert flight.getDepartureTime().toLocalDate().equals(today) : "All flights should be for today";
      }

      System.out.printf("   📅 Found %d flights for today%n", todayFlights.size());
      return true;
    });
  }

  private static void testFlightsByDateRange() {
    test("Flight Search by Date Range", () -> {
      LocalDate today = LocalDate.now();
      LocalDate endDate = today.plusDays(3);

      List<Flight> rangeFlights = system.getFlightsByDateRange(today, endDate);

      assert !rangeFlights.isEmpty() : "Should find flights in date range";

      for (Flight flight : rangeFlights) {
        LocalDate flightDate = flight.getDepartureTime().toLocalDate();
        assert !flightDate.isBefore(today) && !flightDate.isAfter(endDate) : "Flight should be within date range";
      }

      System.out.printf("   📆 Found %d flights in date range%n", rangeFlights.size());
      return true;
    });
  }

  private static void testRouteAndDateSearch() {
    test("Route and Date Search", () -> {
      LocalDate today = LocalDate.now();
      List<Flight> istJfkFlights = system.searchFlights("IST", "JFK", today);

      for (Flight flight : istJfkFlights) {
        assert flight.getOrigin().equals("IST") : "Origin should be IST";
        assert flight.getDestination().equals("JFK") : "Destination should be JFK";
        assert flight.getDepartureTime().toLocalDate().equals(today) : "Date should match";
      }

      System.out.printf("   🛫 Found %d IST→JFK flights for today%n", istJfkFlights.size());
      return true;
    });
  }

  private static void testTodaysTomorrowsFlights() {
    test("Today's and Tomorrow's Flights", () -> {
      List<Flight> todayFlights = system.getTodaysFlights();
      List<Flight> tomorrowFlights = system.getTomorrowsFlights();

      LocalDate today = LocalDate.now();
      LocalDate tomorrow = today.plusDays(1);

      for (Flight flight : todayFlights) {
        assert flight.getDepartureTime().toLocalDate().equals(today) : "Today's flight should be for today";
      }

      for (Flight flight : tomorrowFlights) {
        assert flight.getDepartureTime().toLocalDate().equals(tomorrow) : "Tomorrow's flight should be for tomorrow";
      }

      System.out.printf("   📅 Today: %d flights, Tomorrow: %d flights%n",
          todayFlights.size(), tomorrowFlights.size());
      return true;
    });
  }

  private static void testWeeklyFlights() {
    test("Weekly Flight Schedule", () -> {
      List<Flight> weeklyFlights = system.getWeeklyFlights();

      LocalDate today = LocalDate.now();
      LocalDate weekEnd = today.plusDays(7);

      for (Flight flight : weeklyFlights) {
        LocalDate flightDate = flight.getDepartureTime().toLocalDate();
        assert !flightDate.isBefore(today) && !flightDate.isAfter(weekEnd) : "Flight should be within this week";
      }

      System.out.printf("   📆 Found %d flights this week%n", weeklyFlights.size());
      return !weeklyFlights.isEmpty();
    });
  }

  private static void testUpcomingFlights() {
    test("Upcoming Flights", () -> {
      List<Flight> upcomingFlights = system.getUpcomingFlights();

      LocalDateTime now = LocalDateTime.now();

      for (Flight flight : upcomingFlights) {
        assert flight.getDepartureTime().isAfter(now) : "Upcoming flight should be in the future";
      }

      // Check if flights are sorted chronologically
      for (int i = 1; i < upcomingFlights.size(); i++) {
        assert !upcomingFlights.get(i).getDepartureTime()
            .isBefore(upcomingFlights.get(i - 1).getDepartureTime()) : "Flights should be sorted chronologically";
      }

      System.out.printf("   ⏰ Found %d upcoming flights (sorted)%n", upcomingFlights.size());
      return !upcomingFlights.isEmpty();
    });
  }

  private static void testCancellation() {
    test("Reservation Cancellation", () -> {
      Passenger passenger = new Passenger("Jane", "Smith", "jane@example.com", "+0987654321", "P654321");
      Reservation reservation = system.makeReservation("TK003", "2B", passenger);

      assert reservation != null : "Initial reservation should succeed";

      String reservationId = reservation.getReservationId();
      boolean cancelled = system.cancelReservation(reservationId);

      assert cancelled : "Cancellation should succeed";

      Reservation cancelledReservation = system.getReservation(reservationId);
      assert cancelledReservation == null : "Cancelled reservation should not exist";

      return true;
    });
  }

  private static void testSystemStats() {
    test("System Statistics", () -> {
      AirlineReservationSystem.SystemStats stats = system.getSystemStats();

      assert stats.getTotalFlights() > 0 : "Should have flights";
      assert stats.getTotalSeats() > 0 : "Should have seats";
      assert stats.getAvailableSeats() <= stats.getTotalSeats() : "Available seats should not exceed total";
      assert stats.getOccupancyRate() >= 0 && stats.getOccupancyRate() <= 100
          : "Occupancy rate should be between 0-100%";

      System.out.printf("   📊 Stats: %d flights, %d/%d seats, %.1f%% occupancy%n",
          stats.getTotalFlights(), stats.getAvailableSeats(),
          stats.getTotalSeats(), stats.getOccupancyRate());
      return true;
    });
  }

  private static void test(String testName, TestFunction testFunction) {
    testCounter++;
    System.out.printf("%n🧪 Test %d: %s%n", testCounter, testName);

    try {
      boolean result = testFunction.run();
      if (result) {
        System.out.println("   ✅ PASSED");
        passedTests++;
      } else {
        System.out.println("   ❌ FAILED");
      }
    } catch (Exception e) {
      System.out.println("   ❌ FAILED - Exception: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @FunctionalInterface
  private interface TestFunction {
    boolean run() throws Exception;
  }
}
