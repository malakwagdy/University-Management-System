package com.example.ums;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

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

        try {
            user.Login(email, password);
            userID = GlobalData.getCurrentlyLoggedIN();
            errorMessage.setStyle("-fx-text-fill: green;");
            errorMessage.setText("Login Successful!");
            // Create a PauseTransition with a delay of 2 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            if(isInstructor){
                delay.setOnFinished(e -> {
                    try {
                        SceneController.switchScene(event, "InstructorDashboard.fxml", "Instructor Dashboard");
//                        MainVendorPageController.initialize();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            else if(isStudent){
                delay.setOnFinished(e -> {
                    try {
                        SceneController.switchScene(event, "StudentDashboard.fxml", "Student Dashboard");
//                        MainVendorPageController.initialize();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            else if(isHr){
                delay.setOnFinished(e -> {
                    try {
                        SceneController.switchScene(event, "HrDashboard.fxml", "Hr Dashboard");
//                        MainVendorPageController.initialize();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            else if(isDepartmentHead){
                delay.setOnFinished(e -> {
                    try {
                        SceneController.switchScene(event, "DeptHeadDashboard.fxml", "Department Head Dashboard");
//                        MainVendorPageController.initialize();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            else if(isParent) {
                delay.setOnFinished(e -> {
                    try {
                        SceneController.switchScene(event, "ParentDashboard.fxml", "Parent Dashboard");
//                        MainVendorPageController.initialize();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            else {
                delay.setOnFinished(e -> {
                    try {
                        SceneController.switchScene(event, "AdminDashboard.fxml", "Admin Dashboard");
//                        MainVendorPageController.initialize();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            delay.play();
        } catch (LoginException e) {
            // Show error dialog
            errorMessage.setStyle("-fx-text-fill: red;");
            errorMessage.setText(e.getMessage());
        }
    }
}
