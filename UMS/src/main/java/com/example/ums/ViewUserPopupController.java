package com.example.ums;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class ViewUserPopupController {
    @FXML
    private VBox detailsBox;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button closeBtn;
    
    private User user;
    private Stage popupStage;
    private Admin admin;
    private Runnable onRefresh;
    
    public void setUser(User user) {
        this.user = user;
        populateUserDetails();
    }
    
    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }
    
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
    
    public void setOnRefresh(Runnable onRefresh) {
        this.onRefresh = onRefresh;
    }
    
    private void populateUserDetails() {
        if (user == null) return;
        
        // Common fields for all users
        detailsBox.getChildren().add(new Label("ID: " + (user.getId() != null ? user.getId() : "N/A")));
        detailsBox.getChildren().add(new Label("Name: " + (user.getName() != null ? user.getName() : "N/A")));
        detailsBox.getChildren().add(new Label("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A")));
        detailsBox.getChildren().add(new Label("Phone Number: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A")));
        detailsBox.getChildren().add(new Label("User Type: " + getUserType(user)));
        
        // Add type-specific fields
        if (user instanceof Student) {
            Student student = (Student) user;
            detailsBox.getChildren().add(new Label("Date of Birth: " + (student.getdateOfBirth() != null ? student.getdateOfBirth() : "N/A")));
            detailsBox.getChildren().add(new Label("Major: " + (student.getMajor() != null ? student.getMajor() : "N/A")));
            detailsBox.getChildren().add(new Label("GPA: " + (student.getGpa() != null ? student.getGpa() : "N/A")));
            detailsBox.getChildren().add(new Label("Semester: " + (student.getSemester() != null ? student.getSemester() : "N/A")));
            if (student.getCurrentCourses() != null && !student.getCurrentCourses().isEmpty()) {
                detailsBox.getChildren().add(new Label("Current Courses: " + String.join(", ", student.getCurrentCourses())));
            } else {
                detailsBox.getChildren().add(new Label("Current Courses: N/A"));
            }
            if (student.getTakenCourses() != null && !student.getTakenCourses().isEmpty()) {
                StringBuilder takenCoursesStr = new StringBuilder();
                for (Map.Entry<Integer, String> entry : student.getTakenCourses().entrySet()) {
                    if (takenCoursesStr.length() > 0) {
                        takenCoursesStr.append(", ");
                    }
                    takenCoursesStr.append(entry.getKey()).append(" (").append(entry.getValue()).append(")");
                }
                detailsBox.getChildren().add(new Label("Taken Courses: " + takenCoursesStr.toString()));
            } else {
                detailsBox.getChildren().add(new Label("Taken Courses: N/A"));
            }
        } else if (user instanceof Instructor) {
            Instructor instructor = (Instructor) user;
            detailsBox.getChildren().add(new Label("Department: " + (instructor.getDepartmentName() != null ? instructor.getDepartmentName() : "N/A")));
            detailsBox.getChildren().add(new Label("Role: " + (instructor.getRole() != null ? instructor.getRole() : "Instructor")));
            detailsBox.getChildren().add(new Label("Salary: " + (instructor.getSalary() != null ? instructor.getSalary() : "N/A")));
            if (instructor.getCourses() != null && !instructor.getCourses().isEmpty()) {
                detailsBox.getChildren().add(new Label("Courses: " + String.join(", ", instructor.getCourses())));
            } else {
                detailsBox.getChildren().add(new Label("Courses: N/A"));
            }
            if (instructor.getResponsibilities() != null && !instructor.getResponsibilities().isEmpty()) {
                detailsBox.getChildren().add(new Label("Responsibilities: " + String.join(", ", instructor.getResponsibilities())));
            } else {
                detailsBox.getChildren().add(new Label("Responsibilities: N/A"));
            }
            if (instructor.getOfficeHours() != null && !instructor.getOfficeHours().isEmpty()) {
                detailsBox.getChildren().add(new Label("Office Hours: " + String.join(", ", instructor.getOfficeHours())));
            } else {
                detailsBox.getChildren().add(new Label("Office Hours: N/A"));
            }
            if (instructor.getBenefits() != null && !instructor.getBenefits().isEmpty()) {
                detailsBox.getChildren().add(new Label("Benefits: " + String.join(", ", instructor.getBenefits())));
            } else {
                detailsBox.getChildren().add(new Label("Benefits: N/A"));
            }
        } else if (user instanceof Admin) {
            Admin adminUser = (Admin) user;
            detailsBox.getChildren().add(new Label("Salary: " + (adminUser.getSalary() != null ? adminUser.getSalary() : "N/A")));
        } else if (user instanceof HR) {
            HR hr = (HR) user;
            detailsBox.getChildren().add(new Label("Department: " + (hr.getDepartmentName() != null ? hr.getDepartmentName() : "N/A")));
            detailsBox.getChildren().add(new Label("Salary: " + (hr.getSalary() != null ? hr.getSalary() : "N/A")));
        } else if (user instanceof Parent) {
            Parent parent = (Parent) user;
            detailsBox.getChildren().add(new Label("Relation: " + (parent.getRelation() != null ? parent.getRelation() : "N/A")));
            if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
                detailsBox.getChildren().add(new Label("Children IDs: " + String.join(", ", parent.getChildren())));
            } else {
                detailsBox.getChildren().add(new Label("Children IDs: N/A"));
            }
        }
    }
    
    private String getUserType(User user) {
        if (user instanceof Student) return "Student";
        if (user instanceof Instructor) return "Instructor";
        if (user instanceof Admin) return "Admin";
        if (user instanceof HR) return "HR";
        if (user instanceof Parent) return "Parent";
        return "Unknown";
    }
    
    @FXML
    private void handleEditButton(ActionEvent event) {
        popupStage.close();
        Platform.runLater(() -> {
            EditUserPopupController.show(user, admin, () -> {
                if (onRefresh != null) {
                    onRefresh.run();
                }
            });
        });
    }
    
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete User");
        confirmAlert.setHeaderText("Confirm Deletion");
        confirmAlert.setContentText("Are you sure you want to delete user: " + user.getName() + "?\nThis action cannot be undone.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String userId = user.getId();
                    admin.deleteUser(userId);
                    
                    // Close popup
                    popupStage.close();
                    
                    // Refresh the table
                    if (onRefresh != null) {
                        onRefresh.run();
                    }
                    
                    // Show success message
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
    private void handleCloseButton(ActionEvent event) {
        popupStage.close();
    }
    
    public static void show(User user, Admin admin, Runnable onRefresh) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewUserPopupController.class.getResource("/com/example/ums/ViewUserPopup.fxml"));
            VBox root = loader.load();
            ViewUserPopupController controller = loader.getController();
            
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("User Details");
            
            controller.setUser(user);
            controller.setPopupStage(popupStage);
            controller.setAdmin(admin);
            controller.setOnRefresh(onRefresh);
            
            Scene scene = new Scene(root, 500, 500);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

