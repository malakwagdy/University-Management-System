package com.example.ums;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    static String userID;
    static boolean isInstructor= false;
    static boolean isStudent= false;
    static boolean isHr= false;
    static boolean isDepartmentHead= false;
    static boolean isParent= false;
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorMessage;

    private User user = new User();

    @FXML
    private void handleLoginButton(ActionEvent event) throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();

        errorMessage.setText("Logging in...");
        errorMessage.setStyle("-fx-text-fill: blue;");

        Task<String> loginTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                user.Login(email, password);
                userID = GlobalData.getCurrentlyLoggedIN();
                
                javafx.application.Platform.runLater(() -> {
                    errorMessage.setStyle("-fx-text-fill: green;");
                    errorMessage.setText("Login Successful!");
                });
                
                if(isInstructor) return "InstructorDashboard.fxml";
                else if(isStudent) return "StudentDashboard.fxml";
                else if(isHr) return "HrDashboard.fxml";
                else if(isDepartmentHead) return "DeptHeadDashboard.fxml";
                else if(isParent) return "ParentDashboard.fxml";
                else return "AdminDashboard.fxml";
            }
        };

        loginTask.setOnSucceeded(e -> {
            try {
                String fxml = loginTask.getValue();
                String title = fxml.replace(".fxml", "").replace("Dashboard", " Dashboard");
                SceneController.switchScene(event, fxml, title);
            } catch (IOException ex) {
                errorMessage.setStyle("-fx-text-fill: red;");
                errorMessage.setText("Failed to load dashboard");
                ex.printStackTrace();
            }
        });

        loginTask.setOnFailed(e -> {
            errorMessage.setStyle("-fx-text-fill: red;");
            Throwable exception = loginTask.getException();
            errorMessage.setText(exception.getMessage());
            exception.printStackTrace();
        });

        new Thread(loginTask).start();
    }

    @FXML
    private void handleApplyHyperlink(ActionEvent event) {
        try {
            SceneController.switchScene(event, "Admission.fxml", "Apply");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
