package com.hospital.controller;

import com.hospital.model.Appointment;
import com.hospital.service.HospitalSystem;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class AppointmentController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> colId;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colDoctor;
    @FXML private TableColumn<Appointment, String> colPatient;
    @FXML private TableColumn<Appointment, String> colStatus;
    @FXML private VBox emptyState;

    private HospitalSystem sys;
    private ObservableList<Appointment> masterData;

    @FXML
    public void initialize() {
        sys = HospitalSystem.getInstance();
        masterData = FXCollections.observableArrayList(sys.getAllAppointments());

        setupTable();
        setupFilters();
        updateEmptyState();
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAppointmentId()));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        colTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimeSlot()));
        colDoctor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDoctor()));
        
        colPatient.setCellValueFactory(cellData -> {
            String pId = cellData.getValue().getPatient();
            return new SimpleStringProperty(pId != null ? pId : "-");
        });

        // Status Badge Styling Logic
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isBooked() ? "Booked" : "Available"));
        colStatus.setCellFactory(column -> new TableCell<Appointment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    Label badge = new Label(item);
                    badge.setStyle("-fx-padding: 3 8; -fx-background-radius: 10; -fx-font-weight: bold;");
                    if (item.equals("Booked")) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #DEF7EC; -fx-text-fill: #046C4E;"); // Green
                    } else {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #F1F5F9; -fx-text-fill: #475569;"); // Gray
                    }
                    setGraphic(badge);
                }
            }
        });

        appointmentTable.setRowFactory(tv -> {
            TableRow<Appointment> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            
            MenuItem bookItem = new MenuItem("📖 Book Appointment");
            bookItem.setOnAction(event -> handleBookAppointment(row.getItem()));
            
            MenuItem cancelBookItem = new MenuItem("❌ Cancel Booking");
            cancelBookItem.setOnAction(event -> handleCancelBooking(row.getItem()));
            
            MenuItem deleteSlotItem = new MenuItem("🗑 Delete Slot");
            deleteSlotItem.setStyle("-fx-text-fill: red;");
            deleteSlotItem.setOnAction(event -> handleDeleteSlot(row.getItem()));
            
            // Dynamic context menu based on row state
            row.setOnContextMenuRequested(event -> {
                contextMenu.getItems().clear();
                if (row.getItem() != null) {
                    if (row.getItem().isBooked()) {
                        contextMenu.getItems().addAll(cancelBookItem, deleteSlotItem);
                    } else {
                        contextMenu.getItems().addAll(bookItem, deleteSlotItem);
                    }
                }
            });

            row.contextMenuProperty().bind(
                javafx.beans.binding.Bindings.when(row.emptyProperty())
                .then((ContextMenu)null)
                .otherwise(contextMenu)
            );
            return row;
        });
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList("All Statuses", "Available", "Booked"));
        statusFilter.getSelectionModel().selectFirst();

        FilteredList<Appointment> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(app -> matchesFilters(app, newValue, statusFilter.getValue()));
            updateEmptyState(filteredData.isEmpty());
        });

        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(app -> matchesFilters(app, searchField.getText(), newValue));
            updateEmptyState(filteredData.isEmpty());
        });

        SortedList<Appointment> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(appointmentTable.comparatorProperty());
        appointmentTable.setItems(sortedData);
    }

    private boolean matchesFilters(Appointment app, String searchText, String status) {
        boolean matchSearch = true;
        if (searchText != null && !searchText.trim().isEmpty()) {
            String lowerCase = searchText.toLowerCase();
            String patientId = app.getPatient() != null ? app.getPatient().toLowerCase() : "";
            matchSearch = app.getAppointmentId().toLowerCase().contains(lowerCase) ||
                          app.getDoctor().toLowerCase().contains(lowerCase) ||
                          patientId.contains(lowerCase);
        }

        boolean matchStatus = true;
        if (status != null && !status.equals("All Statuses")) {
            boolean isBookedFilter = status.equals("Booked");
            matchStatus = (app.isBooked() == isBookedFilter);
        }

        return matchSearch && matchStatus;
    }

    private void updateEmptyState() { updateEmptyState(masterData.isEmpty()); }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            appointmentTable.setVisible(false); appointmentTable.setManaged(false);
            emptyState.setVisible(true); emptyState.setManaged(true);
        } else {
            appointmentTable.setVisible(true); appointmentTable.setManaged(true);
            emptyState.setVisible(false); emptyState.setManaged(false);
        }
    }

    @FXML
    public void handleAddSlot() {
        Dialog<Appointment> dialog = new Dialog<>();
        dialog.setTitle("+ Create Available Slot");
        dialog.setHeaderText("Set up a new appointment slot for a doctor.");

        ButtonType saveBtn = new ButtonType("Create Slot", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField(); idField.setPromptText("e.g. A004");
        TextField docField = new TextField(); docField.setPromptText("Doctor ID (e.g. D001)");
        TextField dateField = new TextField(); dateField.setPromptText("DD-MM-YYYY");
        TextField timeField = new TextField(); timeField.setPromptText("HH:MM AM - HH:MM PM");

        grid.add(new Label("Appt ID:"), 0, 0); grid.add(idField, 1, 0);
        grid.add(new Label("Doctor ID:"), 0, 1); grid.add(docField, 1, 1);
        grid.add(new Label("Date:"), 0, 2); grid.add(dateField, 1, 2);
        grid.add(new Label("Time Slot:"), 0, 3); grid.add(timeField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveBtn) {
                if (idField.getText().isEmpty() || docField.getText().isEmpty()) {
                    showToast("Error", "Appointment ID and Doctor ID are required.", Alert.AlertType.ERROR);
                    return null;
                }
                if (sys.getDoctorById(docField.getText()) == null) {
                    showToast("Error", "Doctor ID not found in system.", Alert.AlertType.ERROR);
                    return null;
                }
                return new Appointment(idField.getText(), docField.getText(), dateField.getText(), timeField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(app -> {
            // Check for duplicate Appt ID
            boolean exists = sys.getAllAppointments().stream().anyMatch(a -> a.getAppointmentId().equals(app.getAppointmentId()));
            if(exists){
                showToast("Error", "An appointment with this ID already exists.", Alert.AlertType.ERROR);
            } else {
                sys.addAvailableSlot(app.getAppointmentId(), app.getDoctor(), app.getDate(), app.getTimeSlot());
                masterData.add(app);
                updateEmptyState();
                showToast("Success", "New slot created successfully.", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void handleBookAppointment(Appointment appt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Book Appointment");
        dialog.setHeaderText("Booking slot: " + appt.getAppointmentId() + " on " + appt.getDate());
        dialog.setContentText("Enter Patient ID:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String pId = result.get();
            if (sys.bookAppointment(pId, appt.getAppointmentId())) {
                refreshTable();
                showToast("Success", "Appointment booked for patient " + pId, Alert.AlertType.INFORMATION);
            } else {
                showToast("Error", "Failed to book. Please check if Patient ID is correct.", Alert.AlertType.ERROR);
            }
        }
    }

    private void handleCancelBooking(Appointment appt) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Cancel this appointment for Patient " + appt.getPatient() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (sys.cancelAppointment(appt.getAppointmentId())) {
                refreshTable();
                showToast("Success", "Booking cancelled successfully.", Alert.AlertType.INFORMATION);
            }
        }
    }

    private void handleDeleteSlot(Appointment appt) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Slot");
        alert.setHeaderText("Are you sure you want to delete this slot?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            sys.deleteAppointment(appt.getAppointmentId());
            masterData.remove(appt);
            updateEmptyState();
            showToast("Success", "Slot deleted.", Alert.AlertType.INFORMATION);
        }
    }

    private void refreshTable() {
        appointmentTable.refresh();
    }

    private void showToast(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    
    @FXML public void handleTableClick() {}
}