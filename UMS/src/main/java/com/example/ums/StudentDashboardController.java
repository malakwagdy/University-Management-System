package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentDashboardController {

    @FXML
    private Label studentNameLabel;
    
    @FXML
    private Label gpaLabel;
    
    @FXML
    private Label coursesCountLabel;
    
    @FXML
    private Label semesterLabel;

    @FXML
    private Button logoutBtn;
    
    @FXML
    private TableView<CourseInfo> coursesTable;
    
    @FXML
    private TableColumn<CourseInfo, String> courseIdCol;
    
    @FXML
    private TableColumn<CourseInfo, String> courseNameCol;
    
    @FXML
    private TableColumn<CourseInfo, String> instructorCol;
    
    @FXML
    private TableColumn<CourseInfo, String> gradeCol;

    // Inner class to represent course information for the table
    public static class CourseInfo {
        private String courseId;
        private String courseName;
        private String instructor;
        private String grade;
        
        public CourseInfo(String courseId, String courseName, String instructor, String grade) {
            this.courseId = courseId;
            this.courseName = courseName;
            this.instructor = instructor;
            this.grade = grade;
        }
        
        public String getCourseId() { return courseId; }
        public String getCourseName() { return courseName; }
        public String getInstructor() { return instructor; }
        public String getGrade() { return grade; }
    }

    @FXML
    private void initialize() {
        Student student = Student.getCurrentStudent();
        if (student != null) {
            studentNameLabel.setText("Welcome, " + student.getName());
            
            // Update student info labels
            gpaLabel.setText(student.getGpa() != null ? student.getGpa() : "N/A");
            semesterLabel.setText(student.getSemester() != null ? student.getSemester() : "N/A");
            
            // Setup courses table
            setupCoursesTable();
            loadStudentCourses(student);
        }
    }
    
    private void setupCoursesTable() {
        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        instructorCol.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
    }
    
    private void loadStudentCourses(Student student) {
        ObservableList<CourseInfo> courseList = FXCollections.observableArrayList();
        
        try {
            // Load current courses using DatabaseManager method
            ArrayList<String> currentCourses = Student.getCurrentCoursesForStudent(student.getId());
            if (currentCourses != null) {
                for (String courseIdStr : currentCourses) {
                    // Get course details
                    Course course = Course.getCourseById(Integer.parseInt(courseIdStr));
                    String courseName = course != null ? course.getCourseName() : "Course " + courseIdStr;
                    
                    // Get instructors for this course
                    ArrayList<Instructor> instructors = Course.getCourseInstructors(courseIdStr);
                    String instructorNames = instructors.isEmpty() ? "TBD" : 
                        String.join(", ", instructors.stream().map(Instructor::getName).toArray(String[]::new));
                    
                    courseList.add(new CourseInfo(
                        courseIdStr,
                        courseName,
                        instructorNames,
                        "In Progress"
                    ));
                }
            }
            
            coursesTable.setItems(courseList);
            coursesCountLabel.setText(String.valueOf(courseList.size()));
            
        } catch (Exception e) {
            e.printStackTrace();
            coursesCountLabel.setText("0");
        }
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        User.Logout();
        try {
            SceneController.switchScene(event, "Login.fxml", "Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleChangePassword(ActionEvent actionEvent) {
        try {
            SceneController.switchScene(actionEvent, "ChangePassword.fxml", "Change Password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
