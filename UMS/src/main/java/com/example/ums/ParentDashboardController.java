package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.io.IOException;

public class ParentDashboardController {
    @FXML
    private Label parentNameLabel;

    @FXML
    private Button logoutBtn;

    @FXML
    private void initialize() {
        FirestoreManager fm = FirestoreManager.getInstance();
        Parent parent = fm.getParent(GlobalData.getCurrentlyLoggedIN());
        if (parent != null) {
            parentNameLabel.setText("Welcome, " + parent.getName());
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
