
package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // Add an employee
    public void addEmployee(Employee employee) throws SQLException {
        String sql = """
            INSERT INTO employees
            (employee_id, name, department, designation,
             employee_type, basic_salary, hra, da,
             working_hours, hourly_rate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            setEmployeeParameters(ps, employee);
            ps.executeUpdate();
        }
    }

    // Retrieve all employees
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY employee_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }
        }

        return employees;
    }

    // Search by employee ID or name
    public List<Employee> searchEmployees(String search) throws SQLException {
        List<Employee> employees = new ArrayList<>();

        String sql = """
            SELECT * FROM employees
            WHERE CAST(employee_id AS CHAR) LIKE ?
               OR name LIKE ?
            ORDER BY employee_id
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String value = "%" + search.trim() + "%";
            ps.setString(1, value);
            ps.setString(2, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapEmployee(rs));
                }
            }
        }

        return employees;
    }

    // Update employee details
    public boolean updateEmployee(Employee employee) throws SQLException {
        String sql = """
            UPDATE employees SET
                name = ?, department = ?, designation = ?,
                employee_type = ?, basic_salary = ?, hra = ?,
                da = ?, working_hours = ?, hourly_rate = ?
            WHERE employee_id = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employee.getName());
            ps.setString(2, employee.getDepartment());
            ps.setString(3, employee.getDesignation());
            ps.setString(4, employee.getEmployeeType());
            ps.setDouble(5, employee.getBasicSalary());
            ps.setDouble(6, employee.getHra());
            ps.setDouble(7, employee.getDa());
            ps.setDouble(8, employee.getWorkingHours());
            ps.setDouble(9, employee.getHourlyRate());
            ps.setInt(10, employee.getEmployeeId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete an employee by ID
    public boolean deleteEmployee(int employeeId) throws SQLException {
        String sql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        }
    }

    // Shared helper for inserting employee data
    private void setEmployeeParameters(
            PreparedStatement ps, Employee employee) throws SQLException {

        ps.setInt(1, employee.getEmployeeId());
        ps.setString(2, employee.getName());
        ps.setString(3, employee.getDepartment());
        ps.setString(4, employee.getDesignation());
        ps.setString(5, employee.getEmployeeType());
        ps.setDouble(6, employee.getBasicSalary());
        ps.setDouble(7, employee.getHra());
        ps.setDouble(8, employee.getDa());
        ps.setDouble(9, employee.getWorkingHours());
        ps.setDouble(10, employee.getHourlyRate());
    }

    // Convert a database row into an Employee object
    private Employee mapEmployee(ResultSet rs) throws SQLException {
        return new Employee(
            rs.getInt("employee_id"),
            rs.getString("name"),
            rs.getString("department"),
            rs.getString("designation"),
            rs.getString("employee_type"),
            rs.getDouble("basic_salary"),
            rs.getDouble("hra"),
            rs.getDouble("da"),
            rs.getDouble("working_hours"),
            rs.getDouble("hourly_rate")
        );
    }
    // Retrieve all payroll history records
public List<String[]> getPayrollHistory() throws SQLException {

    List<String[]> history = new ArrayList<>();

    String sql = """
        SELECT employee_id, employee_name, employee_type,
               gross_salary, payslip_date
        FROM payroll_history
        ORDER BY record_id DESC
        """;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            String[] record = {
                String.valueOf(rs.getInt("employee_id")),
                rs.getString("employee_name"),
                rs.getString("employee_type"),
                String.format("%.2f", rs.getDouble("gross_salary")),
                rs.getDate("payslip_date").toString()
            };

            history.add(record);
        }
    }

    return history;
}
}
