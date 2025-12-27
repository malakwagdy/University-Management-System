package com.example.ums;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ChangePasswordController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private String userId;
    private Stage stage;

    public static void show(String userId) {
        try {
            FXMLLoader loader = new FXMLLoader(ChangePasswordController.class.getResource("ChangePassword.fxml"));
            Parent root = loader.load();

            ChangePasswordController controller = loader.getController();
            controller.initData(userId);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Change Password");
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initData(String userId) {
        this.userId = userId;
    }

    private void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleSavePassword() {
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showMessage("Both fields are required.", "red");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showMessage("Passwords do not match!", "red");
            return;
        }

        if (newPass.length() < 6) {
            showMessage("Password must be at least 6 characters.", "red");
            return;
        }

        try {
            DatabaseManager dm = new DatabaseManager();
            User user = dm.getUser(userId);
            if (user != null) {
                user.changePassword(newPass);
                showMessage("Password Changed Successfully!", "green");

                // Close after brief delay to show success message
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                        javafx.util.Duration.seconds(1.0));
                pause.setOnFinished(e -> {
                    if (stage != null)
                        stage.close();
                });
                pause.play();
            } else {
                showMessage("User not found!", "red");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error changing password.", "red");
        }
    }

    @FXML
    private void handleBack() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setVisible(true);
    }
}
