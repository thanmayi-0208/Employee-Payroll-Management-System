
package com.example;

public class Employee {

    private int employeeId;
    private String name;
    private String department;
    private String designation;
    private String employeeType;

    private double basicSalary;
    private double hra;
    private double da;
    private double workingHours;
    private double hourlyRate;

    public Employee(int employeeId, String name,
                    String department, String designation,
                    String employeeType, double basicSalary,
                    double hra, double da,
                    double workingHours, double hourlyRate) {

        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.designation = designation;
        this.employeeType = employeeType;
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.da = da;
        this.workingHours = workingHours;
        this.hourlyRate = hourlyRate;
    }

    public double calculateSalary() {
        if (employeeType.equalsIgnoreCase("Permanent")) {
            return basicSalary + hra + da;
        }

        if (employeeType.equalsIgnoreCase("Contract")) {
            return workingHours * hourlyRate;
        }

        throw new IllegalArgumentException("Invalid employee type");
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getDesignation() {
        return designation;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public double getHra() {
        return hra;
    }

    public double getDa() {
        return da;
    }

    public double getWorkingHours() {
        return workingHours;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}
