package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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

    @FXML
    private TableView<Course> coursesTable;

    @FXML
    private TableColumn<Course, Integer> courseIdCol;

    @FXML
    private TableColumn<Course, String> courseNameCol;

    @FXML
    private TableColumn<Course, String> courseDescriptionCol;

    @FXML
    private TableColumn<Course, String> courseActionsCol;

    @FXML
    private TableColumn<Course, String> courseInstructorsCol;

    @FXML
    private TableColumn<Course, String> bylawCol;

    @FXML
    private TextField searchByCourseIdField;

    @FXML
    private Label coursePlaceholder;

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
        instructorRoleFilter.setValue("All");

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
        loadAllInstructors();
        loadCoursesData();
    }

    private void loadAllInstructors() {
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

    @FXML
    private void handleSearchByCourseIdField(ActionEvent event) {
        String searchId = searchByCourseIdField.getText().trim();
        if (searchId.isEmpty()) {
            loadCoursesData();
            return;
        }

        try {
            int courseId = Integer.parseInt(searchId);
            Course course = dm.getCourse(courseId);
            if (course != null) {
                ObservableList<Course> filteredList = FXCollections.observableArrayList(course);
                coursesTable.setItems(filteredList);
            } else {
                coursesTable.setItems(FXCollections.observableArrayList());
            }
        } catch (NumberFormatException e) {
            coursesTable.setItems(FXCollections.observableArrayList());
            e.printStackTrace();
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
            loadAllInstructors();
        } else {
            // Ensure allUsers is loaded
            if (allUsers == null) {
                loadAllInstructors();
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

    private void loadCoursesData() {
        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("courseDescription"));
        courseInstructorsCol.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            ArrayList<Instructor> instructors = Course.getCourseInstructors(String.valueOf(course.getCourseId()));
            String instructorNames = instructors.isEmpty() ? "N/A" :
                String.join(", ", instructors.stream().map(Instructor::getName).toArray(String[]::new));
            return new javafx.beans.property.SimpleStringProperty(instructorNames);
        });
        bylawCol.setCellValueFactory(new PropertyValueFactory<>("year"));


        courseActionsCol.setCellFactory(new Callback<TableColumn<Course, String>, TableCell<Course, String>>() {
            @Override
            public TableCell<Course, String> call(TableColumn<Course, String> param) {
                return new TableCell<Course, String>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    private final Button addMaterialBtn = new Button("Add Material");
                    private final HBox buttonBox = new HBox(5, editBtn, deleteBtn, addMaterialBtn);

                    {
                        editBtn.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            handleEditCourse(course);
                        });

                        deleteBtn.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            handleDeleteCourse(course);
                        });
                        
                        addMaterialBtn.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            AddMaterialController.show(course.getCourseId(), deptHead, () -> loadCoursesData());
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

        if (coursePlaceholder != null) {
            coursePlaceholder.setText("Loading Data...");
        }

        Task<ObservableList<Course>> task = new Task<>() {
            @Override
            protected ObservableList<Course> call() throws Exception {
                ArrayList<Course> courses = dm.getAllCourses();
                return FXCollections.observableArrayList(courses);
            }
        };

        task.setOnSucceeded(e -> {
            coursesTable.setItems(task.getValue());
            if (coursePlaceholder != null) {
                coursePlaceholder.setText("No Records Found");
            }
        });
        task.setOnFailed(e -> {
            if (coursePlaceholder != null) {
                coursePlaceholder.setText("No Records Found");
            }
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    private void handleAddCourseButton(ActionEvent event) {
        try {
            SceneController.switchScene(event, "AddCourse.fxml", "Add new Course");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEditCourse(Course course) {
        EditCourseController.show(course, deptHead, () -> loadCoursesData());
    }

    private void handleDeleteCourse(Course course) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Course");
        confirm.setHeaderText("Are you sure you want to delete this course?");
        confirm.setContentText("Course: " + course.getCourseName());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deptHead.deleteCourse(course.getCourseId());
                loadCoursesData();
            }
        });
    }

    private void handleViewUserButton(User user) {
        ViewUserPopupController.show(user, deptHead, () -> loadAllInstructors());
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
