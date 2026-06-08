package com.hospital.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static final String URL = "jdbc:sqlite:hospital.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "role TEXT NOT NULL, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS doctors (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "phone_number TEXT, " +
                    "email TEXT, " +
                    "age INTEGER, " +
                    "specialization TEXT, " +
                    "license_number TEXT, " +
                    "consultation_fee REAL, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS patients (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "phone_number TEXT, " +
                    "email TEXT, " +
                    "age INTEGER, " +
                    "medical_history_id TEXT, " +
                    "blood_type TEXT, " +
                    "allergies TEXT, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS appointments (" +
                    "appointment_id TEXT PRIMARY KEY, " +
                    "doctor_id TEXT, " +
                    "patient_id TEXT, " +
                    "date TEXT, " +
                    "time_slot TEXT, " +
                    "is_booked BOOLEAN, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("INSERT OR IGNORE INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN')");

            LOGGER.info("Database tables initialized successfully with timestamps.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database initialization failed.", e);
        }
    }
}