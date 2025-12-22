package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

public class InstructorDashboardController {
    @FXML
    private Label instructorNameLabel;

    @FXML
    private Label departmentLabel;

    static DatabaseManager dm = new DatabaseManager();

    @FXML
    private void initialize() {
        Instructor instructor = null;
        try {
            instructor = dm.getInstructor(GlobalData.getCurrentlyLoggedIN());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (instructor != null) {
            instructorNameLabel.setText("Welcome, " + instructor.getName());
            departmentLabel.setText("Department: " + instructor.getDepartmentName());
        }
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        User.Logout();
        try {
            SceneController.switchScene(event, "Login.fxml", "Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ViewClassroom(ActionEvent event) {
        try {
            SceneController.switchScene(event, "ViewClassrooms.fxml", "View Classrooms");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
