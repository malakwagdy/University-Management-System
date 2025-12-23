package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DeptHeadDashboardController implements Initializable {
    @FXML
    private Label deptHeadNameLabel;

    @FXML
    private Label departmentNameLabel;

    @FXML
    private Label instructorsCountLabel;

    @FXML
    private ComboBox<String> instructorRoleFilter;

    @FXML
    private TableView<User> instructorsTable;

    @FXML
    private TableColumn<User, String> instructorIdCol;

    @FXML
    private TableColumn<User, String> instructorNameCol;

    @FXML
    private TableColumn<User, String> instructorEmailCol;

    @FXML
    private TableColumn<User, String> instructorRoleCol;

    @FXML
    private TableColumn<User, String> actionsCol;

    private ObservableList<User> allUsers;

    static DatabaseManager dm = new DatabaseManager();

    private Instructor deptHead;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            deptHead = dm.getInstructor(GlobalData.getCurrentlyLoggedIN());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (deptHead != null) {
            deptHeadNameLabel.setText("Welcome, " + deptHead.getName());
            departmentNameLabel.setText("Department: " + deptHead.getDepartmentName());
        }
        ObservableList<String> instructorRoleOptions = FXCollections.observableArrayList(
                "All",
                "Professor",
                "Teaching Assistant"
        );
        instructorRoleFilter.setItems(instructorRoleOptions);
        instructorRoleFilter.setValue("All"); // Set default to "All"

        // Set up user table columns
        instructorIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        instructorNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        instructorEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        // Custom cell value factory for user type
        instructorRoleCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String type = getUserRole(user);
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
                            handleViewUserButton(user);
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

    private void loadAllUsers() {
        ArrayList<User> InstructorList = new ArrayList<>();

        ArrayList<Instructor> instructors = null;
        try {
            instructors = dm.getAllInstructors();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Filter instructors by department if department name is set
        if (instructors != null && deptHead.getDepartmentName() != null) {
            for (Instructor instructor : instructors) {
                if (deptHead.getDepartmentName().equals(instructor.getDepartmentName())) {
                    InstructorList.add(instructor);
                }
            }
        } else if (instructors != null) {
            InstructorList.addAll(instructors);
        }

        allUsers = FXCollections.observableArrayList(InstructorList);
        instructorsTable.setItems(allUsers);
        
        // Update counts
        if (instructorsCountLabel != null) {
            instructorsCountLabel.setText(String.valueOf(InstructorList.size()));
        }
    }

    /**
     * Helper method to determine the user type from the User object
     */
    private String getUserRole(User user) {
        Instructor instructor = (Instructor) user;
        if (instructor.isDepartmentHead()){
            return "Department Head";
        }
        else {
            return instructor.getRole();
        }
    }

    @FXML
    private void handleInstructorRoleFilter(ActionEvent event) {
        String selectedType = instructorRoleFilter.getValue();

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
                String userRole = getUserRole(user);
                if (selectedType.equals(userRole)) {
                    filteredList.add(user);
                }
            }
            instructorsTable.setItems(filteredList);
        }
    }

    private void handleViewUserButton(User user) {
        ViewUserPopupController.show(user, deptHead, () -> loadAllUsers());
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
}
