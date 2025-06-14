package com.airline.console;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import com.airline.core.AirlineReservationSystem;
import com.airline.core.Reservation;
import com.airline.model.Aircraft;
import com.airline.model.Flight;
import com.airline.model.Passenger;

/**
 * Enhanced console application for the airline reservation system.
 * Now includes comprehensive date-based flight management.
 *
 * @author İshak Duran
 * @version 2.0
 */
public class ConsoleApp {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  private static final Scanner scanner = new Scanner(System.in);
  private static final AirlineReservationSystem system = new AirlineReservationSystem();

  public static void main(String[] args) {
    System.out.println("✈️  Welcome to Advanced Airline Reservation System");
    System.out.println("================================================");

    // Initialize sample data with dates
    initializeSampleData();

    boolean running = true;
    while (running) {
      showMainMenu();
      int choice = getIntInput("Enter your choice: ");

      switch (choice) {
        case 1 -> searchFlightsByDate();
        case 2 -> searchFlightsByRoute();
        case 3 -> viewTodaysFlights();
        case 4 -> viewUpcomingFlights();
        case 5 -> viewWeeklySchedule();
        case 6 -> makeReservation();
        case 7 -> cancelReservation();
        case 8 -> viewFlightDetails();
        case 9 -> viewSystemStats();
        case 10 -> addNewFlight();
        case 0 -> {
          System.out.println("Thank you for using Airline Reservation System!");
          running = false;
        }
        default -> System.out.println("❌ Invalid choice. Please try again.");
      }
    }
    scanner.close();
  }

  private static void showMainMenu() {
    System.out.println("\n📋 MAIN MENU");
    System.out.println("=============");
    System.out.println("1. 📅 Search Flights by Date");
    System.out.println("2. 🛫 Search Flights by Route");
    System.out.println("3. 📅 Today's Flights");
    System.out.println("4. ⏰ Upcoming Flights");
    System.out.println("5. 📆 Weekly Schedule");
    System.out.println("6. ✅ Make Reservation");
    System.out.println("7. ❌ Cancel Reservation");
    System.out.println("8. 📊 Flight Details");
    System.out.println("9. 📈 System Statistics");
    System.out.println("10. ➕ Add New Flight");
    System.out.println("0. 🚪 Exit");
  }

  private static void searchFlightsByDate() {
    System.out.println("\n📅 SEARCH FLIGHTS BY DATE");
    System.out.println("==========================");

    LocalDate date = getDateInput("Enter date (YYYY-MM-DD): ");
    if (date == null)
      return;

    List<Flight> flights = system.getFlightsByDate(date);
    displayFlights(flights, "Flights on " + date.format(DATE_FORMAT));
  }

  private static void searchFlightsByRoute() {
    System.out.println("\n🛫 SEARCH FLIGHTS BY ROUTE");
    System.out.println("===========================");

    System.out.print("Origin (e.g., IST): ");
    String origin = scanner.nextLine().trim();

    System.out.print("Destination (e.g., JFK): ");
    String destination = scanner.nextLine().trim();

    LocalDate date = getDateInput("Travel date (YYYY-MM-DD): ");
    if (date == null)
      return;

    List<Flight> flights = system.searchFlights(origin, destination, date);
    displayFlights(flights, String.format("Flights from %s to %s on %s",
        origin, destination, date.format(DATE_FORMAT)));
  }

  private static void viewTodaysFlights() {
    List<Flight> flights = system.getTodaysFlights();
    displayFlights(flights, "Today's Flights - " + LocalDate.now().format(DATE_FORMAT));
  }

  private static void viewUpcomingFlights() {
    List<Flight> flights = system.getUpcomingFlights();
    displayFlights(flights, "Upcoming Flights");
  }

  private static void viewWeeklySchedule() {
    List<Flight> flights = system.getWeeklyFlights();
    displayFlights(flights, "This Week's Schedule");
  }

