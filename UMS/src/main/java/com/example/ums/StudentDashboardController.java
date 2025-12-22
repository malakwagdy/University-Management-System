package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

public class StudentDashboardController {

    @FXML
    private Label studentNameLabel;

    @FXML
    private Button logoutBtn;

    static DatabaseManager dm = new DatabaseManager();

    @FXML
    private void initialize() {
        Student student = null;
        try {
            student = dm.getStudent(GlobalData.getCurrentlyLoggedIN());
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
