# 📚 Library Management System

A desktop-based Library Management System built with Java Swing and MySQL, developed as a final project for the **Object-Oriented and Visual Programming** course.

---

## ✨ Features

- **Books Management** — Add, update, delete, and search books by title, author, or ISBN
- **Members Management** — Register and manage library members with personal details and status
- **Issue & Return** — Handle book lending transactions with automatic due date tracking
- **Overdue Fine Calculation** — Automatically calculates fines at Rp 1,000/day for overdue returns
- **Dashboard** — Real-time statistics including total books, available copies, active members, active issues, overdue count, and book categories

---

## 🛠️ Tech Stack

| Technology | Details |
|------------|---------|
| Language | Java (JDK 8+) |
| UI Framework | Java Swing (Nimbus Look & Feel) |
| IDE | Apache NetBeans |
| Database | MySQL |
| DB Driver | MySQL Connector/J 9.7.0 |
| Architecture | Object-Oriented (Singleton pattern for DB connection) |

---

## 🗂️ Project Structure

```
LibraryManagementSystem/
├── src/
│   └── library/
│       ├── DBConnection.java       # Singleton MySQL connection manager
│       ├── MainFrame.java          # Main JFrame with tabbed navigation
│       ├── BooksPanel.java         # CRUD panel for books
│       ├── MembersPanel.java       # CRUD panel for members
│       ├── IssuedBooksPanel.java   # Issue & return transactions
│       └── DashboardPanel.java     # Real-time statistics dashboard
├── mysql-connector-j-9.7.0.jar
├── library_db.sql
└── manifest.mf
```

---

## ⚙️ How to Run

### 1. Set Up the Database

1. Open **phpMyAdmin** or your MySQL client
2. Create a new database named `library_db`
3. Import the provided `library_db.sql` file

### 2. Configure the Connection

Open `src/library/DBConnection.java` and update the credentials if needed:

```java
private static final String URL = "jdbc:mysql://localhost:3306/library_db";
private static final String USER = "root";
private static final String PASSWORD = ""; // your MySQL password
```

### 3. Open in NetBeans

1. Open **Apache NetBeans**
2. Go to **File → Open Project** and select this folder
3. Make sure `mysql-connector-j-9.7.0.jar` is added to the project libraries
4. Click **Run** (F6)

---

## 📸 Screenshots

### Books Management
![Books Management](screenshots/books.png)

### Members Management
![Members Management](screenshots/members.png)

### Issue & Return
![Issue Return](screenshots/issue-return.png)

### Dashboard
![Dashboard](screenshots/dashboard.png)

---

## 📝 Notes

- Make sure MySQL server is running before launching the application
- Default fine rate: **Rp 1,000 per day** for overdue books
- The application uses the **Nimbus** Look and Feel for a modern UI appearance
