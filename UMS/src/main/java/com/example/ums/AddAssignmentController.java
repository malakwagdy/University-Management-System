package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class AddAssignmentController {
    @FXML
    private TextField assignmentNameField;
    @FXML
    private DatePicker deadlineDatePicker;
    @FXML
    private TextField materialFileField;

    private int courseId;
    private Instructor currentUser;
    private Runnable onSaveCallback;
    private Stage stage;
    private String selectedFilePath = null;

    private String dateOfBirth;

    @FXML
    private void handleDatePicker(ActionEvent event) {

        if (deadlineDatePicker == null) {
            System.out.println("DOB Picker is NULL - check fx:id");
            return;
        }

        LocalDate dob = deadlineDatePicker.getValue();

        if (dob == null) {
            dateOfBirth = null;
            return;
        }

        dateOfBirth = dob.toString(); // yyyy-MM-dd
        System.out.println("DOB selected: " + dateOfBirth);
    }

    public static void show(int courseId, Instructor currentUser, Runnable onSaveCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(AddAssignmentController.class.getResource("AddAssignment.fxml"));
            Scene scene = new Scene(loader.load());

            AddAssignmentController controller = loader.getController();
            controller.courseId = courseId;
            controller.currentUser = currentUser;
            controller.onSaveCallback = onSaveCallback;

            Stage stage = new Stage();
            controller.stage = stage;
            stage.setTitle("Add Assignment");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBrowseAssign(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Assignment File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            selectedFilePath = file.getAbsolutePath();
            materialFileField.setText(file.getName());
        }
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        String assignmentName = assignmentNameField.getText().trim();

        if (assignmentName.isEmpty() || selectedFilePath == null) {
            showAlert("Validation Error", "Please add assignment details.");
            return;
        }


        Assignment assignment = new Assignment(assignmentName, selectedFilePath, dateOfBirth, courseId);
        currentUser.createAssignment(courseId, assignment);

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }

        stage.close();
    }


    @FXML
    public void handleCancelButton(ActionEvent event) {
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
