📚 Library Management System

A Library Management System developed using HTML, CSS, JavaScript, Java, JDBC, and MySQL. The project provides a simple interface for managing library books and performing basic database operations.

🚀 Features
Add new books
View available books
Update book details
Delete books
Store book information in MySQL
Java-based backend for application logic
Responsive and user-friendly frontend
JDBC connectivity between Java and MySQL
🏗️ Project Structure
Library-Management-System/
│
├── Frontend/
│   ├── index.html
│   ├── style.css
│   └── script.js
│
├── Backend/
│   ├── Main.java
│   ├── Book.java
│   ├── BookController.java
│   └── DatabaseConnection.java
│
├── Database/
│   └── library.sql
│
├── README.md
└── .gitattributes
💻 Technologies Used
Frontend
HTML5
CSS3
JavaScript
Backend
Java
JDBC
Database
MySQL
Development Tools
Git
GitHub
Java IDE / VS Code
🔄 System Architecture
             USER
               │
               ▼
       ┌────────────────┐
       │    FRONTEND    │
       │ HTML CSS JS    │
       └───────┬────────┘
               │
               ▼
       ┌────────────────┐
       │    BACKEND     │
       │     JAVA       │
       │ BookController │
       └───────┬────────┘
               │
              JDBC
               │
               ▼
       ┌────────────────┐
       │    DATABASE    │
       │     MySQL      │
       │  library.sql   │
       └────────────────┘