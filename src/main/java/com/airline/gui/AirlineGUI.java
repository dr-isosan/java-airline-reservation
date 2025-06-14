package com.airline.gui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.table.TableColumn;
import javax.swing.text.TableView;
import javax.swing.text.TableView.TableCell;

import com.airline.model.*;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import src.main.java.com.airline.core.AirlineReservationSystem;
import src.main.java.com.airline.core.Reservation;
import src.main.java.com.airline.model.Aircraft;
import src.main.java.com.airline.model.Flight;
import src.main.java.com.airline.model.Passenger;
import src.main.java.com.airline.model.Seat;

/**
 * JavaFX GUI for the Airline Reservation System with enhanced date features.
 *
 * @author İshak Duran
 * @version 2.0
 */
public class AirlineGUI extends Application {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private AirlineReservationSystem system;
  private TableView<Flight> flightTable;
  private Label statusLabel;
  private DatePicker datePicker;
  private ComboBox<String> originCombo;
  private ComboBox<String> destinationCombo;

  @Override
  public void start(Stage primaryStage) {
    system = new AirlineReservationSystem();
    initializeSampleData();

    primaryStage.setTitle("✈️ Airline Reservation System - Enhanced Date Features");

    // Create main layout
    BorderPane root = new BorderPane();
    root.setPadding(new Insets(10));

    // Header
    VBox header = createHeader();
    root.setTop(header);

    // Center - Flight table and search controls
    VBox center = createCenterPane();
    root.setCenter(center);

    // Bottom - Status and statistics
    VBox bottom = createBottomPane();
    root.setBottom(bottom);

    Scene scene = new Scene(root, 1200, 800);
    scene.getStylesheets().add(getClass().getResource("/styles/airline.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.show();

    // Load today's flights initially
    loadTodaysFlights();
  }

  private VBox createHeader() {
    VBox header = new VBox(10);
    header.setAlignment(Pos.CENTER);
    header.setPadding(new Insets(20));

    Label titleLabel = new Label("✈️ Advanced Airline Reservation System");
    titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
    titleLabel.setTextFill(Color.DARKBLUE);

    Label subtitleLabel = new Label("Enhanced Date-Based Flight Management");
    subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
    subtitleLabel.setTextFill(Color.GRAY);

    header.getChildren().addAll(titleLabel, subtitleLabel);
    return header;
  }

  private VBox createCenterPane() {
    VBox center = new VBox(15);
    center.setPadding(new Insets(10));

    // Search controls
    HBox searchControls = createSearchControls();

    // Flight table
    createFlightTable();

    center.getChildren().addAll(searchControls, flightTable);
    return center;
  }

  private HBox createSearchControls() {
    HBox searchBox = new HBox(10);
    searchBox.setAlignment(Pos.CENTER_LEFT);
    searchBox.setPadding(new Insets(10));

    // Date picker
    Label dateLabel = new Label("📅 Date:");
    dateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    datePicker = new DatePicker(LocalDate.now());
    datePicker.setOnAction(e -> searchFlights());

    // Origin combo
    Label originLabel = new Label("🛫 From:");
    originLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    originCombo = new ComboBox<>();
    originCombo.getItems().addAll("All", "IST", "JFK", "LHR", "CDG", "LAX", "DXB", "FRA");
    originCombo.setValue("All");
    originCombo.setOnAction(e -> searchFlights());

    // Destination combo
    Label destLabel = new Label("🛬 To:");
    destLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    destinationCombo = new ComboBox<>();
    destinationCombo.getItems().addAll("All", "IST", "JFK", "LHR", "CDG", "LAX", "DXB", "FRA");
    destinationCombo.setValue("All");
    destinationCombo.setOnAction(e -> searchFlights());

    // Quick action buttons
    Button todayBtn = new Button("📅 Today");
    todayBtn.setOnAction(e -> loadTodaysFlights());

    Button tomorrowBtn = new Button("📅 Tomorrow");
    tomorrowBtn.setOnAction(e -> loadTomorrowsFlights());

    Button weeklyBtn = new Button("📆 This Week");
    weeklyBtn.setOnAction(e -> loadWeeklyFlights());

    Button upcomingBtn = new Button("⏰ Upcoming");
    upcomingBtn.setOnAction(e -> loadUpcomingFlights());

    searchBox.getChildren().addAll(
        dateLabel, datePicker,
        new Separator(), originLabel, originCombo,
        new Separator(), destLabel, destinationCombo,
        new Separator(), todayBtn, tomorrowBtn, weeklyBtn, upcomingBtn);

    return searchBox;
  }

  private void createFlightTable() {
    flightTable = new TableView<>();

    // Flight Number column
    TableColumn<Flight, String> flightCol = new TableColumn<>("Flight");
    flightCol.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
    flightCol.setPrefWidth(80);

    // Route column
    TableColumn<Flight, String> routeCol = new TableColumn<>("Route");
    routeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoute()));
    routeCol.setPrefWidth(120);

    // Departure column
    TableColumn<Flight, String> departureCol = new TableColumn<>("Departure");
    departureCol.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getDepartureTime().format(DATETIME_FORMAT)));
    departureCol.setPrefWidth(140);

    // Arrival column
    TableColumn<Flight, String> arrivalCol = new TableColumn<>("Arrival");
    arrivalCol.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getArrivalTime().format(DATETIME_FORMAT)));
    arrivalCol.setPrefWidth(140);

    // Aircraft column
    TableColumn<Flight, String> aircraftCol = new TableColumn<>("Aircraft");
    aircraftCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAircraft().getModel()));
    aircraftCol.setPrefWidth(150);

    // Status column
    TableColumn<Flight, String> statusCol = new TableColumn<>("Status");
    statusCol
        .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().getDisplayName()));
    statusCol.setPrefWidth(100);

    // Available Seats column
    TableColumn<Flight, String> seatsCol = new TableColumn<>("Available");
    seatsCol
        .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAvailableSeatsCount() + "/" +
            cellData.getValue().getTotalSeats()));
    seatsCol.setPrefWidth(80);

    // Price column
    TableColumn<Flight, String> priceCol = new TableColumn<>("Price");
    priceCol.setCellValueFactory(
        cellData -> new SimpleStringProperty("$" + String.format("%.0f", cellData.getValue().getBasePrice())));
    priceCol.setPrefWidth(80);

    // Actions column
    TableColumn<Flight, Void> actionCol = new TableColumn<>("Actions");
    actionCol.setCellFactory(param -> new TableCell<Flight, Void>() {
      private final Button reserveBtn = new Button("📋 Reserve");

      {
        reserveBtn.setOnAction(event -> {
          Flight flight = getTableView().getItems().get(getIndex());
          showReservationDialog(flight);
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setGraphic(null);
        } else {
          setGraphic(reserveBtn);
        }
      }
    });
    actionCol.setPrefWidth(100);

    flightTable.getColumns().addAll(flightCol, routeCol, departureCol, arrivalCol,
        aircraftCol, statusCol, seatsCol, priceCol, actionCol);
  }

  private VBox createBottomPane() {
    VBox bottom = new VBox(10);
    bottom.setPadding(new Insets(10));

    statusLabel = new Label("Ready - Showing today's flights");
    statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

    // Statistics
    HBox statsBox = createStatsBox();

    bottom.getChildren().addAll(new Separator(), statusLabel, statsBox);
    return bottom;
  }

  private HBox createStatsBox() {
    HBox statsBox = new HBox(20);
    statsBox.setAlignment(Pos.CENTER);
    statsBox.setPadding(new Insets(10));

    AirlineReservationSystem.SystemStats stats = system.getSystemStats();

    Label flightsLabel = new Label("✈️ Flights: " + stats.getTotalFlights());
    Label reservationsLabel = new Label("📋 Reservations: " + stats.getTotalReservations());
    Label seatsLabel = new Label("💺 Seats: " + stats.getAvailableSeats() + "/" + stats.getTotalSeats());
    Label occupancyLabel = new Label("📊 Occupancy: " + String.format("%.1f%%", stats.getOccupancyRate()));

    statsBox.getChildren().addAll(flightsLabel, reservationsLabel, seatsLabel, occupancyLabel);
    return statsBox;
  }

  private void searchFlights() {
    LocalDate selectedDate = datePicker.getValue();
    String origin = originCombo.getValue();
    String destination = destinationCombo.getValue();

    List<Flight> flights;

    if ("All".equals(origin) && "All".equals(destination)) {
      flights = system.getFlightsByDate(selectedDate);
      statusLabel.setText("Showing flights for " + selectedDate.format(DATE_FORMAT));
    } else if ("All".equals(origin)) {
      flights = system.getFlightsByDate(selectedDate).stream()
          .filter(f -> f.getDestination().equals(destination))
          .toList();
      statusLabel.setText("Showing flights to " + destination + " on " + selectedDate.format(DATE_FORMAT));
    } else if ("All".equals(destination)) {
      flights = system.getFlightsByDate(selectedDate).stream()
          .filter(f -> f.getOrigin().equals(origin))
          .toList();
      statusLabel.setText("Showing flights from " + origin + " on " + selectedDate.format(DATE_FORMAT));
    } else {
      flights = system.searchFlights(origin, destination, selectedDate);
      statusLabel
          .setText("Showing flights from " + origin + " to " + destination + " on " + selectedDate.format(DATE_FORMAT));
    }

    updateFlightTable(flights);
  }

  private void loadTodaysFlights() {
    List<Flight> flights = system.getTodaysFlights();
    updateFlightTable(flights);
    datePicker.setValue(LocalDate.now());
    statusLabel.setText("Showing today's flights (" + LocalDate.now().format(DATE_FORMAT) + ")");
  }

  private void loadTomorrowsFlights() {
    List<Flight> flights = system.getTomorrowsFlights();
    updateFlightTable(flights);
    datePicker.setValue(LocalDate.now().plusDays(1));
    statusLabel.setText("Showing tomorrow's flights (" + LocalDate.now().plusDays(1).format(DATE_FORMAT) + ")");
  }

  private void loadWeeklyFlights() {
    List<Flight> flights = system.getWeeklyFlights();
    updateFlightTable(flights);
    statusLabel.setText("Showing this week's flights (next 7 days)");
  }

  private void loadUpcomingFlights() {
    List<Flight> flights = system.getUpcomingFlights();
    updateFlightTable(flights);
    statusLabel.setText("Showing all upcoming flights");
  }

  private void updateFlightTable(List<Flight> flights) {
    ObservableList<Flight> flightData = FXCollections.observableArrayList(flights);
    flightTable.setItems(flightData);
  }

  private void showReservationDialog(Flight flight) {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Make Reservation - " + flight.getFlightNumber());
    dialog.setHeaderText("Reserve a seat on " + flight.getRoute());

    // Create form
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField firstNameField = new TextField();
    TextField lastNameField = new TextField();
    TextField emailField = new TextField();
    TextField phoneField = new TextField();
    ComboBox<String> seatCombo = new ComboBox<>();

    // Populate available seats
    for (int row = 1; row <= 10; row++) {
      for (String col : flight.getAircraft().getColumns()) {
        String seatNumber = row + col;
        Seat seat = flight.getAircraft().getSeat(seatNumber);
        if (seat != null && !seat.isReserved()) {
          seatCombo.getItems().add(seatNumber);
        }
      }
    }

    grid.add(new Label("First Name:"), 0, 0);
    grid.add(firstNameField, 1, 0);
    grid.add(new Label("Last Name:"), 0, 1);
    grid.add(lastNameField, 1, 1);
    grid.add(new Label("Email:"), 0, 2);
    grid.add(emailField, 1, 2);
    grid.add(new Label("Phone:"), 0, 3);
    grid.add(phoneField, 1, 3);
    grid.add(new Label("Seat:"), 0, 4);
    grid.add(seatCombo, 1, 4);

    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == ButtonType.OK) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String seat = seatCombo.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || seat == null) {
          showAlert("Error", "Please fill all fields and select a seat.");
          return null;
        }

        Passenger passenger = new Passenger(firstName, lastName, email, phone, "");
        Reservation reservation = system.makeReservation(flight.getFlightNumber(), seat, passenger);

        if (reservation != null) {
          showAlert("Success", "Reservation successful!\nReservation ID: " + reservation.getReservationId());
          // Refresh the table
          searchFlights();
        } else {
          showAlert("Error", "Reservation failed. Seat may be already reserved.");
        }
      }
      return null;
    });

    dialog.showAndWait();
  }

  private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void initializeSampleData() {
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
      system.addFlight(new Flight("TK" + String.format("%03d", i + 1), "IST", "DXB",
          futureDate.withHour(6 + i).withMinute(0),
          futureDate.withHour(10 + i).withMinute(30),
          airbusA320, 199.99 + (i * 50)));
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
