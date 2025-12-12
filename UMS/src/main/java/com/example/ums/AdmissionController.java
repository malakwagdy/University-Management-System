package com.example.ums;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.ChoiceBox;

public class AdmissionController implements Initializable {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField dateOfBirthField;

    @FXML
    private ChoiceBox<String> majorChoiceBox;

    @FXML
    private TextField highschoolGPAField;

    @FXML
    private Label errorMsg;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        majorChoiceBox.getItems().addAll("CESS",
            "COMM",
            "MCTA",
            "ERGY",
            "BLDG",
            "LAAR",
            "HOUD",
            "CISE",
            "MANF",
            "ENVR",
            "MATL");
    }

    @FXML
    private void handleApplyButton(ActionEvent event) {
        try {
            Admission.newAdmission(fullNameField.getText(), phoneNumberField.getText(), emailField.getText(), dateOfBirthField.getText(), majorChoiceBox.getValue(), highschoolGPAField.getText());
            SceneController.switchScene(event, "Login.fxml", "Login");
        } catch (IllegalArgumentException e) {
            errorMsg.setStyle("-fx-text-fill: red;");
            errorMsg.setText(e.getMessage());
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
