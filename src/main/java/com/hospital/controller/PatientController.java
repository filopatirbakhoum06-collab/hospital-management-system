package com.hospital.controller;

import com.hospital.model.Patient;
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

public class PatientController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> bloodTypeFilter;
    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, String> colId;
    @FXML private TableColumn<Patient, String> colName;
    @FXML private TableColumn<Patient, String> colAge;
    @FXML private TableColumn<Patient, String> colBlood;
    @FXML private TableColumn<Patient, String> colPhone;
    @FXML private TableColumn<Patient, String> colHistory;
    @FXML private TableColumn<Patient, String> colAllergies;
    @FXML private VBox emptyState;

    private HospitalSystem sys;
    private ObservableList<Patient> masterData;

    @FXML
    public void initialize() {
        sys = HospitalSystem.getInstance();
        masterData = FXCollections.observableArrayList(sys.getAllPatients());

        setupTable();
        setupFilters();
        updateEmptyState();
    }

    private void setupTable() {
        // Fix for specific bindings since your model doesn't use "StringProperty" natively natively natively
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colAge.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAge())));
        colBlood.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBloodType()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));
        colHistory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedicalHistoryId()));
        colAllergies.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAllergies()));

        // We can add ContextMenu to row for Edit/Delete
        patientTable.setRowFactory(tv -> {
            TableRow<Patient> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("✎ Edit");
            editItem.setOnAction(event -> handleEditPatient(row.getItem()));
            MenuItem deleteItem = new MenuItem("🗑 Delete");
            deleteItem.setStyle("-fx-text-fill: red;");
            deleteItem.setOnAction(event -> handleDeletePatient(row.getItem()));
            
            contextMenu.getItems().addAll(editItem, deleteItem);
            row.contextMenuProperty().bind(
                javafx.beans.binding.Bindings.when(row.emptyProperty())
                .then((ContextMenu)null)
                .otherwise(contextMenu)
            );
            return row;
        });
    }

    private void setupFilters() {
        bloodTypeFilter.setItems(FXCollections.observableArrayList("All Types", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"));
        bloodTypeFilter.getSelectionModel().selectFirst();

        FilteredList<Patient> filteredData = new FilteredList<>(masterData, p -> true);

        // Name/Phone/ID Search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(patient -> matchesFilters(patient, newValue, bloodTypeFilter.getValue()));
            updateEmptyState(filteredData.isEmpty());
        });

        // Blood Type Dropdown
        bloodTypeFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(patient -> matchesFilters(patient, searchField.getText(), newValue));
            updateEmptyState(filteredData.isEmpty());
        });

        SortedList<Patient> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(patientTable.comparatorProperty());
        patientTable.setItems(sortedData);
    }

    private boolean matchesFilters(Patient patient, String searchText, String bloodType) {
        boolean matchSearch = true;
        if (searchText != null && !searchText.trim().isEmpty()) {
            String lowerCaseFilter = searchText.toLowerCase();
            matchSearch = patient.getName().toLowerCase().contains(lowerCaseFilter) ||
                          patient.getId().toLowerCase().contains(lowerCaseFilter) ||
                          patient.getPhoneNumber().contains(lowerCaseFilter);
        }

        boolean matchBlood = true;
        if (bloodType != null && !bloodType.equals("All Types")) {
            matchBlood = patient.getBloodType().equalsIgnoreCase(bloodType);
        }

        return matchSearch && matchBlood;
    }

    private void updateEmptyState() {
        updateEmptyState(masterData.isEmpty());
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            patientTable.setVisible(false);
            patientTable.setManaged(false);
            emptyState.setVisible(true);
            emptyState.setManaged(true);
        } else {
            patientTable.setVisible(true);
            patientTable.setManaged(true);
            emptyState.setVisible(false);
            emptyState.setManaged(false);
        }
    }

    @FXML
    public void handleAddPatient() {
        showPatientDialog(null);
    }

    private void handleEditPatient(Patient patient) {
        if (patient != null) {
            showPatientDialog(patient);
        }
    }

    private void handleDeletePatient(Patient patient) {
        if (patient == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Patient");
        alert.setHeaderText("Are you sure you want to delete this patient?");
        alert.setContentText("Name: " + patient.getName() + " | ID: " + patient.getId());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = sys.deletePatient(patient.getId());
            if (success) {
                masterData.remove(patient); // Update UI
                showToast("Success", "Patient deleted successfully.", Alert.AlertType.INFORMATION);
                updateEmptyState();
            } else {
                showToast("Error", "Could not delete patient.", Alert.AlertType.ERROR);
            }
        }
    }

    private void showPatientDialog(Patient existingPatient) {
        Dialog<Patient> dialog = new Dialog<>();
        boolean isEditMode = (existingPatient != null);
        dialog.setTitle(isEditMode ? "✎ Edit Patient" : "+ Add New Patient");
        dialog.setHeaderText(isEditMode ? "Update details for " + existingPatient.getName() : "Enter new patient details.");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField(); 
        idField.setPromptText("e.g. P123");
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField ageField = new TextField();
        TextField histField = new TextField();
        ComboBox<String> bloodCombo = new ComboBox<>(FXCollections.observableArrayList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        TextField allergiesField = new TextField();

        if (isEditMode) {
            idField.setText(existingPatient.getId());
            idField.setDisable(true); // Cannot change Primary Key
            nameField.setText(existingPatient.getName());
            phoneField.setText(existingPatient.getPhoneNumber());
            emailField.setText(existingPatient.getEmail());
            ageField.setText(String.valueOf(existingPatient.getAge()));
            histField.setText(existingPatient.getMedicalHistoryId());
            bloodCombo.setValue(existingPatient.getBloodType());
            allergiesField.setText(existingPatient.getAllergies());
        }

        grid.add(new Label("Patient ID:"), 0, 0); grid.add(idField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Age:"), 0, 2); grid.add(ageField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3); grid.add(phoneField, 1, 3);
        grid.add(new Label("Email:"), 0, 4); grid.add(emailField, 1, 4);
        grid.add(new Label("History ID:"), 0, 5); grid.add(histField, 1, 5);
        grid.add(new Label("Blood Type:"), 0, 6); grid.add(bloodCombo, 1, 6);
        grid.add(new Label("Allergies:"), 0, 7); grid.add(allergiesField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int age = Integer.parseInt(ageField.getText());
                    // Very basic validation check
                    if (idField.getText().isEmpty() || nameField.getText().isEmpty()) {
                        showToast("Validation Error", "ID and Name cannot be empty.", Alert.AlertType.ERROR);
                        return null;
                    }
                    
                    return new Patient(
                        nameField.getText(), idField.getText(), phoneField.getText(),
                        emailField.getText(), age, histField.getText(),
                        bloodCombo.getValue() != null ? bloodCombo.getValue() : "Unknown",
                        allergiesField.getText()
                    );
                } catch (NumberFormatException e) {
                    showToast("Validation Error", "Age must be a valid integer number.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        Optional<Patient> result = dialog.showAndWait();
        result.ifPresent(patient -> {
            if (isEditMode) {
                sys.editPatient(patient.getId(), patient.getName(), patient.getPhoneNumber(), patient.getEmail(), patient.getAge(), patient.getMedicalHistoryId(), patient.getBloodType(), patient.getAllergies());
                // Update table by replacing the object
                masterData.set(masterData.indexOf(existingPatient), patient);
                showToast("Success", "Patient updated successfully.", Alert.AlertType.INFORMATION);
            } else {
                // Check if ID already exists
                if (sys.getPatientById(patient.getId()) != null) {
                    showToast("Error", "A patient with this ID already exists.", Alert.AlertType.ERROR);
                } else {
                    sys.addPatient(patient);
                    masterData.add(patient);
                    updateEmptyState();
                    showToast("Success", "New patient registered.", Alert.AlertType.INFORMATION);
                }
            }
        });
    }

    private void showToast(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    public void handleTableClick() {
        // Logic for single clicks on row if needed
    }
}