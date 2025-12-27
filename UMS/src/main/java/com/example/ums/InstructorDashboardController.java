package com.example.ums;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.event.ActionEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
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

    @FXML
    private TableView<Exam> examsTable;
    @FXML
    private TableColumn<Exam, Integer> examIdCol;
    @FXML
    private TableColumn<Exam, String> examDateCol;
    @FXML
    private TableColumn<Exam, String> examTypeCol;
    @FXML
    private TableColumn<Exam, String> examCourseCol;
    @FXML
    private TableColumn<Exam, String> examActionsCol;

    @FXML
    private TextField searchByCourseId;
    @FXML
    private TextField searchByStudentIdField;
    @FXML
    private TextField searchByCourseNameField;
    @FXML
    private TextField searchByHallIdField;

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
        loadExams();
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

        actionsCol.setCellFactory(new Callback<TableColumn<Course, String>, TableCell<Course, String>>() {
            @Override
            public TableCell<Course, String> call(TableColumn<Course, String> param) {
                return new TableCell<Course, String>() {
                    private final Button viewBtn = new Button("View");
                    private final Button addMaterialBtn = new Button("Add Material");
                    private final Button addAssignmentBtn = new Button("Add Assignment");
                    private final Button addExamBtn = new Button("Add Exam");
                    private final HBox buttonBox = new HBox(5, viewBtn, addMaterialBtn, addAssignmentBtn, addExamBtn);

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
                        addExamBtn.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            handleAddExam(course);
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
        try {
            // Reload instructor from database to get updated office hours
            instructor = dm.getInstructor(GlobalData.getCurrentlyLoggedIN());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ObservableList<String> hours = FXCollections.observableArrayList();
        if (instructor != null && instructor.getOfficeHours() != null) {
            instructor.getOfficeHours().forEach((day, hour) -> hours.add(day + ": " + hour));
        }
        officeHoursList.setItems(hours);
    }

    @FXML
    private void handleAddOfficeHoursButton(ActionEvent e) {
        AddOfficeHoursController.show(instructor, () -> loadOfficeHours());
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
                if (!instructorCourseIds.contains(courseId))
                    continue;
                String name = instructorCourseNameMap.get(courseId);
                if (name == null) {
                    Course course = Course.getCourseById(courseId);
                    if (course != null)
                        name = course.getCourseName();
                }
                if (name != null) {
                    if (courseNames.length() > 0)
                        courseNames.append(", ");
                    courseNames.append(name);
                }
            }
            return new javafx.beans.property.SimpleStringProperty(
                    courseNames.length() > 0 ? courseNames.toString() : "N/A");
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
        assignmentCourseCol.setCellValueFactory(cellData -> {
            Assignment assignment = cellData.getValue();
            Course course = Course.getCourseById(assignment.getCourseId());
            return new javafx.beans.property.SimpleStringProperty(course != null ? course.getCourseName() : "N/A");
        });

        assignmentActionsCol
                .setCellFactory(new Callback<TableColumn<Assignment, String>, TableCell<Assignment, String>>() {
                    @Override
                    public TableCell<Assignment, String> call(TableColumn<Assignment, String> param) {
                        return new TableCell<Assignment, String>() {
                            private final Button viewBtn = new Button("View");
                            private final Button gradeBtn = new Button("Grade");
                            private final HBox buttonBox = new HBox(5, viewBtn, gradeBtn);

                            {
                                viewBtn.setOnAction(event -> {
                                    Assignment assignment = getTableView().getItems().get(getIndex());
                                    String urlStr = assignment.getUrl();
                                    // Open in system browser with cross-platform fallback
                                    try {
                                        if (Desktop.isDesktopSupported()) {
                                            Desktop.getDesktop().browse(new URI(urlStr));
                                        } else {
                                            String os = System.getProperty("os.name").toLowerCase();
                                            if (os.contains("mac")) {
                                                Runtime.getRuntime().exec(new String[] { "open", urlStr });
                                            } else if (os.contains("win")) {
                                                Runtime.getRuntime().exec(new String[] { "rundll32",
                                                        "url.dll,FileProtocolHandler", urlStr });
                                            } else {
                                                Runtime.getRuntime().exec(new String[] { "xdg-open", urlStr });
                                            }
                                        }
                                    } catch (Exception e) {
                                        Platform.runLater(() -> {
                                            Alert a = new Alert(Alert.AlertType.ERROR);
                                            a.setTitle("Open URL Failed");
                                            a.setHeaderText(null);
                                            a.setContentText(e.getMessage());
                                            a.showAndWait();
                                        });
                                    }
                                });

                                gradeBtn.setOnAction(event -> {
                                    Assignment assignment = getTableView().getItems().get(getIndex());
                                    AddGradesController.showForAssignment(assignment.getAssignmentId(), instructor,
                                            () -> loadAssignments());
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

    private void loadExams() {
        examIdCol.setCellValueFactory(new PropertyValueFactory<>("examId"));
        examDateCol.setCellValueFactory(new PropertyValueFactory<>("examDate"));
        examTypeCol.setCellValueFactory(cellData -> {
            Exam exam = cellData.getValue();
            String typeStr = exam.getExamType() != null ? exam.getExamType().toString() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(typeStr);
        });

        // Display course name instead of course ID (using same approach as students
        // table)
        examCourseCol.setCellValueFactory(cellData -> {
            Exam exam = cellData.getValue();
            Course course = Course.getCourseById(exam.getCourseId());
            return new javafx.beans.property.SimpleStringProperty(course != null ? course.getCourseName() : "N/A");
        });

        // Add Grade button in actions column
        examActionsCol.setCellFactory(new Callback<TableColumn<Exam, String>, TableCell<Exam, String>>() {
            @Override
            public TableCell<Exam, String> call(TableColumn<Exam, String> param) {
                return new TableCell<Exam, String>() {
                    private final Button gradeBtn = new Button("Grade");

                    {
                        gradeBtn.setOnAction(event -> {
                            Exam exam = getTableView().getItems().get(getIndex());
                            handleGradeExam(exam);
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(gradeBtn);
                        }
                    }
                };
            }
        });

        // Load exams in background task
        Task<ObservableList<Exam>> task = new Task<>() {
            @Override
            protected ObservableList<Exam> call() throws Exception {
                ArrayList<Exam> exams = instructor.getInstructorExams(instructor.getId());
                return FXCollections.observableArrayList(exams);
            }
        };

        task.setOnSucceeded(e -> examsTable.setItems(task.getValue()));
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
        String searchId = searchByCourseId.getText().trim();
        if (searchId.isEmpty() || searchId.equals("Search")) {
            loadMyCourses();
            return;
        }

        try {
            int courseId = Integer.parseInt(searchId);
            ArrayList<Course> allCourses = instructor.getInstructorCourses(instructor.getId());
            ObservableList<Course> filteredList = FXCollections.observableArrayList();

            for (Course course : allCourses) {
                if (course.getCourseId() == courseId) {
                    filteredList.add(course);
                    break;
                }
            }
            coursesTable.setItems(filteredList);
        } catch (NumberFormatException e) {
            coursesTable.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void handleSearchByStudentIdField(ActionEvent event) {
        String searchId = searchByStudentIdField.getText().trim();
        if (searchId.isEmpty() || searchId.equals("Search")) {
            loadMyStudents();
            return;
        }

        try {
            Student student = dm.getStudent(searchId);
            if (student != null) {
                // Check if this student is enrolled in any of the instructor's courses
                ArrayList<Student> instructorStudents = instructor.getInstructorStudents(instructor.getId());
                boolean isInstructorStudent = false;
                for (Student s : instructorStudents) {
                    if (s.getId().equals(student.getId())) {
                        isInstructorStudent = true;
                        break;
                    }
                }

                if (isInstructorStudent) {
                    ObservableList<Student> filteredList = FXCollections.observableArrayList(student);
                    studentsTable.setItems(filteredList);
                } else {
                    studentsTable.setItems(FXCollections.observableArrayList());
                }
            } else {
                studentsTable.setItems(FXCollections.observableArrayList());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            studentsTable.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void handleSearchByCourseNameField(ActionEvent event) {
        String searchName = searchByCourseNameField.getText().trim().toLowerCase();
        if (searchName.isEmpty() || searchName.equals("search")) {
            loadAssignments();
            return;
        }

        ArrayList<Assignment> allAssignments = instructor.getInstructorAssignments(instructor.getId());
        ObservableList<Assignment> filteredList = FXCollections.observableArrayList();

        for (Assignment assignment : allAssignments) {
            Course course = Course.getCourseById(assignment.getCourseId());
            if (course != null && course.getCourseName().toLowerCase().contains(searchName)) {
                filteredList.add(assignment);
            }
        }
        assignmentsTable.setItems(filteredList);
    }

    @FXML
    private void handleSearchByHallIdField(ActionEvent event) {
        String searchId = searchByHallIdField.getText().trim();
        if (searchId.isEmpty() || searchId.equals("Search")) {
            loadHalls();
            return;
        }

        try {
            int hallId = Integer.parseInt(searchId);
            ArrayList<Classroom> allHalls = instructor.getAllHalls();
            ObservableList<Classroom> filteredList = FXCollections.observableArrayList();

            for (Classroom hall : allHalls) {
                if (hall.getHallId() == hallId) {
                    filteredList.add(hall);
                    break;
                }
            }
            hallsTable.setItems(filteredList);
        } catch (NumberFormatException e) {
            hallsTable.setItems(FXCollections.observableArrayList());
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

    private void handleAddAssignment(Course course) {
        AddAssignmentController.show(course.getCourseId(), instructor, () -> loadMyCourses());
    }

    private void handleAddExam(Course course) {
        AddExamController.show(course.getCourseId(), instructor, () -> loadMyCourses());
    }

    private void handleGradeExam(Exam exam) {
        AddGradesController.showForExam(exam.getExamId(), instructor, () -> loadExams());
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
