package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static com.example.ums.User.dm;

public class ViewClassroomsController {

    @FXML
    private TableView<Classroom> HallsTable;

    @FXML
    private TableColumn<Classroom, Integer> colHallId;

    @FXML
    private TableColumn<Classroom, String> colCapacity;

    @FXML
    private TableColumn<Classroom, String> colType;

    @FXML
    private TableColumn<Classroom, Boolean> colMaintenance;

    @FXML
    private TableColumn<Classroom, Boolean> colAvailability;


    @FXML
    public void initialize() {

        colHallId.setCellValueFactory(new PropertyValueFactory<>("hallId"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("hallCapacity"));
        colType.setCellValueFactory(new PropertyValueFactory<>("hallType"));
        colMaintenance.setCellValueFactory(new PropertyValueFactory<>("hallMaintenance"));
        colAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));

        loadHallsData();
    }


    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

//    private void loadHallData() {
//        ObservableList<Classroom> hallList = FXCollections.observableArrayList();
//
//        String sql = "SELECT hallid, hallcapacity, halltype, hallmaintenance, availability FROM hall";
//
//        try (Connection conn = DriverManager.getConnection(
//                "jdbc:postgresql://localhost:5432/yourDB",
//                "username",
//                "password");
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            while (rs.next()) {
//                hallList.add(new Classroom(
//                        //rs.getInt("hallid"),
//                        rs.getString("hallcapacity"),
//                        rs.getString("halltype")
//                        //rs.getBoolean("hallmaintenance"),
//                        //rs.getBoolean("availability")
//                ));
//            }
//
//            HallsTable.setItems(hallList);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//private void loadHallDataAsync() {
//
//    Task<ObservableList<Classroom>> task = new Task<>() {
//        @Override
//        protected ObservableList<Classroom> call() throws Exception {
//            ObservableList<Classroom> hallList = FXCollections.observableArrayList();
//
//            String sql = "SELECT hallid, hallcapacity, halltype, hallmaintenance, availability FROM hall";
//
//            try (Connection conn = DriverManager.getConnection(
//                    "jdbc:postgresql://localhost:5432/ums_db",
//                    "postgres",
//                    "Postgres@2025");
//                 Statement stmt = conn.createStatement();
//                 ResultSet rs = stmt.executeQuery(sql)) {
//
//                while (rs.next()) {
//                    hallList.add(new Classroom(
//                            //rs.getInt("hallid"),
//                            rs.getString("hallcapacity"),
//                            rs.getString("halltype")
//                            //rs.getBoolean("hallmaintenance"),
//                            //rs.getBoolean("availability")
//                    ));
//                }
//            }
//
//            return hallList;
//        }
//    };
//
//    task.setOnSucceeded(e -> HallsTable.setItems(task.getValue()));
//    task.setOnFailed(e -> task.getException().printStackTrace());
//
//    new Thread(task).start();
//}

    private void loadHallsData() {

        Task<ObservableList<Classroom>> task = new Task<>() {
            @Override
            protected ObservableList<Classroom> call() throws Exception {

                DatabaseManager db = new DatabaseManager();

                // ✅ Authentication comes ONLY from DatabaseManager
                ArrayList<Classroom> classrooms = db.getAllClassrooms();

                return FXCollections.observableArrayList(classrooms);
            }
        };

        task.setOnSucceeded(e -> HallsTable.setItems(task.getValue()));

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
        });

        new Thread(task).start();
    }
    @FXML
    private void handleBackbutton(ActionEvent event) {
        try {
            SceneController.switchScene(event, "AdminDashboard.fxml", "Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    private void handleBookbutton() {

        Classroom selected = HallsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("No Selection", "Please select a classroom first.");
            return;
        }

        // Case 1: already booked
        if (!selected.isAvailability()) {
            showAlert("Booking Error", "This classroom is already booked.");
            return;
        }

        if (selected.isHallMaintenance()) {
            showAlert("Booking Error", "This classroom is under maintenance and cannot be booked.");
            return;
        }

        // Case 2: available → book it
        boolean success = DatabaseManager.bookClassroom(selected.getHallId());

        if (success) {
            selected.setAvailability(false);   // update object
            HallsTable.refresh();               // update UI
            showAlert("Success", "Classroom booked successfully.");
        } else {
            showAlert("Database Error", "Could not book classroom.");
        }
    }

    @FXML
    public void handleNewclassroomButton(ActionEvent event){
        try {
            SceneController.switchScene(event, "AddClassroom.fxml", "Add new Classroom");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
