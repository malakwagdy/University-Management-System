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
    
    @FXML
    private TableView<AnnouncementInfo> announcementsTable;
    
    @FXML
    private TableColumn<AnnouncementInfo, String> announcementTitleCol;
    
    @FXML
    private TableColumn<AnnouncementInfo, String> announcementContentCol;
    
    @FXML
    private TableView<AssignmentInfo> assignmentsTable;
    
    @FXML
    private TableColumn<AssignmentInfo, String> assignmentNameCol;
    
    @FXML
    private TableColumn<AssignmentInfo, String> courseCol;
    
    @FXML
    private TableColumn<AssignmentInfo, String> dueDateCol;
    
    @FXML
    private TableColumn<AssignmentInfo, String> gradeCol2;
    
    @FXML
    private Label pendingAssignmentsLabel;

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
        
        public int getCourseId() { return courseId; }
        public String getCourseName() { return courseName; }
        public String getInstructor() { return instructor; }
        public String getGrade() { return grade; }
    }
    
    public static class AssignmentInfo {
        private String assignmentName;
        private String course;
        private String dueDate;
        private String grade;
        
        public AssignmentInfo(String assignmentName, String course, String dueDate, String grade) {
            this.assignmentName = assignmentName;
            this.course = course;
            this.dueDate = dueDate;
            this.grade = grade;
        }
        
        public String getAssignmentName() { return assignmentName; }
        public String getCourse() { return course; }
        public String getDueDate() { return dueDate; }
        public String getGrade() { return grade; }
    }
    
    public static class AnnouncementInfo {
        private String title;
        private String content;
        
        public AnnouncementInfo(String title, String content) {
            this.title = title;
            this.content = content;
        }
        
        public String getTitle() { return title; }
        public String getContent() { return content; }
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
            
            // Setup and load announcements (if available)
            if (announcementsTable != null) {
                setupAnnouncementsTable();
                loadStudentAnnouncements(student);
            }
            
            // Setup and load assignments
            if (assignmentsTable != null) {
                setupAssignmentsTable();
                loadStudentAssignments(student);
            }
        }
    }
    
    private void setupAssignmentsTable() {
        assignmentNameCol.setCellValueFactory(new PropertyValueFactory<>("assignmentName"));
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        gradeCol2.setCellValueFactory(new PropertyValueFactory<>("grade"));
    }
    
    private void loadStudentAssignments(Student student) {
        ArrayList<Assignment> assignments = student.getStudentAssignments(student.getId());
        ObservableList<AssignmentInfo> assignmentList = FXCollections.observableArrayList();
        
        for (Assignment assignment : assignments) {
            Course course = Course.getCourseById(assignment.getCourseId());
            String courseName = course != null ? course.getCourseName() : "Course " + assignment.getCourseId();
            String grade = student.getAssignmentGradeForStudent(assignment.getAssignmentId(), student.getId());
            
            assignmentList.add(new AssignmentInfo(
                assignment.getAssignmentName(),
                courseName,
                assignment.getAssignmentDate(),
                grade != null ? grade : "Not Graded"
            ));
        }
        
        assignmentsTable.setItems(assignmentList);
        if (pendingAssignmentsLabel != null) {
            long pendingCount = assignmentList.stream().filter(a -> "Not Graded".equals(a.getGrade())).count();
            pendingAssignmentsLabel.setText(String.valueOf(pendingCount));
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
    
    private void setupAnnouncementsTable() {
        if (announcementTitleCol != null && announcementContentCol != null) {
            announcementTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            announcementContentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        }
    }
    
    private void loadStudentAnnouncements(Student student) {
        if (announcementsTable != null) {
            ArrayList<Announcment> announcements = student.getStudentAnnouncements(student.getId());
            ObservableList<AnnouncementInfo> announcementList = FXCollections.observableArrayList();
            
            if (announcements != null) {
                for (Announcment announcement : announcements) {
                    announcementList.add(new AnnouncementInfo(
                        announcement.getTitle(),
                        announcement.getContent()
                    ));
                }
            }
            
            announcementsTable.setItems(announcementList);
        }
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
                    String instructorNames = instructors.isEmpty() ? "TBD" : 
                        String.join(", ", instructors.stream().map(Instructor::getName).toArray(String[]::new));
                    
                    courseList.add(new CourseInfo(
                            courseId,
                        courseName,
                        instructorNames,
                        "In Progress"
                    ));
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
            SceneController.switchScene(actionEvent, "ChangePassword.fxml", "Change Password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
