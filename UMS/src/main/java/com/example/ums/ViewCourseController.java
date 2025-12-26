package com.example.ums;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ViewCourseController {
    @FXML
    private Label courseIdLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label bylawLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label professorLabel;
    @FXML
    private Label taLabel;
    @FXML
    private Label materialsLabel;
    @FXML
    private Button saveBtn;
    @FXML
    private Button closeBtn;
    
    private Course course;
    private Stage popupStage;
    
    public static void show(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewCourseController.class.getResource("ViewCourse.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("View Course");
            stage.initModality(Modality.APPLICATION_MODAL);
            
            ViewCourseController controller = loader.getController();
            controller.setCourse(course);
            controller.setPopupStage(stage);
            
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setCourse(Course course) {
        this.course = course;
        populateCourseDetails();
    }
    
    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }
    
    @FXML
    private void initialize() {
        saveBtn.setVisible(false);
        saveBtn.setManaged(false);
    }
    
    private void populateCourseDetails() {
        if (course == null) return;
        
        courseIdLabel.setText(String.valueOf(course.getCourseId()));
        nameLabel.setText(course.getCourseName());
        bylawLabel.setText(course.getYear());
        descriptionLabel.setText(course.getCourseDescription());
        
        ArrayList<Instructor> instructors = Course.getCourseInstructors(course.getCourseId());
        StringBuilder professors = new StringBuilder();
        StringBuilder tas = new StringBuilder();
        
        for (Instructor instructor : instructors) {
            if ("Professor".equals(instructor.getRole())) {
                if (professors.length() > 0) professors.append(", ");
                professors.append(instructor.getName());
            } else if ("Teaching Assistant".equals(instructor.getRole())) {
                if (tas.length() > 0) tas.append(", ");
                tas.append(instructor.getName());
            }
        }
        
        professorLabel.setText(professors.length() > 0 ? professors.toString() : "N/A");
        taLabel.setText(tas.length() > 0 ? tas.toString() : "N/A");
        
        ArrayList<Material> materials = course.getCourseMaterials(course.getCourseId());
        if (materials != null && !materials.isEmpty()) {
            StringBuilder materialsStr = new StringBuilder();
            for (Material material : materials) {
                if (materialsStr.length() > 0) materialsStr.append(", ");
                materialsStr.append(material.getMaterialName());
            }
            materialsLabel.setText(materialsStr.toString());
        } else {
            materialsLabel.setText("N/A");
        }
    }
    
    @FXML
    private void handleSaveButton() {
        // Not used in view mode
    }
    
    @FXML
    private void handleCloseButton() {
        if (popupStage != null) {
            popupStage.close();
        }
    }
}
