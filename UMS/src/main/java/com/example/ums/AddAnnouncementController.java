package com.example.ums;

import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddAnnouncementController {
    @FXML
    private TextField titleTextField;
    @FXML
    private TextArea announceTextArea;

    private User currentUser;
    private int courseId;
    private Runnable onSaveCallback;
    private Stage stage;

    public static void show(int courseId, User user, Runnable onSaveCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(AddAnnouncementController.class.getResource("AddAnnouncement.fxml"));
            Scene scene = new Scene(loader.load());

            AddAnnouncementController controller = loader.getController();
            controller.courseId = courseId;
            controller.currentUser = user;
            controller.onSaveCallback = onSaveCallback;

            Stage stage = new Stage();
            controller.stage = stage;
            stage.setTitle("Add Announcement");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSavebtn() {
        String title = titleTextField.getText().trim();
        String content = announceTextArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields.");
            return;
        }

        String date = LocalDate.now().toString();
        Announcment announcement = new Announcment(title, content, date, courseId);

        try {
            if (currentUser instanceof Instructor) {
                ((Instructor) currentUser).createAnnouncement(announcement);
            } else if (currentUser instanceof Admin) {
                ((Admin) currentUser).createAnnouncement(announcement);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Announcement created successfully!");
            alert.showAndWait();

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create announcement.");
        }
    }

    @FXML
    public void handleCancelBtn() {
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
