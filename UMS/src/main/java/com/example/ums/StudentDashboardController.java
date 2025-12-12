package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.io.IOException;

public class StudentDashboardController {

    @FXML
    private Label studentNameLabel;

    @FXML
    private Button logoutBtn;

    @FXML
    private void initialize() {
        FirestoreManager fm = FirestoreManager.getInstance();
        Student student = fm.getStudent(GlobalData.getCurrentlyLoggedIN());
        if (student != null) {
            studentNameLabel.setText("Welcome, " + student.getName());
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
    public void handleChangePassword(ActionEvent actionEvent) {
        try {
            SceneController.switchScene(actionEvent, "ChangePassword.fxml", "Change Password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
