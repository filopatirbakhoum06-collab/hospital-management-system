# Interview Preparation & Common Questions

When presenting this project in a Software Engineering interview, expect questions targeting your architectural decisions, OOP understanding, and Java fundamentals.

### Q1: Why did you choose the MVC Architecture for this project?
**Strong Answer:** "Initially, the project was a console application where UI strings and logic were mixed. I chose MVC (Model-View-Controller) because it strictly separates concerns. The Models handle only data definitions (`Doctor`, `Patient`), the Views (`.fxml`) handle only UI rendering, and the Controllers bridge the gap. This makes the code highly maintainable; for example, if I wanted to swap JavaFX for a Spring Boot REST API in the future, my Models and Database DAOs would remain 100% untouched."

### Q2: How did you implement Object-Oriented Principles in this project?
**Strong Answer:** 
*   **Abstraction:** I created an abstract `Person` class that handles shared identity logic (ID, Name, Phone). 
*   **Inheritance:** `Doctor` and `Patient` extend `Person`, gaining standard traits but adding specific ones (Specializations vs. Blood Types). 
*   **Encapsulation:** All fields in the Models and UI Controllers are `private`. Data is exclusively accessed via strict getters/setters, preventing untracked state mutations.
*   **Polymorphism:** Method overriding is used (e.g., `displayInfo()`) so that treating a `Doctor` as a `Person` still yields proper contextual output.

### Q3: Why is `HospitalSystem` a Singleton?
**Strong Answer:** "Because JavaFX instantiates controllers dynamically via `FXMLLoader`, each controller needs access to a unified, centralized 'truth' for the data. If I used `new HospitalSystem()` in every controller, they would each have their own isolated local memory lists. A Singleton (`getInstance()`) guarantees that when the `PatientController` registers a patient, the `DashboardController` sees that exact same patient update in memory instantly."

### Q4: How are you connecting to the database and preventing SQL injection?
**Strong Answer:** "I used SQLite via standard Java JDBC. I intentionally abstracted this into the Data Access Object (DAO) pattern (`PatientDAO`, `DoctorDAO`) so my Service classes don't write raw SQL. To prevent SQL Injection, I strictly avoided string concatenation and exclusively used `PreparedStatement`, which pre-compiles the SQL schema and treats user input strictly as literal values."

### Q5: What would you improve in this project if it were scaling to 100,000 patients?
**Strong Answer:** "Right now, the SQLite database loads all records into Java `ArrayLists` at startup. For 100,000 records, this would consume massive RAM and slow down boot times. I would change the DAOs to use Pagination (`LIMIT` and `OFFSET` in SQL). Additionally, I would implement `javafx.concurrent.Task` or completely switch to a headless Spring Boot backend to move synchronous database blocking off the main JavaFX Application UI Thread."