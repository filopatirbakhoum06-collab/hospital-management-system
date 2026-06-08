package com.hospital.controller;

import com.hospital.model.Appointment;
import com.hospital.model.Patient;
import com.hospital.service.HospitalSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class DashboardController {

    @FXML private Label lblTotalPatients;
    @FXML private Label lblTotalDoctors;
    @FXML private Label lblTotalAppts;
    @FXML private Label lblAvailableSlots;
    @FXML private PieChart appointmentChart;
    @FXML private ListView<String> activityList;
    @FXML private VBox emptyActivityState;

    private HospitalSystem sys;

    @FXML
    public void initialize() {
        sys = HospitalSystem.getInstance();
        loadDashboardData();
    }

    private void loadDashboardData() {
        // 1. Fetch Real Data from OOP backend
        int tPatients = sys.getTotalPatients();
        int tDoctors = sys.getTotalDoctors();
        ArrayList<Appointment> allAppts = sys.getAllAppointments();
        
        int tAppts = allAppts.size();
        int availableAppts = 0;
        int bookedAppts = 0;
        
        for (Appointment a : allAppts) {
            if (a.isBooked()) bookedAppts++;
            else availableAppts++;
        }

        // 2. Bind to Summary Cards
        lblTotalPatients.setText(String.valueOf(tPatients));
        lblTotalDoctors.setText(String.valueOf(tDoctors));
        lblTotalAppts.setText(String.valueOf(tAppts));
        lblAvailableSlots.setText(String.valueOf(availableAppts));

        // 3. Populate Chart
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        if (tAppts == 0) {
            // Chart Empty State
            chartData.add(new PieChart.Data("No Appointments", 1));
        } else {
            chartData.add(new PieChart.Data("Booked (" + bookedAppts + ")", bookedAppts));
            chartData.add(new PieChart.Data("Available (" + availableAppts + ")", availableAppts));
        }
        appointmentChart.setData(chartData);

        // 4. Generate "Recent Activity" (A mockup of audit logs based on actual DB presence). 
        // In a true scalable app, we'd have an AnalyticsDAO. Here we'll generate logs dynamically from recent entries.
        ObservableList<String> activities = FXCollections.observableArrayList();
        
        if (!sys.getAllPatients().isEmpty()) {
            Patient lastP = sys.getAllPatients().get(sys.getAllPatients().size() - 1);
            activities.add("✨ New Patient joined: " + lastP.getName());
        }
        if (bookedAppts > 0) {
            activities.add("📅 " + bookedAppts + " patient(s) have booked appointments.");
        }
        if (sys.getTotalDoctors() > 0) {
            activities.add("🩺 System initialized with " + sys.getTotalDoctors() + " doctors.");
        }

        if (activities.isEmpty()) {
            emptyActivityState.setVisible(true);
            emptyActivityState.setManaged(true);
            activityList.setVisible(false);
            activityList.setManaged(false);
        } else {
            emptyActivityState.setVisible(false);
            emptyActivityState.setManaged(false);
            activityList.setVisible(true);
            activityList.setManaged(true);
            activityList.setItems(activities);
            
            // Custom cell style for activity list
            activityList.setCellFactory(list -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        setText(item);
                        setStyle("-fx-font-size: 14px; -fx-padding: 10px 0; -fx-text-fill: #334155; -fx-background-color: transparent; -fx-border-color: transparent transparent #F1F5F9 transparent; -fx-border-width: 1px;");
                    }
                }
            });
        }
    }
}