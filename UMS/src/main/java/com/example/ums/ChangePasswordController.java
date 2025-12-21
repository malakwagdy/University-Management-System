package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.sql.SQLException;

public class ChangePasswordController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    // Handle Save Password button
    @FXML
    private void handleSavePassword() {

        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        // Validation: fields must not be empty
        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showMessage("Both fields are required.", "red");
            return;
        }

        // Validation: passwords must match
        if (!newPass.equals(confirmPass)) {
            showMessage("Passwords do not match!", "red");
            return;
        }

        // Validation: minimum length
        if (newPass.length() < 6) {
            showMessage("Password must be at least 6 characters.", "red");
            return;
        }

        // SUCCESS
        String currentUser = GlobalData.getCurrentlyLoggedIN();
        DatabaseManager dm = new DatabaseManager();
        User user = dm.getUser(currentUser);
        user.changePassword(newPass);
        showMessage("Password Changed Successfully!", "green");

        // OPTIONAL: return to dashboard after 1.5 seconds
        javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));

        pause.setOnFinished(e -> {
            SceneController.switchTo("StudentDashboard.fxml");
        });

        pause.play();
    }

    // Handle Back button
    @FXML
    private void handleBack() {
        SceneController.switchTo("StudentDashboard.fxml");
    }


    // Utility method to show messages
    private void showMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setVisible(true);
    }
}
