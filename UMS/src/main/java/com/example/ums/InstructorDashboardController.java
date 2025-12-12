package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.io.IOException;

public class InstructorDashboardController {
    @FXML
    private Label instructorNameLabel;

    @FXML
    private Button logoutBtn;

    @FXML
    private void initialize() {
        FirestoreManager fm = FirestoreManager.getInstance();
        Instructor instructor = fm.getInstructor(GlobalData.getCurrentlyLoggedIN());
        if (instructor != null) {
            instructorNameLabel.setText("Welcome, " + instructor.getName());
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
    private void viewClassrooms() {

    }
}
