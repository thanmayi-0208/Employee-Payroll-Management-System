
package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

public class PayrollDAO {

    public void savePayrollRecord(Employee employee)
            throws SQLException {

        String sql = """
            INSERT INTO payroll_history
            (employee_id, employee_name, employee_type,
             basic_salary, hra, da, working_hours,
             hourly_rate, gross_salary, payslip_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, employee.getEmployeeId());
            statement.setString(2, employee.getName());
            statement.setString(3, employee.getEmployeeType());

            statement.setDouble(4, employee.getBasicSalary());
            statement.setDouble(5, employee.getHra());
            statement.setDouble(6, employee.getDa());

            statement.setDouble(7, employee.getWorkingHours());
            statement.setDouble(8, employee.getHourlyRate());

            statement.setDouble(9, employee.calculateSalary());

            statement.setDate(10, Date.valueOf(LocalDate.now()));

            statement.executeUpdate();
        }
    }
}
