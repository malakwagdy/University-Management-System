package com.example.ums;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ViewTranscriptPopupController {
    @FXML
    private Label studentIdLabel;
    
    @FXML
    private Label studentNameLabel;

    @FXML
    private Label studentGpaLabel;
    
    @FXML
    private TableView<CourseRecord> coursesTable;
    
    @FXML
    private TableColumn<CourseRecord, String> courseIdCol;
    
    @FXML
    private TableColumn<CourseRecord, String> courseNameCol;
    
    @FXML
    private TableColumn<CourseRecord, String> gradeCol;
    
    private DatabaseManager dm = new DatabaseManager();
    private Student currentStudent;
    private Stage stage;
    
    public static void show(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewTranscriptPopupController.class.getResource("ViewTranscriptPopup.fxml"));
            Scene scene = new Scene(loader.load());
            
            ViewTranscriptPopupController controller = loader.getController();
            controller.currentStudent = student;
            controller.populateData(student);
            
            Stage stage = new Stage();
            controller.stage = stage;
            stage.setTitle("Student Transcript");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void populateData(Student student) {
        studentIdLabel.setText(student.getId());
        studentNameLabel.setText(student.getName());
        studentGpaLabel.setText(String.valueOf(student.getGpa()));
        
        courseIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseId()));
        courseNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        gradeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGrade()));
        
        ObservableList<CourseRecord> records = FXCollections.observableArrayList();
        
        // Add taken courses with grades
        if (student.getTakenCourses() != null) {
            for (Map.Entry<Integer, String> entry : student.getTakenCourses().entrySet()) {
                    Course course = dm.getCourse(entry.getKey());
                    String courseName = course != null ? course.getCourseName() : "Unknown";
                    records.add(new CourseRecord(String.valueOf(entry.getKey()), courseName, entry.getValue()));
            }
        }
        
        // Add current courses as "In Progress"
        if (student.getCurrentCourses() != null) {
            for (String courseIdStr : student.getCurrentCourses()) {
                try {
                    int courseId = Integer.parseInt(courseIdStr);
                    Course course = dm.getCourse(courseId);
                    String courseName = course != null ? course.getCourseName() : "Unknown";
                    records.add(new CourseRecord(courseIdStr, courseName, "In Progress"));
                } catch (NumberFormatException e) {
                    records.add(new CourseRecord(courseIdStr, "Unknown", "In Progress"));
                }
            }
        }
        
        coursesTable.setItems(records);
    }
    
    @FXML
    public void handleDownloadButton(ActionEvent e){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transcript");
        fileChooser.setInitialFileName("Transcript_" + currentStudent.getId() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("STUDENT TRANSCRIPT\n");
                writer.write("==================\n\n");
                writer.write("Student ID: " + currentStudent.getId() + "\n");
                writer.write("Student Name: " + currentStudent.getName() + "\n");
                writer.write("GPA: " + currentStudent.getGpa() + "\n\n");
                writer.write("COURSES\n");
                writer.write("-------\n");
                
                for (CourseRecord record : coursesTable.getItems()) {
                    writer.write(String.format("%-15s %-30s %s\n", 
                        record.getCourseId(), 
                        record.getCourseName(), 
                        record.getGrade()));
                }
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Transcript saved successfully!");
                alert.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Failed to save transcript: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    @FXML
    public void handleCancelButton(ActionEvent e){
        stage.close();
    }
    
    public static class CourseRecord {
        private final String courseId;
        private final String courseName;
        private final String grade;
        
        public CourseRecord(String courseId, String courseName, String grade) {
            this.courseId = courseId;
            this.courseName = courseName;
            this.grade = grade;
        }
        
        public String getCourseId() { return courseId; }
        public String getCourseName() { return courseName; }
        public String getGrade() { return grade; }
    }
}
