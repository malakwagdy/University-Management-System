package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;

public class StudentDashboardController {

    @FXML
    private Label studentNameLabel;

    @FXML
    private Label gpaLabel;

    @FXML
    private Label coursesCountLabel;

    @FXML
    private Label semesterLabel;

    @FXML
    private Button logoutBtn;

    @FXML
    private TableView<CourseInfo> coursesTable;

    @FXML
    private TableColumn<CourseInfo, String> courseIdCol;

    @FXML
    private TableColumn<CourseInfo, String> courseNameCol;

    @FXML
    private TableColumn<CourseInfo, String> instructorCol;

    @FXML
    private TableColumn<CourseInfo, String> gradeCol;

    @FXML
    private TableColumn<CourseInfo, Void> actionsCol;

    // Inner class to represent course information for the table
    public static class CourseInfo {
        private int courseId;
        private String courseName;
        private String instructor;
        private String grade;

        public CourseInfo(int courseId, String courseName, String instructor, String grade) {
            this.courseId = courseId;
            this.courseName = courseName;
            this.instructor = instructor;
            this.grade = grade;
        }

        public int getCourseId() {
            return courseId;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getInstructor() {
            return instructor;
        }

        public String getGrade() {
            return grade;
        }
    }

    @FXML
    private void initialize() {
        Student student = Student.getCurrentStudent();
        if (student != null) {
            studentNameLabel.setText("Welcome, " + student.getName());

            // Update student info labels
            gpaLabel.setText(student.getGpa() != null ? student.getGpa() : "N/A");
            semesterLabel.setText(student.getSemester() != null ? student.getSemester() : "N/A");

            // Setup courses table
            setupCoursesTable();
            loadStudentCourses(student);
        }
    }

    private void setupCoursesTable() {
        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        instructorCol.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // Add action buttons column
        actionsCol.setCellFactory(col -> {
            TableCell<CourseInfo, Void> cell = new TableCell<CourseInfo, Void>() {
                private final Button detailsBtn = new Button("Details");
                private final Button gradesBtn = new Button("Grades");
                private final HBox buttons = new HBox(5, detailsBtn, gradesBtn);

                {
                    detailsBtn.setOnAction(e -> {
                        CourseInfo course = getTableView().getItems().get(getIndex());
                        openViewCourse(course.getCourseId());
                    });
                    gradesBtn.setOnAction(e -> {
                        CourseInfo course = getTableView().getItems().get(getIndex());
                        openViewGrades(course.getCourseId());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : buttons);
                }
            };
            return cell;
        });
    }

    private void loadStudentCourses(Student student) {
        ObservableList<CourseInfo> courseList = FXCollections.observableArrayList();

        try {
            // Load current courses using DatabaseManager method
            ArrayList<Integer> currentCourses = Student.getCurrentCoursesForStudent(student.getId());
            if (currentCourses != null) {
                for (int courseId : currentCourses) {
                    // Get course details
                    Course course = Course.getCourseById(courseId);
                    String courseName = course != null ? course.getCourseName() : "Course " + courseId;

                    // Get instructors for this course
                    ArrayList<Instructor> instructors = Course.getCourseInstructors(courseId);
                    String instructorNames = instructors.isEmpty() ? "TBD"
                            : String.join(", ", instructors.stream().map(Instructor::getName).toArray(String[]::new));

                    courseList.add(new CourseInfo(
                            courseId,
                            courseName,
                            instructorNames,
                            "In Progress"));
                }
            }

            coursesTable.setItems(courseList);
            coursesCountLabel.setText(String.valueOf(courseList.size()));

        } catch (Exception e) {
            e.printStackTrace();
            coursesCountLabel.setText("0");
        }
    }

    private void openViewCourse(int courseId) {
        Course course = Course.getCourseById(courseId);
        if (course != null) {
            ViewCourseController.show(course);
        }
    }

    private void openViewGrades(int courseId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ums/ViewGradesStudent.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Course Grades");
            stage.initModality(Modality.APPLICATION_MODAL);

            ViewGradesStudentController controller = loader.getController();
            controller.setCourseId(courseId);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
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

    @FXML
    public void handleChangePassword(ActionEvent actionEvent) {
        try {
            String userId = GlobalData.getCurrentlyLoggedIN();
            ChangePasswordController.show(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
