
package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import java.sql.SQLException;
import java.util.List;

public class Main extends Application {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    private final ObservableList<Employee> employeeList =
            FXCollections.observableArrayList();

    private TableView<Employee> employeeTable;

    private TextField idField, nameField, departmentField, designationField;
    private TextField basicField, hraField, daField, hoursField, rateField;
    private TextField searchField;
    private ComboBox<String> typeBox;
    private Label resultLabel;

    @Override
    public void start(Stage stage) {

        Label title = new Label("EMPLOYEE PAYROLL MANAGEMENT");
        title.setStyle(
            "-fx-font-size: 25px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #17365D;"
        );

        Label subtitle = new Label(
            "Manage employees, salaries and payroll records"
        );

        idField = new TextField();
        idField.setPromptText("Employee ID");

        nameField = new TextField();
        nameField.setPromptText("Full name");

        departmentField = new TextField();
        departmentField.setPromptText("Department");

        designationField = new TextField();
        designationField.setPromptText("Designation");

        typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Permanent", "Contract");
        typeBox.setPromptText("Select type");
        typeBox.setMaxWidth(Double.MAX_VALUE);

        basicField = new TextField();
        basicField.setPromptText("Permanent employees");

        hraField = new TextField();
        hraField.setPromptText("Permanent employees");

        daField = new TextField();
        daField.setPromptText("Permanent employees");

        hoursField = new TextField();
        hoursField.setPromptText("Contract employees");

        rateField = new TextField();
        rateField.setPromptText("Contract employees");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new Insets(15));

        form.addRow(0, new Label("Employee ID"), idField,
                       new Label("Employee Name"), nameField);
        form.addRow(1, new Label("Department"), departmentField,
                       new Label("Designation"), designationField);
        form.addRow(2, new Label("Employee Type"), typeBox);
        form.addRow(3, new Label("Basic Salary (₹)"), basicField,
                       new Label("HRA (₹)"), hraField);
        form.addRow(4, new Label("DA (₹)"), daField,
                       new Label("Working Hours"), hoursField);
        form.addRow(5, new Label("Hourly Rate (₹)"), rateField);

        resultLabel = new Label("Select an action to get started.");
        resultLabel.setWrapText(true);
        resultLabel.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #17365D;"
        );

        Button calculateButton = new Button("Calculate Salary");
        Button saveButton = new Button("Save Employee");
        Button updateButton = new Button("Update Employee");
        Button deleteButton = new Button("Delete Selected");
        Button clearButton = new Button("Clear Form");
        Button refreshButton = new Button("Refresh Records");
        Button searchButton = new Button("Search");
        Button payslipButton = new Button("Generate Payslip");
        Button historyButton = new Button("View Payroll History");

        String buttonStyle =
            "-fx-background-color: #17365D;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 9 12 9 12;";

        for (Button button : List.of(
        calculateButton, saveButton, updateButton,
        deleteButton, clearButton, refreshButton,
        searchButton, payslipButton, historyButton)) {

    button.setStyle(buttonStyle);
}

        // Validate form data and create an Employee object.
        java.util.function.Supplier<Employee> readEmployee = () -> {
            try {
                if (idField.getText().isBlank()
                        || nameField.getText().isBlank()
                        || departmentField.getText().isBlank()
                        || designationField.getText().isBlank()) {
                    throw new IllegalArgumentException(
                        "Fill in ID, name, department and designation."
                    );
                }

                String type = typeBox.getValue();

                if (type == null) {
                    throw new IllegalArgumentException(
                        "Select an employee type."
                    );
                }

                int id = Integer.parseInt(idField.getText().trim());

                if (id <= 0) {
                    throw new IllegalArgumentException(
                        "Employee ID must be positive."
                    );
                }

                double basic = 0, hra = 0, da = 0;
                double hours = 0, rate = 0;

                if (type.equals("Permanent")) {
                    basic = Double.parseDouble(basicField.getText().trim());
                    hra = Double.parseDouble(hraField.getText().trim());
                    da = Double.parseDouble(daField.getText().trim());

                    if (basic < 0 || hra < 0 || da < 0) {
                        throw new IllegalArgumentException(
                            "Salary components cannot be negative."
                        );
                    }
                } else {
                    hours = Double.parseDouble(hoursField.getText().trim());
                    rate = Double.parseDouble(rateField.getText().trim());

                    if (hours < 0 || rate < 0) {
                        throw new IllegalArgumentException(
                            "Hours and hourly rate cannot be negative."
                        );
                    }
                }

                return new Employee(
                    id,
                    nameField.getText().trim(),
                    departmentField.getText().trim(),
                    designationField.getText().trim(),
                    type,
                    basic, hra, da, hours, rate
                );

            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                    "Enter valid numbers in the required fields."
                );
            }
        };

        // Calculate salary.
        calculateButton.setOnAction(event -> {
            try {
                Employee employee = readEmployee.get();

                resultLabel.setText(
                    "Employee: " + employee.getName() +
                    "\nType: " + employee.getEmployeeType() +
                    "\nGross Salary: ₹" +
                    String.format("%.2f", employee.calculateSalary())
                );

            } catch (IllegalArgumentException ex) {
                showAlert("Input Error", ex.getMessage());
            }
        });

        // Save a new employee.
        saveButton.setOnAction(event -> {
            try {
                Employee employee = readEmployee.get();
                employeeDAO.addEmployee(employee);

                loadEmployees();

                resultLabel.setText(
                    "Employee saved successfully.\nGross Salary: ₹" +
                    String.format("%.2f", employee.calculateSalary())
                );

                showInformation("Success", "Employee saved to MySQL.");

            } catch (IllegalArgumentException ex) {
                showAlert("Input Error", ex.getMessage());
            } catch (SQLException ex) {
                showAlert(
                    "Database Error",
                    "Could not save employee.\n" + ex.getMessage()
                );
            }
        });

        // Update the selected employee.
        updateButton.setOnAction(event -> {
            try {
                Employee employee = readEmployee.get();

                boolean updated = employeeDAO.updateEmployee(employee);

                if (updated) {
                    loadEmployees();
                    resultLabel.setText(
                        "Employee updated successfully.\nGross Salary: ₹" +
                        String.format("%.2f", employee.calculateSalary())
                    );
                    showInformation("Success", "Employee details updated.");
                } else {
                    showAlert(
                        "Not Found",
                        "No employee exists with this ID."
                    );
                }

            } catch (IllegalArgumentException ex) {
                showAlert("Input Error", ex.getMessage());
            } catch (SQLException ex) {
                showAlert(
                    "Database Error",
                    "Could not update employee.\n" + ex.getMessage()
                );
            }
        });
                // Generate payslip for the selected employee.
        payslipButton.setOnAction(event -> {

            Employee selectedEmployee = employeeTable
                    .getSelectionModel()
                    .getSelectedItem();

            if (selectedEmployee == null) {
                showAlert(
                    "No Employee Selected",
                    "Please select an employee from the records table."
                );
                return;
            }

            generatePayslip(selectedEmployee, stage);
        });

        // Display previously generated payroll records.
