package com.example.ums;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

public class AddCourseController {

    @FXML
    private TextField courseNameField;
    @FXML
    private TextField bylawField;
    @FXML
    private TextArea courseDescriptionTextArea;

    @FXML
    private void handleAddCourseButton(ActionEvent event) {
        String courseName = courseNameField.getText();
        String bylaw = bylawField.getText();
        String courseDescription = courseDescriptionTextArea.getText();

        // Validate input
        if (courseName.isEmpty() || bylaw.isEmpty() || courseDescription.isEmpty()) {
            // Handle empty fields
            return;
        }

        Admin admin = new Admin();
        String currentUser = GlobalData.getCurrentlyLoggedIN();
        DatabaseManager dm = new DatabaseManager();
        try {
            admin = dm.getAdmin(currentUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add course to the database
        admin.addCourse(courseName, courseDescription, bylaw);

        try {
            SceneController.switchScene(event, "AdminDashboard.fxml", "Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleCancelButton(ActionEvent event) {
        try {
            SceneController.switchScene(event, "AdminDashboard.fxml", "Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
