package com.example.ums;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class EditCourseController {
    @FXML
    private Label courseIdLabel;
    
    @FXML
    private TextField courseNameField;
    
    @FXML
    private TextField bylawField;
    
    @FXML
    private TextArea courseDescriptionArea;
    
    private Course course;
    private User currentUser;
    private Runnable onSaveCallback;
    private Stage stage;

    public static void show(Course course, User currentUser, Runnable onSaveCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(EditCourseController.class.getResource("EditCourse.fxml"));
            Scene scene = new Scene(loader.load());
            
            EditCourseController controller = loader.getController();
            controller.course = course;
            controller.currentUser = currentUser;
            controller.onSaveCallback = onSaveCallback;
            controller.populateFields();
            
            Stage stage = new Stage();
            controller.stage = stage;
            stage.setTitle("Edit Course");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void populateFields() {
        courseIdLabel.setText(String.valueOf(course.getCourseId()));
        courseNameField.setText(course.getCourseName());
        bylawField.setText(course.getYear());
        courseDescriptionArea.setText(course.getCourseDescription());
    }
    
    @FXML
    private void handleSaveButton() {
        String courseName = courseNameField.getText().trim();
        String bylaw = bylawField.getText().trim();
        String description = courseDescriptionArea.getText().trim();
        
        if (courseName.isEmpty() || bylaw.isEmpty()) {
            showAlert("Validation Error", "Course name and bylaw are required.");
            return;
        }
        
        if (currentUser instanceof Admin) {
            ((Admin) currentUser).editCourseDetails(course.getCourseId(), courseName, description, bylaw);
        } else if (currentUser instanceof Instructor) {
            ((Instructor) currentUser).editCourseDetails(course.getCourseId(), courseName, description, bylaw);
        }
        
        if (onSaveCallback != null) {
            onSaveCallback.run();
        }
        
        stage.close();
    }
    
    @FXML
    private void handleCancelButton() {
        stage.close();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
