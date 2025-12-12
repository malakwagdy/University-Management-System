package com.example.ums;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditUserPopupController {
    // Common fields
    @FXML
    private Label userTypeValue;
    @FXML
    private Label idValue;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private Label emailValue;
    
    // Student fields
    @FXML
    private HBox dateOfBirthHBox;
    @FXML
    private TextField dateOfBirthField;
    @FXML
    private HBox majorHBox;
    @FXML
    private ChoiceBox<String> majorChoiceBox;
    @FXML
    private HBox gpaHBox;
    @FXML
    private TextField gpaField;
    @FXML
    private HBox semesterHBox;
    @FXML
    private TextField semesterField;
    @FXML
    private HBox currentCoursesHBox;
    @FXML
    private TextField currentCoursesField;
    @FXML
    private HBox takenCoursesHBox;
    @FXML
    private TextField takenCoursesField;
    
    // Instructor fields
    @FXML
    private HBox departmentHBox;
    @FXML
    private ChoiceBox<String> departmentChoiceBox;
    @FXML
    private HBox roleHBox;
    @FXML
    private ChoiceBox<String> roleChoiceBox;
    @FXML
    private HBox departmentHeadHBox;
    @FXML
    private CheckBox departmentHeadCheckBox;
    @FXML
    private HBox instructorSalaryHBox;
    @FXML
    private Label instructorSalaryValue;
    @FXML
    private HBox coursesHBox;
    @FXML
    private TextField coursesField;
    @FXML
    private HBox responsibilitiesHBox;
    @FXML
    private TextField responsibilitiesField;
    @FXML
    private HBox officeHoursHBox;
    @FXML
    private Label officeHoursValue;
    @FXML
    private HBox benefitsHBox;
    @FXML
    private Label benefitsValue;
    
    // Admin fields
    @FXML
    private HBox adminSalaryHBox;
    @FXML
    private Label adminSalaryValue;
    
    // HR fields
    @FXML
    private HBox hrDepartmentHBox;
    @FXML
    private ChoiceBox<String> hrDepartmentChoiceBox;
    @FXML
    private HBox hrSalaryHBox;
    @FXML
    private Label hrSalaryValue;
    
    // Parent fields
    @FXML
    private HBox relationHBox;
    @FXML
    private ChoiceBox<String> relationChoiceBox;
    @FXML
    private HBox childIdHBox;
    @FXML
    private TextField childIdField;
    
    // Buttons
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    
    private User user;
    private Admin admin;
    private Runnable onSuccess;
    private Stage popupStage;
    
    public static void show(User user, Admin admin, Runnable onSuccess) {
        if (user == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No user selected.");
            alert.showAndWait();
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(EditUserPopupController.class.getResource("/com/example/ums/EditUserPopup.fxml"));
            VBox root = loader.load();
            EditUserPopupController controller = loader.getController();
            
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Edit User");
            
            controller.user = user;
            controller.admin = admin;
            controller.onSuccess = onSuccess;
            controller.popupStage = popupStage;
            
            controller.initializeFields();
            
            Scene scene = new Scene(root, 600, 700);
            popupStage.setScene(scene);
            popupStage.setResizable(true);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initializeFields() {
        String userType = getUserType(user);
        
        // Set common fields
        userTypeValue.setText(userType);
        idValue.setText(user.getId() != null ? user.getId() : "N/A");
        if (user.getName() != null) {
            nameField.setText(user.getName());
        }
        if (user.getPhoneNumber() != null) {
            phoneField.setText(user.getPhoneNumber());
        }
        emailValue.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        
        // Populate choice boxes
        majorChoiceBox.getItems().addAll("CESS", "COMM", "MCTA", "ERGY", "BLDG", "LAAR", "HOUD", "CISE", "MANF", "ENVR", "MATL");
        departmentChoiceBox.getItems().addAll("CESS", "COMM", "MCTA", "ERGY", "BLDG", "LAAR", "HOUD", "CISE", "MANF", "ENVR", "MATL");
        hrDepartmentChoiceBox.getItems().addAll("CESS", "COMM", "MCTA", "ERGY", "BLDG", "LAAR", "HOUD", "CISE", "MANF", "ENVR", "MATL", "HR");
        roleChoiceBox.getItems().addAll("Teaching Assistant", "Professor");
        relationChoiceBox.getItems().addAll("Father", "Mother");
        
        // Hide all type-specific fields initially
        hideAllFields();
        
        // Show and populate fields based on user type
        if (user instanceof Student) {
            setupStudentFields((Student) user);
        } else if (user instanceof Instructor) {
            setupInstructorFields((Instructor) user);
        } else if (user instanceof Admin) {
            setupAdminFields((Admin) user);
        } else if (user instanceof HR) {
            setupHRFields((HR) user);
        } else if (user instanceof Parent) {
            setupParentFields((Parent) user);
        }
    }
    
    private void hideAllFields() {
        // Student fields
        if (dateOfBirthHBox != null) {
            dateOfBirthHBox.setVisible(false);
            dateOfBirthHBox.setManaged(false);
        }
        if (majorHBox != null) {
            majorHBox.setVisible(false);
            majorHBox.setManaged(false);
        }
        if (gpaHBox != null) {
            gpaHBox.setVisible(false);
            gpaHBox.setManaged(false);
        }
        if (semesterHBox != null) {
            semesterHBox.setVisible(false);
            semesterHBox.setManaged(false);
        }
        if (currentCoursesHBox != null) {
            currentCoursesHBox.setVisible(false);
            currentCoursesHBox.setManaged(false);
        }
        if (takenCoursesHBox != null) {
            takenCoursesHBox.setVisible(false);
            takenCoursesHBox.setManaged(false);
        }
        
        // Instructor fields
        if (departmentHBox != null) {
            departmentHBox.setVisible(false);
            departmentHBox.setManaged(false);
        }
        if (roleHBox != null) {
            roleHBox.setVisible(false);
            roleHBox.setManaged(false);
        }
        if (departmentHeadHBox != null) {
            departmentHeadHBox.setVisible(false);
            departmentHeadHBox.setManaged(false);
        }
        if (instructorSalaryHBox != null) {
            instructorSalaryHBox.setVisible(false);
            instructorSalaryHBox.setManaged(false);
        }
        if (coursesHBox != null) {
            coursesHBox.setVisible(false);
            coursesHBox.setManaged(false);
        }
        if (responsibilitiesHBox != null) {
            responsibilitiesHBox.setVisible(false);
            responsibilitiesHBox.setManaged(false);
        }
        if (officeHoursHBox != null) {
            officeHoursHBox.setVisible(false);
            officeHoursHBox.setManaged(false);
        }
        if (benefitsHBox != null) {
            benefitsHBox.setVisible(false);
            benefitsHBox.setManaged(false);
        }
        
        // Admin fields
        if (adminSalaryHBox != null) {
            adminSalaryHBox.setVisible(false);
            adminSalaryHBox.setManaged(false);
        }
        
        // HR fields
        if (hrDepartmentHBox != null) {
            hrDepartmentHBox.setVisible(false);
            hrDepartmentHBox.setManaged(false);
        }
        if (hrSalaryHBox != null) {
            hrSalaryHBox.setVisible(false);
            hrSalaryHBox.setManaged(false);
        }
        
        // Parent fields
        if (relationHBox != null) {
            relationHBox.setVisible(false);
            relationHBox.setManaged(false);
        }
        if (childIdHBox != null) {
            childIdHBox.setVisible(false);
            childIdHBox.setManaged(false);
        }
    }
    
    private void setupStudentFields(Student student) {
        // Show student fields
        dateOfBirthHBox.setVisible(true);
        dateOfBirthHBox.setManaged(true);
        majorHBox.setVisible(true);
        majorHBox.setManaged(true);
        gpaHBox.setVisible(true);
        gpaHBox.setManaged(true);
        semesterHBox.setVisible(true);
        semesterHBox.setManaged(true);
        currentCoursesHBox.setVisible(true);
        currentCoursesHBox.setManaged(true);
        takenCoursesHBox.setVisible(true);
        takenCoursesHBox.setManaged(true);
        
        // Populate fields
        if (student.getdateOfBirth() != null) {
            dateOfBirthField.setText(student.getdateOfBirth());
        }
        if (student.getMajor() != null) {
            majorChoiceBox.setValue(student.getMajor());
        }
        if (student.getGpa() != null) {
            gpaField.setText(student.getGpa());
        }
        if (student.getSemester() != null) {
            semesterField.setText(student.getSemester());
        }
        if (student.getCurrentCourses() != null && !student.getCurrentCourses().isEmpty()) {
            currentCoursesField.setText(String.join(", ", student.getCurrentCourses()));
        }
        if (student.getTakenCourses() != null && !student.getTakenCourses().isEmpty()) {
            StringBuilder takenCoursesStr = new StringBuilder();
            for (Map.Entry<String, String> entry : student.getTakenCourses().entrySet()) {
                if (takenCoursesStr.length() > 0) {
                    takenCoursesStr.append(", ");
                }
                takenCoursesStr.append(entry.getKey()).append(":").append(entry.getValue());
            }
            takenCoursesField.setText(takenCoursesStr.toString());
        }
    }
    
    private void setupInstructorFields(Instructor instructor) {
        // Show instructor fields
        departmentHBox.setVisible(true);
        departmentHBox.setManaged(true);
        roleHBox.setVisible(true);
        roleHBox.setManaged(true);
        departmentHeadHBox.setVisible(true);
        departmentHeadHBox.setManaged(true);
        instructorSalaryHBox.setVisible(true);
        instructorSalaryHBox.setManaged(true);
        coursesHBox.setVisible(true);
        coursesHBox.setManaged(true);
        responsibilitiesHBox.setVisible(true);
        responsibilitiesHBox.setManaged(true);
        officeHoursHBox.setVisible(true);
        officeHoursHBox.setManaged(true);
        benefitsHBox.setVisible(true);
        benefitsHBox.setManaged(true);
        
        // Populate fields
        if (instructor.getDepartmentName() != null) {
            departmentChoiceBox.setValue(instructor.getDepartmentName());
        }
        if (instructor.getRole() != null) {
            roleChoiceBox.setValue(instructor.getRole());
        }
        departmentHeadCheckBox.setSelected(instructor.isDepartmentHead());
        if (instructor.isDepartmentHead()) {
            roleChoiceBox.setValue("Professor");
        }
        instructorSalaryValue.setText(instructor.getSalary() != null ? instructor.getSalary() : "N/A");
        if (instructor.getCourses() != null && !instructor.getCourses().isEmpty()) {
            coursesField.setText(String.join(", ", instructor.getCourses()));
        }
        if (instructor.getResponsibilities() != null && !instructor.getResponsibilities().isEmpty()) {
            responsibilitiesField.setText(String.join(", ", instructor.getResponsibilities()));
        }
        officeHoursValue.setText(
            instructor.getOfficeHours() != null && !instructor.getOfficeHours().isEmpty() 
                ? String.join(", ", instructor.getOfficeHours()) 
                : "N/A"
        );
        benefitsValue.setText(
            instructor.getBenefits() != null && !instructor.getBenefits().isEmpty() 
                ? String.join(", ", instructor.getBenefits()) 
                : "N/A"
        );
        
    }
    
    private void setupAdminFields(Admin adminUser) {
        adminSalaryHBox.setVisible(true);
        adminSalaryHBox.setManaged(true);
        adminSalaryValue.setText(adminUser.getSalary() != null ? adminUser.getSalary() : "N/A");
    }
    
    private void setupHRFields(HR hr) {
        hrDepartmentHBox.setVisible(true);
        hrDepartmentHBox.setManaged(true);
        hrSalaryHBox.setVisible(true);
        hrSalaryHBox.setManaged(true);
        
        if (hr.getDepartmentName() != null) {
            hrDepartmentChoiceBox.setValue(hr.getDepartmentName());
        }
        hrSalaryValue.setText(hr.getSalary() != null ? hr.getSalary() : "N/A");
    }
    
    private void setupParentFields(Parent parent) {
        relationHBox.setVisible(true);
        relationHBox.setManaged(true);
        childIdHBox.setVisible(true);
        childIdHBox.setManaged(true);
        
        if (parent.getRelation() != null) {
            relationChoiceBox.setValue(parent.getRelation());
        }
        if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
            childIdField.setText(String.join(", ", parent.getChildren()));
        }
    }
    
    @FXML
    private void handleSaveButton(ActionEvent event) {
        try {
            // Update common fields
            user.setName(nameField.getText());
            user.setPhoneNumber(phoneField.getText());
            
            // Update type-specific fields and save
            if (user instanceof Student) {
                Student student = (Student) user;
                student.setdateOfBirth(dateOfBirthField.getText().trim());
                if (majorChoiceBox.getValue() != null) {
                    student.setMajor(majorChoiceBox.getValue());
                }
                student.setGpa(gpaField.getText().trim());
                student.setSemester(semesterField.getText().trim());
                if (!currentCoursesField.getText().trim().isEmpty()) {
                    String[] courses = currentCoursesField.getText().split(",");
                    ArrayList<String> currentCourses = new ArrayList<>();
                    for (String course : courses) {
                        String trimmed = course.trim();
                        if (!trimmed.isEmpty()) {
                            currentCourses.add(trimmed);
                        }
                    }
                    student.setCurrentCourses(currentCourses);
                }
                if (!takenCoursesField.getText().trim().isEmpty()) {
                    String[] entries = takenCoursesField.getText().split(",");
                    Map<String, String> takenCourses = new HashMap<>();
                    for (String entry : entries) {
                        String trimmed = entry.trim();
                        if (!trimmed.isEmpty() && trimmed.contains(":")) {
                            String[] parts = trimmed.split(":", 2);
                            if (parts.length == 2) {
                                takenCourses.put(parts[0].trim(), parts[1].trim());
                            }
                        }
                    }
                    student.setTakenCourses(takenCourses);
                }
                admin.updateUser(user);
                
            } else if (user instanceof Instructor) {
                Instructor instructor = (Instructor) user;
                if (departmentChoiceBox.getValue() != null) {
                    instructor.setDepartmentName(departmentChoiceBox.getValue());
                }
                instructor.setDepartmentHead(departmentHeadCheckBox.isSelected());
                if (departmentHeadCheckBox.isSelected()) {
                    instructor.setRole("Professor");
                } else {
                    if (roleChoiceBox.getValue() != null) {
                        instructor.setRole(roleChoiceBox.getValue());
                    }
                }
                if (!coursesField.getText().trim().isEmpty()) {
                    String[] courses = coursesField.getText().split(",");
                    ArrayList<String> coursesList = new ArrayList<>();
                    for (String course : courses) {
                        String trimmed = course.trim();
                        if (!trimmed.isEmpty()) {
                            coursesList.add(trimmed);
                        }
                    }
                    instructor.setCourses(coursesList);
                }
                if (!responsibilitiesField.getText().trim().isEmpty()) {
                    String[] responsibilities = responsibilitiesField.getText().split(",");
                    ArrayList<String> responsibilitiesList = new ArrayList<>();
                    for (String resp : responsibilities) {
                        String trimmed = resp.trim();
                        if (!trimmed.isEmpty()) {
                            responsibilitiesList.add(trimmed);
                        }
                    }
                    instructor.setResponsibilities(responsibilitiesList);
                }
                admin.updateUser(user);
                
            } else if (user instanceof Admin) {
                admin.updateUser(user);
                
            } else if (user instanceof HR) {
                HR hr = (HR) user;
                if (hrDepartmentChoiceBox.getValue() != null) {
                    hr.setDepartmentName(hrDepartmentChoiceBox.getValue());
                }
                admin.updateUser(user);
                
            } else if (user instanceof Parent) {
                Parent parent = (Parent) user;
                if (relationChoiceBox.getValue() != null) {
                    parent.setRelation(relationChoiceBox.getValue());
                }
                if (!childIdField.getText().trim().isEmpty()) {
                    String[] childIds = childIdField.getText().split(",");
                    ArrayList<String> children = new ArrayList<>();
                    for (String id : childIds) {
                        String trimmed = id.trim();
                        if (!trimmed.isEmpty()) {
                            children.add(trimmed);
                        }
                    }
                    parent.setChildren(children);
                }
                admin.updateUser(user);
            }
            
            // Close popup
            popupStage.close();
            
            // Show success message
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText("User Updated");
            successAlert.setContentText("User " + user.getName() + " has been updated successfully.");
            successAlert.showAndWait();
            
            // Call success callback
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Failed to update user: " + ex.getMessage());
            errorAlert.showAndWait();
        }
    }
    
    @FXML
    private void handleCancelButton(ActionEvent event) {
        popupStage.close();
    }
    
    @FXML
    private void handleDepartmentHeadCheckBox(ActionEvent event) {
        if (departmentHeadCheckBox.isSelected()) {
            roleChoiceBox.setValue("Professor");
        }
    }
    
    private static String getUserType(User user) {
        if (user instanceof Student) return "Student";
        if (user instanceof Instructor) return "Instructor";
        if (user instanceof Admin) return "Admin";
        if (user instanceof HR) return "HR";
        if (user instanceof Parent) return "Parent";
        return "Unknown";
    }
}
