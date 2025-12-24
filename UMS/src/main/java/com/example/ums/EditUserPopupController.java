package com.example.ums;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditUserPopupController {
    // Common fields
    @FXML
    private Label userTypeValue;
    @FXML
    private Label idValue;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameField;
    @FXML
    private Label phoneLabel;
    @FXML
    private TextField phoneField;
    @FXML
    private Label emailValue;
    
    // Student fields
    @FXML
    private HBox dateOfBirthHBox;
    @FXML
    private DatePicker dateOfBirthField;
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
    private HBox SalaryHBox;
    @FXML
    private Label SalaryValue;
    @FXML
    private TextField salaryField;
    @FXML
    private HBox coursesHBox;
    @FXML
    private Label coursesLabel;
    @FXML
    private TextField coursesField;
    @FXML
    private HBox responsibilitiesHBox;
    @FXML
    private Label responsibilitiesLabel;
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
    @FXML
    private TextField benefitsField;

    // Parent fields
    @FXML
    private HBox relationHBox;
    @FXML
    private ChoiceBox<String> relationChoiceBox;
    @FXML
    private HBox childIdHBox;
    @FXML
    private TextField childIdField;
    
    private User user;
    private User currentUser;
    private Runnable onSuccess;
    private Stage popupStage;
    private User currentlyLoggedInUser;
    static DatabaseManager dm = new DatabaseManager();
    
    public static void show(User user, User currentUser, Runnable onSuccess) {
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
            controller.currentUser = currentUser;
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
        
        // Determine who is editing to control name/phone field display
        String editorType = getCurrentUserType();
        boolean isHR = "HR".equals(editorType);
        boolean isAdmin = "Admin".equals(editorType);
        boolean isDeptHead = "DepartmentHead".equals(editorType);
        
        if (!isAdmin) {
            // Self-edit or Non-Admin: Show labels, hide text fields
            if (nameLabel != null) {
                nameLabel.setText(user.getName() != null ? user.getName() : "N/A");
                nameLabel.setVisible(true);
                nameLabel.setManaged(true);
            }
            if (nameField != null) {
                nameField.setVisible(false);
                nameField.setManaged(false);
            }
            if (phoneLabel != null) {
                phoneLabel.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A");
                phoneLabel.setVisible(true);
                phoneLabel.setManaged(true);
            }
            if (phoneField != null) {
                phoneField.setVisible(false);
                phoneField.setManaged(false);
            }
        } else if (isAdmin) {
            // Admin editing others: Show text fields, hide labels
            if (user.getName() != null) {
                nameField.setText(user.getName());
            }
            if (user.getPhoneNumber() != null) {
                phoneField.setText(user.getPhoneNumber());
            }
            if (nameLabel != null) {
                nameLabel.setVisible(false);
                nameLabel.setManaged(false);
            }
            if (phoneLabel != null) {
                phoneLabel.setVisible(false);
                phoneLabel.setManaged(false);
            }
        }
        
        emailValue.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        
        // Populate choice boxes
        majorChoiceBox.getItems().addAll("CESS", "COMM", "MCTA", "ERGY", "BLDG", "LAAR", "HOUD", "CISE", "MANF", "ENVR", "MATL");
        departmentChoiceBox.getItems().addAll("CESS", "COMM", "MCTA", "ERGY", "BLDG", "LAAR", "HOUD", "CISE", "MANF", "ENVR", "MATL");
        roleChoiceBox.getItems().addAll("Teaching Assistant", "Professor");
        relationChoiceBox.getItems().addAll("Father", "Mother");
        
        // Initialize salary and benefits fields - hide TextFields and show Labels by default
        if (salaryField != null) {
            salaryField.setVisible(false);
            salaryField.setManaged(false);
        }
        if (SalaryValue != null) {
            SalaryValue.setVisible(true);
            SalaryValue.setManaged(true);
        }
        if (benefitsField != null) {
            benefitsField.setVisible(false);
            benefitsField.setManaged(false);
        }
        if (benefitsValue != null) {
            benefitsValue.setVisible(true);
            benefitsValue.setManaged(true);
        }
        
        // Hide all type-specific fields initially
        hideAllFields();
        
        // Show and populate fields based on user type and editor permissions
        if (user instanceof Student){
            setupStudentFields((Student) user);
        } else if (user instanceof Instructor) {
            setupInstructorFields((Instructor) user, editorType);
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
        if (SalaryHBox != null) {
            SalaryHBox.setVisible(false);
            SalaryHBox.setManaged(false);
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
        
        // Populate date of birth in DatePicker
        populateDateOfBirth(student.getdateOfBirth());
        
        // Populate other fields
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
            for (Map.Entry<Integer, String> entry : student.getTakenCourses().entrySet()) {
                if (takenCoursesStr.length() > 0) {
                    takenCoursesStr.append(", ");
                }
                takenCoursesStr.append(entry.getKey()).append(":").append(entry.getValue());
            }
            takenCoursesField.setText(takenCoursesStr.toString());
        }
    }
    
    private void setupInstructorFields(Instructor instructor, String editorType) {
        // Determine what fields to show and make editable based on editor type
        boolean isHR = "HR".equals(editorType);
        boolean isDeptHead = "DepartmentHead".equals(editorType);
        boolean isAdmin = "Admin".equals(editorType);
        
        // Show instructor fields (except salary and benefits which are handled separately)
        departmentHBox.setVisible(true);
        departmentHBox.setManaged(true);
        roleHBox.setVisible(true);
        roleHBox.setManaged(true); 
        departmentHeadHBox.setVisible(true);
        departmentHeadHBox.setManaged(true);
        coursesHBox.setVisible(true);
        coursesHBox.setManaged(true);
        responsibilitiesHBox.setVisible(true);
        responsibilitiesHBox.setManaged(true);
        officeHoursHBox.setVisible(true);
        officeHoursHBox.setManaged(true);
        
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
        
        // Set salary - show only for HR users
        if (isHR) {
            SalaryHBox.setVisible(true);
            SalaryHBox.setManaged(true);
            // HR: Always show TextField, hide Label
            if (salaryField != null) {
                String salaryStr = (instructor.getSalary() != null) ? instructor.getSalary() : "";
                salaryField.setText(salaryStr);
                salaryField.setVisible(true);
                salaryField.setManaged(true);
            }
            if (SalaryValue != null) {
                SalaryValue.setVisible(false);
                SalaryValue.setManaged(false);
            }
        } else {
            // Non-HR: Hide entire salary HBox
            SalaryHBox.setVisible(false);
            SalaryHBox.setManaged(false);
        }
        
        if (instructor.getCourses() != null && !instructor.getCourses().isEmpty()) {
            String coursesStr = String.join(", ", instructor.getCourses());
            if (!isDeptHead) {
                // HR: Show Label, hide TextField
                if (coursesLabel != null) {
                    coursesLabel.setText(coursesStr);
                    coursesLabel.setVisible(true);
                    coursesLabel.setManaged(true);
                }
                if (coursesField != null) {
                    coursesField.setVisible(false);
                    coursesField.setManaged(false);
                }
            } else {
                // Non-HR: Show TextField, hide Label
                coursesField.setText(coursesStr);
                if (coursesLabel != null) {
                    coursesLabel.setVisible(false);
                    coursesLabel.setManaged(false);
                }
            }
        } else {
            if (!isDeptHead) {
                // HR: Show Label with "N/A", hide TextField
                if (coursesLabel != null) {
                    coursesLabel.setText("N/A");
                    coursesLabel.setVisible(true);
                    coursesLabel.setManaged(true);
                }
                if (coursesField != null) {
                    coursesField.setVisible(false);
                    coursesField.setManaged(false);
                }
            } else {
                // Non-HR: Show empty TextField, hide Label
                coursesField.setText("");
                if (coursesLabel != null) {
                    coursesLabel.setVisible(false);
                    coursesLabel.setManaged(false);
                }
            }
        }
        
        if (instructor.getResponsibilities() != null && !instructor.getResponsibilities().isEmpty()) {
            String responsibilitiesStr = String.join(", ", instructor.getResponsibilities());
            if (!isDeptHead) {
                // HR: Show Label, hide TextField
                if (responsibilitiesLabel != null) {
                    responsibilitiesLabel.setText(responsibilitiesStr);
                    responsibilitiesLabel.setVisible(true);
                    responsibilitiesLabel.setManaged(true);
                }
                if (responsibilitiesField != null) {
                    responsibilitiesField.setVisible(false);
                    responsibilitiesField.setManaged(false);
                }
            } else {
                // Non-HR: Show TextField, hide Label
                responsibilitiesField.setText(responsibilitiesStr);
                if (responsibilitiesLabel != null) {
                    responsibilitiesLabel.setVisible(false);
                    responsibilitiesLabel.setManaged(false);
                }
            }
        } else {
            if (!isDeptHead) {
                // HR: Show Label with "N/A", hide TextField
                if (responsibilitiesLabel != null) {
                    responsibilitiesLabel.setText("N/A");
                    responsibilitiesLabel.setVisible(true);
                    responsibilitiesLabel.setManaged(true);
                }
                if (responsibilitiesField != null) {
                    responsibilitiesField.setVisible(false);
                    responsibilitiesField.setManaged(false);
                }
            } else {
                // Non-HR: Show empty TextField, hide Label
                responsibilitiesField.setText("");
                if (responsibilitiesLabel != null) {
                    responsibilitiesLabel.setVisible(false);
                    responsibilitiesLabel.setManaged(false);
                }
            }
        }
        officeHoursValue.setText(
            instructor.getOfficeHours() != null && !instructor.getOfficeHours().isEmpty() 
                ? String.join(", ", instructor.getOfficeHours()) 
                : "N/A"
        );
        
        // Set benefits - show only for HR users
        if (isHR) {
            benefitsHBox.setVisible(true);
            benefitsHBox.setManaged(true);
            // HR: Always show TextField, hide Label
            if (benefitsField != null) {
                String benefitsStr = (instructor.getBenefits() != null && !instructor.getBenefits().isEmpty()) 
                    ? String.join(", ", instructor.getBenefits()) : "";
                benefitsField.setText(benefitsStr);
                benefitsField.setVisible(true);
                benefitsField.setManaged(true);
            }
            if (benefitsValue != null) {
                benefitsValue.setVisible(false);
                benefitsValue.setManaged(false);
            }
        } else {
            // Non-HR: Hide entire benefits HBox
            benefitsHBox.setVisible(false);
            benefitsHBox.setManaged(false);
        }
        
        // Apply edit restrictions based on editor type
        applyEditRestrictions(isHR, isDeptHead, isAdmin);
    }
    
    private void setupAdminFields(Admin adminUser) {
        // Determine who is editing
        String editorType = getCurrentUserType();
        boolean isHR = "HR".equals(editorType);
        
        if (isHR) {
            SalaryHBox.setVisible(true);
            SalaryHBox.setManaged(true);
            SalaryValue.setText(adminUser.getSalary() != null ? adminUser.getSalary() : "N/A");
        } else {
            SalaryHBox.setVisible(false);
            SalaryHBox.setManaged(false);
        }
    }
    
    private void setupHRFields(HR hr) {
        // Determine who is editing
        String editorType = getCurrentUserType();
        boolean isHR = "HR".equals(editorType);
        
        departmentHBox.setVisible(true);
        departmentHBox.setManaged(true);
        
        if (hr.getDepartmentName() != null) {
            departmentChoiceBox.setValue(hr.getDepartmentName());
        }
        
        // Set salary - show only for HR users
        if (isHR) {
            SalaryHBox.setVisible(true);
            SalaryHBox.setManaged(true);
            // HR: Always show TextField, hide Label
            if (salaryField != null) {
                String salaryStr = (hr.getSalary() != null) ? hr.getSalary() : "";
                salaryField.setText(salaryStr);
                salaryField.setVisible(true);
                salaryField.setManaged(true);
            }
            if (SalaryValue != null) {
                SalaryValue.setVisible(false);
                SalaryValue.setManaged(false);
            }
            // Disable department editing for HR
            departmentChoiceBox.setDisable(true);
        } else {
            // Non-HR: Hide entire salary HBox
            SalaryHBox.setVisible(false);
            SalaryHBox.setManaged(false);
        }
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

    private String dateOfBirth;

    @FXML
    private void handleDatePicker(ActionEvent event) {
        if (dateOfBirthField == null) {
            System.out.println("DOB Picker is NULL - check fx:id");
            return;
        }

        LocalDate dob = dateOfBirthField.getValue();

        if (dob == null) {
            dateOfBirth = null;
            return;
        }

        dateOfBirth = dob.toString(); // yyyy-MM-dd format
        System.out.println("DOB selected: " + dateOfBirth);
    }


    @FXML
    private void handleSaveButton(ActionEvent event) {
        try {
            // Get editor type to determine what can be saved
            String editorType = getCurrentUserType();
            boolean isHR = "HR".equals(editorType);
            boolean isDeptHead = "DepartmentHead".equals(editorType);
            boolean isAdmin = "Admin".equals(editorType);

            // Update common fields (only for non-HR users)
            if (isAdmin) {
                user.setName(nameField.getText());
                user.setPhoneNumber(phoneField.getText());
            }
            
            // Update type-specific fields and save
            if (user instanceof Student) {
                Student student = (Student) user;
                // Update date of birth from DatePicker
                if (dateOfBirth != null) {
                    student.setdateOfBirth(dateOfBirth);
                } else if (dateOfBirthField.getValue() != null) {
                    student.setdateOfBirth(dateOfBirthField.getValue().toString());
                }
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
                    Map<Integer, String> takenCourses = new HashMap<>();
                    for (String entry : entries) {
                        String trimmed = entry.trim();
                        if (!trimmed.isEmpty() && trimmed.contains(":")) {
                            String[] parts = trimmed.split(":", 2);
                            if (parts.length == 2) {
                                try {
                                    takenCourses.put(Integer.parseInt(parts[0].trim()), parts[1].trim());
                                } catch (NumberFormatException e) {
                                    // Skip invalid course ID
                                }
                            }
                        }
                    }
                    student.setTakenCourses(takenCourses);
                }
                if (currentUser instanceof Admin){
                    ((Admin) currentUser).updateStudent(student);
                }
            } else if (user instanceof Instructor) {
                Instructor instructor = (Instructor) user;
                
                // Apply restrictions based on editor type
                if (!isHR && !isDeptHead) {
                    // Admin can edit everything
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
                    if (currentUser instanceof Admin){
                        ((Admin) currentUser).updateInstructor(instructor);
                    }
                } else if (isDeptHead) {
                    // Department Head can edit roles, responsibilities, and courses using instructor methods
                    if (roleChoiceBox.getValue() != null && currentUser instanceof Instructor) {
                        ((Instructor) currentUser).updateRole(user.getId(), roleChoiceBox.getValue());
                    }
                    if (!coursesField.getText().trim().isEmpty() && currentUser instanceof Instructor) {
                        String[] courses = coursesField.getText().split(",");
                        ArrayList<String> coursesList = new ArrayList<>();
                        for (String course : courses) {
                            String trimmed = course.trim();
                            if (!trimmed.isEmpty()) {
                                coursesList.add(trimmed);
                            }
                        }
                        ((Instructor) currentUser).updateCourses(user.getId(), coursesList, instructor.getCourses());
                    }
                    if (!responsibilitiesField.getText().trim().isEmpty() && currentUser instanceof Instructor) {
                        String[] responsibilities = responsibilitiesField.getText().split(",");
                        ArrayList<String> responsibilitiesList = new ArrayList<>();
                        for (String resp : responsibilities) {
                            String trimmed = resp.trim();
                            if (!trimmed.isEmpty()) {
                                responsibilitiesList.add(trimmed);
                            }
                        }
                        ((Instructor) currentUser).updateResponsibilities(user.getId(), responsibilitiesList, instructor.getResponsibilities());
                    }
                } else if (isHR) {
                    // HR can edit department, salary, benefits
                    if (salaryField.getText().trim() != null) {
                        if (currentUser instanceof HR) {
                            ((HR) currentUser).updateSalary(user.getId(), salaryField.getText().trim());
                            
                            // Parse benefits from text field
                            ArrayList<String> newBenefits = new ArrayList<>();
                            if (!benefitsField.getText().trim().isEmpty()) {
                                String[] benefits = benefitsField.getText().split(",");
                                for (String benefit : benefits) {
                                    String trimmed = benefit.trim();
                                    if (!trimmed.isEmpty()) {
                                        newBenefits.add(trimmed);
                                    }
                                }
                            }
                            ((HR) currentUser).updateBenefits(user.getId(), newBenefits, instructor.getBenefits());
                        } 
                    }
                }
                
                // Responsibilities can be edited by both Dept Head and Admin
                if (!isHR && !responsibilitiesField.getText().trim().isEmpty()) {
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
                
                // Salary and benefits can only be edited by HR (same dept) and Admin
                if ((isHR) && salaryField != null && salaryField.isVisible() && !salaryField.getText().trim().isEmpty()) {
                    instructor.setSalary(salaryField.getText().trim());
                }
                
                if ((isHR) && benefitsField != null && benefitsField.isVisible() && !benefitsField.getText().trim().isEmpty()) {
                    String[] benefits = benefitsField.getText().split(",");
                    ArrayList<String> benefitsList = new ArrayList<>();
                    for (String benefit : benefits) {
                        String trimmed = benefit.trim();
                        if (!trimmed.isEmpty()) {
                            benefitsList.add(trimmed);
                        }
                    }
                    instructor.setBenefits(benefitsList);
                } 
            } else if (user instanceof HR) {
                HR hr = (HR) user;
                if (!isHR) {
                    // Admin can edit department
                    if (departmentChoiceBox.getValue() != null) {
                        hr.setDepartmentName(departmentChoiceBox.getValue());
                    }
                }
                
                // HR can edit salary of other HR users
                if (isHR && salaryField != null && salaryField.isVisible() && !salaryField.getText().trim().isEmpty()) {
                    hr.setSalary(salaryField.getText().trim());
                }
                if (currentUser instanceof Admin){
                    ((Admin) currentUser).updateHR(hr);
                }
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
                if (currentUser instanceof Admin){
                    ((Admin) currentUser).updateParent(parent);
                }
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
    
    /**
     * Loads the currently logged in user from the database
     */
    private void loadCurrentlyLoggedInUser() {
        String currentUserId = GlobalData.getCurrentlyLoggedIN();
        if (currentUserId != null) {
            try {
                currentlyLoggedInUser = dm.getUser(currentUserId);
                // If it's an instructor, check if they're a department head
                if (currentlyLoggedInUser instanceof Instructor) {
                    Instructor instructor = dm.getInstructor(currentUserId);
                    if (instructor != null && instructor.isDepartmentHead()) {
                        // We'll handle this in getCurrentlyLoggedInUserType
                    }
                } else if (currentlyLoggedInUser instanceof HR) {
                    // Load full HR data to get department info
                    HR hr = dm.getHR(currentUserId);
                    if (hr != null) {
                        currentlyLoggedInUser = hr;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                currentlyLoggedInUser = null;
            }
        }
    }
    
    /**
     * Gets the type of the current user (passed as parameter)
     * Returns: "Admin", "HR", "DepartmentHead", "Instructor", "Student", "Parent", or "Unknown"
     */
    private String getCurrentUserType() {
        if (currentUser == null) {
            return "Unknown";
        }
        
        if (currentUser instanceof Admin) {
            return "Admin";
        } else if (currentUser instanceof HR) {
            return "HR";
        } else if (currentUser instanceof Instructor) {
            Instructor instructor = (Instructor) currentUser;
            if (instructor.isDepartmentHead()) {
                return "DepartmentHead";
            }
            return "Instructor";
        } else if (currentUser instanceof Student) {
            return "Student";
        } else if (currentUser instanceof Parent) {
            return "Parent";
        }
        
        return "Unknown";
    }
    
    /**
     * Populates the DatePicker with the user's date of birth
     */
    private void populateDateOfBirth(String dateOfBirthStr) {
        if (dateOfBirthStr != null && !dateOfBirthStr.trim().isEmpty()) {
            try {
                // Try parsing as yyyy-MM-dd format
                LocalDate dob = LocalDate.parse(dateOfBirthStr, DateTimeFormatter.ISO_LOCAL_DATE);
                dateOfBirthField.setValue(dob);
                dateOfBirth = dateOfBirthStr;
            } catch (DateTimeParseException e) {
                // If parsing fails, try other common formats
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate dob = LocalDate.parse(dateOfBirthStr, formatter);
                    dateOfBirthField.setValue(dob);
                    dateOfBirth = dob.format(DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e2) {
                    // If all parsing fails, leave it empty
                    System.out.println("Could not parse date of birth: " + dateOfBirthStr);
                }
            }
        }
    }
    
    /**
     * Applies edit restrictions based on the editor type
     * HR can only edit salary and benefits
     * Department Head can only edit roles and responsibilities
     * Admin can edit everything
     */
    private void applyEditRestrictions(boolean isHR, boolean isDeptHead, boolean isAdmin) {
        if (isHR) {
            // HR can only edit salary and benefits
            departmentChoiceBox.setDisable(true);
            roleChoiceBox.setDisable(true);
            departmentHeadCheckBox.setDisable(true);
            coursesField.setEditable(false);
            responsibilitiesField.setEditable(false);
            // Ensure salary and benefits fields are editable for HR
            if (salaryField != null) {
                salaryField.setEditable(true);
            }
            if (benefitsField != null) {
                benefitsField.setEditable(true);
            }
        } else if (isDeptHead) {
            // Department Head can only edit roles and responsibilities
            departmentChoiceBox.setDisable(true);
            departmentHeadCheckBox.setDisable(true);
            if (salaryField != null) {
                salaryField.setEditable(false);
            }
            if (benefitsField != null) {
                benefitsField.setEditable(false);
            }
            // Ensure role, responsibilities, and courses fields are editable for Dept Head
            roleChoiceBox.setDisable(false);
            responsibilitiesField.setEditable(true);
            coursesField.setEditable(true);
        } else if (isAdmin) {
            // Admin can edit everything except salary and benefits
            departmentChoiceBox.setDisable(false);
            roleChoiceBox.setDisable(false);
            departmentHeadCheckBox.setDisable(false);
            coursesField.setEditable(true);
            responsibilitiesField.setEditable(true);
            // Admin cannot edit salary and benefits - they remain as labels
            if (salaryField != null) {
                salaryField.setEditable(false);
            }
            if (benefitsField != null) {
                benefitsField.setEditable(false);
            }
        }
    }
}
