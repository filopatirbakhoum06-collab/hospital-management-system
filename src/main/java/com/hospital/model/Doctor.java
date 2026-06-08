package com.hospital.model;

public class Doctor extends Person {
    private String specialization;
    private String licenseNumber;
    private double consultationFee;
    
    public Doctor(String name, String id, String phoneNumber, String email, int age, String specialization, String licenseNumber, double consultationFee) {
        super(name, id, phoneNumber, email, age);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.consultationFee = consultationFee;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public double getConsultationFee() {
        return consultationFee;
    }
    
    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }
    
    @Override
    public void displayInfo() {
        System.out.println("Doctor ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Phone: " + getPhoneNumber());
        System.out.println("Email: " + getEmail());
        System.out.println("Age: " + getAge());
        System.out.println("Specialization: " + specialization);
        System.out.println("License Number: " + licenseNumber);
        System.out.println("Consultation Fee: " + consultationFee);
    }
    
    @Override
    public String toString() {
        return "Doctor ID: " + getId() + ", Name: " + getName() + ", Specialization: " + specialization;
    }
}