package com.example.ums;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ViewGradesInstructorController {

    @FXML
    private Label courseNameLabel;

    @FXML
    private TableView<GradeEntry> coursesTable; // Keeping ID as coursesTable per FXML

    @FXML
    private TableColumn<GradeEntry, String> nameCol;

    @FXML
    private TableColumn<GradeEntry, String> gradeCol;

    @FXML
    private TableColumn<GradeEntry, String> feedbackCol;

    private Stage stage;

    public static class GradeEntry {
        private final String name;
        private final String grade;
        private final String feedback;

        public GradeEntry(String name, String grade, String feedback) {
            this.name = name;
            this.grade = grade;
            this.feedback = feedback;
        }

        public String getName() {
            return name;
        }

        public String getGrade() {
            return grade;
        }

        public String getFeedback() {
            return feedback;
        }
    }

    public static void showForExam(int examId, int courseId, String courseName, Instructor instructor) {
        show(examId, -1, courseId, courseName, instructor, true);
    }

    public static void showForAssignment(int assignmentId, int courseId, String courseName, Instructor instructor) {
        show(-1, assignmentId, courseId, courseName, instructor, false);
    }

    private static void show(int examId, int assignmentId, int courseId, String courseName, Instructor instructor,
            boolean isExam) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ViewGradesInstructorController.class.getResource("ViewGradesInstructor.fxml"));
            Scene scene = new Scene(loader.load());

            ViewGradesInstructorController controller = loader.getController();
            controller.init(examId, assignmentId, courseId, courseName, instructor, isExam);

            Stage stage = new Stage();
            controller.stage = stage;
            stage.setTitle("View Grades - " + courseName);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init(int examId, int assignmentId, int courseId, String courseName, Instructor instructor,
            boolean isExam) {
        courseNameLabel.setText(courseName);

        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        gradeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGrade()));
        feedbackCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFeedback()));

        loadData(examId, assignmentId, courseId, instructor, isExam);
    }

    private void loadData(int examId, int assignmentId, int courseId, Instructor instructor, boolean isExam) {
        ObservableList<GradeEntry> entries = FXCollections.observableArrayList();

        // 1. Get all students in the course
        // Accessing DatabaseManager via Instructor instance method or creating new one?
        // Instructor.java has getCourseStudentsCount(int courseId) which uses
        // dm.getStudentsByCourse
        // But getStudentsByCourse is package-private or public in DatabaseManager?
        // Instructor has public ArrayList<Student> getStudentsByCourse(int courseId) ?
        // No, it has getCourseStudentsCount.
        // It has getInstructorStudents(String instructorId) which iterates courses.
        // Let's us dm directly or through instructor.
        // Instructor line 367: public int getCourseStudentsCount(int courseId) {
        // ArrayList<Student> students = dm.getStudentsByCourse(courseId); ... }
        // Attempting to access DatabaseManager. Because Instructor has 'static
        // DatabaseManager dm = new DatabaseManager();'
        // passing instructor is good. But we need a method in Instructor to get
        // students by course.
        // I'll assume we can use DatabaseManager directly as it seems to be used
        // loosely in other controllers, or add a helper to Instructor.
        // Actually, DatabaseManager seems to be instantiated in Controllers too.

        DatabaseManager dm = new DatabaseManager();
        ArrayList<Student> students = new ArrayList<>();
        try {
            students = dm.getStudentsByCourse(courseId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Get Maps of grades/feedback
        Map<String, String> gradesMap;
        Map<String, String> feedbackMap;

        if (isExam) {
            gradesMap = instructor.getExamGrades(examId);
            feedbackMap = instructor.getExamFeedback(examId);
        } else {
            gradesMap = instructor.getAssignmentGrades(assignmentId);
            feedbackMap = instructor.getAssignmentFeedback(assignmentId);
        }

        for (Student student : students) {
            String studentId = student.getId();
            String name = student.getName();
            String grade = gradesMap.getOrDefault(studentId, "N/A");
            String feedback = feedbackMap.getOrDefault(studentId, "N/A");

            entries.add(new GradeEntry(name, grade, feedback));
        }

        coursesTable.setItems(entries);
    }

    @FXML
    private void handleDoneButton() {
        stage.close();
    }
}