historyButton.setOnAction(event -> showPayrollHistory());

        // Delete the selected employee after confirmation.
        deleteButton.setOnAction(event -> {
            Employee selected = employeeTable.getSelectionModel()
                                             .getSelectedItem();

            if (selected == null) {
                showAlert(
                    "Select Employee",
                    "Click an employee row in the table first."
                );
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Deletion");
            confirmation.setHeaderText("Delete " + selected.getName() + "?");
            confirmation.setContentText(
                "This will permanently delete employee ID "
                + selected.getEmployeeId() + "."
            );

            if (confirmation.showAndWait().orElse(ButtonType.CANCEL)
                    == ButtonType.OK) {
                try {
                    boolean deleted = employeeDAO.deleteEmployee(
                        selected.getEmployeeId()
                    );

                    if (deleted) {
                        loadEmployees();
                        clearForm();
                        resultLabel.setText("Employee deleted successfully.");
                    } else {
                        showAlert("Not Found", "Employee was not found.");
                    }

                } catch (SQLException ex) {
                    showAlert(
                        "Database Error",
                        "Could not delete employee.\n" + ex.getMessage()
                    );
                }
            }
        });

        // Search by ID or name.
        searchField = new TextField();
        searchField.setPromptText("Enter employee ID or name");
        searchField.setPrefWidth(250);

        searchButton.setOnAction(event -> searchEmployees());
        searchField.setOnAction(event -> searchEmployees());

        // Show all records again.
        refreshButton.setOnAction(event -> {
            searchField.clear();
            loadEmployees();
        });

        // Clear the form.
        clearButton.setOnAction(event -> clearForm());

        // Create employee records table.
        employeeTable = new TableView<>();
        employeeTable.setItems(employeeList);
        employeeTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );
        employeeTable.setPlaceholder(
            new Label("No employee records found.")
        );

        TableColumn<Employee, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data ->
            new javafx.beans.property.SimpleIntegerProperty(
                data.getValue().getEmployeeId()
            )
        );

        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getName()
            )
        );

        TableColumn<Employee, String> departmentColumn =
            new TableColumn<>("Department");
        departmentColumn.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDepartment()
            )
        );

        TableColumn<Employee, String> designationColumn =
            new TableColumn<>("Designation");
        designationColumn.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDesignation()
            )
        );

        TableColumn<Employee, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getEmployeeType()
            )
        );

        TableColumn<Employee, String> salaryColumn =
            new TableColumn<>("Gross Salary (₹)");
        salaryColumn.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                String.format(
                    "%.2f", data.getValue().calculateSalary()
                )
            )
        );

        employeeTable.getColumns().add(idColumn);
