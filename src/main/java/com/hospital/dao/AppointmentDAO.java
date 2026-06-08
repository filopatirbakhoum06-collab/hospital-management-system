package com.hospital.dao;

import com.hospital.model.Appointment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentDAO {
    private static final Logger LOGGER = Logger.getLogger(AppointmentDAO.class.getName());

    public void save(Appointment appointment) {
        String sql = "INSERT INTO appointments (appointment_id, doctor_id, patient_id, date, time_slot, is_booked) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT(appointment_id) DO UPDATE SET " +
                     "patient_id=excluded.patient_id, is_booked=excluded.is_booked, updated_at=CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, appointment.getAppointmentId());
            pstmt.setString(2, appointment.getDoctor());
            pstmt.setString(3, appointment.getPatient());
            pstmt.setString(4, appointment.getDate());
            pstmt.setString(5, appointment.getTimeSlot());
            pstmt.setBoolean(6, appointment.isBooked());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to save appointment: " + appointment.getAppointmentId(), e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete appointment: " + id, e);
        }
    }

    public List<Appointment> loadAll() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Appointment appointment = new Appointment(
                        rs.getString("appointment_id"),
                        rs.getString("doctor_id"),
                        rs.getString("patient_id"),
                        rs.getString("date"),
                        rs.getString("time_slot"),
                        rs.getBoolean("is_booked")
                );
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load appointments", e);
        }
        return appointments;
    }
}