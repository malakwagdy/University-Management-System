package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class InstructorDashboardController {
    @FXML
    private Label instructorNameLabel;
    @FXML
    private Label departmentLabel;
    
    @FXML
    private ScrollPane myCoursesView;
    @FXML
    private ScrollPane officeHoursView;
    @FXML
    private ScrollPane myStudentsView;
    @FXML
    private ScrollPane assignmentsView;
    @FXML
    private ScrollPane examsView;
    @FXML
    private ScrollPane hallsView;
    
    @FXML
    private TableView<Course> coursesTable;
    @FXML
    private TableColumn<Course, Integer> courseIdCol;
    @FXML
    private TableColumn<Course, String> courseNameCol;
    @FXML
    private TableColumn<Course, String> courseDescriptionCol;
    @FXML
    private TableColumn<Course, Integer> studentsCountCol;
    @FXML
    private TableColumn<Course, String> bylawCol;
    @FXML
    private TableColumn<Course, String> actionsCol;
    
    @FXML
    private ListView<String> officeHoursList;
    
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
    private TableColumn<Student, String> studentCoursesCol;

    @FXML
    private TableColumn<Student, String> studentActionsCol;
    
    @FXML
    private TableView<Assignment> assignmentsTable;
    @FXML
    private TableColumn<Assignment, String> assignmentIdCol;
    @FXML
    private TableColumn<Assignment, String> assignmentNameCol;
    @FXML
    private TableColumn<Assignment, String> assignmentDeadlineCol;
    @FXML
    private TableColumn<Assignment, String> assignmentCourseCol;
    @FXML
    private TableColumn<Assignment, String> assignmentActionsCol;
    
    @FXML
    private TableView<Classroom> hallsTable;
    @FXML
    private TableColumn<Classroom, Integer> hallIdCol;
    @FXML
    private TableColumn<Classroom, String> hallCapacityCol;
    @FXML
    private TableColumn<Classroom, String> hallTypeCol;
    @FXML
    private TableColumn<Classroom, Boolean> hallMaintenanceCol;
    @FXML
    private TableColumn<Classroom, String> hallActionsCol;

    static DatabaseManager dm = new DatabaseManager();
    private Instructor instructor;

    @FXML
    private void initialize() {
        try {
            instructor = dm.getInstructor(GlobalData.getCurrentlyLoggedIN());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (instructor != null) {
            instructorNameLabel.setText("Welcome, " + instructor.getName());
            departmentLabel.setText("Department: " + instructor.getDepartmentName());
        }
        loadMyCourses();
    }
    
    private void showView(ScrollPane viewToShow) {
        myCoursesView.setVisible(false);
        myCoursesView.setManaged(false);
        officeHoursView.setVisible(false);
        officeHoursView.setManaged(false);
        myStudentsView.setVisible(false);
        myStudentsView.setManaged(false);
        assignmentsView.setVisible(false);
        assignmentsView.setManaged(false);
        examsView.setVisible(false);
        examsView.setManaged(false);
        hallsView.setVisible(false);
        hallsView.setManaged(false);
        
        viewToShow.setVisible(true);
        viewToShow.setManaged(true);
    }
    
    @FXML
    private void handleMyCoursesButton() {
        showView(myCoursesView);
        loadMyCourses();
    }
    
    @FXML
    private void handleOfficeHoursButton() {
        showView(officeHoursView);
        loadOfficeHours();
    }
    
    @FXML
    private void handleMyStudentsButton() {
        showView(myStudentsView);
        loadMyStudents();
    }
    
    @FXML
    private void handleAssignmentsButton() {
        showView(assignmentsView);
        loadAssignments();
    }
    
    @FXML
    private void handleExamsButton() {
        showView(examsView);
        // Exams functionality not implemented yet
    }
    
    @FXML
    private void handleHallsButton() {
        showView(hallsView);
        loadHalls();
    }
    
    @FXML
    private void handleGradesButton() {
        // Grade management functionality not implemented yet
    }
    
    private void loadMyCourses() {
        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("courseDescription"));
        bylawCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        studentsCountCol.setCellValueFactory(cellData -> {
            int count = instructor.getCourseStudentsCount(cellData.getValue().getCourseId());
            return new javafx.beans.property.SimpleIntegerProperty(count).asObject();
        });

//        actionsCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        actionsCol.setCellFactory(new Callback<TableColumn<Course, String>, TableCell<Course, String>>() {
            @Override
            public TableCell<Course, String> call(TableColumn<Course, String> param) {
                return new TableCell<Course, String>() {
                    private final Button viewBtn = new Button("View");
                    private final Button addMaterialBtn = new Button("Add Material");
                    private final Button addAssignmentBtn = new Button("Add Assignment");
                    private final HBox buttonBox = new HBox(5, viewBtn, addMaterialBtn, addAssignmentBtn);

                    {
                        viewBtn.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            ViewCourseController.show(course);
                        });

                        addMaterialBtn.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            AddMaterialController.show(course.getCourseId(), instructor, () -> loadMyCourses());
                        });

                        addAssignmentBtn.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            handleAddAssignment(course);
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

        ArrayList<Course> courses = instructor.getInstructorCourses(instructor.getId());
        coursesTable.setItems(FXCollections.observableArrayList(courses));
    }

    private void loadOfficeHours() {
        ObservableList<String> hours = FXCollections.observableArrayList();
        instructor.getOfficeHours().forEach((day, hour) -> 
            hours.add(day + ": " + hour)
        );
        officeHoursList.setItems(hours);
    }
    
    private void loadMyStudents() {
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        studentGPACol.setCellValueFactory(new PropertyValueFactory<>("gpa"));

        // Cache instructor courses once to avoid repeated DB calls for each student row
        java.util.HashMap<Integer, String> instructorCourseNameMap = new java.util.HashMap<>();
        java.util.HashSet<Integer> instructorCourseIds = new java.util.HashSet<>();
        ArrayList<Course> instructorCourses = null;
        if (instructor != null) {
            instructorCourses = instructor.getInstructorCourses(instructor.getId());
            if (instructorCourses != null) {
                for (Course ic : instructorCourses) {
                    instructorCourseIds.add(ic.getCourseId());
                    instructorCourseNameMap.put(ic.getCourseId(), ic.getCourseName());
                }
            }
        }

        studentCoursesCol.setCellValueFactory(cellData -> {
            Student student = cellData.getValue();
            ArrayList<Integer> courseIds = student.getCurrentCourses();
            if (courseIds == null || courseIds.isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }

            StringBuilder courseNames = new StringBuilder();
            for (Integer courseId : courseIds) {
                // Only include the course if the instructor teaches it
                if (!instructorCourseIds.contains(courseId)) continue;
                String name = instructorCourseNameMap.get(courseId);
                if (name == null) {
                    Course course = Course.getCourseById(courseId);
                    if (course != null) name = course.getCourseName();
                }
                if (name != null) {
                    if (courseNames.length() > 0) courseNames.append(", ");
                    courseNames.append(name);
                }
            }
            return new javafx.beans.property.SimpleStringProperty(courseNames.length() > 0 ? courseNames.toString() : "N/A");
        });

        Task<ObservableList<Student>> task = new Task<>() {
            @Override
            protected ObservableList<Student> call() throws Exception {
                ArrayList<Student> students = instructor.getInstructorStudents(instructor.getId());
                return FXCollections.observableArrayList(students);
            }
        };

        task.setOnSucceeded(e -> studentsTable.setItems(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());

        new Thread(task).start();
    }
    
    private void loadAssignments() {
        assignmentIdCol.setCellValueFactory(new PropertyValueFactory<>("assignmentId"));
        assignmentNameCol.setCellValueFactory(new PropertyValueFactory<>("assignmentName"));
        assignmentDeadlineCol.setCellValueFactory(new PropertyValueFactory<>("assignmentDate"));
        
        assignmentActionsCol.setCellFactory(new Callback<TableColumn<Assignment, String>, TableCell<Assignment, String>>() {
            @Override
            public TableCell<Assignment, String> call(TableColumn<Assignment, String> param) {
                return new TableCell<Assignment, String>() {
                    private final Button viewBtn = new Button("View");
                    private final Button gradeBtn = new Button("Grade");
                    private final HBox buttonBox = new HBox(5, viewBtn, gradeBtn);
                    
                    {
                        viewBtn.setOnAction(event -> {
                            Assignment assignment = getTableView().getItems().get(getIndex());
                            // View assignment details
                        });
                        
                        gradeBtn.setOnAction(event -> {
                            Assignment assignment = getTableView().getItems().get(getIndex());
                            // Grade assignment
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
        
        Task<ObservableList<Assignment>> task = new Task<>() {
            @Override
            protected ObservableList<Assignment> call() throws Exception {
                ArrayList<Assignment> assignments = instructor.getInstructorAssignments(instructor.getId());
                return FXCollections.observableArrayList(assignments);
            }
        };
        
        task.setOnSucceeded(e -> assignmentsTable.setItems(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());
        
        new Thread(task).start();
    }
    
    private void loadHalls() {
        hallIdCol.setCellValueFactory(new PropertyValueFactory<>("hallId"));
        hallCapacityCol.setCellValueFactory(new PropertyValueFactory<>("hallCapacity"));
        hallTypeCol.setCellValueFactory(new PropertyValueFactory<>("hallType"));
        hallMaintenanceCol.setCellValueFactory(new PropertyValueFactory<>("hallMaintenance"));
        
        hallActionsCol.setCellFactory(new Callback<TableColumn<Classroom, String>, TableCell<Classroom, String>>() {
            @Override
            public TableCell<Classroom, String> call(TableColumn<Classroom, String> param) {
                return new TableCell<Classroom, String>() {
                    private final Button bookBtn = new Button("Book");
                    
                    {
                        bookBtn.setOnAction(event -> {
                            Classroom selectedHall = getTableView().getItems().get(getIndex());
                            int hallId = selectedHall.getHallId();

                            BookingContext.setSelectedHallId(hallId);
                            SceneController.switchTo("BookClassroom.fxml");
                        });
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(bookBtn);
                        }
                    }
                };
            }
        });
        
        Task<ObservableList<Classroom>> task = new Task<>() {
            @Override
            protected ObservableList<Classroom> call() throws Exception {
                ArrayList<Classroom> halls = instructor.getAllHalls();
                return FXCollections.observableArrayList(halls);
            }
        };
        
        task.setOnSucceeded(e -> hallsTable.setItems(task.getValue()));
        task.setOnFailed(e -> task.getException().printStackTrace());
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleSearchByCourseId(ActionEvent event) {
        // Search functionality
    }
    
    @FXML
    private void handleSearchByStudentIdField(ActionEvent event) {
        // Search functionality
    }
    
    @FXML
    private void handleSearchByCourseNameField(ActionEvent event) {
        // Search functionality
    }
    
    @FXML
    private void handleSearchByHallIdField(ActionEvent event) {
        // Search functionality
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
    
    private void handleAddAssignment(Course course) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Assignment");
        alert.setHeaderText("Feature Coming Soon");
        alert.setContentText("Add Assignment functionality for course: " + course.getCourseName() + " will be implemented soon.");
        alert.showAndWait();
    }

    @FXML
    public void HandleChangePassBtn(ActionEvent actionEvent) {
        try {
            SceneController.switchScene(actionEvent, "ChangePassword.fxml", "Change Password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
