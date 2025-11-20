package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    private final HBox buttonBox = new HBox(5, viewBtn, editBtn, deleteBtn);
                    
                    {
                        viewBtn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleViewUser(user);
                        });
                        
                        editBtn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleEditUser(user);
                        });
                        
                        deleteBtn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleDeleteUser(user);
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
    
    private void handleViewUser(User user) {
        // TODO: Implement view user details functionality
        // Could open a popup window or navigate to a user details page
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Details");
        alert.setHeaderText("User Information");
        alert.setContentText("ID: " + user.getId() + "\n" +
                           "Name: " + user.getName() + "\n" +
                           "Email: " + user.getEmail() + "\n" +
                           "Type: " + getUserType(user));
        alert.showAndWait();
    }
    
    private void handleEditUser(User user) {
        // TODO: Implement edit user functionality
        // Could open a popup window with a form to edit user details
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edit User");
        alert.setHeaderText("Edit User: " + user.getName());
        alert.setContentText("Edit functionality will be implemented here.");
        alert.showAndWait();
    }
    
    private void handleDeleteUser(User user) {
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete User");
        confirmAlert.setHeaderText("Confirm Deletion");
        confirmAlert.setContentText("Are you sure you want to delete user: " + user.getName() + "?\nThis action cannot be undone.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Implement delete user functionality
                // Remove from Firestore and refresh the table
                try {
                    // FirestoreManager fm = FirestoreManager.getInstance();
                    // String userId = user.getId();
                    // String userType = getUserType(user);
                    
                    // Delete based on user type
                    // switch (userType) {
                    //     case "Student":
                    //         fm.deleteStudent(userId);
                    //         break;
                    //     case "Instructor":
                    //     case "Department Head":
                    //         fm.deleteInstructor(userId);
                    //         break;
                    //     case "Admin":
                    //         fm.deleteAdmin(userId);
                    //         break;
                    //     case "HR":
                    //         fm.deleteHR(userId);
                    //         break;
                    //     case "Parent":
                    //         fm.deleteParent(userId);
                    //         break;
                    // }
                    
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
}
