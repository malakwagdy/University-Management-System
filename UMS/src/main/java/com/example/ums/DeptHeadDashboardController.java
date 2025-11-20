package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.io.IOException;

public class DeptHeadDashboardController {
    @FXML
    private Label deptHeadNameLabel;

    @FXML
    private Button logoutBtn;
    
    @FXML
    private void initialize() {
        FirestoreManager fm = FirestoreManager.getInstance();
        Instructor deptHead = fm.getInstructor(GlobalData.getCurrentlyLoggedIN());
        if (deptHead != null) {
            deptHeadNameLabel.setText("Welcome, " + deptHead.getName());
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