  private static void makeReservation() {
    System.out.println("\n✅ MAKE RESERVATION");
    System.out.println("====================");

    // First show available flights
    viewUpcomingFlights();

    System.out.print("Enter flight number: ");
    String flightNumber = scanner.nextLine().trim();

    Flight flight = system.getFlight(flightNumber);
    if (flight == null) {
      System.out.println("❌ Flight not found!");
      return;
    }

    // Show seat map
    system.querySeatMap(flightNumber);

    System.out.print("Enter seat number (e.g., 1A): ");
    String seatNumber = scanner.nextLine().trim();

    // Get passenger details
    System.out.println("\n👤 Passenger Information:");
    System.out.print("First Name: ");
    String firstName = scanner.nextLine().trim();

    System.out.print("Last Name: ");
    String lastName = scanner.nextLine().trim();

    System.out.print("Email: ");
    String email = scanner.nextLine().trim();

    System.out.print("Phone: ");
    String phone = scanner.nextLine().trim();

    Passenger passenger = new Passenger(firstName, lastName, email, phone, "");

    Reservation reservation = system.makeReservation(flightNumber, seatNumber, passenger);
    if (reservation != null) {
      System.out.println("\n🎉 Reservation successful!");
      System.out.println("📋 Reservation ID: " + reservation.getReservationId());
      System.out.println("✈️  Flight: " + flight.getFlightNumber());
      System.out.println("💺 Seat: " + seatNumber);
      System.out.println("👤 Passenger: " + passenger.getFullName());
      System.out.println("📅 Date: " + flight.getDepartureTime().format(DATETIME_FORMAT));
    } else {
      System.out.println("❌ Reservation failed. Seat may be already reserved.");
    }
  }

  private static void cancelReservation() {
    System.out.println("\n❌ CANCEL RESERVATION");
    System.out.println("======================");

    System.out.print("Enter reservation ID: ");
    String reservationId = scanner.nextLine().trim();

    if (system.cancelReservation(reservationId)) {
      System.out.println("✅ Reservation cancelled successfully!");
    } else {
      System.out.println("❌ Failed to cancel reservation. Please check the ID.");
    }
  }

  private static void viewFlightDetails() {
    System.out.println("\n📊 FLIGHT DETAILS");
    System.out.println("==================");

    System.out.print("Enter flight number: ");
    String flightNumber = scanner.nextLine().trim();

    Flight flight = system.getFlight(flightNumber);
    if (flight == null) {
      System.out.println("❌ Flight not found!");
      return;
    }

    System.out.println("\n✈️  Flight Information:");
    System.out.println("Flight Number: " + flight.getFlightNumber());
    System.out.println("Route: " + flight.getRoute());
    System.out.println("Aircraft: " + flight.getAircraft().getModel());
    System.out.println("Departure: " + flight.getDepartureTime().format(DATETIME_FORMAT));
    System.out.println("Arrival: " + flight.getArrivalTime().format(DATETIME_FORMAT));
    System.out.println("Status: " + flight.getStatus().getDisplayName());
    System.out.println("Base Price: $" + flight.getBasePrice());
    System.out.println("Available Seats: " + flight.getAvailableSeatsCount() + "/" + flight.getTotalSeats());

    // Show seat map
    system.querySeatMap(flightNumber);
  }

  private static void viewSystemStats() {
    AirlineReservationSystem.SystemStats stats = system.getSystemStats();

    System.out.println("\n📈 SYSTEM STATISTICS");
    System.out.println("=====================");
    System.out.println("Total Flights: " + stats.getTotalFlights());
    System.out.println("Total Reservations: " + stats.getTotalReservations());
    System.out.println("Total Seats: " + stats.getTotalSeats());
    System.out.println("Available Seats: " + stats.getAvailableSeats());
    System.out.println("Reserved Seats: " + stats.getReservedSeats());
    System.out.println("Occupancy Rate: " + String.format("%.1f%%", stats.getOccupancyRate()));
  }

