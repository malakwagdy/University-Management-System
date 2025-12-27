package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class AssignCourseController {

    @FXML
    private ComboBox<Instructor> instructorComboBox;

    private int courseId;
    private Instructor departmentHead;
    private Stage stage;
    private Runnable onCourseAssigned;
    private DatabaseManager dm = new DatabaseManager();

    public static void show(int courseId, Instructor departmentHead, Runnable onActionComplete) {
        try {
            FXMLLoader loader = new FXMLLoader(AssignCourseController.class.getResource("AssignCourse.fxml"));
            Parent root = loader.load();

            AssignCourseController controller = loader.getController();
            controller.initData(courseId, departmentHead, onActionComplete);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Assign Course to Instructor");
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initData(int courseId, Instructor departmentHead, Runnable onActionComplete) {
        this.courseId = courseId;
        this.departmentHead = departmentHead;
        this.onCourseAssigned = onActionComplete;
        loadDepartmentInstructors();
    }

    private void setStage(Stage stage) {
        this.stage = stage;
    }

    private void loadDepartmentInstructors() {
        try {
            ArrayList<Instructor> allInstructors = dm.getAllInstructors();
            ArrayList<Instructor> deptInstructors = new ArrayList<>();

            if (departmentHead != null && departmentHead.getDepartmentName() != null) {
                for (Instructor instructor : allInstructors) {
                    if (departmentHead.getDepartmentName().equals(instructor.getDepartmentName())) {
                        deptInstructors.add(instructor);
                    }
                }
            }

            ObservableList<Instructor> observableInstructors = FXCollections.observableArrayList(deptInstructors);
            instructorComboBox.setItems(observableInstructors);

            instructorComboBox.setConverter(new javafx.util.StringConverter<Instructor>() {
                @Override
                public String toString(Instructor instructor) {
                    return instructor != null ? instructor.getName() + " (" + instructor.getId() + ")" : "";
                }

                @Override
                public Instructor fromString(String string) {
                    return null;
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load instructors.");
        }
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        Instructor selectedInstructor = instructorComboBox.getValue();

        if (selectedInstructor == null) {
            showAlert("Error", "Please select an instructor.");
            return;
        }

        try {
            dm.addCurrentCourse(selectedInstructor.getId(), courseId);
            showAlert("Success", "Course assigned successfully.");
            if (onCourseAssigned != null) {
                onCourseAssigned.run();
            }
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to assign course. It might already be assigned.");
        }
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
