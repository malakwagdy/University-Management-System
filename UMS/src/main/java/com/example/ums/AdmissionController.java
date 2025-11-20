package com.example.ums;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import javafx.scene.control.TextField;

public class AdmissionController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField dateOfBirthField;

    @FXML
    private TextField majorField;

    @FXML
    private TextField highschoolGPAField;
    @FXML
    private void handleApplyButton(ActionEvent event) {
        Admission.newAdmission(fullNameField.getText(), phoneNumberField.getText(), emailField.getText(), dateOfBirthField.getText(), majorField.getText(), highschoolGPAField.getText());
        try {
            SceneController.switchScene(event, "Login.fxml", "Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            SceneController.switchScene(event, "Login.fxml", "Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
