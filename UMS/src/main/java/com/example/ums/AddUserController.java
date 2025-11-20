package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.control.Label;

public class AddUserController implements Initializable {

    @FXML
    private ChoiceBox<String> userTypeChoiceBox;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private ChoiceBox<String> departmentChoiceBox;

    @FXML
    private CheckBox departmentHeachCheckBox;

    @FXML
    private ChoiceBox<String> userRoleChoiceBox;
    
    @FXML
    private HBox roleHBox;
    
    @FXML
    private HBox departmentHBox;
    
    @FXML
    private HBox departmentHeadHBox;

    @FXML
    private ChoiceBox<String> relationChoiceBox;

    @FXML
    private HBox relationHBox;

    @FXML
    private TextField childIdField;

    @FXML
    private HBox childIdHBox;

    @FXML
    private Label errorMsg;

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

        ObservableList<String> userRoles = FXCollections.observableArrayList(
            "Teaching Assistant",
            "Professor"
        );
        userRoleChoiceBox.setItems(userRoles);

        ObservableList<String> departments = FXCollections.observableArrayList(
            "CESS",
            "COMM",
            "MCTA",
            "ERGY",
            "BLDG",
            "LAAR",
            "HOUD",
            "CISE",
            "MANF",
            "ENVR",
            "MATL"
        );
        departmentChoiceBox.setItems(departments);

        ObservableList<String> relation = FXCollections.observableArrayList(
            "Father",
            "Mother"
        );
        relationChoiceBox.setItems(relation);
        
        // Initially hide and disable Department Head checkbox, Department field, and Role field
        departmentHeachCheckBox.setDisable(true);
        departmentChoiceBox.setDisable(true);
        userRoleChoiceBox.setDisable(true);

        
        // Hide the HBox containers initially
        if (roleHBox != null) {
            roleHBox.setVisible(false);
            roleHBox.setManaged(false);
        }
        if (departmentHBox != null) {
            departmentHBox.setVisible(false);
            departmentHBox.setManaged(false);
        }
        if (departmentHeadHBox != null) {
            departmentHeadHBox.setVisible(false);
            departmentHeadHBox.setManaged(false);
        }
        if (relationHBox != null) {
            relationHBox.setVisible(false);
            relationHBox.setManaged(false);
        }
        if (childIdHBox != null) {
            childIdHBox.setVisible(false);
            childIdHBox.setManaged(false);
        }
        
        // Add listener to user type choice box
        userTypeChoiceBox.setOnAction(event -> {
            String selectedType = userTypeChoiceBox.getValue();
            if ("Instructor".equals(selectedType)) {
                // Show and enable Department Head checkbox, Department field, and Role field for Instructor
                if (roleHBox != null) {
                    roleHBox.setVisible(true);
                    roleHBox.setManaged(true);
                }
                if (departmentHBox != null) {
                    departmentHBox.setVisible(true);
                    departmentHBox.setManaged(true);
                }
                if (departmentHeadHBox != null) {
                    departmentHeadHBox.setVisible(true);
                    departmentHeadHBox.setManaged(true);
                }
                if (relationHBox != null) {
                    relationHBox.setVisible(false);
                    relationHBox.setManaged(false);
                }
                if (childIdHBox != null) {
                    childIdHBox.setVisible(false);
                    childIdHBox.setManaged(false);
                }
                departmentHeachCheckBox.setDisable(false);
                departmentChoiceBox.setDisable(false);
                userRoleChoiceBox.setDisable(false);
            } else if ("Parent".equals(selectedType)) {
                // Show and enable Relation field and Child ID field for Parent
                if (roleHBox != null) {
                    roleHBox.setVisible(false);
                    roleHBox.setManaged(false);
                }
                if (departmentHBox != null) {
                    departmentHBox.setVisible(false);
                    departmentHBox.setManaged(false);
                }
                if (departmentHeadHBox != null) {
                    departmentHeadHBox.setVisible(false);
                    departmentHeadHBox.setManaged(false);
                }
                if (relationHBox != null) {
                    relationHBox.setVisible(true);
                    relationHBox.setManaged(true);
                }
                if (childIdHBox != null) {
                    childIdHBox.setVisible(true);
                    childIdHBox.setManaged(true);
                }
                relationChoiceBox.setDisable(false);
                childIdField.setDisable(false);
            } else {
                // Hide and disable for other user types
                if (roleHBox != null) {
                    roleHBox.setVisible(false);
                    roleHBox.setManaged(false);
                }
                if (departmentHBox != null) {
                    departmentHBox.setVisible(false);
                    departmentHBox.setManaged(false);
                }
                if (departmentHeadHBox != null) {
                    departmentHeadHBox.setVisible(false);
                    departmentHeadHBox.setManaged(false);
                }
                if (relationHBox != null) {
                    relationHBox.setVisible(false);
                    relationHBox.setManaged(false);
                }
                if (childIdHBox != null) {
                    childIdHBox.setVisible(false);
                    childIdHBox.setManaged(false);
                }
                departmentHeachCheckBox.setDisable(true);
                departmentHeachCheckBox.setSelected(false);
                departmentChoiceBox.setDisable(true);
                userRoleChoiceBox.setDisable(true);
            }
        });
    }
    
    @FXML
    private void handleAddUserButton(ActionEvent event) {
        String selectedType = userTypeChoiceBox.getValue();
        
        if (selectedType == null) {
            if (errorMsg != null) {
                errorMsg.setStyle("-fx-text-fill: red;");
                errorMsg.setText("Please select a user type.");
            }
            return;
        }
        
        // Get form data
        String name = fullNameField.getText();
        String phoneNumber = phoneNumberField.getText();

        Admin admin = new Admin();      
        String currentUser = GlobalData.getCurrentlyLoggedIN();
        FirestoreManager fm = FirestoreManager.getInstance();
        admin = fm.getAdmin(currentUser);

        String password = "12345";
        
        try {
            switch (selectedType) {
                case "Instructor":
                    String department = departmentChoiceBox.getValue();
                    boolean isDepartmentHead = departmentHeachCheckBox.isSelected();
                    String role = userRoleChoiceBox.getValue();
                    admin.createInstructor(phoneNumber, password, name, department, role, isDepartmentHead);
                    break;
                    
                case "Admin":
                    admin.createAdmin(phoneNumber, password, name, "0");
                    break;
                    
                case "HR":
                    admin.createHR(phoneNumber, password, name, "0");
                    break;
                    
                case "Parent":
                    String childId = childIdField.getText();
                    // Split by comma and trim each ID
                    String[] childIds = childId.split(",");
                    ArrayList<String> children = new ArrayList<>();
                    for (String id : childIds) {
                        String trimmedId = id.trim();
                        if (!trimmedId.isEmpty()) {
                            children.add(trimmedId);
                        }
                    }
                    String relation = relationChoiceBox.getValue();
                    admin.createParent(phoneNumber, password, name, relation, children);
                    break;
            }
            // Only navigate back if user creation was successful
            SceneController.switchScene(event, "AdminDashboard.fxml", "Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            errorMsg.setStyle("-fx-text-fill: red;");
            errorMsg.setText(e.getMessage());
        }
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        try {
            SceneController.switchScene(event, "AdminDashboard.fxml", "Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
