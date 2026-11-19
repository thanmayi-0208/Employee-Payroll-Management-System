
package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/employee_payroll";

    private static final String USER = "root";

    // Replace this with your MySQL password on your own computer.
    private static final String PASSWORD = "Thanmayi@4211";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("MySQL connection successful!");
            System.out.println("Database: " +
                    connection.getCatalog());
        } catch (SQLException e) {
            System.out.println("MySQL connection failed.");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}
