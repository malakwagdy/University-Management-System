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

public class HrDashboardController implements Initializable {
    @FXML
    private Label hrNameLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private ComboBox<String> employeeRoleFilter;

    @FXML
    private TableView<User> employeeTable;

    @FXML
    private TableColumn<User, String> employeeIdCol;

    @FXML
    private TableColumn<User, String> nameCol;

    @FXML
    private TableColumn<User, String> emailCol;

    @FXML
    private TableColumn<User, String> roleCol;

    @FXML
    private TableColumn<User, String> salaryCol;

    @FXML
    private TableColumn<User, String> actionsCol;

    private ObservableList<User> allUsers;

    static DatabaseManager dm = new DatabaseManager();

    HR hr = new HR();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            hr = dm.getHR(GlobalData.getCurrentlyLoggedIN());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (hr != null) {
            hrNameLabel.setText("Welcome, " + hr.getName());
            departmentLabel.setText("Department: " + hr.getDepartmentName());
        }
        ObservableList<String> instructorRoleOptions = FXCollections.observableArrayList(
                "All",
                "Professor",
                "Teaching Assistant",
                "HR");
        if (employeeRoleFilter != null) {
            employeeRoleFilter.setItems(instructorRoleOptions);
            employeeRoleFilter.setValue("All"); // Set default to "All"
        }

        // Set up user table columns
        if (employeeIdCol != null) {
            employeeIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        }
        if (nameCol != null) {
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
        if (emailCol != null) {
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        }
        // Custom cell value factory for user type
        if (roleCol != null) {
            roleCol.setCellValueFactory(cellData -> {
                User user = cellData.getValue();
                String type = getUserRole(user);
                return new javafx.beans.property.SimpleStringProperty(type);
            });
        }
        // Custom cell value factory for salary
        if (salaryCol != null) {
            salaryCol.setCellValueFactory(cellData -> {
                User user = cellData.getValue();
                String salary = "N/A";
                if (user instanceof Instructor) {
                    salary = ((Instructor) user).getSalary() != null ? ((Instructor) user).getSalary() : "N/A";
                } else if (user instanceof HR) {
                    salary = ((HR) user).getSalary() != null ? ((HR) user).getSalary() : "N/A";
                }
                return new javafx.beans.property.SimpleStringProperty(salary);
            });
        }

        // Set up actions column with buttons
        if (actionsCol != null) {
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
        }

        loadAllUsers();
    }

    private void loadAllUsers() {
        ArrayList<User> EmployeeList = new ArrayList<>();

        ArrayList<Instructor> instructors = null;
        ArrayList<HR> hrs = null;
        try {
            instructors = dm.getAllInstructors();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            hrs = dm.getAllHR();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Filter employees by department if department name is set
        if (hr != null && hr.getDepartmentName() != null) {
            if (instructors != null) {
                for (Instructor instructor : instructors) {
                    if (hr.getDepartmentName().equals(instructor.getDepartmentName())) {
                        EmployeeList.add(instructor);
                    }
                }
            }
            if (hrs != null) {
                for (HR hrEmp : hrs) {
                    if (hr.getDepartmentName().equals(hrEmp.getDepartmentName())) {
                        EmployeeList.add(hrEmp);
                    }
                }
            }
        } else {
            // If no department filter, add all
            if (instructors != null) {
                EmployeeList.addAll(instructors);
            }
            if (hrs != null) {
                EmployeeList.addAll(hrs);
            }
        }

        allUsers = FXCollections.observableArrayList(EmployeeList);
        if (employeeTable != null) {
            employeeTable.setItems(allUsers);
        }
    }

    /**
     * Helper method to determine the user type from the User object
     */
    private String getUserRole(User user) {
        if (user instanceof HR) {
            return "HR";
        } else {
            Instructor instructor = (Instructor) user;
            if (instructor.isDepartmentHead()) {
                return "Department Head";
            } else {
                return instructor.getRole();
            }
        }
    }

    @FXML
    private void handleEmployeeRoleFilter(ActionEvent event) {
        String selectedType = employeeRoleFilter.getValue();

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
            employeeTable.setItems(filteredList);
        }
    }

    private void handleViewUserButton(User user) {
        ViewUserPopupController.show(user, hr, () -> loadAllUsers());
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

    @FXML
    public void HandleChangePassBtn(ActionEvent actionEvent) {
        try {
            String userId = GlobalData.getCurrentlyLoggedIN();
            ChangePasswordController.show(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}