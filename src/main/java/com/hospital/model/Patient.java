package com.hospital.model;

public class Patient extends Person {
    private String medicalHistoryId;
    private String bloodType;
    private String allergies;
    
    public Patient(String name, String id, String phoneNumber, String email, int age, String medicalHistoryId, String bloodType, String allergies) {
        super(name, id, phoneNumber, email, age);
        this.medicalHistoryId = medicalHistoryId;
        this.bloodType = bloodType;
        this.allergies = allergies;
    }
    
    public String getMedicalHistoryId() {
        return medicalHistoryId;
    }
    
    public void setMedicalHistoryId(String medicalHistoryId) {
        this.medicalHistoryId = medicalHistoryId;
    }
    
    public String getBloodType() {
        return bloodType;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
    
    public String getAllergies() {
        return allergies;
    }
    
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    
    @Override
    public void displayInfo() {
        System.out.println("Patient ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Phone: " + getPhoneNumber());
        System.out.println("Email: " + getEmail());
        System.out.println("Age: " + getAge());
        System.out.println("Medical History ID: " + medicalHistoryId);
        System.out.println("Blood Type: " + bloodType);
        System.out.println("Allergies: " + allergies);
    }
    
    @Override
    public String toString() {
        return "Patient ID: " + getId() + ", Name: " + getName() + ", Blood Type: " + bloodType;
    }
}


