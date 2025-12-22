package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

public class HrDashboardController {
    @FXML
    private Label hrNameLabel;

    static DatabaseManager dm = new DatabaseManager();

    @FXML
    private void initialize() {
        HR hr = null;
        try {
            hr = dm.getHR(GlobalData.getCurrentlyLoggedIN());
        } catch (SQLException e) {
            e.printStackTrace();
        }
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