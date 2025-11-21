package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {
    @FXML
    private Label adminNameLabel;
    
    @FXML
    private ComboBox<String> admissionStatusFilter;
    
    @FXML
    private TableView<Admission> admissionsTable;
    
    @FXML
    private TableColumn<Admission, String> applicantIdCol;
    
    @FXML
    private TableColumn<Admission, String> applicantNameCo;
    
    @FXML
    private TableColumn<Admission, String> applicantEmailCol;
    
    @FXML
    private TableColumn<Admission, String> applicantPhoneNoCol;
    
    @FXML
    private TableColumn<Admission, String> applicantStatusCol;
    
    @FXML
    private TableColumn<Admission, String> actionsCol1;
    
    @FXML
    private ComboBox<String> userTypeFilter;
    
    @FXML
    private TableView<User> usersTable;
    
    @FXML
    private TableColumn<User, String> userIdCol;
    
    @FXML
    private TableColumn<User, String> userNameCol;
    
    @FXML
    private TableColumn<User, String> userEmailCol;
    
    @FXML
    private TableColumn<User, String> userTypeCol;
    
    @FXML
    private TableColumn<User, String> actionsCol;
    
    private ObservableList<Admission> allAdmissions;
    private ObservableList<User> allUsers;
    private Admin admin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize admin instance - get from GlobalData
        String currentUser = GlobalData.getCurrentlyLoggedIN();
        FirestoreManager fm = FirestoreManager.getInstance();
        admin = fm.getAdmin(currentUser);
        if (admin != null) {
            adminNameLabel.setText("Welcome, " + admin.getName());
        }

        // Populate the status filter ComboBox
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
            "All",
            "Pending",
            "Accepted",
            "Rejected"
        );
        admissionStatusFilter.setItems(statusOptions);
        admissionStatusFilter.setValue("All"); // Set default to "All"
        
        // Set up table columns
        applicantIdCol.setCellValueFactory(new PropertyValueFactory<>("admissionId"));
        applicantNameCo.setCellValueFactory(new PropertyValueFactory<>("name"));
        applicantEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        applicantPhoneNoCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        applicantStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Set up actions column for admissions table
        actionsCol1.setCellFactory(new Callback<TableColumn<Admission, String>, TableCell<Admission, String>>() {
            @Override
            public TableCell<Admission, String> call(TableColumn<Admission, String> param) {
                return new TableCell<Admission, String>() {
                    private final Button viewBtn = new Button("View");
                    private final HBox buttonBox = new HBox(5, viewBtn);
                    
                    {
                        viewBtn.setOnAction(event -> {
                            Admission admission = getTableView().getItems().get(getIndex());
                            handleViewAdmission(admission);
                        });
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(buttonBox);
                        }
                    }
                };
            }
        });
        
        // Load all admissions initially
        loadAdmissions();
        
        // Set up user filter ComboBox
        ObservableList<String> userTypeOptions = FXCollections.observableArrayList(
            "All",
            "Admin",
            "Department Head",
            "Instructor",
            "Student",
            "Parent",
            "HR"
        );
        userTypeFilter.setItems(userTypeOptions);
        userTypeFilter.setValue("All"); // Set default to "All"
        
        // Set up user table columns
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        // Custom cell value factory for user type
        userTypeCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String type = getUserType(user);
            return new javafx.beans.property.SimpleStringProperty(type);
        });
        
        // Set up actions column with buttons
        actionsCol.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                return new TableCell<User, String>() {
                    private final Button viewBtn = new Button("View");                    
                    {
                        viewBtn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleViewUser(user);
                        });
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(viewBtn);
                        }
                    }
                };
            }
        });
        
        // Load all users initially
        loadAllUsers();
        }
    
    private void loadAdmissions() {
        if (admin != null) {
            ArrayList<Admission> admissions = admin.getAdmissions();
            allAdmissions = FXCollections.observableArrayList(admissions);
            admissionsTable.setItems(allAdmissions);
        }
    }
    
    private void loadAllUsers() {
        FirestoreManager fm = FirestoreManager.getInstance();
        ArrayList<User> usersList = new ArrayList<>();
        
        // Get all users from each collection
        ArrayList<Student> students = fm.getAllStudents();
        usersList.addAll(students);
        
        ArrayList<Instructor> instructors = fm.getAllInstructors();
        usersList.addAll(instructors);
        
        ArrayList<Admin> admins = fm.getAllAdmins();
        usersList.addAll(admins);
        
        ArrayList<HR> hrList = fm.getAllHR();
        usersList.addAll(hrList);
        
        ArrayList<Parent> parents = fm.getAllParents();
        usersList.addAll(parents);
        
        allUsers = FXCollections.observableArrayList(usersList);
        usersTable.setItems(allUsers);
    }
    
    /**
     * Helper method to determine the user type from the User object
     */
    private String getUserType(User user) {
        if (user instanceof Admin) {
            return "Admin";
        } else if (user instanceof Instructor) {
            Instructor instructor = (Instructor) user;
            return instructor.isDepartmentHead() ? "Department Head" : "Instructor";
        } else if (user instanceof Student) {
            return "Student";
        } else if (user instanceof Parent) {
            return "Parent";
        } else if (user instanceof HR) {
            return "HR";
        }
        return "Unknown";
    }
    
    @FXML
    private void handleUserTypeFilter(ActionEvent event) {
        String selectedType = userTypeFilter.getValue();
        
        if (selectedType == null || "All".equals(selectedType)) {
            // Show all users
            loadAllUsers();
        } else {
            // Ensure allUsers is loaded
            if (allUsers == null) {
                loadAllUsers();
            }
            // Filter by selected type
            ObservableList<User> filteredList = FXCollections.observableArrayList();
            for (User user : allUsers) {
                String userType = getUserType(user);
                if (selectedType.equals(userType)) {
                    filteredList.add(user);
                }
            }
            usersTable.setItems(filteredList);
        }
    }
    
    @FXML
    private void handleStatusFilter(ActionEvent event) {
        String selectedStatus = admissionStatusFilter.getValue();
        
        if (selectedStatus == null || "All".equals(selectedStatus)) {
            // Show all admissions
            loadAdmissions();
        } else {
            // Filter by selected status
            if (admin != null) {
                ArrayList<Admission> filteredAdmissions = admin.getAdmissionsByStatus(selectedStatus);
                ObservableList<Admission> filteredList = FXCollections.observableArrayList(filteredAdmissions);
                admissionsTable.setItems(filteredList);
            }
        }
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        User.Logout();
        try {
            SceneController.switchScene(event, "Login.fxml", "Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleViewAdmission(Admission admission) {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Admission Details");
        
        // Create labels for all admission details
        Label titleLabel = new Label("Admission Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label idLabel = new Label("Application ID: " + (admission.getAdmissionId() != null ? admission.getAdmissionId() : "N/A"));
        Label nameLabel = new Label("Name: " + (admission.getName() != null ? admission.getName() : "N/A"));
        Label emailLabel = new Label("Email: " + (admission.getEmail() != null ? admission.getEmail() : "N/A"));
        Label phoneLabel = new Label("Phone Number: " + (admission.getPhoneNumber() != null ? admission.getPhoneNumber() : "N/A"));
        Label dobLabel = new Label("Date of Birth: " + (admission.getDateOfBirth() != null ? admission.getDateOfBirth() : "N/A"));
        Label majorLabel = new Label("Major: " + (admission.getMajor() != null ? admission.getMajor() : "N/A"));
        Label gpaLabel = new Label("High School GPA: " + (admission.getHighschoolGPA() != null ? admission.getHighschoolGPA() : "N/A"));
        Label statusLabel = new Label("Status: " + (admission.getStatus() != null ? admission.getStatus() : "N/A"));
        Label yearLabel = new Label("Year of Admission: " + (admission.getYearOfAdmission() != null ? admission.getYearOfAdmission() : "N/A"));
        
        // Create Accept and Reject buttons
        Button acceptBtn = new Button("Accept");
        Button rejectBtn = new Button("Reject");
        Button closeBtn = new Button("Close");
        
        // Style buttons
        acceptBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        
        // Disable Accept/Reject buttons if already processed
        if ("Accepted".equals(admission.getStatus()) || "Rejected".equals(admission.getStatus())) {
            acceptBtn.setDisable(true);
            rejectBtn.setDisable(true);
        }
        
        // Handle Accept button
        acceptBtn.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Acceptance");
            confirmAlert.setHeaderText("Accept Admission");
            confirmAlert.setContentText("Are you sure you want to accept this admission application?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        admin.acceptAdmission(admission);
                        // Refresh the admissions table
                        loadAdmissions();
                        // Refresh the users table to show the newly created student
                        loadAllUsers();
                        // Close the popup
                        popupStage.close();
                        // Show success message
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Admission Accepted");
                        successAlert.setContentText("The admission has been accepted and a student account has been created.");
                        successAlert.showAndWait();
                    } catch (Exception ex) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Failed to Accept Admission");
                        errorAlert.setContentText("An error occurred: " + ex.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });
        });
        
        // Handle Reject button
        rejectBtn.setOnAction(e -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Rejection");
            confirmAlert.setHeaderText("Reject Admission");
            confirmAlert.setContentText("Are you sure you want to reject this admission application?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        admin.rejectAdmission(admission);
                        // Refresh the admissions table
                        loadAdmissions();
                        // Close the popup
                        popupStage.close();
                        // Show success message
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Admission Rejected");
                        successAlert.setContentText("The admission has been rejected.");
                        successAlert.showAndWait();
                    } catch (Exception ex) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Failed to Reject Admission");
                        errorAlert.setContentText("An error occurred: " + ex.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });
        });
        
        // Handle Close button
        closeBtn.setOnAction(e -> popupStage.close());
        
        // Create layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setSpacing(10);
        
        VBox detailsBox = new VBox(8);
        detailsBox.getChildren().addAll(
            titleLabel,
            new Separator(),
            idLabel,
            nameLabel,
            emailLabel,
            phoneLabel,
            dobLabel,
            majorLabel,
            gpaLabel,
            statusLabel,
            yearLabel
        );
        
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(acceptBtn, rejectBtn, closeBtn);
        buttonBox.setSpacing(10);
        
        root.getChildren().addAll(detailsBox, new Separator(), buttonBox);
        
        // Create scene and show
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 500, 400);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    
    private void handleViewUser(User user) {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("User Details");
        
        // Create labels for common user details
        Label titleLabel = new Label("User Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        VBox detailsBox = new VBox(8);
        detailsBox.getChildren().add(titleLabel);
        detailsBox.getChildren().add(new Separator());
        
        // Common fields for all users
        detailsBox.getChildren().add(new Label("ID: " + (user.getId() != null ? user.getId() : "N/A")));
        detailsBox.getChildren().add(new Label("Name: " + (user.getName() != null ? user.getName() : "N/A")));
        detailsBox.getChildren().add(new Label("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A")));
        detailsBox.getChildren().add(new Label("Phone Number: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A")));
        detailsBox.getChildren().add(new Label("User Type: " + getUserType(user)));
        
        // Add type-specific fields
        if (user instanceof Student) {
            Student student = (Student) user;
            detailsBox.getChildren().add(new Label("Date of Birth: " + (student.getdateOfBirth() != null ? student.getdateOfBirth() : "N/A")));
            detailsBox.getChildren().add(new Label("Major: " + (student.getMajor() != null ? student.getMajor() : "N/A")));
            detailsBox.getChildren().add(new Label("GPA: " + (student.getGpa() != null ? student.getGpa() : "N/A")));
            detailsBox.getChildren().add(new Label("Semester: " + (student.getSemester() != null ? student.getSemester() : "N/A")));
            if (student.getCurrentCourses() != null && !student.getCurrentCourses().isEmpty()) {
                detailsBox.getChildren().add(new Label("Current Courses: " + String.join(", ", student.getCurrentCourses())));
            }
        } else if (user instanceof Instructor) {
            Instructor instructor = (Instructor) user;
            detailsBox.getChildren().add(new Label("Department: " + (instructor.getDepartmentName() != null ? instructor.getDepartmentName() : "N/A")));
            // Show "Department Head" if isDepartmentHead is true, otherwise show the role
            String roleDisplay = instructor.isDepartmentHead() ? "Department Head" : (instructor.getRole() != null ? instructor.getRole() : "Instructor");
            detailsBox.getChildren().add(new Label("Role: " + roleDisplay));
            detailsBox.getChildren().add(new Label("Salary: " + (instructor.getSalary() != null ? instructor.getSalary() : "N/A")));
            if (instructor.getCourses() != null && !instructor.getCourses().isEmpty()) {
                detailsBox.getChildren().add(new Label("Courses: " + String.join(", ", instructor.getCourses())));
            }
        } else if (user instanceof Admin) {
            Admin adminUser = (Admin) user;
            detailsBox.getChildren().add(new Label("Salary: " + (adminUser.getSalary() != null ? adminUser.getSalary() : "N/A")));
        } else if (user instanceof HR) {
            HR hr = (HR) user;
            detailsBox.getChildren().add(new Label("Department: " + (hr.getDepartmentName() != null ? hr.getDepartmentName() : "N/A")));
            detailsBox.getChildren().add(new Label("Salary: " + (hr.getSalary() != null ? hr.getSalary() : "N/A")));
        } else if (user instanceof Parent) {
            Parent parent = (Parent) user;
            detailsBox.getChildren().add(new Label("Relation: " + (parent.getRelation() != null ? parent.getRelation() : "N/A")));
            if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
                detailsBox.getChildren().add(new Label("Children IDs: " + String.join(", ", parent.getChildren())));
            }
        }
        
        // Create Edit and Delete buttons
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        Button closeBtn = new Button("Close");
        
        // Style buttons
        editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        
        // Handle Edit button
        editBtn.setOnAction(e -> {
            popupStage.close();
            // Use Platform.runLater to ensure the popup is closed before opening the edit form
            javafx.application.Platform.runLater(() -> {
                handleEditUser(user);
            });
        });
        
        // Handle Delete button
        deleteBtn.setOnAction(e -> {
            popupStage.close();
            // Use Platform.runLater to ensure the popup is closed before showing the delete confirmation
            javafx.application.Platform.runLater(() -> {
                handleDeleteUser(user);
            });
        });
        
        // Handle Close button
        closeBtn.setOnAction(e -> popupStage.close());
        
        // Create layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setSpacing(10);
        
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(editBtn, deleteBtn, closeBtn);
        buttonBox.setSpacing(10);
        
        root.getChildren().addAll(detailsBox, new Separator(), buttonBox);
        
        // Create scene and show
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 500, 500);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    
    private void handleEditUser(User user) {
        if (user == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No user selected.");
            alert.showAndWait();
            return;
        }
        
        // Create a new stage for the edit popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        if (usersTable != null && usersTable.getScene() != null) {
            popupStage.initOwner(usersTable.getScene().getWindow());
        }
        popupStage.setTitle("Edit User");
        
        String userType = getUserType(user);
        
        // Create form fields
        Label titleLabel = new Label("Edit User");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // User Type (read-only)
        Label userTypeLabel = new Label("User Type:");
        Label userTypeValue = new Label(userType);
        userTypeValue.setStyle("-fx-font-weight: bold;");
        HBox userTypeBox = new HBox(10, userTypeLabel, userTypeValue);
        
        // Name field
        Label nameLabel = new Label("Full Name:");
        TextField nameField = new TextField();
        if (user.getName() != null) {
            nameField.setText(user.getName());
        }
        nameField.setPrefWidth(200);
        HBox nameBox = new HBox(10, nameLabel, nameField);
        nameLabel.setMinWidth(100);
        
        // Phone Number field
        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField();
        if (user.getPhoneNumber() != null) {
            phoneField.setText(user.getPhoneNumber());
        }
        phoneField.setPrefWidth(200);
        HBox phoneBox = new HBox(10, phoneLabel, phoneField);
        phoneLabel.setMinWidth(100);
        
        // Email field (read-only, as it's generated from ID)
        Label emailLabel = new Label("Email:");
        Label emailValue = new Label(user.getEmail() != null ? user.getEmail() : "N/A");
        emailValue.setStyle("-fx-font-weight: bold;");
        HBox emailBox = new HBox(10, emailLabel, emailValue);
        emailLabel.setMinWidth(100);
        
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(10));
        formBox.getChildren().addAll(titleLabel, new Separator(), userTypeBox, nameBox, phoneBox, emailBox);
        
        // Type-specific fields - use arrays to make them effectively final
        final ChoiceBox<String>[] departmentChoiceBoxRef = new ChoiceBox[1];
        final ChoiceBox<String>[] roleChoiceBoxRef = new ChoiceBox[1];
        final CheckBox[] departmentHeadCheckBoxRef = new CheckBox[1];
        final ChoiceBox<String>[] relationChoiceBoxRef = new ChoiceBox[1];
        final TextField[] childIdFieldRef = new TextField[1];
        
        if (user instanceof Instructor) {
            Instructor instructor = (Instructor) user;
            
            // Department
            Label deptLabel = new Label("Department:");
            ChoiceBox<String> departmentChoiceBox = new ChoiceBox<>();
            departmentChoiceBox.getItems().addAll("CESS", "COMM", "MCTA", "ERGY", "BLDG", "LAAR", "HOUD", "CISE", "MANF", "ENVR", "MATL");
            departmentChoiceBox.setPrefWidth(200);
            if (instructor.getDepartmentName() != null) {
                departmentChoiceBox.setValue(instructor.getDepartmentName());
            }
            departmentChoiceBoxRef[0] = departmentChoiceBox;
            HBox deptBox = new HBox(10, deptLabel, departmentChoiceBox);
            deptLabel.setMinWidth(100);
            formBox.getChildren().add(deptBox);
            
            // Role
            Label roleLabel = new Label("Role:");
            ChoiceBox<String> roleChoiceBox = new ChoiceBox<>();
            roleChoiceBox.getItems().addAll("Teaching Assistant", "Professor");
            roleChoiceBox.setPrefWidth(200);
            if (instructor.getRole() != null && !instructor.isDepartmentHead()) {
                roleChoiceBox.setValue(instructor.getRole());
            }
            roleChoiceBoxRef[0] = roleChoiceBox;
            HBox roleBox = new HBox(10, roleLabel, roleChoiceBox);
            roleLabel.setMinWidth(100);
            formBox.getChildren().add(roleBox);
            
            // Department Head checkbox
            Label deptHeadLabel = new Label("Department Head:");
            CheckBox departmentHeadCheckBox = new CheckBox();
            departmentHeadCheckBox.setSelected(instructor.isDepartmentHead());
            departmentHeadCheckBoxRef[0] = departmentHeadCheckBox;
            if (instructor.isDepartmentHead()) {
                roleChoiceBox.setDisable(true);
            }
            departmentHeadCheckBox.setOnAction(e -> {
                if (departmentHeadCheckBox.isSelected()) {
                    roleChoiceBoxRef[0].setDisable(true);
                    roleChoiceBoxRef[0].setValue(null);
                } else {
                    roleChoiceBoxRef[0].setDisable(false);
                }
            });
            HBox deptHeadBox = new HBox(10, deptHeadLabel, departmentHeadCheckBox);
            deptHeadLabel.setMinWidth(100);
            formBox.getChildren().add(deptHeadBox);
            
        } else if (user instanceof Parent) {
            Parent parent = (Parent) user;
            
            // Relation
            Label relationLabel = new Label("Relation:");
            ChoiceBox<String> relationChoiceBox = new ChoiceBox<>();
            relationChoiceBox.getItems().addAll("Father", "Mother");
            relationChoiceBox.setPrefWidth(200);
            if (parent.getRelation() != null) {
                relationChoiceBox.setValue(parent.getRelation());
            }
            relationChoiceBoxRef[0] = relationChoiceBox;
            HBox relationBox = new HBox(10, relationLabel, relationChoiceBox);
            relationLabel.setMinWidth(100);
            formBox.getChildren().add(relationBox);
            
            // Children IDs
            Label childrenLabel = new Label("Children IDs:");
            TextField childIdField = new TextField();
            childIdField.setPrefWidth(200);
            if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
                childIdField.setText(String.join(", ", parent.getChildren()));
            }
            childIdFieldRef[0] = childIdField;
            HBox childrenBox = new HBox(10, childrenLabel, childIdField);
            childrenLabel.setMinWidth(100);
            formBox.getChildren().add(childrenBox);
        }
        
        // Buttons
        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        saveBtn.setOnAction(e -> {
            try {
                // Update common fields
                user.setName(nameField.getText());
                user.setPhoneNumber(phoneField.getText());
                
                FirestoreManager fm = FirestoreManager.getInstance();
                
                // Update type-specific fields and save
                if (user instanceof Student) {
                    Student student = (Student) user;
                    Student existing = fm.getStudent(student.getId());
                    if (existing != null) {
                        if ((student.getCurrentCourses() == null || student.getCurrentCourses().isEmpty()) && existing.getCurrentCourses() != null) {
                            student.setCurrentCourses(existing.getCurrentCourses());
                        }
                        if ((student.getTakenCourses() == null || student.getTakenCourses().isEmpty()) && existing.getTakenCourses() != null) {
                            student.setTakenCourses(existing.getTakenCourses());
                        }
                        if (student.getdateOfBirth() == null && existing.getdateOfBirth() != null) {
                            student.setdateOfBirth(existing.getdateOfBirth());
                        }
                        if (student.getGpa() == null && existing.getGpa() != null) {
                            student.setGpa(existing.getGpa());
                        }
                        if (student.getSemester() == null && existing.getSemester() != null) {
                            student.setSemester(existing.getSemester());
                        }
                    }
                    fm.addStudent(student); // addStudent uses set() which updates
                } else if (user instanceof Instructor) {
                    Instructor instructor = (Instructor) user;
                    if (departmentChoiceBoxRef[0] != null && departmentChoiceBoxRef[0].getValue() != null) {
                        instructor.setDepartmentName(departmentChoiceBoxRef[0].getValue());
                    }
                    if (departmentHeadCheckBoxRef[0] != null && departmentHeadCheckBoxRef[0].isSelected()) {
                        instructor.setDepartmentHead(true);
                        instructor.setRole("Department Head");
                    } else {
                        instructor.setDepartmentHead(false);
                        if (roleChoiceBoxRef[0] != null && roleChoiceBoxRef[0].getValue() != null) {
                            instructor.setRole(roleChoiceBoxRef[0].getValue());
                        }
                    }
                    fm.updateInstructor(instructor);
                } else if (user instanceof Admin) {
                    Admin adminUser = (Admin) user;
                    fm.addAdmin(adminUser); // addAdmin uses set() which updates
                } else if (user instanceof HR) {
                    HR hr = (HR) user;
                    fm.addHR(hr); // addHR uses set() which updates
                } else if (user instanceof Parent) {
                    Parent parent = (Parent) user;
                    if (relationChoiceBoxRef[0] != null && relationChoiceBoxRef[0].getValue() != null) {
                        parent.setRelation(relationChoiceBoxRef[0].getValue());
                    }
                    if (childIdFieldRef[0] != null && !childIdFieldRef[0].getText().trim().isEmpty()) {
                        String[] childIds = childIdFieldRef[0].getText().split(",");
                        ArrayList<String> children = new ArrayList<>();
                        for (String id : childIds) {
                            children.add(id.trim());
                        }
                        parent.setChildren(children);
                    }
                    fm.addParent(parent); // addParent uses set() which updates
                }
                
                // Refresh the table
                loadAllUsers();
                
                // Close popup
                popupStage.close();
                
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("User Updated");
                successAlert.setContentText("User " + user.getName() + " has been updated successfully.");
                successAlert.showAndWait();
            } catch (Exception ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Update Failed");
                errorAlert.setContentText("Failed to update user: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        });
        
        cancelBtn.setOnAction(e -> popupStage.close());
        
        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setSpacing(10);
        root.getChildren().addAll(formBox, new Separator(), buttonBox);
        
        // Create scene and show
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 500, 500);
        popupStage.setScene(scene);
        popupStage.setResizable(true);
        popupStage.showAndWait();
    }
    
    private void handleDeleteUser(User user) {
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete User");
        confirmAlert.setHeaderText("Confirm Deletion");
        confirmAlert.setContentText("Are you sure you want to delete user: " + user.getName() + "?\nThis action cannot be undone.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String userId = user.getId();
                    admin.deleteUser(userId);
                    
                    // Refresh the table
                    loadAllUsers();
                    
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("User Deleted");
                    successAlert.setContentText("User " + user.getName() + " has been deleted successfully.");
                    successAlert.showAndWait();
                } catch (Exception e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Delete Failed");
                    errorAlert.setContentText("Failed to delete user: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });
    }
    @FXML
    private void handleAddUserButton(ActionEvent event) {
        try {
            SceneController.switchScene(event, "AddUser.fxml", "Add User");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleAcceptAdmission(ActionEvent event) {
        Admission admission = admissionsTable.getSelectionModel().getSelectedItem();
        if (admission != null) {
            handleViewAdmission(admission);
        }
    }
}
