package com.hospital.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Basic verification against SQLite seed data (admin/admin123)
        // In a real scenario, this queries the `users` table via UserDAO.
        if ("admin".equals(username) && "admin123".equals(password)) {
            errorLabel.setVisible(false);
            loadMainLayout();
        } else {
            errorLabel.setVisible(true);
            // Quick CSS shake animation could be added here for polish
        }
    }

    private void loadMainLayout() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            // Load the Main Layout Shell
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainLayout.fxml"));
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
            
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Failed to load MainLayout.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}