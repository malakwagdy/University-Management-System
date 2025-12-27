package com.example.ums;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddExamController {
    @FXML
    private ComboBox<String> examTypeComboBox;
    @FXML
    private DatePicker examDatePicker;

    private int courseId;
    private Instructor currentInstructor;
    private Runnable onSaveCallback;
    private Stage stage;

    public static void show(int courseId, Instructor currentInstructor, Runnable onSaveCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(AddExamController.class.getResource("AddExam.fxml"));
            Scene scene = new Scene(loader.load());

            AddExamController controller = loader.getController();
            controller.courseId = courseId;
            controller.currentInstructor = currentInstructor;
            controller.onSaveCallback = onSaveCallback;

            Stage stage = new Stage();
            controller.stage = stage;
            stage.setTitle("Add Exam");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        // Populate exam type combo box with available types
        examTypeComboBox.getItems().addAll("Quiz", "Midterm", "Final");
    }

    @FXML
    private void handleCancelBtn() {
        stage.close();
    }

    @FXML
    private void handleSaveBtn() {
        // Validate inputs
        String selectedType = examTypeComboBox.getValue();
        LocalDate selectedDate = examDatePicker.getValue();

        if (selectedType == null || selectedType.isEmpty()) {
            showAlert("Validation Error", "Please select an exam type.");
            return;
        }

        if (selectedDate == null) {
            showAlert("Validation Error", "Please select an exam date.");
            return;
        }

        // Convert LocalDate to String format (YYYY-MM-DD)
        String examDateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Convert string to ExamType enum
        Exam.ExamType examType = null;
        switch (selectedType) {
            case "Quiz":
                examType = Exam.ExamType.QUIZ;
                break;
            case "Midterm":
                examType = Exam.ExamType.MIDTERM;
                break;
            case "Final":
                examType = Exam.ExamType.FINAL;
                break;
        }

        // Create exam object
        Exam exam = new Exam(courseId, examDateStr, examType);

        // Call appropriate instructor method based on exam type
        try {
            switch (examType) {
                case QUIZ:
                    currentInstructor.addQuiz(exam);
                    break;
                case MIDTERM:
                    currentInstructor.addMidterm(exam);
                    break;
                case FINAL:
                    currentInstructor.addFinal(exam);
                    break;
            }

            showAlert("Success", "Exam added successfully!");

            // Run callback if provided
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            stage.close();
        } catch (Exception e) {
            showAlert("Error", "Failed to add exam: " + e.getMessage());
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
