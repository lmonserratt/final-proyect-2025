🎬 Movie Manager DMS — Final Project (Phase 4: MySQL Integration)

Course: CEN 3024C – Software Development I
Student: Luis Augusto Monserratt Alvarado
Professor: Dr. Lisa Macon
Semester: Fall 2025

📖 Project Overview

The Movie Manager DMS is a fully implemented Database Management System developed in Java (OOP) with a Swing-based GUI and a MySQL backend.
This system enables users to manage a collection of movies through complete CRUD functionality, real-time data persistence, and a custom mathematical feature that calculates the average duration of all stored films.

This project represents the final deliverable in a four-phase development cycle, combining:

✔ Object-Oriented Programming

✔ GUI Programming with Java Swing

✔ Database Integration via JDBC

✔ Exception Handling and Validation

✔ Modular Architecture (DAO, Service, Model, GUI)

✔ SDLC Documentation, Planning, and Iterative Refinement

⚙️ Key Features
🎯 Core CRUD

Create – Add a new movie through a user-friendly form

Read – Display the full movies table in the GUI

Update – Edit any movie field

Delete – Remove a selected movie with confirmation

🔍 Search + Custom Action

Search by Title (case-insensitive)

Custom Action: Compute Average Duration of all movies

💾 Persistence

Real-time MySQL storage

JDBC connection with validation

Automatic seeding if database starts empty

🖥 GUI

Clean, modern Swing interface

Table sorting, refreshing, and live updates

Dialogs for validation and errors

🗂 Project Structure

movie-manager-dms-phase4/
│
├── pom.xml
├── sql/
│   ├── schema.sql
│   └── sample_data.sql
│
├── src/main/java/dms/
│   ├── app/GuiMainMysql.java
│   ├── dao/MysqlMovieDao.java
│   ├── gui/MovieTableFrameMysql.java
│   ├── gui/MovieFormDialog.java
│   ├── model/Movie.java
│   └── service/MovieService.java
│
└── target/
    └── movie-manager-dms-1.0.0.jar


🧠 Class Architecture Summary
Layer	Class	Responsibility
app	GuiMainMysql	Launches GUI, handles DB login, sets up system properties
dao	MysqlMovieDao	JDBC operations, CRUD, SQL queries
model	Movie	Entity representing a movie record
service	MovieService	Validation + business logic + DAO orchestration
gui	MovieTableFrameMysql, MovieFormDialog	Interactive Swing interface

💽 Database Setup (MySQL / DataGrip)
1. Create or connect to MySQL server

Use DataGrip, MySQL Workbench, or terminal.

2. Run schema
CREATE DATABASE dms_movies;


Then execute:

sql/schema.sql → creates the movies table

sql/sample_data.sql → loads sample 20+ movies

3. Test connection
USE dms_movies;
SELECT * FROM movies;

🚀 How to Run the Application
▶️ Option 1 — IntelliJ IDEA

Open the project

Run:

dms.app.GuiMainMysql


Enter your MySQL credentials:

Host: localhost

User: your MySQL username

Password: your MySQL password

▶️ Option 2 — Terminal
cd target
java -jar movie-manager-dms-1.0.0.jar

🔢 Custom Feature: Average Duration

The GUI includes a button:

🎬 Calculate Average Duration

Example output:

🎥 Average Duration: 121.4 minutes


The system computes the average from all records currently in the database.

🧰 Technologies Used

Java 17

Swing GUI

MySQL 8+

JDBC

Maven (Shade Plugin)

IntelliJ IDEA

DataGrip

🛡 Validation & Error Handling

Prevents empty or invalid fields

Guards against SQL injection via PreparedStatements

Prevents user from crashing the program

Handles DB connection errors gracefully

Confirms destructive actions (deletes)

Ensures proper connection lifecycle (connect / close)

🎥 Final Video Presentation (YouTube)

👉 [Click Here to Watch the Full Demonstration](https://youtu.be/aDpJzg3VKL8)

(Includes GUI demo, MySQL interaction, SDLC explanation, and reflection.)

📚 Acknowledgments

A special thank you to Professor Ashley Evans and Valencia College for guiding each project phase:

Phase 1: CLI File Parsing

Phase 2: Unit Testing & Refactoring

Phase 3: GUI Development

Phase 4: MySQL Integration

👨‍💻 About the Author

Luis Augusto Monserratt Alvarado
📍 Orlando, FL
📧 lmonserrattalvara@mail.valenciacollege.edu

🔗 GitHub: https://github.com/lmonserratt

🌐 GitHub Repository (Final Project)

👉 https://github.com/lmonserratt/final-proyect-2025
