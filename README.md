# Library Management System

## Overview
The Library Management System is a client-server-based Java application that enables efficient book management, user management, loan processing, and fine calculations. It is built using Java, MySQL, JDBC, Java Swing (GUI), and multi-threaded networking for concurrent client connections.

## Features
- **User Management**: Admins can add, update, and delete users.
- **Book Management**: Books can be added, updated, deleted, and searched.
- **Loan Management**: Users can borrow and return books, with due dates enforced.
- **Fine Management**: Late returns automatically incur fines.
- **Multi-Threading**: The server handles multiple client connections simultaneously.
- **GUI Support**: Java Swing-based graphical user interface.
- **Networking**: A dedicated server handles requests from multiple clients.

## Technologies Used
- **Java** (JDK 17+)
- **MySQL** (Database Management)
- **JDBC with HikariCP** (Database Connection Pooling)
- **Java Swing** (GUI Development)
- **Sockets & Multi-threading** (Client-Server Communication)
- **DAO Pattern** (Data Access Layer)
- **Logging** (For debugging and monitoring)

## Project Structure
```
LibraryManagementSystem/
│── src/
│   ├── com/library/
│   │   ├── model/                 (Contains Java models like User, Book, Loan)
│   │   ├── dao/                   (Database Access using JDBC)
│   │   ├── service/               (Business logic layer)
│   │   ├── networking/            (Client-Server Communication)
│   │   ├── database            
│   │   ├── ui/                    (Java Swing GUI components)
│   │   ├── util/                  (Utility classes)
│── pom.xml                         (Maven Dependencies)
│── README.md                       (Project Documentation)
```

## Setup Instructions
### 1. Clone the Repository
```bash
git clone https://github.com/prathmeshborse/LibraryManagementSystem.git
cd LibraryManagementSystem
```

### 2. Configure Database
- Create a MySQL database:
```sql
CREATE DATABASE LibraryDB;
USE LibraryDB;
```
- Run the provided `schema.sql` file to create necessary tables.

### 3. Configure Database Connection
Update `DatabaseConnection.java` with your MySQL credentials:
```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://localhost:3306/library");
config.setUsername("your_username");
config.setPassword("your_password");
```

### 4. Build and Run the Project
- **Run**:
```bash
javac Main.java
```

## Usage
### Admin Functionalities:
- Add, update, and delete books.
- Manage users.
- View issued books and fines.

### Member Functionalities:
- Borrow and return books.
- View issued books and due dates.
- Pay fines if applicable.

## Authentication & Security
- Secure user authentication with hashed passwords.
- Prevents duplicate book entries and incorrect transactions.

## Contributions
Feel free to fork the repository and contribute!


