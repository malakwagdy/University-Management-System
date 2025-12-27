package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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

    // Views
    @FXML
    private ScrollPane homePageView;

    @FXML
    private ScrollPane studentRecordsView;

    @FXML
    private ScrollPane hallManagementView;

    @FXML
    private ScrollPane courseManagementView;

    // Student Records Table
    @FXML
    private TableView<Student> studentRecordsTable;

    @FXML
    private TableColumn<Student, String> studentIdCol;

    @FXML
    private TableColumn<Student, String> studentNameCol;

    @FXML
    private TableColumn<Student, String> studentEmailCol;

    @FXML
    private TableColumn<Student, String> studentGPACol;

    @FXML
    private TableColumn<Student, String> recordsActionsCol;

    @FXML
    private TextField searchByIdField;

    @FXML
    private Label studentPlaceholder;

    // Halls Table
    @FXML
    private TableView<Classroom> hallsTable;

    @FXML
    private TableColumn<Classroom, Integer> hallIdCol;

    @FXML
    private TableColumn<Classroom, String> capacityCol;

    @FXML
    private TableColumn<Classroom, String> hallTypeCol;

    @FXML
    private TableColumn<Classroom, Boolean> maintenanceCol;

    @FXML
    private TableColumn<Classroom, Boolean> availabilityCol;

    @FXML
    private TableColumn<Classroom, String> hallsActionsCol;

    @FXML
    private Label hallsPlaceholder;

    // Course Management Table
    @FXML
    private TableView<Course> courseManagementTable;

    @FXML
    private TableColumn<Course, Integer> courseIdCol;

    @FXML
    private TableColumn<Course, String> courseNameCol;

    @FXML
    private TableColumn<Course, String> courseDescriptionCol;

    @FXML
    private TableColumn<Course, String> courseActionsCol;

    @FXML
    private TextField searchByCourseIdField;

    @FXML
    private Label coursePlaceholder;

    private ObservableList<Admission> allAdmissions;
    private ObservableList<User> allUsers;
    private Admin admin;
    private DatabaseManager dm = new DatabaseManager();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize admin instance - get from GlobalData
        String currentUser = GlobalData.getCurrentlyLoggedIN();
        try {
            admin = dm.getAdmin(currentUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (admin != null) {
            adminNameLabel.setText("Welcome, " + admin.getName());
        }

        // Populate the status filter ComboBox
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "All",
                "Pending",
                "Accepted",
                "Rejected");
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
                            handleViewAdmissionButton(admission);
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
                "HR");
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

    private void loadAdmissions() {
        if (admin != null) {
            ArrayList<Admission> admissions = admin.retrieveAdmissions();
            allAdmissions = FXCollections.observableArrayList(admissions);
            admissionsTable.setItems(allAdmissions);
        }
    }

    private void loadAllUsers() {
        ArrayList<User> usersList = new ArrayList<>();

        // Get all users from each collection
        ArrayList<Student> students = null;
        try {
            students = dm.getAllStudents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        usersList.addAll(students);

        ArrayList<Instructor> instructors = null;
        try {
            instructors = dm.getAllInstructors();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        usersList.addAll(instructors);

        ArrayList<Admin> admins = null;
        try {
            admins = dm.getAllAdmins();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        usersList.addAll(admins);

        ArrayList<HR> hrList = null;
        try {
            hrList = dm.getAllHR();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        usersList.addAll(hrList);

        ArrayList<Parent> parents = null;
        try {
            parents = dm.getAllParents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                ArrayList<Admission> filteredAdmissions = admin.retrieveAdmissionsByStatus(selectedStatus);
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

    private void handleViewAdmissionButton(Admission admission) {
        ViewAdmissionPopupController.show(admission, admin, () -> {
            loadAdmissions();
            loadAllUsers();
        });
    }

    private void handleViewUserButton(User user) {
        ViewUserPopupController.show(user, admin, () -> loadAllUsers());
    }

    @FXML
    private void handleAddAnnouncement() {
        AddAnnouncementController.show(0, admin, null);
    }

    @FXML
    private void handleAddUserButton(ActionEvent event) {
        try {
            SceneController.switchScene(event, "AddUser.fxml", "Add User");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStudentRecordsButton(ActionEvent event) {
        showView(studentRecordsView);
        loadStudentRecords();
    }

    @FXML
    private void handleUserManagementBtn(ActionEvent event) {
        showView(homePageView);
    }

    @FXML
    private void handleHallManagementButton(ActionEvent event) {
        showView(hallManagementView);
        loadHallsData();
    }

    @FXML
    private void handleCourseManagementButton(ActionEvent event) {
        showView(courseManagementView);
        loadCoursesData();
    }

    @FXML
    private void handleSearchByIdField(ActionEvent event) {
        String searchId = searchByIdField.getText().trim();
        if (searchId.isEmpty()) {
            loadStudentRecords();
            return;
        }

        try {
            Student student = dm.getStudent(searchId);
            if (student != null) {
                ObservableList<Student> filteredList = FXCollections.observableArrayList(student);
                studentRecordsTable.setItems(filteredList);
            } else {
                studentRecordsTable.setItems(FXCollections.observableArrayList());
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                courseManagementTable.setItems(filteredList);
            } else {
                courseManagementTable.setItems(FXCollections.observableArrayList());
            }
        } catch (NumberFormatException e) {
            courseManagementTable.setItems(FXCollections.observableArrayList());
            e.printStackTrace();
        }
    }

    private void showView(ScrollPane viewToShow) {
        homePageView.setVisible(false);
        homePageView.setManaged(false);
        studentRecordsView.setVisible(false);
        studentRecordsView.setManaged(false);
        hallManagementView.setVisible(false);
        hallManagementView.setManaged(false);
        courseManagementView.setVisible(false);
        courseManagementView.setManaged(false);

        viewToShow.setVisible(true);
        viewToShow.setManaged(true);
    }

    private void loadStudentRecords() {
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        studentGPACol.setCellValueFactory(new PropertyValueFactory<>("gpa"));

        recordsActionsCol.setCellFactory(new Callback<TableColumn<Student, String>, TableCell<Student, String>>() {
            @Override
            public TableCell<Student, String> call(TableColumn<Student, String> param) {
                return new TableCell<Student, String>() {
                    private final Button viewBtn = new Button("Generate Transcript");

                    {
                        viewBtn.setOnAction(event -> {
                            Student student = getTableView().getItems().get(getIndex());
                            ViewTranscriptPopupController.show(student);
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

        studentPlaceholder.setText("Loading Data...");

        Task<ObservableList<Student>> task = new Task<>() {
            @Override
            protected ObservableList<Student> call() throws Exception {
                ArrayList<Student> students = dm.getAllStudents();
                return FXCollections.observableArrayList(students);
            }
        };

        task.setOnSucceeded(e -> {
            studentRecordsTable.setItems(task.getValue());
            studentPlaceholder.setText("No Records Found");
        });
        task.setOnFailed(e -> {
            studentPlaceholder.setText("No Records Found");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void loadHallsData() {
        hallIdCol.setCellValueFactory(new PropertyValueFactory<>("hallId"));
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("hallCapacity"));
        hallTypeCol.setCellValueFactory(new PropertyValueFactory<>("hallType"));
        maintenanceCol.setCellValueFactory(new PropertyValueFactory<>("hallMaintenance"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));

        hallsPlaceholder.setText("Loading Data...");

        Task<ObservableList<Classroom>> task = new Task<>() {
            @Override
            protected ObservableList<Classroom> call() throws Exception {
                DatabaseManager db = new DatabaseManager();
                ArrayList<Classroom> classrooms = db.getAllClassrooms();
                return FXCollections.observableArrayList(classrooms);
            }
        };

        task.setOnSucceeded(e -> {
            hallsTable.setItems(task.getValue());
            hallsPlaceholder.setText("No Records Found");
        });
        task.setOnFailed(e -> {
            hallsPlaceholder.setText("No Records Found");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void loadCoursesData() {
        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("courseDescription"));

        courseActionsCol.setCellFactory(new Callback<TableColumn<Course, String>, TableCell<Course, String>>() {
            @Override
            public TableCell<Course, String> call(TableColumn<Course, String> param) {
                return new TableCell<Course, String>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    private final HBox buttonBox = new HBox(5, editBtn, deleteBtn);

                    {
                        editBtn.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            handleEditCourse(course);
                        });

                        deleteBtn.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            handleDeleteCourse(course);
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

        coursePlaceholder.setText("Loading Data...");

        Task<ObservableList<Course>> task = new Task<>() {
            @Override
            protected ObservableList<Course> call() throws Exception {
                ArrayList<Course> courses = dm.getAllCourses();
                return FXCollections.observableArrayList(courses);
            }
        };

        task.setOnSucceeded(e -> {
            courseManagementTable.setItems(task.getValue());
            coursePlaceholder.setText("No Records Found");
        });
        task.setOnFailed(e -> {
            coursePlaceholder.setText("No Records Found");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void handleEditCourse(Course course) {
        EditCourseController.show(course, admin, () -> loadCoursesData());
    }

    private void handleDeleteCourse(Course course) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Course");
        confirm.setHeaderText("Are you sure you want to delete this course?");
        confirm.setContentText("Course: " + course.getCourseName());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                admin.deleteCourse(course.getCourseId());
                loadCoursesData();
            }
        });
    }

    @FXML
    private void handleAddClassroomButton(ActionEvent event) {
        try {
            SceneController.switchScene(event, "AddClassroom.fxml", "Add new Classroom");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddCourseButton(ActionEvent event) {
        try {
            SceneController.switchScene(event, "AddCourse.fxml", "Add new Course");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
