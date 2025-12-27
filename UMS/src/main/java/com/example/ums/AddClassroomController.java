package com.example.ums;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

import static com.example.ums.ViewClassroomsController.showAlert;

public class AddClassroomController {

    @FXML
    private TextField nhall_id;
    @FXML
    private TextField nhall_capacity;
    @FXML
    private TextField nhall_type;
    @FXML
    private TextField nhall_maintenance;
    @FXML
    private TextField nhall_availability;


    private boolean isInteger(String value) {
        return value.matches("\\d+");
    }

    private boolean isAlphabetic(String value) {
        return value.matches("[a-zA-Z ]+");
    }

    @FXML
    public void handleAddclassroomButton(ActionEvent event) {
        //int hallId = Integer.parseInt(nhall_id.getText());
        String hallCapacity = nhall_capacity.getText();
        String hallType = nhall_type.getText();
        //boolean hallMaintenance = Boolean.parseBoolean(nhall_maintenance.getText());
        //boolean availability = true; // default

        if (hallCapacity.isEmpty() || hallType.isEmpty()) {
            showAlert("Validation Error", "All fields are required");
            return;
        }


        // 🔴 hallcapacity: varchar(6), numeric
        if (!isInteger(hallCapacity)) {
            showAlert("Validation Error", "Hall capacity must be numeric");
            return;
        }

        if (hallCapacity.length() > 6) {
            showAlert("Validation Error", "Hall capacity cannot exceed 6 digits");
            return;
        }

        // 🔴 halltype: varchar(50)
        if (!isAlphabetic(hallType)) {
            showAlert("Validation Error", "Hall type must contain letters only");
            return;
        }

        if (hallType.length() > 50) {
            showAlert("Validation Error", "Hall type cannot exceed 50 characters");
            return;
        }


        Classroom classroom = new Classroom(
                hallCapacity,
                hallType
        );

        try {
            DatabaseManager.AddClassroom(classroom);
            showAlert("Success", "Classroom added successfully");
        } catch (SQLException e) {
            showAlert("Error", "Failed to add classroom");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            SceneController.switchScene(event, "AdminDashboard.fxml", "Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}