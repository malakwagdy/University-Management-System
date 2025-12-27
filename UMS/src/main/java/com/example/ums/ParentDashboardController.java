package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

public class ParentDashboardController {
    @FXML
    private Label parentNameLabel;

    static DatabaseManager dm = new DatabaseManager();

    @FXML
    private void initialize() {
        Parent parent = null;
        try {
            parent = dm.getParent(GlobalData.getCurrentlyLoggedIN());
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    @FXML
    public void HandleChangePassBtn(ActionEvent actionEvent) {
        try {
            String userId = GlobalData.getCurrentlyLoggedIN();
            ChangePasswordController.show(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
