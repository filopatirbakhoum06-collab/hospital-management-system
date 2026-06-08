package com.hospital.service;

import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import java.util.ArrayList;
import java.util.Scanner;

public class MainCLI {
    private static HospitalSystem hospitalSystem;
    private static Scanner scanner;
    public static void main(String[] args) {
        hospitalSystem = HospitalSystem.getInstance();
        scanner = new Scanner(System.in);
        
        // initial data (Skipped seeding here if DB already handles it, but left for legacy compatibility)
        Doctor doc1 = new Doctor("Dr. Mohamed Ahmed", "D001", "0123456789", "Mohamed.Ahmed07@hospital.com", 45, "Cardiology", "LIC12345", 350.0);
        Doctor doc2 = new Doctor("Dr. Sara Ali", "D002", "01587654321", "Sara.Ali23@hospital.com", 38, "Neurology", "LIC67890", 300.0);
        
        Patient pat1 = new Patient("Ayman Hassan", "P001", "01122334455", "Ayman.Hassan0@gmail.com", 30, "MH001", "O+", "Peanuts");
        Patient pat2 = new Patient("Laila Youssef", "P002", "01087654321", "Laila.Youssef@gmail.com", 25, "MH002", "A-", "None");

        hospitalSystem.addDoctor(doc1);
        hospitalSystem.addDoctor(doc2);
        hospitalSystem.addPatient(pat1);
        hospitalSystem.addPatient(pat2);
        hospitalSystem.addAvailableSlot("A001", "D001", "16-12-2025", "10:00 AM - 11:00 AM");
        hospitalSystem.addAvailableSlot("A002", "D002", "16-12-2025", "02:00 PM - 03:00 PM");
        hospitalSystem.addAvailableSlot("A003", "D001", "17-12-2025", "11:00 AM - 12:00 PM");
        

        System.out.println("Welcome to the Hospital Management System!");
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline left by nextInt()
            
            running = handleUserChoice(choice);
        }
        scanner.close();
        System.out.println("\n========================================");
        System.out.println("Thank you for using Hospital Appointment System!");
        System.out.println("========================================");
    }
    private static void displayMenu() {
        System.out.println("\n========================================");
        System.out.println("   Hospital Appointment System");
        System.out.println("========================================");
        System.out.println("1.  Add Doctor");
        System.out.println("2.  Edit Doctor");
        System.out.println("3.  Display All Doctors");
        System.out.println("4.  Add Patient");
        System.out.println("5.  Edit Patient");
        System.out.println("6.  Display All Patients");
        System.out.println("7.  Add Available Appointment Slot");
        System.out.println("8.  Book Appointment");
        System.out.println("9.  Cancel Appointment");
        System.out.println("10. View Booked Appointments by Doctor");
        System.out.println("11. View Available Appointments by Doctor");
        System.out.println("0.  Exit");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");
    }
    // Handle user's menu choice
    private static boolean handleUserChoice(int choice) {
        System.out.println(); // Add blank line for readability
        
        switch (choice) {
            case 1:
                addDoctor();
                break;
            case 2:
                editDoctor();
                break;
            case 3:
                hospitalSystem.displayAllDoctors();
                break;
            case 4:
                addPatient();
                break;
            case 5:
                editPatient();
                break;
            case 6:
                hospitalSystem.displayAllPatients();
                break;
            case 7:
                addAvailableSlot();
                break;
            case 8:
                bookAppointment();
                break;
            case 9:
                cancelAppointment();
                break;
            case 10:
                viewBookedAppointments();
                break;
            case 11:
                viewAvailableAppointments();
                break;
            case 0:
                return false; // Exit program
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        
        return true; // Continue program
    }
    // Add a new doctor
    private static void addDoctor() {
        System.out.println("========== Add New Doctor ==========");
        
        // generate doctor ID
        String id = "D" + String.format("%03d", hospitalSystem.getTotalDoctors() + 1);
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter Age: ");
        int age = getIntInput();
        
        System.out.print("Enter Specialization: ");
        String specialization = scanner.nextLine();
        
        System.out.print("Enter License Number: ");
        String license = scanner.nextLine();
        
        System.out.print("Enter Consultation Fee: ");
        double fee = getDoubleInput();
        
        Doctor doctor = new Doctor(name, id, phone, email, age, 
                                   specialization, license, fee);
        hospitalSystem.addDoctor(doctor);
        System.out.print("Doctor added successfully!");
    }
    // Edit doctor information
    private static void editDoctor() {
        System.out.println("========== Edit Doctor ==========");
        hospitalSystem.displayAllDoctors();
        System.out.print("Enter Doctor ID to edit: ");
        String doctorId = scanner.nextLine();
        
        Doctor doctor = hospitalSystem.getDoctorById(doctorId);
        
        if (doctor == null) {
            System.out.println("✗ Doctor not found with ID: " + doctorId);
            return;
        }
        
        System.out.println("\nCurrent Doctor Information:");
        doctor.displayInfo();
        
        System.out.println("\nEnter new information (press Enter to keep current value):");
        
        System.out.print("Enter New Name [" + doctor.getName() + "]: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            name = doctor.getName();
        }
        
        System.out.print("Enter New Phone [" + doctor.getPhoneNumber() + "]: ");
        String phone = scanner.nextLine();
        if (phone.isEmpty()) {
            phone = doctor.getPhoneNumber();
        }
        
        System.out.print("Enter New Email [" + doctor.getEmail() + "]: ");
        String email = scanner.nextLine();
        if (email.isEmpty()) {
            email = doctor.getEmail();
        }
        
        System.out.print("Enter New Age [" + doctor.getAge() + "]: ");
        String ageStr = scanner.nextLine();
        int age = doctor.getAge();
        if (!ageStr.isEmpty()) {
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid age, keeping current value.");
            }
        }
        
        System.out.print("Enter New Specialization [" + doctor.getSpecialization() + "]: ");
        String spec = scanner.nextLine();
        if (spec.isEmpty()) {
            spec = doctor.getSpecialization();
        }
        
        System.out.print("Enter New License Number [" + doctor.getLicenseNumber() + "]: ");
        String license = scanner.nextLine();
        if (license.isEmpty()) {
            license = doctor.getLicenseNumber();
        }
        
        System.out.print("Enter New Consultation Fee [" + doctor.getConsultationFee() + "]: ");
        String feeStr = scanner.nextLine();
        double fee = doctor.getConsultationFee();
        if (!feeStr.isEmpty()) {
            try {
                fee = Double.parseDouble(feeStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid fee, keeping current value.");
            }
        }
        
        if (hospitalSystem.editDoctor(doctorId, name, phone, email, age, spec, license, fee)) {
            System.out.println("\n✓ Doctor information updated successfully!");
            System.out.println("\nUpdated Information:");
            doctor = hospitalSystem.getDoctorById(doctorId);
            doctor.displayInfo();
        }
    }
    
    // Add a new patient
    private static void addPatient() {
        System.out.println("========== Add New Patient ==========");
        
        // generate patient ID
        String id = "P" + String.format("%03d", hospitalSystem.getTotalPatients() + 1);
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter Age: ");
        int age = getIntInput();
        
        System.out.print("Enter Medical History ID: ");
        String medicalHistoryId = scanner.nextLine();
        
        System.out.print("Enter Blood Type: ");
        String bloodType = scanner.nextLine();
        
        System.out.print("Enter Allergies (or 'None'): ");
        String allergies = scanner.nextLine();
        
        Patient patient = new Patient(name, id, phone, email, age, 
                                     medicalHistoryId, bloodType, allergies);
        hospitalSystem.addPatient(patient);
        
        System.out.println("\n✓ Patient added successfully!");
    }
    
    // Edit patient information
    private static void editPatient() {
        System.out.println("========== Edit Patient ==========");
        
        hospitalSystem.displayAllPatients();
        System.out.print("Enter Patient ID to edit: ");
        String patientId = scanner.nextLine();
        
        Patient patient = hospitalSystem.getPatientById(patientId);
        
        if (patient == null) {
            System.out.println("✗ Patient not found with ID: " + patientId);
            return;
        }
        
        System.out.println("\nCurrent Patient Information:");
        patient.displayInfo();
        
        System.out.println("\nEnter new information (press Enter to keep current value):");
        
        System.out.print("Enter New Name [" + patient.getName() + "]: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            name = patient.getName();
        }
        
        System.out.print("Enter New Phone [" + patient.getPhoneNumber() + "]: ");
        String phone = scanner.nextLine();
        if (phone.isEmpty()) {
            phone = patient.getPhoneNumber();
        }
        
        System.out.print("Enter New Email [" + patient.getEmail() + "]: ");
        String email = scanner.nextLine();
        if (email.isEmpty()) {
            email = patient.getEmail();
        }
        
        System.out.print("Enter New Age [" + patient.getAge() + "]: ");
        String ageStr = scanner.nextLine();
        int age = patient.getAge();
        if (!ageStr.isEmpty()) {
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid age, keeping current value.");
            }
        }
        
        System.out.print("Enter New Medical History ID [" + patient.getMedicalHistoryId() + "]: ");
        String medId = scanner.nextLine();
        if (medId.isEmpty()) {
            medId = patient.getMedicalHistoryId();
        }
        
        System.out.print("Enter New Blood Type [" + patient.getBloodType() + "]: ");
        String bloodType = scanner.nextLine();
        if (bloodType.isEmpty()) {
            bloodType = patient.getBloodType();
        }
        
        System.out.print("Enter New Allergies [" + patient.getAllergies() + "]: ");
        String allergies = scanner.nextLine();
        if (allergies.isEmpty()) {
            allergies = patient.getAllergies();
        }
        
        if (hospitalSystem.editPatient(patientId, name, phone, email, age, medId, bloodType, allergies)) {
            System.out.println("\n✓ Patient information updated successfully!");
            System.out.println("\nUpdated Information:");
            patient = hospitalSystem.getPatientById(patientId);
            patient.displayInfo();
        }
    }
    
    // Add an available appointment slot
    private static void addAvailableSlot() {
        System.out.println("========== Add Available Appointment Slot ==========");
        // generate appointment ID
        String appointmentId = "A" + String.format("%03d", hospitalSystem.getTotalAppointments() + 1);

        // show all doctors
        hospitalSystem.displayAllDoctors();

        System.out.print("Enter Doctor ID: ");
        String doctorId = scanner.nextLine();

        System.out.print("Enter Date (e.g., 2025-12-20): ");
        String date = scanner.nextLine();
        
        System.out.print("Enter Time Slot (e.g., 10:00 AM - 11:00 AM): ");
        String timeSlot = scanner.nextLine();
        
        hospitalSystem.addAvailableSlot(appointmentId, doctorId, date, timeSlot);
        System.out.println("✓ Available slot added successfully!");
    }
    
    // Book an appointment
    private static void bookAppointment() {
        System.out.println("========== Book Appointment ==========");

        hospitalSystem.displayAllDoctors();
        System.out.print("\nEnter Doctor ID: ");
        String doctorId = scanner.nextLine();
        // Show all available appointments
        System.out.println("\n--- Available Appointments ---");
        ArrayList<Appointment> available = hospitalSystem.getAvailableAppointmentsByDoctor(doctorId);
        if (available.isEmpty()) {
            System.out.println("No available appointments.");
            return;
        } else {
            for (Appointment apt : available) {
                apt.displayInfo();
                System.out.println();
            }
        }
        
        System.out.print("\nEnter Appointment ID: ");
        String appointmentId = scanner.nextLine();
        
        hospitalSystem.displayAllPatients();
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();
        
        hospitalSystem.bookAppointment(patientId, appointmentId);
    }
    
    // Cancel an appointment
    private static void cancelAppointment() {
        System.out.println("========== Cancel Appointment ==========");
        
        hospitalSystem.displayAllDoctors();
        System.out.print("\nEnter Doctor ID: ");
        String doctorId = scanner.nextLine();

        // Show all booked appointments
        System.out.println("\n--- Booked Appointments ---");
        ArrayList<Appointment> booked = hospitalSystem.getBookedAppointmentsByDoctor(doctorId);
        if (booked.isEmpty()) {
            System.out.println("No booked appointments.");
            return;
        } else {
            for (Appointment apt : booked) {
                apt.displayInfo();
                System.out.println();
            }
        }

        System.out.print("Enter Appointment ID to cancel: ");
        String appointmentId = scanner.nextLine();
        
        hospitalSystem.cancelAppointment(appointmentId);
    }
    
    // View booked appointments for a specific doctor
    private static void viewBookedAppointments() {
        System.out.println("========== View Booked Appointments ==========");
        
        System.out.print("Enter Doctor ID: ");
        String doctorId = scanner.nextLine();
        
        Doctor doctor = hospitalSystem.getDoctorById(doctorId);
        
        if (doctor == null) {
            System.out.println("✗ Doctor not found with ID: " + doctorId);
            return;
        }
        System.out.println("== Booked Appointments for " + doctor.getName() + "==");
        
        ArrayList<Appointment> booked = hospitalSystem.getBookedAppointmentsByDoctor(doctorId);
        if (booked.isEmpty()) {
            System.out.println("No booked appointments.");
        } 
        else {
            for (Appointment apt : booked) {
                apt.displayInfo();
                System.out.println();
            }
        }
    }
    
    // View available appointments for a specific doctor
    private static void viewAvailableAppointments() {
        System.out.println("========== View Available Appointments ==========");
        
        System.out.print("Enter Doctor ID: ");
        String doctorId = scanner.nextLine();
        
        Doctor doctor = hospitalSystem.getDoctorById(doctorId);
        
        if (doctor == null) {
            System.out.println("✗ Doctor not found with ID: " + doctorId);
            return;
        }
        
        System.out.println("\nDoctor: " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        System.out.println("== Available Appointments for " + doctor.getName() + "==");
        
        ArrayList<Appointment> availableAppointments = 
            hospitalSystem.getAvailableAppointmentsByDoctor(doctorId);
        
        if (availableAppointments.isEmpty()) {
            System.out.println("No available appointments for this doctor.");
        } else {
            for (Appointment apt : availableAppointments) {
                apt.displayInfo();
                System.out.println();
            }
        }
    }
    
    // Helper method to get integer input with error handling
    private static int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
    
    // Helper method to get double input with error handling
    private static double getDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }
}
