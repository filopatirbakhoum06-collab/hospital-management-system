package com.hospital.dao;

import com.hospital.model.Patient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatientDAO {
    private static final Logger LOGGER = Logger.getLogger(PatientDAO.class.getName());

    public void save(Patient patient) {
        String sql = "INSERT INTO patients (id, name, phone_number, email, age, medical_history_id, blood_type, allergies) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT(id) DO UPDATE SET " +
                     "name=excluded.name, phone_number=excluded.phone_number, email=excluded.email, " +
                     "age=excluded.age, medical_history_id=excluded.medical_history_id, blood_type=excluded.blood_type, " +
                     "allergies=excluded.allergies, updated_at=CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getId());
            pstmt.setString(2, patient.getName());
            pstmt.setString(3, patient.getPhoneNumber());
            pstmt.setString(4, patient.getEmail());
            pstmt.setInt(5, patient.getAge());
            pstmt.setString(6, patient.getMedicalHistoryId());
            pstmt.setString(7, patient.getBloodType());
            pstmt.setString(8, patient.getAllergies());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to save patient: " + patient.getId(), e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete patient: " + id, e);
        }
    }

    public List<Patient> loadAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getString("name"),
                        rs.getString("id"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getInt("age"),
                        rs.getString("medical_history_id"),
                        rs.getString("blood_type"),
                        rs.getString("allergies")
                );
                patients.add(patient);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load patients", e);
        }
        return patients;
    }
}