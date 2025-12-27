package com.example.ums;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AddGradesController {
    @FXML
    private TextField studentIdTextField;
    @FXML
    private TextField gradeTextField;
    @FXML
    private TextArea feedbackTextArea;

    private Instructor currentInstructor;
    private Runnable onSaveCallback;
    private Stage stage;

    // For exams
    private Integer examId;

    // For assignments
    private Integer assignmentId;

    // Type indicator
    private GradeType gradeType;

    public enum GradeType {
        EXAM,
        ASSIGNMENT
    }

    /**
     * Show the AddGrades popup for an exam
     */
    public static void showForExam(int examId, Instructor currentInstructor, Runnable onSaveCallback) {
        show(examId, null, GradeType.EXAM, currentInstructor, onSaveCallback);
    }

    /**
     * Show the AddGrades popup for an assignment
     */
    public static void showForAssignment(int assignmentId, Instructor currentInstructor, Runnable onSaveCallback) {
        show(null, assignmentId, GradeType.ASSIGNMENT, currentInstructor, onSaveCallback);
    }

    /**
     * Internal method to show the popup
     */
    private static void show(Integer examId, Integer assignmentId, GradeType gradeType,
            Instructor currentInstructor, Runnable onSaveCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(AddGradesController.class.getResource("AddGrades.fxml"));
            Scene scene = new Scene(loader.load());

            AddGradesController controller = loader.getController();
            controller.examId = examId;
            controller.assignmentId = assignmentId;
            controller.gradeType = gradeType;
            controller.currentInstructor = currentInstructor;
            controller.onSaveCallback = onSaveCallback;

            Stage stage = new Stage();
            controller.stage = stage;

            String title = gradeType == GradeType.EXAM ? "Add Exam Grade" : "Add Assignment Grade";
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        // No initialization needed for this controller
    }

    @FXML
    private void handleCancelBtn() {
        stage.close();
    }

    @FXML
    private void handleSaveBtn() {
        // Validate inputs
        String studentId = studentIdTextField.getText().trim();
        String grade = gradeTextField.getText().trim();
        String feedback = feedbackTextArea.getText().trim();

        if (studentId.isEmpty()) {
            showAlert("Validation Error", "Please enter a student ID.");
            return;
        }

        if (grade.isEmpty()) {
            showAlert("Validation Error", "Please enter a grade.");
            return;
        }

        // Feedback is optional, so we don't validate it

        try {
            if (gradeType == GradeType.EXAM) {
                // Add exam grade and feedback
                currentInstructor.addExamGrade(examId, studentId, grade);

                if (!feedback.isEmpty()) {
                    currentInstructor.addExamFeedback(examId, studentId, feedback);
                }

                showAlert("Success", "Exam grade and feedback added successfully!");
            } else {
                // Add assignment grade and feedback
                currentInstructor.addAssignmentGrade(assignmentId, studentId, grade);

                if (!feedback.isEmpty()) {
                    currentInstructor.addAssignmentFeedback(assignmentId, studentId, feedback);
                }

                showAlert("Success", "Assignment grade and feedback added successfully!");
            }

            // Run callback if provided
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            stage.close();
        } catch (Exception e) {
            showAlert("Error", "Failed to add grade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
