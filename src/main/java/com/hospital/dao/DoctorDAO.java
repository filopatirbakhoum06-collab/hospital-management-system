package com.hospital.dao;

import com.hospital.model.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoctorDAO {
    private static final Logger LOGGER = Logger.getLogger(DoctorDAO.class.getName());

    public void save(Doctor doctor) {
        String sql = "INSERT INTO doctors (id, name, phone_number, email, age, specialization, license_number, consultation_fee) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT(id) DO UPDATE SET " +
                     "name=excluded.name, phone_number=excluded.phone_number, email=excluded.email, " +
                     "age=excluded.age, specialization=excluded.specialization, license_number=excluded.license_number, " +
                     "consultation_fee=excluded.consultation_fee, updated_at=CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, doctor.getId());
            pstmt.setString(2, doctor.getName());
            pstmt.setString(3, doctor.getPhoneNumber());
            pstmt.setString(4, doctor.getEmail());
            pstmt.setInt(5, doctor.getAge());
            pstmt.setString(6, doctor.getSpecialization());
            pstmt.setString(7, doctor.getLicenseNumber());
            pstmt.setDouble(8, doctor.getConsultationFee());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to save doctor: " + doctor.getId(), e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete doctor: " + id, e);
        }
    }

    public List<Doctor> loadAll() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Doctor doctor = new Doctor(
                        rs.getString("name"),
                        rs.getString("id"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getInt("age"),
                        rs.getString("specialization"),
                        rs.getString("license_number"),
                        rs.getDouble("consultation_fee")
                );
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load doctors", e);
        }
        return doctors;
    }
}