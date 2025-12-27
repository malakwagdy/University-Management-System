package com.example.ums;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddOfficeHoursController {

    @FXML
    private ComboBox<String> dayComboBox;

    @FXML
    private ComboBox<String> hourComboBox;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    private Instructor currentInstructor;
    private Runnable onSaveCallback;
    private Stage stage;

    public static void show(Instructor instructor, Runnable onSaveCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(AddOfficeHoursController.class.getResource("AddOfficeHours.fxml"));
            Scene scene = new Scene(loader.load());

            AddOfficeHoursController controller = loader.getController();
            controller.currentInstructor = instructor;
            controller.onSaveCallback = onSaveCallback;

            Stage stage = new Stage();
            controller.stage = stage;
            controller.currentInstructor = instructor;
            stage.setTitle("Add Office Hours");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        // Populate dayComboBox with weekdays
        dayComboBox.getItems().addAll(
                Arrays.asList("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"));

        // Populate hourComboBox with time slots
        hourComboBox.getItems().addAll(
                "12 PM - 1 PM",
                "1 PM - 2 PM",
                "2 PM - 3 PM",
                "3 PM - 4 PM",
                "4 PM - 5 PM");
    }

    @FXML
    private void handleCancelButton() {
        // Close the popup window
        stage.close();
    }

    @FXML
    private void handleSaveButton() {
        // Get selected values
        String selectedDay = dayComboBox.getValue();
        String selectedHour = hourComboBox.getValue();

        // Validate selections
        if (selectedDay == null || selectedDay.isEmpty()) {
            showAlert("Validation Error", "Please select a day.");
            return;
        }

        if (selectedHour == null || selectedHour.isEmpty()) {
            showAlert("Validation Error", "Please select an hour.");
            return;
        }

        if (currentInstructor == null) {
            showAlert("Error", "No instructor set. Cannot save office hours.");
            return;
        }

        // Create a map with the selected day and hour
        Map<String, String> officeHours = new HashMap<>();
        officeHours.put(selectedDay, selectedHour);

        // Call the instructor's addOfficeHours method
        currentInstructor.addOfficeHours(currentInstructor.getId(), officeHours);

        // Show success message
        showAlert("Success", "Office hours added successfully!");

        // Run callback if provided
        if (onSaveCallback != null) {
            onSaveCallback.run();
        }

        // Close the popup window
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
