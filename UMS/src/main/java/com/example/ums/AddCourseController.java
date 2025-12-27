package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

public class AddCourseController {

    @FXML
    private TextField courseNameField;
    @FXML
    private TextField bylawField;
    @FXML
    private TextArea courseDescriptionTextArea;
    @FXML
    private javafx.scene.control.ComboBox<String> departmentComboBox;

    @FXML
    private javafx.scene.layout.HBox departmentHBox;

    @FXML
    public void initialize() {
        departmentComboBox.getItems().addAll("CESS", "COMM", "MCTA", "ERGY", "BLDG", "LAAR", "HOUD", "CISE", "MANF",
                "ENVR", "MATL");

        String currentUser = GlobalData.getCurrentlyLoggedIN();
        DatabaseManager dm = new DatabaseManager();

        try {
            User user = dm.getUser(currentUser);
            if ("Instructor".equals(user.getType())) {
                Instructor instructor = dm.getInstructor(currentUser);
                if (instructor != null && instructor.isDepartmentHead()) {
                    // Department Head: Hide combobox, pre-select their department
                    departmentHBox.setVisible(false);
                    departmentHBox.setManaged(false);
                    departmentComboBox.setValue(instructor.getDepartmentName());
                }
            } else {
                // Admin/HR: Show combobox
                departmentHBox.setVisible(true);
                departmentHBox.setManaged(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddCourseButton(ActionEvent event) {
        String courseName = courseNameField.getText();
        String bylaw = bylawField.getText();
        String courseDescription = courseDescriptionTextArea.getText();
        String department = departmentComboBox.getValue();

        // Validate input
        if (courseName.isEmpty() || bylaw.isEmpty() || courseDescription.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("All fields must be filled out.");
            alert.showAndWait();
            return;
        }

        if (department == null || department.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Department must be selected.");
            alert.showAndWait();
            return;
        }

        Admin admin = new Admin();
        String currentUser = GlobalData.getCurrentlyLoggedIN();
        DatabaseManager dm = new DatabaseManager();
        try {
            Admin fetchedAdmin = dm.getAdmin(currentUser);
            if (fetchedAdmin != null) {
                admin = fetchedAdmin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        admin.addCourse(courseName, courseDescription, bylaw, department);

        try {
            User user = dm.getUser(currentUser);
            if (user != null && "Instructor".equals(user.getType())) {
                SceneController.switchScene(event, "DeptHeadDashboard.fxml", "Department Head Dashboard");
            } else {
                SceneController.switchScene(event, "AdminDashboard.fxml", "Admin Dashboard");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleCancelButton(ActionEvent event) {
        try {
            SceneController.switchScene(event, "AdminDashboard.fxml", "Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
