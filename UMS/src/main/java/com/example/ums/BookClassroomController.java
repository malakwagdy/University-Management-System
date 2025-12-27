package com.example.ums;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

import static com.example.ums.ViewClassroomsController.showAlert;

public class BookClassroomController {
    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timeCombo;

    private int hallId;

    @FXML
    public void initialize() {
        hallId = BookingContext.getSelectedHallId();
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
                "17:00"
        );

        timeCombo.setItems(timeSlots);
    }
    public void HandleConfirmBooking(ActionEvent actionEvent) {
        if (datePicker.getValue() == null || timeCombo.getValue() == null) {
            showAlert("alert","Please select date and time");
            return;
        }

        String slotDate = datePicker.getValue().toString(); // yyyy-MM-dd
        String slotTime = timeCombo.getValue();

        boolean success = DatabaseManager.reserveSlot(
                hallId,
                slotDate,
                slotTime
        );

        if (success) {
            DatabaseManager.bookClassroom(hallId); // optional: mark unavailable
            showSuccess("Booking confirmed!");
            closeWindow();
        } else {
            showAlert("alert","This slot is already booked.");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }

    private void showSuccess(String s) {
        
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(s);
            alert.showAndWait();
        
    }
}

//@FXML
//private void confirmBooking() {
//    int hallId = BookingContext.getSelectedHallId();
//    LocalDate date = datePicker.getValue();
//    String time = timeCombo.getValue();
//
//    if (date == null || time == null) {
//        showError("Please select date and time");
//        return;
//    }
//
//    ReserveSlotDAO.insert(hallId, date.toString(), time);
//}