  private static void addNewFlight() {
    System.out.println("\n➕ ADD NEW FLIGHT");
    System.out.println("==================");

    System.out.print("Flight Number (e.g., TK001): ");
    String flightNumber = scanner.nextLine().trim();

    System.out.print("Origin (e.g., IST): ");
    String origin = scanner.nextLine().trim();

    System.out.print("Destination (e.g., JFK): ");
    String destination = scanner.nextLine().trim();

    LocalDateTime departureTime = getDateTimeInput("Departure (YYYY-MM-DD HH:MM): ");
    if (departureTime == null)
      return;

    LocalDateTime arrivalTime = getDateTimeInput("Arrival (YYYY-MM-DD HH:MM): ");
    if (arrivalTime == null)
      return;

    double price = getDoubleInput("Base Price ($): ");

    // For simplicity, use a default aircraft
    Aircraft aircraft = new Aircraft("B737", "Boeing 737-800", 30, new String[] { "A", "B", "C", "D", "E", "F" });

    Flight flight = new Flight(flightNumber, origin, destination, departureTime, arrivalTime, aircraft, price);
    system.addFlight(flight);

    System.out.println("✅ Flight added successfully!");
  }

  private static void displayFlights(List<Flight> flights, String title) {
    System.out.println("\n" + title);
    System.out.println("=".repeat(title.length()));

    if (flights.isEmpty()) {
      System.out.println("No flights found.");
      return;
    }

    System.out.printf("%-8s %-12s %-16s %-8s %-10s %-8s%n",
        "Flight", "Route", "Departure", "Status", "Available", "Price");
    System.out.println("-".repeat(70));

    for (Flight flight : flights) {
      System.out.printf("%-8s %-12s %-16s %-8s %-10s $%-7.0f%n",
          flight.getFlightNumber(),
          flight.getRoute(),
          flight.getDepartureTime().format(DATETIME_FORMAT),
          flight.getStatus().getDisplayName(),
          flight.getAvailableSeatsCount() + "/" + flight.getTotalSeats(),
          flight.getBasePrice());
    }
  }

  private static LocalDate getDateInput(String prompt) {
    System.out.print(prompt);
    String input = scanner.nextLine().trim();
    try {
      return LocalDate.parse(input, DATE_FORMAT);
    } catch (DateTimeParseException e) {
      System.out.println("❌ Invalid date format. Please use YYYY-MM-DD");
      return null;
    }
  }

  private static LocalDateTime getDateTimeInput(String prompt) {
    System.out.print(prompt);
    String input = scanner.nextLine().trim();
    try {
      return LocalDateTime.parse(input, DATETIME_FORMAT);
    } catch (DateTimeParseException e) {
      System.out.println("❌ Invalid datetime format. Please use YYYY-MM-DD HH:MM");
      return null;
    }
  }

  private static int getIntInput(String prompt) {
    System.out.print(prompt);
    try {
      return Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  private static double getDoubleInput(String prompt) {
    System.out.print(prompt);
    try {
      return Double.parseDouble(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  private static void initializeSampleData() {
    System.out.println("📦 Initializing sample flight data...");

    // Create sample aircraft
    Aircraft boeing737 = new Aircraft("B737", "Boeing 737-800", 30, new String[] { "A", "B", "C", "D", "E", "F" });
    Aircraft airbusA320 = new Aircraft("A320", "Airbus A320", 28, new String[] { "A", "B", "C", "D", "E", "F" });
    Aircraft boeing777 = new Aircraft("B777", "Boeing 777-300ER", 42,
        new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "J", "K" });

    // Create flights with various dates
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

    // Next week flights
    LocalDateTime nextWeek = now.plusDays(7);
    system.addFlight(new Flight("AF007", "CDG", "JFK",
        nextWeek.withHour(9).withMinute(30),
        nextWeek.withHour(17).withMinute(45),
        boeing777, 825.50));

    system.addFlight(new Flight("TK011", "IST", "LAX",
        nextWeek.withHour(15).withMinute(20),
        nextWeek.plusDays(1).withHour(8).withMinute(30),
        boeing777, 1250.00));

    // Various other dates
    for (int i = 2; i <= 6; i++) {
      LocalDateTime futureDate = now.plusDays(i);
      system.addFlight(new Flight("TK" + String.format("%03d", i), "IST", "DXB",
          futureDate.withHour(6 + i).withMinute(0),
          futureDate.withHour(10 + i).withMinute(30),
          airbusA320, 199.99 + (i * 50)));
    }

    System.out.println("✅ Sample data loaded successfully!");
    System.out.println("📊 Total flights: " + system.getAllFlights().size());
  }
}
