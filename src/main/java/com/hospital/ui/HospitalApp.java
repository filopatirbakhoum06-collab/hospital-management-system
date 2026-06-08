package com.hospital.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HospitalApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(root, 1000, 600);
        
        // Try loading CSS, fail silently if not found yet (development mode)
        try {
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        } catch (NullPointerException e) {
            System.out.println("Warning: CSS file not found, running without styles.");
        }
        
        primaryStage.setTitle("Hospital Management System");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}