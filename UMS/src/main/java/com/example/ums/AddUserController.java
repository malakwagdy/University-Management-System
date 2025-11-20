package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddUserController implements Initializable {

    @FXML
    private ChoiceBox<String> userTypeChoiceBox;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField departmentField;

    @FXML
    private CheckBox departmentHeachCheckBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate the choice box with user types
        ObservableList<String> userTypes = FXCollections.observableArrayList(
            "Parent",
            "HR",
            "Admin",
            "Instructor"
        );
        userTypeChoiceBox.setItems(userTypes);
        
        // Initially disable Department Head checkbox and Department field
        departmentHeachCheckBox.setDisable(true);
        departmentField.setDisable(true);
        
        // Add listener to user type choice box
        userTypeChoiceBox.setOnAction(event -> {
            String selectedType = userTypeChoiceBox.getValue();
            if ("Instructor".equals(selectedType)) {
                // Enable Department Head checkbox and Department field for Instructor
                departmentHeachCheckBox.setDisable(false);
                departmentField.setDisable(false);
            } else {
                // Disable for other user types
                departmentHeachCheckBox.setDisable(true);
                departmentHeachCheckBox.setSelected(false);
                departmentField.setDisable(true);
            }
        });
    }

    // @FXML
    // private void handleDepartmentCheckBox(ActionEvent event) {
    //     // This method is called when the checkbox is toggled
    //     // The departmentHead boolean will be set when creating the Instructor instance
    // }
    
    @FXML
    private void handleAddUserButton(ActionEvent event) {
        String selectedType = userTypeChoiceBox.getValue();
        
        if (selectedType == null) {
            // Show error: user type not selected
            return;
        }
        
        // Get form data
        String name = fullNameField.getText();
        String phoneNumber = phoneNumberField.getText();
        String department = departmentField.getText();
        
        // Validate required fields
        if (name == null || name.trim().isEmpty() ||
            phoneNumber == null || phoneNumber.trim().isEmpty()) {
            // Show error: required fields missing
            return;
        }

        Admin admin = new Admin();      
        String currentUser = GlobalData.getCurrentlyLoggedIN();
        FirestoreManager fm = FirestoreManager.getInstance();
        admin = fm.getAdmin(currentUser);

        String password = "12345";
        
        switch (selectedType) {
            case "Instructor":
                boolean isDepartmentHead = departmentHeachCheckBox.isSelected();
                if (department == null || department.trim().isEmpty()) {
                    // Show error: department required for Instructor
                    return;
                }
                admin.createInstructor(phoneNumber, password, name, department, "add role here", isDepartmentHead);
                break;
                
            // case "Admin":
                // user = new Admin(id, phoneNumber, email, password, name, "0");
                // break;
                
            case "HR":
                admin.createHR(phoneNumber, password, name, "0");
                break;
                
            case "Parent":
                ArrayList<String> children = new ArrayList<>();
                admin.createParent(phoneNumber, password, name, "Parent", children);
                break;
        }
        
        // Clear form or navigate back
        try {
            SceneController.switchScene(event, "AdminDashboard.fxml", "Admin Dashboard");
        } catch (IOException e) {
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
