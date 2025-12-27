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

    @FXML
    private ScrollPane homepageView;
    @FXML
    private ScrollPane departmentCoursesView;
    @FXML
    private ScrollPane myCoursesView;
    @FXML
    private ScrollPane departmentStudentsView;

    @FXML
    private TableView<Course> myCoursesTable;
    @FXML
    private TableColumn<Course, Integer> myCourseIdCol;
    @FXML
    private TableColumn<Course, String> myCourseNameCol;
    @FXML
    private TableColumn<Course, String> myCourseDescriptionCol;
    @FXML
    private TableColumn<Course, String> myCourseBylawCol;
    @FXML
    private TableColumn<Course, String> myCourseActionsCol;

    @FXML
    private TableView<Student> studentsTable;
    @FXML
    private TableColumn<Student, String> studentIdCol;
    @FXML
    private TableColumn<Student, String> studentNameCol;
    @FXML
    private TableColumn<Student, String> studentEmailCol;
    @FXML
    private TableColumn<Student, String> studentGPACol;
    @FXML
    private TableColumn<Student, String> studentSemesterCol;

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
                "Teaching Assistant");
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

    private void showView(ScrollPane viewToShow) {
        homepageView.setVisible(false);
        homepageView.setManaged(false);
        departmentCoursesView.setVisible(false);
        departmentCoursesView.setManaged(false);
        myCoursesView.setVisible(false);
        myCoursesView.setManaged(false);
        departmentStudentsView.setVisible(false);
        departmentStudentsView.setManaged(false);

        viewToShow.setVisible(true);
        viewToShow.setManaged(true);
    }

    @FXML
    private void handleDepartmentInstructorsButton() {
        showView(homepageView);
        loadAllInstructors();
    }

    @FXML
    private void handleDepartmentCoursesButton() {
        showView(departmentCoursesView);
        loadCoursesData();
    }

    @FXML
    private void handleDepartmentStudentsButton() {
        showView(departmentStudentsView);
        loadDepartmentStudents();
    }

    @FXML
    private void hamdleMyCoursesButton() {
        showView(myCoursesView);
        loadMyCoursesData();
    }

    private void loadDepartmentStudents() {
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        studentGPACol.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        studentSemesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));

        Task<ObservableList<Student>> task = new Task<>() {
            @Override
            protected ObservableList<Student> call() throws Exception {
                ArrayList<Student> deptStudents = deptHead.getStudentsByMajor(deptHead.getDepartmentName());
                return FXCollections.observableArrayList(deptStudents);
            }
        };

        task.setOnSucceeded(e -> studentsTable.setItems(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());

        new Thread(task).start();
    }

    private void loadMyCoursesData() {
        myCourseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        myCourseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        myCourseDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("courseDescription"));
        myCourseBylawCol.setCellValueFactory(new PropertyValueFactory<>("year"));

        myCourseActionsCol.setCellFactory(new Callback<TableColumn<Course, String>, TableCell<Course, String>>() {
            @Override
            public TableCell<Course, String> call(TableColumn<Course, String> param) {
                return new TableCell<Course, String>() {
                    private final Button addMaterialBtn = new Button("Add Material");

                    {
                        addMaterialBtn.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            AddMaterialController.show(course.getCourseId(), deptHead, () -> loadMyCoursesData());
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(addMaterialBtn);
                        }
                    }
                };
            }
        });

        ArrayList<Course> courses = deptHead.getInstructorCourses(deptHead.getId());
        myCoursesTable.setItems(FXCollections.observableArrayList(courses));
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
        if (instructor.isDepartmentHead()) {
            return "Department Head";
        } else {
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
            ArrayList<Instructor> instructors = Course.getCourseInstructors(course.getCourseId());
            String instructorNames = instructors.isEmpty() ? "N/A"
                    : String.join(", ", instructors.stream().map(Instructor::getName).toArray(String[]::new));
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
                    private final Button assignBtn = new Button("Assign");

                    private final HBox buttonBox = new HBox(5, editBtn, deleteBtn, addMaterialBtn, assignBtn);

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

                        assignBtn.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            AssignCourseController.show(course.getCourseId(), deptHead, () -> loadCoursesData());
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
                ArrayList<Course> courses = deptHead.getDepartmentCourses(deptHead.getDepartmentName());
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

    @FXML
    public void HandleChangePassBtn(ActionEvent actionEvent) {
        try {
            String userId = GlobalData.getCurrentlyLoggedIN();
            ChangePasswordController.show(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNavToInstructorDashboard(ActionEvent event) {
        try {
            SceneController.switchScene(event, "InstructorDashboard.fxml", "Instructor Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
