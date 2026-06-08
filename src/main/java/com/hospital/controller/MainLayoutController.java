package com.hospital.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainLayoutController {

    @FXML private VBox sidebar;
    @FXML private Label brandLabel;
    @FXML private Label pageTitle;
    @FXML private StackPane contentArea;

    private boolean isSidebarExpanded = true;

    @FXML
    public void initialize() {
        // Load the Dashboard by default when Main Layout opens
        showDashboard();
    }

    @FXML
    public void showDashboard() {
        pageTitle.setText("Dashboard Overview");
        loadView("Dashboard");
    }

    @FXML
    public void showPatients() {
        pageTitle.setText("Patients Management");
        loadView("Patients");
    }

    @FXML
    public void showDoctors() {
        pageTitle.setText("Doctors Directory");
        loadView("Doctors");
    }

    @FXML
    public void showAppointments() {
        pageTitle.setText("Appointment Schedule");
        loadView("Appointments");
    }

    private void loadView(String fxmlName) {
        try {
            // Note: We haven't created these nested FXML files yet, so this will throw an exception
            // if triggered right now. We will build Dashboard.fxml next.
            Parent view = FXMLLoader.load(getClass().getResource("/fxml/" + fxmlName + ".fxml"));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Could not load " + fxmlName + ".fxml");
            // Show an empty/construction state temporarily
            Label placeholder = new Label(fxmlName + " Module Under Construction");
            placeholder.setStyle("-fx-font-size: 24px; -fx-text-fill: #94A3B8;");
            contentArea.getChildren().setAll(placeholder);
        }
    }

    @FXML
    public void toggleSidebar() {
        if (isSidebarExpanded) {
            sidebar.setPrefWidth(70);
            brandLabel.setVisible(false);
            // In a full implementation, we would toggle Button texts off, leaving only icons
        } else {
            sidebar.setPrefWidth(250);
            brandLabel.setVisible(true);
        }
        isSidebarExpanded = !isSidebarExpanded;
    }

    @FXML
    public void handleLogout() {
        try {
            Stage stage = (Stage) sidebar.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root, 1000, 600);
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}