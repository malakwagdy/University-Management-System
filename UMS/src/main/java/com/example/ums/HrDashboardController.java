package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.io.IOException;

public class HrDashboardController {
    @FXML
    private Label hrNameLabel;

    @FXML
    private Button logoutBtn;

    @FXML
    private void initialize() {
        FirestoreManager fm = FirestoreManager.getInstance();
        HR hr = fm.getHR(GlobalData.getCurrentlyLoggedIN());
        if (hr != null) {
            hrNameLabel.setText("Welcome, " + hr.getName());
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
}