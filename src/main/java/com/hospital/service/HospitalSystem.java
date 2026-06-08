package com.hospital.service;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.DatabaseManager;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.*;

import java.util.ArrayList;

public class HospitalSystem {
    private static HospitalSystem instance;

    private final ArrayList<Doctor> doctors;
    private final ArrayList<Patient> patients;
    private final ArrayList<Appointment> appointments;

    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;
    private final AppointmentDAO appointmentDAO;

    public HospitalSystem() {
        DatabaseManager.initializeDatabase(); // Ensure DB is ready
        doctorDAO = new DoctorDAO();
        patientDAO = new PatientDAO();
        appointmentDAO = new AppointmentDAO();

        // Load existing data from DB
        doctors = new ArrayList<>(doctorDAO.loadAll());
        patients = new ArrayList<>(patientDAO.loadAll());
        appointments = new ArrayList<>(appointmentDAO.loadAll());
    }

    public static HospitalSystem getInstance() {
        if (instance == null) {
            instance = new HospitalSystem();
        }
        return instance;
    }

    // --- Added getters for FX Controllers ---
    public ArrayList<Doctor> getAllDoctors() { return doctors; }
    public ArrayList<Patient> getAllPatients() { return patients; }
    public ArrayList<Appointment> getAllAppointments() { return appointments; }
    // ----------------------------------------

    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        doctorDAO.save(doctor);
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
        patientDAO.save(patient);
    }

    public void addAvailableSlot(String appointmentId, String doctorId, String date, String timeSlot) {
        Appointment appointment = new Appointment(appointmentId, doctorId, date, timeSlot);
        appointments.add(appointment);
        appointmentDAO.save(appointment);
    }

    public boolean deletePatient(String patientId) {
        Patient patient = getPatientById(patientId);
        if (patient != null) {
            patients.remove(patient);
            patientDAO.delete(patientId);
            return true;
        }
        return false;
    }

    public boolean deleteDoctor(String doctorId) {
        Doctor doctor = getDoctorById(doctorId);
        if (doctor != null) {
            doctors.remove(doctor);
            doctorDAO.delete(doctorId);
            return true;
        }
        return false;
    }

    public boolean deleteAppointment(String appointmentId) {
        Appointment appointment = findAppointmentById(appointmentId);
        if (appointment != null) {
            appointments.remove(appointment);
            appointmentDAO.delete(appointmentId);
            return true;
        }
        return false;
    }

    public Doctor getDoctorById(String doctorId) { 
        for (Doctor doctor : doctors) {
            if (doctor.getId().equals(doctorId)) {
                return doctor;
            }
        }
        return null;
    }

    public Patient getPatientById(String patientId) {
        for (Patient patient : patients) {
            if (patient.getId().equals(patientId)) {
                return patient;
            }
        }
        return null;
    }

    private Appointment findAppointmentById(String appointmentId) {
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                return appointment;
            }
        }
        return null;
    }

    public boolean bookAppointment(String patientId, String appointmentId) {
        Patient patient = getPatientById(patientId);
        Appointment appointment = findAppointmentById(appointmentId);
        
        if (patient == null) {
            System.out.println("Patient not found with ID: " + patientId);
            return false;
        }
        
        if (appointment == null) {
            System.out.println("Appointment not found with ID: " + appointmentId);
            return false;
        }
        
        if (appointment.book(patientId)) {
            System.out.println("Appointment booked successfully for " + patient.getName());
            appointmentDAO.save(appointment); // Update DB state
            return true;
        }
        
        return false;
    }

    public boolean cancelAppointment(String appointmentId) {
        Appointment appointment = findAppointmentById(appointmentId);
        
        if (appointment == null) {
            System.out.println("Appointment not found with ID: " + appointmentId);
            return false;
        }
        
        if (appointment.cancel()) {
            System.out.println("Appointment cancelled successfully!");
            appointmentDAO.save(appointment); // Update DB state
            return true;
        }
        
        return false;
    }
    
    public boolean editDoctor(String doctorId, String name, String phoneNumber, String email, int age, String specialization, String licenseNumber, double consultationFee) {
        Doctor doctor = getDoctorById(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found with ID: " + doctorId);
            return false;
        }
        doctor.setName(name);
        doctor.setPhoneNumber(phoneNumber);
        doctor.setEmail(email);
        doctor.setAge(age);
        doctor.setSpecialization(specialization);
        doctor.setLicenseNumber(licenseNumber);
        doctor.setConsultationFee(consultationFee);
        
        doctorDAO.save(doctor); // Update DB state
        
        System.out.println("Doctor updated successfully");
        return true;
    }
    
    public boolean editPatient(String patientId, String name, String phoneNumber, String email, int age, String medicalHistoryId, String bloodType, String allergies) {
        Patient patient = getPatientById(patientId);
        if (patient == null) {
            System.out.println("Patient not found with ID: " + patientId);
            return false;
        }
        patient.setName(name);
        patient.setPhoneNumber(phoneNumber);
        patient.setEmail(email);
        patient.setAge(age);
        patient.setMedicalHistoryId(medicalHistoryId);
        patient.setBloodType(bloodType);
        patient.setAllergies(allergies);
        
        patientDAO.save(patient); // Update DB state
        
        System.out.println("Patient updated successfully");
        return true;
    }
    
    public ArrayList<Appointment> getBookedAppointmentsByDoctor(String doctorId) {
        ArrayList<Appointment> booked = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDoctor().equals(doctorId) && appointment.isBooked()) {
                booked.add(appointment);
            }
        }
        return booked;
    }

    public ArrayList<Appointment> getAvailableAppointmentsByDoctor(String doctorId) {
        ArrayList<Appointment> available = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDoctor().equals(doctorId) && !appointment.isBooked()) {
                available.add(appointment);
            }
        }
        return available;
    }
    
    
    public void displayAllDoctors() {
        if (doctors.isEmpty()) {
            System.out.println("No doctors available.");
            return;
        }
        System.out.println("All Doctors : ");
        for (Doctor doctor : doctors) {
            System.out.println(doctor);
        }
    }
    
    public void displayAllPatients() {
        if (patients.isEmpty()) {
            System.out.println("No patients registered.");
            return;
        }
        System.out.println("All Patients : ");
        for (Patient patient : patients) {
            System.out.println(patient);
        }
    }
    
    public void displayAllAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("No appointments available.");
            return;
        }
        System.out.println(" All Appointments: ");
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }
    }
    public int getTotalAppointments() {
        return appointments.size();
    }
    public int getTotalDoctors() {
        return doctors.size();
    }
    public int getTotalPatients() {
        return patients.size();
    }
}




