package com.example.ums;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @FXML
    private DatePicker dobPicker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate the choice box with user types
        userTypeChoiceBox.getItems().addAll("Parent", "HR", "Admin", "Instructor");

        userRoleChoiceBox.getItems().addAll("Teaching Assistant", "Professor");

        departmentChoiceBox.getItems().addAll("CESS", "COMM", "MCTA", "ERGY", "BLDG", "LAAR", "HOUD", "CISE", "MANF", "ENVR", "MATL");

        relationChoiceBox.getItems().addAll("Father", "Mother");

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
            } else if ("HR".equals(selectedType)) {
                if (roleHBox != null) {
                    roleHBox.setVisible(false);
                    roleHBox.setManaged(false);
                }
                if (departmentHBox != null) {
                    departmentHBox.setVisible(true);
                    departmentHBox.setManaged(true);
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
                departmentChoiceBox.setDisable(false);
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
        if (dateOfBirth == null) {
            errorMsg.setStyle("-fx-text-fill: red;");
            errorMsg.setText("Please select date of birth.");
            return;
        }

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
        DatabaseManager dm = new DatabaseManager();
        try {
            admin = dm.getAdmin(currentUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String password = "12345";

        try {
            switch (selectedType) {
                case "Instructor":
                    String department = departmentChoiceBox.getValue();
                    boolean isDepartmentHead = departmentHeachCheckBox.isSelected();
                    String role = null;
                    if (isDepartmentHead) {
                        role = "Professor";
                    } else {
                        role = userRoleChoiceBox.getValue();
                    }
                    admin.createInstructor(phoneNumber, password, dateOfBirth, name, department, role, isDepartmentHead);
                    break;

                case "Admin":
                    admin.createAdmin(phoneNumber, password, dateOfBirth, name, "0");
                    break;

                case "HR":
                    department = departmentChoiceBox.getValue();
                    admin.createHR(phoneNumber, password, dateOfBirth, name, "0", department);
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
                    admin.createParent(phoneNumber, password, dateOfBirth, name, relation, children);
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

    private String dateOfBirth;

    @FXML
    private void handleDatePicker(ActionEvent event) {

        if (dobPicker == null) {
            System.out.println("DOB Picker is NULL - check fx:id");
            return;
        }

        LocalDate dob = dobPicker.getValue();

        if (dob == null) {
            dateOfBirth = null;
            return;
        }

        dateOfBirth = dob.toString(); // yyyy-MM-dd
        System.out.println("DOB selected: " + dateOfBirth);
    }

}