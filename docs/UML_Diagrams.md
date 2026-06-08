# HealthCare Pro - UML Documentation

## 1. Package Architecture

```mermaid
graph TD;
    com.hospital --> com.hospital.model
    com.hospital --> com.hospital.dao
    com.hospital --> com.hospital.service
    com.hospital --> com.hospital.controller
    com.hospital --> com.hospital.ui
```

## 2. Core Class Diagram (OOP & Models)

```mermaid
classDiagram
    class Person {
        <<abstract>>
        -String name
        -String id
        -String phoneNumber
        -String email
        -int age
        +displayInfo()*
    }

    class Doctor {
        -String specialization
        -String licenseNumber
        -double consultationFee
        +displayInfo()
    }

    class Patient {
        -String medicalHistoryId
        -String bloodType
        -String allergies
        +displayInfo()
    }

    Person <|-- Doctor
    Person <|-- Patient

    class Appointment {
        -String appointmentId
        -String doctorId
        -String patientId
        -String date
        -String timeSlot
        -boolean isBooked
        +book(String patientId)
        +cancel()
    }

    class HospitalSystem {
        -ArrayList~Doctor~ doctors
        -ArrayList~Patient~ patients
        -ArrayList~Appointment~ appointments
        +getInstance() HospitalSystem
        +bookAppointment()
        +addAvailableSlot()
    }

    HospitalSystem "1" *-- "*" Doctor : Contains
    HospitalSystem "1" *-- "*" Patient : Contains
    HospitalSystem "1" *-- "*" Appointment : Contains
```

## 3. Database DAO & MVC Integration Diagram

```mermaid
classDiagram
    class PatientController {
        -TableView patientTable
        -TextField searchField
        +handleAddPatient()
    }

    class HospitalSystem {
        <<Singleton>>
        +addPatient(Patient p)
    }

    class PatientDAO {
        +save(Patient p)
        +delete(String id)
        +loadAll() List~Patient~
    }

    PatientController --> HospitalSystem : Invokes Business Logic
    HospitalSystem --> PatientDAO : Requests Persistence
    PatientDAO ..> DatabaseManager : Uses JDBC Connection
```

## 4. System Use Case Diagram

```mermaid
usecaseDiagram
    actor Admin
    
    rectangle "Hospital Management System" {
        Admin --> (Secure Login)
        Admin --> (View Dashboard Statistics)
        Admin --> (Add/Edit/Delete Patients)
        Admin --> (Add/Edit/Delete Doctors)
        Admin --> (Create Available Appt Slots)
        Admin --> (Book Patients to Slots)
        Admin --> (Search & Filter Records)
    }
```