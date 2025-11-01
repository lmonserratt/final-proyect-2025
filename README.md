# 🎬 Movie Manager DMS (Phase 4 – MySQL Integration)

**Course:** CEN 3024C – Software Development I  
**Author:** Luis Augusto Monserratt Alvarado  
**Professor:** Ashley Evans  
**Semester:** Fall 2025  

---

## 🧩 Project Overview

The **Movie Manager DMS** is a **Database Management System** built in **Java (OOP)** with **Swing GUI** and **MySQL backend**.  
It allows users to perform **CRUD operations** on a movie database, view data in a graphical interface, and run a **custom action** that calculates the **average duration of all movies** stored.

This project represents the **final phase (Phase 4)** of the multi-stage DMS implementation, integrating all previous concepts:
- Object-Oriented Design (Encapsulation, Polymorphism, Inheritance)
- GUI with Swing Components
- Database Connectivity (JDBC with MySQL)
- Error Handling & Validation
- Data Persistence and CRUD Testing

---

## ⚙️ Features

✅ Add, Update, Delete, and View movies  
✅ Custom Action — calculate the **average movie duration**  
✅ Real-time database connection via **MySQL JDBC**  
✅ GUI interface built with **Java Swing**  
✅ Strong input validation and exception handling  
✅ Dynamic connection prompt (host, user, password)  
✅ Modular architecture (DAO + Service + GUI + Model layers)  

---

## 🗂️ Project Structure

movie-manager-dms-phase4/
├── pom.xml
├── sql/
│ ├── schema.sql
│ └── sample_data.sql
├── src/
│ ├── main/java/dms/
│ │ ├── app/GuiMainMysql.java
│ │ ├── dao/MysqlMovieDao.java
│ │ ├── gui/MovieTableFrameMysql.java
│ │ ├── gui/MovieFormDialog.java
│ │ ├── model/Movie.java
│ │ └── service/MovieService.java
└── target/movie-manager-dms-1.0.0.jar


---

## 🧠 Class Architecture

| Layer | Class | Responsibility |
|-------|--------|----------------|
| **app** | `GuiMainMysql` | Entry point — initializes the GUI and handles DB credentials |
| **dao** | `MysqlMovieDao` | Database access logic using JDBC |
| **model** | `Movie` | Represents a movie entity (id, title, director, year, duration) |
| **service** | `MovieService` | Business logic between DAO and GUI |
| **gui** | `MovieTableFrameMysql`, `MovieFormDialog` | Swing interface for user interaction |

---

## 💾 Database Setup (DataGrip or MySQL)

1. Open MySQL or **DataGrip** and connect to your local server.  
2. Create a new database:
   ```sql
   CREATE DATABASE dms_movies;
3. Copy and run the contents of:

sql/schema.sql → creates the movies table

sql/sample_data.sql → inserts example movies
4. Confirm with:

USE dms_movies;
SELECT * FROM movies;

🚀 How to Run the Application

🧱 Option 1: Run from IntelliJ IDEA

1. Open the project folder in IntelliJ IDEA.

2. Run the class:
dms.app.GuiMainMysql

3. When prompted, enter:

Host: localhost

Username: your MySQL user (e.g. root)

Password: your MySQL password (root)

The GUI will load and show all records from the database.

💻 Option 2: Run from Terminal

cd target
java -jar movie-manager-dms-1.0.0.jar

Then follow the same login prompts (host, user, password).

🧮 Example of Custom Action

Custom Action:
When clicking Calculate Average Duration, the program computes the average of all movie durations and shows it in a message dialog box:
🎥 Average Duration: 121.4 minutes

🧰 Technologies Used

Java 17

Swing GUI

MySQL 8+

JDBC

Maven

IntelliJ IDEA

DataGrip

🧪 Validation & Error Handling

Checks for empty fields, invalid inputs, and SQL exceptions.

Prompts user before deleting records.

Ensures safe DB connection handling (connect, close, and isConnected methods).

📸 Demonstration Video

🎥 Watch the Final Phase 4 Video Presentation on YouTube


📚 Acknowledgments

Special thanks to Professor Ashley Evans and Valencia College for guidance through all DMS project phases:

Phase 1: CLI File-Based

Phase 2: Unit Testing

Phase 3: GUI Integration

Phase 4: MySQL Database Integration

🧑‍💻 Author

Luis Augusto Monserratt Alvarado
📍 Orlando, FL
📧 lmonserrattalvara@mail.valenciacollege.edu

🌐 GitHub Profile
