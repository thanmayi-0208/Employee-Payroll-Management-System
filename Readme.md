# Employee Payroll Management System

## 1. Project Overview

The Employee Payroll Management System is a Java-based desktop application developed to manage employee information, calculate salaries, and maintain payroll records.

The system supports two types of employees:

- **Permanent Employees:** Salary is calculated using Basic Salary, House Rent Allowance (HRA), and Dearness Allowance (DA).
- **Contract Employees:** Salary is calculated based on Working Hours and Hourly Rate.

The application uses JavaFX for the graphical user interface and MySQL for storing employee and payroll information.

## 2. Technologies Used

- **Programming Language:** Java
- **GUI Framework:** JavaFX
- **Database:** MySQL
- **Database Connectivity:** JDBC
- **Build Tool:** Apache Maven
- **IDE:** Visual Studio Code

## 3. Project Features

1. Add new employee records.
2. View all employee records.
3. Update employee information.
4. Delete employee records.
5. Search employees using Employee ID or Name.
6. Calculate salaries for Permanent and Contract employees.
7. Generate and save employee payslips as text files.
8. Maintain payroll history in the MySQL database.
9. View previously generated payroll records.
10. Refresh employee records.

## 4. Project Structure

```text
demo/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── Main.java
│   │   │           ├── Employee.java
│   │   │           ├── EmployeeDAO.java
│   │   │           ├── DBConnection.java
│   │   │           └── PayrollDAO.java
│   │   │
│   │   └── resources/
│   │
│   └── test/
│       └── java/
│
├── pom.xml
├── run.ps1
└── README.md
```

*Note: Additional Java files may be present depending on the implementation.*

## 5. Software Requirements

Install the following software before running the project:

- JDK 26
- Visual Studio Code
- Apache Maven 3.10.0 or a compatible Maven version
- MySQL Server 8.0
- MySQL Workbench

## 6. Database Setup

### Step 1: Create the Database

Open MySQL Workbench and execute:

```sql
CREATE DATABASE IF NOT EXISTS employee_payroll;

USE employee_payroll;
```

### Step 2: Create the Employees Table

```sql
CREATE TABLE IF NOT EXISTS employees (
    employee_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    employee_type VARCHAR(20) NOT NULL,
    basic_salary DOUBLE,
    hra DOUBLE,
    da DOUBLE,
    working_hours DOUBLE,
    hourly_rate DOUBLE
);
```

### Step 3: Create the Payroll History Table

```sql
CREATE TABLE IF NOT EXISTS payroll_history (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    employee_name VARCHAR(100) NOT NULL,
    employee_type VARCHAR(20) NOT NULL,
    basic_salary DOUBLE DEFAULT 0,
    hra DOUBLE DEFAULT 0,
    da DOUBLE DEFAULT 0,
    working_hours DOUBLE DEFAULT 0,
    hourly_rate DOUBLE DEFAULT 0,
    gross_salary DOUBLE NOT NULL,
    payslip_date DATE NOT NULL
);
```

### Step 4: Verify the Tables

Execute:

```sql
SHOW TABLES;

DESCRIBE employees;

DESCRIBE payroll_history;
```

## 7. Configure Database Connection

Open `DBConnection.java` in VS Code.

Update the database connection details according to your local MySQL installation.

The connection should use:

- Database: `employee_payroll`
- Host: `localhost`
- Port: `3306`
- Username: Your MySQL username
- Password: Your own MySQL password

Each team member must configure their own MySQL credentials.

**Important:** Do not share personal database passwords.

## 8. Running the Application

### Step 1: Extract the Project

Extract the ZIP file into a folder on your computer.

### Step 2: Open in VS Code

Open Visual Studio Code and select:

`File → Open Folder → demo`

### Step 3: Start MySQL

Ensure that the MySQL Server service is running.

### Step 4: Run the Application

Open the VS Code terminal in the project root directory.

If Maven is installed and configured in PATH, execute:

```powershell
mvn javafx:run
```

If you have configured the `run.ps1` script with your local Maven installation path, execute:

```powershell
.\run.ps1
```

Maven will download the required dependencies specified in `pom.xml` automatically.

## 9. Salary Calculation

### Permanent Employee

Gross Salary is calculated as:

```text
Gross Salary = Basic Salary + HRA + DA
```

### Contract Employee

Gross Salary is calculated as:

```text
Gross Salary = Working Hours × Hourly Rate
```

## 10. Application Workflow

1. Launch the Employee Payroll Management System.
2. Enter employee details.
3. Select the employee type.
4. Enter the relevant salary information.
5. Calculate the gross salary.
6. Save the employee details to MySQL.
7. View, search, update, or delete employee records.
8. Select an employee and generate a payslip.
9. View generated payroll records using the Payroll History feature.

## 11. Expected Output

The application provides:

- A JavaFX-based employee management interface.
- Employee records stored in MySQL.
- Automatic salary calculation.
- Payslip generation in text-file format.
- Payroll history stored and retrieved from MySQL.

## 12. Important Notes

- MySQL Server must be running before launching the application.
- Database credentials must be configured locally.
- The database and required tables must be created before using the application.
- The `target` directory is generated automatically by Maven and does not need to be included when sharing the source project.
- Do not share personal database passwords with other team members.

---

**Project:** Employee Payroll Management System  
**Package:** `com.example`  
**Build Tool:** Apache Maven  
**Database:** MySQL

## Project Demonstration Video

The following video demonstrates the complete working of our Employee Payroll Management System, including employee management, salary calculation, database operations, payslip generation and payroll history.

**Video Demonstration:** [Click here to watch the project demonstration](https://drive.google.com/file/d/1i3EMGRcrmrhGQW6epYToLMRK16CxGFyB/view?usp=sharing)