employeeTable.getColumns().add(nameColumn);
employeeTable.getColumns().add(departmentColumn);
employeeTable.getColumns().add(designationColumn);
employeeTable.getColumns().add(typeColumn);
employeeTable.getColumns().add(salaryColumn);
        employeeTable.setPrefHeight(300);

        // Selecting a row loads its details into the form.
        employeeTable.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, oldEmployee, selected) -> {
                if (selected != null) {
                    fillForm(selected);
                }
            });

        HBox mainButtons = new HBox(
            10, calculateButton, saveButton, updateButton, clearButton
        );
        mainButtons.setAlignment(Pos.CENTER);

      HBox recordButtons = new HBox(
    10, deleteButton, refreshButton,
    payslipButton, historyButton
);

recordButtons.setAlignment(Pos.CENTER);
        recordButtons.setAlignment(Pos.CENTER);

        HBox searchRow = new HBox(
            10, searchField, searchButton
        );
        searchRow.setAlignment(Pos.CENTER_LEFT);

        Label recordsTitle = new Label("EMPLOYEE RECORDS");
        recordsTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #17365D;"
        );

        VBox root = new VBox(
            14, title, subtitle, new Separator(), form,
            mainButtons, resultLabel, recordsTitle,
            searchRow, employeeTable, recordButtons
        );

        root.setPadding(new Insets(22));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F3F6FA;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 1150, 850);

        stage.setTitle("Employee Payroll Management System");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(700);
        stage.show();

        loadEmployees();
    }

    private void searchEmployees() {
        String search = searchField.getText().trim();

        if (search.isEmpty()) {
            loadEmployees();
            return;
        }

        try {
            List<Employee> results = employeeDAO.searchEmployees(search);
            employeeList.setAll(results);

            resultLabel.setText(
                results.size() + " matching employee(s) found."
            );

            if (results.isEmpty()) {
                showInformation(
                    "Search Results",
                    "No matching employees were found."
                );
            }

        } catch (SQLException ex) {
            showAlert(
                "Database Error",
                "Search failed.\n" + ex.getMessage()
            );
        }
    }

    private void loadEmployees() {
        try {
            List<Employee> employees = employeeDAO.getAllEmployees();
            employeeList.setAll(employees);
        } catch (SQLException ex) {
            showAlert(
                "Database Error",
                "Could not load employee records.\n" + ex.getMessage()
            );
        }
    }

    private void fillForm(Employee employee) {
        idField.setText(String.valueOf(employee.getEmployeeId()));
        nameField.setText(employee.getName());
        departmentField.setText(employee.getDepartment());
        designationField.setText(employee.getDesignation());
        typeBox.setValue(employee.getEmployeeType());

        basicField.setText(String.valueOf(employee.getBasicSalary()));
        hraField.setText(String.valueOf(employee.getHra()));
        daField.setText(String.valueOf(employee.getDa()));
        hoursField.setText(String.valueOf(employee.getWorkingHours()));
        rateField.setText(String.valueOf(employee.getHourlyRate()));

        resultLabel.setText(
            "Selected: " + employee.getName() +
            " | Gross Salary: ₹" +
            String.format("%.2f", employee.calculateSalary())
        );
    }

    private void clearForm() {
        idField.clear();
        nameField.clear();
        departmentField.clear();
        designationField.clear();
        typeBox.setValue(null);
        basicField.clear();
        hraField.clear();
        daField.clear();
        hoursField.clear();
        rateField.clear();

        if (employeeTable != null) {
            employeeTable.getSelectionModel().clearSelection();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
    private void generatePayslip(Employee employee, Stage stage) {

    StringBuilder payslip = new StringBuilder();

    payslip.append("========================================\n");
    payslip.append("             EMPLOYEE PAYSLIP\n");
    payslip.append("        EMPLOYEE PAYROLL SYSTEM\n");
    payslip.append("========================================\n\n");

    payslip.append("Payslip Date: ")
           .append(LocalDate.now()).append("\n\n");

    payslip.append("EMPLOYEE DETAILS\n");
    payslip.append("----------------------------------------\n");

    payslip.append("Employee ID   : ")
           .append(employee.getEmployeeId()).append("\n");

    payslip.append("Employee Name : ")
           .append(employee.getName()).append("\n");

    payslip.append("Department    : ")
           .append(employee.getDepartment()).append("\n");

    payslip.append("Designation   : ")
           .append(employee.getDesignation()).append("\n");

    payslip.append("Employee Type : ")
           .append(employee.getEmployeeType()).append("\n\n");

    payslip.append("SALARY BREAKDOWN\n");
    payslip.append("----------------------------------------\n");

    if (employee.getEmployeeType().equalsIgnoreCase("Permanent")) {

        payslip.append(String.format(
            "Basic Salary  : Rs. %.2f%n",
            employee.getBasicSalary()
        ));

        payslip.append(String.format(
            "HRA           : Rs. %.2f%n",
            employee.getHra()
        ));

        payslip.append(String.format(
            "DA            : Rs. %.2f%n",
            employee.getDa()
        ));

    } else {

        payslip.append(String.format(
            "Working Hours : %.2f%n",
            employee.getWorkingHours()
        ));

        payslip.append(String.format(
            "Hourly Rate   : Rs. %.2f%n",
            employee.getHourlyRate()
        ));

        payslip.append(String.format(
            "Calculation   : %.2f x %.2f%n",
            employee.getWorkingHours(),
            employee.getHourlyRate()
        ));
    }

    payslip.append("----------------------------------------\n");

    payslip.append(String.format(
        "GROSS SALARY  : Rs. %.2f%n",
        employee.calculateSalary()
    ));

    payslip.append("----------------------------------------\n");
    payslip.append("             END OF PAYSLIP\n");
    payslip.append("========================================\n");

    FileChooser fileChooser = new FileChooser();

    fileChooser.setTitle("Save Employee Payslip");

    fileChooser.setInitialFileName(
        "Payslip_" + employee.getEmployeeId() + ".txt"
    );

    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter(
            "Text Files", "*.txt"
        )
    );

    File file = fileChooser.showSaveDialog(stage);

    if (file != null) {

        try {

            Files.writeString(
                file.toPath(),
                payslip.toString(),
                StandardCharsets.UTF_8
            );
            // Save payroll details into MySQL history.
PayrollDAO payrollDAO = new PayrollDAO();
payrollDAO.savePayrollRecord(employee);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Payslip Generated");
            alert.setHeaderText("Payslip saved successfully!");
            alert.setContentText(file.getAbsolutePath());
            alert.showAndWait();

            } catch (SQLException ex) {

    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Database Error");
    alert.setHeaderText("Payslip file saved, but payroll history could not be saved.");
    alert.setContentText(ex.getMessage());
    alert.showAndWait();

        } catch (IOException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Error");
            alert.setHeaderText("Could not save payslip");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
}
// Display payroll history in a separate window.
private void showPayrollHistory() {

    try {

        List<String[]> records = employeeDAO.getPayrollHistory();

        TableView<String[]> historyTable = new TableView<>();

        TableColumn<String[], String> idColumn =
                new TableColumn<>("Employee ID");

        idColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue()[0]
                ));

        TableColumn<String[], String> nameColumn =
                new TableColumn<>("Employee Name");

        nameColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue()[1]
                ));

        TableColumn<String[], String> typeColumn =
                new TableColumn<>("Employee Type");

        typeColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue()[2]
                ));

        TableColumn<String[], String> salaryColumn =
                new TableColumn<>("Gross Salary (₹)");

        salaryColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue()[3]
                ));

        TableColumn<String[], String> dateColumn =
                new TableColumn<>("Payslip Date");

        dateColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue()[4]
                ));
historyTable.getColumns().add(idColumn);
historyTable.getColumns().add(nameColumn);
historyTable.getColumns().add(typeColumn);
historyTable.getColumns().add(salaryColumn);
historyTable.getColumns().add(dateColumn);

        historyTable.setItems(
                FXCollections.observableArrayList(records)
        );

        historyTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        historyTable.setPlaceholder(
                new Label("No payroll history available.")
        );

        Label heading = new Label("PAYROLL HISTORY");
        heading.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #17365D;"
        );

        VBox layout = new VBox(15, heading, historyTable);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        layout.setStyle("-fx-background-color: #F3F6FA;");

        Stage historyStage = new Stage();
        historyStage.setTitle("Payroll History");
        historyStage.setScene(new Scene(layout, 850, 450));
        historyStage.show();

    } catch (SQLException ex) {

        showAlert(
                "Database Error",
                "Could not retrieve payroll history.\n"
                + ex.getMessage()
        );
    }
}
}
