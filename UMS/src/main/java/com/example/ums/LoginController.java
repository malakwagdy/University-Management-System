package com.example.ums;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    static boolean isAdmin = false;
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
    private Button loginBtn;
    
    @FXML
    private Label errorMessage;

    private User user = new User();

    @FXML
    private void handleLoginButton(ActionEvent event) throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();
        
        // TODO: Add your login validation logic here
        // For example, using your User class and FirestoreManager
        
//        try {
//            // Example: Validate login
//            // User user = new User(...);
//            // user.Login(email, password);
//
//            // If login successful, navigate to next screen
//            // The maximized state will be maintained automatically!
//            navigateToDashboard();
//
//        } catch (Exception e) {
//            errorMessage.setText("Login failed: " + e.getMessage());
//            errorMessage.getStyleClass().add("error");
//        }

        try {
            user.Login(email, password);
            email = GlobalData.getCurrentlyLoggedIN();
            errorMessage.setStyle("-fx-text-fill: green;");
            errorMessage.setText("Login Successful!");
            // Create a PauseTransition with a delay of 2 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            if(isAdmin){
                delay.setOnFinished(e -> {
                    try {
                        SceneController.switchScene(event, "AdminDashboard.fxml", "Admin Dashboard");
//                        MainVendorPageController.initialize();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            else if(isInstructor){
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
            delay.play();
//            SceneController.switchScene(event, GlobalData.path + "MainPageClient.fxml", "Homepage");

        } catch (LoginException e) {
            // Show error dialog
            errorMessage.setText(e.getMessage());
        }
    }
    
//    private void navigateToDashboard() {
//        try {
//            // Example: Navigate to dashboard
//            // Replace "Dashboard.fxml" with your actual dashboard FXML file
//            // When you implement navigation, call SceneController.switchScene("Dashboard.fxml", "Dashboard - UMS");
//
//            // For now, just show a message
//            errorMessage.setText("Login successful! (Dashboard navigation not yet implemented)");
//            errorMessage.getStyleClass().remove("error");
//
//        } catch (Exception e) {
//            errorMessage.setText("Error navigating to dashboard: " + e.getMessage());
//            errorMessage.getStyleClass().add("error");
//        }
//    }
}
