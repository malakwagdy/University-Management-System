package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

public class DeptHeadDashboardController {
    @FXML
    private Label deptHeadNameLabel;

    @FXML
    private Label departmentNameLabel;

    static DatabaseManager dm = new DatabaseManager();

    @FXML
    private void initialize() {
        Instructor deptHead = null;
        try {
            deptHead = dm.getInstructor(GlobalData.getCurrentlyLoggedIN());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (deptHead != null) {
            deptHeadNameLabel.setText("Welcome, " + deptHead.getName());
            departmentNameLabel.setText("Department: " + deptHead.getDepartmentName());
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
