package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class BookClassroomController {
    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timeCombo;

    private int hallId;
    private Instructor instructor;
    private Runnable onSaveCallback;
    private Stage stage;

    /**
     * Static method to show the BookClassroom popup
     * 
     * @param hallId         The hall/classroom ID to book
     * @param instructor     The current instructor booking the hall
     * @param onSaveCallback Optional callback to run after successful booking
     */
    public static void show(int hallId, Instructor instructor, Runnable onSaveCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(BookClassroomController.class.getResource("BookClassroom.fxml"));
            Scene scene = new Scene(loader.load());

            BookClassroomController controller = loader.getController();
            controller.hallId = hallId;
            controller.instructor = instructor;
            controller.onSaveCallback = onSaveCallback;

            Stage stage = new Stage();
            controller.stage = stage;
            stage.setTitle("Book Classroom");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        setupTimeSlots();
        restrictPastDates();
    }

    private void restrictPastDates() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });
    }

    private void setupTimeSlots() {
        ObservableList<String> timeSlots = FXCollections.observableArrayList(
                "08:00",
                "09:30",
                "11:00",
                "12:30",
                "14:00",
                "15:30",
                "17:00");

        timeCombo.setItems(timeSlots);
    }

    @FXML
    public void HandleConfirmBooking() {
        if (datePicker.getValue() == null || timeCombo.getValue() == null) {
            showAlert("Validation Error", "Please select date and time");
            return;
        }

        String slotDate = datePicker.getValue().toString(); // yyyy-MM-dd
        String slotTime = timeCombo.getValue();

        boolean success = DatabaseManager.reserveSlot(
                hallId,
                slotDate,
                slotTime);

        if (success) {
            DatabaseManager.bookClassroom(hallId); // optional: mark unavailable
            showSuccess("Booking confirmed!");

            // Run callback if provided
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            closeWindow();
        } else {
            showAlert("Error", "This slot is already booked.");
        }
    }

    private void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
