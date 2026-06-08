package com.hospital.model;

public class Appointment {

    private final String appointmentId;
    private final String doctorId;
    private String patientId;
    private final String date;
    private final String timeSlot;
    private boolean isBooked;

    public Appointment(String appointmentId, String doctorId, String patientId, String date, String timeSlot, boolean isBooked)
     {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.isBooked = isBooked;
    }
    
    
    public Appointment(String appointmentId, String doctorId, String date, String timeSlot)
    {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.patientId = null;
        this.isBooked = false;
       }

   

    public String getAppointmentId() {
        return appointmentId;
    }

    public boolean book(String patientId) {
        if (isBooked) {
            System.out.println("Error: Appointment is already booked");
            return false;
        }
        
        if (patientId == null ) {
            System.out.println("Error: Patient ID cannot be empty");
            return false;
        }
        
        this.patientId = patientId;
        this.isBooked = true;
        return true;
    }

    public boolean cancel() {
        if (!isBooked) {
            System.out.println("Error: Appointment is not booked");
            return false;
        }
        
        this.isBooked = false;
        this.patientId = null;
        return true;
    }

    public boolean isBooked() {
       return isBooked;
    }

    public String getDoctor() {
         return doctorId;
    }

    public String getPatient() {
      return patientId;
    }

    public String getDate()
    {
        return date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void displayInfo() {
        
        System.out.println("Appointment ID: " + appointmentId);
        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Patient ID: " + (patientId != null ? patientId : "None"));
        System.out.println("Date: " + date);
        System.out.println("Time Slot: " + timeSlot);
        System.out.println("Status: " + (isBooked ? "Booked" : "Available"));
       
    }

    @Override
   
    public String toString() {
        return "Appointment ID: " + appointmentId + 
               ", Doctor: " + doctorId + 
               ", Patient: " + patientId + 
               ", Date: " + date + 
               ", Time: " + timeSlot;
    }
}