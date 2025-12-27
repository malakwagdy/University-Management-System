package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ViewGradesStudentController implements Initializable {

    private static int pendingCourseId = -1;
    
    @FXML
    private Label courseNameLabel;
    
    @FXML
    private TableView<GradeItem> coursesTable;
    
    @FXML
    private TableColumn<GradeItem, String> nameCol;
    
    @FXML
    private TableColumn<GradeItem, String> gradeCol;
    
    @FXML
    private TableColumn<GradeItem, String> feedbackCol;

    private int courseId;
    private Student student = new Student();

    public static class GradeItem {
        private String name;
        private String grade;
        private String feedback;

        public GradeItem(String name, String grade, String feedback) {
            this.name = name;
            this.grade = grade != null ? grade : "N/A";
            this.feedback = feedback != null ? feedback : "No feedback";
        }

        public String getName() { return name; }
        public String getGrade() { return grade; }
        public String getFeedback() { return feedback; }
    }

    public static void setCurrentCourseId(int courseId) {
        pendingCourseId = courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
        loadGrades();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        feedbackCol.setCellValueFactory(new PropertyValueFactory<>("feedback"));
        
        if (pendingCourseId != -1) {
            setCourseId(pendingCourseId);
            pendingCourseId = -1;
        }
    }

    private void loadGrades() {
        Course course = Course.getCourseById(courseId);
        if (course != null) {
            courseNameLabel.setText(course.getCourseName());
        }

        String userId = GlobalData.getCurrentlyLoggedIN();
        ObservableList<GradeItem> gradeItems = FXCollections.observableArrayList();

        // Get course grades and feedback (includes both assignments and exams)
        var grades = student.getStudentCourseGrades(userId, courseId);
        var feedback = student.getStudentCourseFeedback(userId, courseId);
        
        for (String itemName : grades.keySet()) {
            String grade = grades.get(itemName);
            String feedbackText = feedback.get(itemName);
            gradeItems.add(new GradeItem(itemName, grade, feedbackText));
        }

        coursesTable.setItems(gradeItems);
    }

    @FXML
    private void handleDoneButton() {
        Stage stage = (Stage) coursesTable.getScene().getWindow();
        stage.close();
    }
}