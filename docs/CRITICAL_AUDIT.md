# Critical Engineering Audit (Senior Engineer Review)

*This document outlines a professional critique of the current iteration of the Hospital Management System, highlighting areas where the project excels for a Junior/Mid-level portfolio piece, but also exposing the limitations and "tech debt" that a Senior Engineer would identify during a Code Review.*

## 1. Project Strengths (What you did right)
*   **Pristine OOP Foundation:** The use of `abstract class Person` into concrete `Patient/Doctor` is textbook.
*   **Decoupled Architecture:** Using the DAO pattern isolates the JDBC logic perfectly. `HospitalSystem` is blissfully unaware that SQLite even exists.
*   **Defense in Depth:** Mandatory use of `PreparedStatement` completely neutralizes SQL injection risks.
*   **Excellent UX/UI:** The FXML layouts mock real web-based SPAs (Single Page Applications) by dynamically swapping a central `StackPane` rather than opening multiple OS windows.

## 2. Weak Points & Architectural Debt
*   **Synchronous UI Thread Blocking:** 
    *   *The Issue:* In `PatientController`, calling `patientDAO.save()` executes synchronously on the JavaFX Application Thread.
    *   *The Consequence:* If the database takes 2 seconds to write, the entire UI freezes and becomes unresponsive for 2 seconds.
    *   *The Fix:* Database calls should be wrapped in `javafx.concurrent.Task<T>` and executed via an `ExecutorService` (background threads).
*   **Memory vs. Database Drift:**
    *   *The Issue:* `HospitalSystem` caches `ArrayLists` in RAM on start-up.
    *   *The Consequence:* In a multi-user environment (e.g., two receptionists running the app on different PCs against a shared DB), caching causes extreme data drift. Admin A won't see Admin B's new patient until restarting the app.
    *   *The Fix:* The App should query the DB for the TableView directly, rather than relying on an in-memory cache, or use a streaming data platform/web-sockets.
*   **Security Vulnerability (Plaintext Passwords):**
    *   *The Issue:* `DatabaseManager` seeds `'admin123'`. 
    *   *The Fix:* Never store plaintext passwords. The system must hash passwords using `BCrypt` or `Argon2` before inserting them into SQLite.

## 3. Missing Infrastructure (To look ultra-professional)
*   **Dependency Injection (DI):** Creating `new PatientDAO()` is tight coupling. A Senior Engineer would expect an interface `IPatientDAO` injected into the Service constructor, completely mocking the DB out during Unit Testing.
*   **Testing:** There are no JUnit tests. Adding a `/src/test/java` directory asserting that `Appointment.book()` properly rejects duplicate bookings without booting up the UI is a hallmark of a professional.
*   **Database Migration Tooling:** Using `stmt.execute("CREATE TABLE...")` in a Java string is considered legacy. Professionals use **Flyway** or **Liquibase** `.sql` scripts to formally version and track database schema tweaks over time.

## 4. Final Verdict
For a developer seeking an internship or junior role, this application is **top 1%**. It exhibits an understanding of the full stack (UI -> OOP Service -> Database) that most candidates lack. Acknowledging the "Weak Points" during an interview explicitly proves you have the foresight of a Senior Engineer.