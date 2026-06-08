package com.hospital.controller;

import com.hospital.model.Doctor;
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

public class DoctorController {

    @FXML private TextField searchField;
    @FXML private TableView<Doctor> doctorTable;
    @FXML private TableColumn<Doctor, String> colId;
    @FXML private TableColumn<Doctor, String> colName;
    @FXML private TableColumn<Doctor, String> colSpecialization;
    @FXML private TableColumn<Doctor, String> colLicense;
    @FXML private TableColumn<Doctor, String> colPhone;
    @FXML private TableColumn<Doctor, String> colEmail;
    @FXML private TableColumn<Doctor, String> colFee;
    @FXML private VBox emptyState;

    private HospitalSystem sys;
    private ObservableList<Doctor> masterData;

    @FXML
    public void initialize() {
        sys = HospitalSystem.getInstance();
        masterData = FXCollections.observableArrayList(sys.getAllDoctors());

        setupTable();
        setupFilters();
        updateEmptyState();
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colSpecialization.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialization()));
        colLicense.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLicenseNumber()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        
        // Custom formatting for currency
        colFee.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getConsultationFee())));

        doctorTable.setRowFactory(tv -> {
            TableRow<Doctor> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("✎ Edit");
            editItem.setOnAction(event -> handleEditDoctor(row.getItem()));
            MenuItem deleteItem = new MenuItem("🗑 Delete");
            deleteItem.setStyle("-fx-text-fill: red;");
            deleteItem.setOnAction(event -> handleDeleteDoctor(row.getItem()));
            
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
        FilteredList<Doctor> filteredData = new FilteredList<>(masterData, p -> true);

        // Name/License/Specialization Search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(doctor -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return doctor.getName().toLowerCase().contains(lowerCaseFilter) ||
                       doctor.getLicenseNumber().toLowerCase().contains(lowerCaseFilter) ||
                       doctor.getSpecialization().toLowerCase().contains(lowerCaseFilter);
            });
            updateEmptyState(filteredData.isEmpty());
        });

        SortedList<Doctor> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(doctorTable.comparatorProperty());
        doctorTable.setItems(sortedData);
    }

    private void updateEmptyState() {
        updateEmptyState(masterData.isEmpty());
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            doctorTable.setVisible(false);
            doctorTable.setManaged(false);
            emptyState.setVisible(true);
            emptyState.setManaged(true);
        } else {
            doctorTable.setVisible(true);
            doctorTable.setManaged(true);
            emptyState.setVisible(false);
            emptyState.setManaged(false);
        }
    }

    @FXML
    public void handleAddDoctor() {
        showDoctorDialog(null);
    }

    private void handleEditDoctor(Doctor doctor) {
        if (doctor != null) {
            showDoctorDialog(doctor);
        }
    }

    private void handleDeleteDoctor(Doctor doctor) {
        if (doctor == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Doctor");
        alert.setHeaderText("Are you sure you want to delete this doctor?");
        alert.setContentText("Name: " + doctor.getName() + " | License: " + doctor.getLicenseNumber());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = sys.deleteDoctor(doctor.getId());
            if (success) {
                masterData.remove(doctor);
                showToast("Success", "Doctor deleted successfully.", Alert.AlertType.INFORMATION);
                updateEmptyState();
            } else {
                showToast("Error", "Could not delete doctor.", Alert.AlertType.ERROR);
            }
        }
    }

    private void showDoctorDialog(Doctor existingDoctor) {
        Dialog<Doctor> dialog = new Dialog<>();
        boolean isEditMode = (existingDoctor != null);
        dialog.setTitle(isEditMode ? "✎ Edit Doctor" : "+ Register New Doctor");
        dialog.setHeaderText(isEditMode ? "Update details for " + existingDoctor.getName() : "Enter new doctor details.");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField(); 
        idField.setPromptText("e.g. D001");
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField ageField = new TextField();
        TextField specField = new TextField();
        TextField licenseField = new TextField();
        TextField feeField = new TextField();
        feeField.setPromptText("e.g. 150.00");

        if (isEditMode) {
            idField.setText(existingDoctor.getId());
            idField.setDisable(true); // Cannot change Primary Key
            nameField.setText(existingDoctor.getName());
            phoneField.setText(existingDoctor.getPhoneNumber());
            emailField.setText(existingDoctor.getEmail());
            ageField.setText(String.valueOf(existingDoctor.getAge()));
            specField.setText(existingDoctor.getSpecialization());
            licenseField.setText(existingDoctor.getLicenseNumber());
            feeField.setText(String.valueOf(existingDoctor.getConsultationFee()));
        }

        grid.add(new Label("Doctor ID:"), 0, 0); grid.add(idField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Age:"), 0, 2); grid.add(ageField, 1, 2);
        grid.add(new Label("Specialization:"), 0, 3); grid.add(specField, 1, 3);
        grid.add(new Label("License Number:"), 0, 4); grid.add(licenseField, 1, 4);
        grid.add(new Label("Consultation Fee ($):"), 0, 5); grid.add(feeField, 1, 5);
        grid.add(new Label("Phone:"), 0, 6); grid.add(phoneField, 1, 6);
        grid.add(new Label("Email:"), 0, 7); grid.add(emailField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int age = Integer.parseInt(ageField.getText());
                    double fee = Double.parseDouble(feeField.getText());
                    
                    if (idField.getText().isEmpty() || nameField.getText().isEmpty()) {
                        showToast("Validation Error", "ID and Name cannot be empty.", Alert.AlertType.ERROR);
                        return null;
                    }
                    
                    return new Doctor(
                        nameField.getText(), idField.getText(), phoneField.getText(),
                        emailField.getText(), age, specField.getText(),
                        licenseField.getText(), fee
                    );
                } catch (NumberFormatException e) {
                    showToast("Validation Error", "Please verify Age (integer) and Fee (number).", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        Optional<Doctor> result = dialog.showAndWait();
        result.ifPresent(doctor -> {
            if (isEditMode) {
                sys.editDoctor(doctor.getId(), doctor.getName(), doctor.getPhoneNumber(), doctor.getEmail(), doctor.getAge(), doctor.getSpecialization(), doctor.getLicenseNumber(), doctor.getConsultationFee());
                // Update table by replacing the object
                masterData.set(masterData.indexOf(existingDoctor), doctor);
                showToast("Success", "Doctor updated successfully.", Alert.AlertType.INFORMATION);
            } else {
                if (sys.getDoctorById(doctor.getId()) != null) {
                    showToast("Error", "A doctor with this ID already exists.", Alert.AlertType.ERROR);
                } else {
                    sys.addDoctor(doctor);
                    masterData.add(doctor);
                    updateEmptyState();
                    showToast("Success", "New doctor registered.", Alert.AlertType.INFORMATION);
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
    public void handleTableClick() {}
}