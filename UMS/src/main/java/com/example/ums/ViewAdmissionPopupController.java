package com.example.ums;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewAdmissionPopupController {
    @FXML
    private VBox detailsBox;
    @FXML
    private Button acceptBtn;
    @FXML
    private Button rejectBtn;
    @FXML
    private Button closeBtn;
    
    private Admission admission;
    private Admin admin;
    private Stage popupStage;
    private Runnable onRefresh;
    
    public void setAdmission(Admission admission) {
        this.admission = admission;
        populateAdmissionDetails();
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
    
    private void populateAdmissionDetails() {
        if (admission == null) return;
        
        // Add all admission details
        String admissionIdLabel = admission.getAdmissionId() > 0 ? String.valueOf(admission.getAdmissionId()) : "N/A";
        detailsBox.getChildren().add(new Label("Application ID: " + admissionIdLabel));
        detailsBox.getChildren().add(new Label("Name: " + (admission.getName() != null ? admission.getName() : "N/A")));
        detailsBox.getChildren().add(new Label("Email: " + (admission.getEmail() != null ? admission.getEmail() : "N/A")));
        detailsBox.getChildren().add(new Label("Phone Number: " + (admission.getPhoneNumber() != null ? admission.getPhoneNumber() : "N/A")));
        detailsBox.getChildren().add(new Label("Date of Birth: " + (admission.getDateOfBirth() != null ? admission.getDateOfBirth() : "N/A")));
        detailsBox.getChildren().add(new Label("Major: " + (admission.getMajor() != null ? admission.getMajor() : "N/A")));
        detailsBox.getChildren().add(new Label("High School GPA: " + (admission.getHighschoolGPA() != null ? admission.getHighschoolGPA() : "N/A")));
        detailsBox.getChildren().add(new Label("Status: " + (admission.getStatus() != null ? admission.getStatus() : "N/A")));
        detailsBox.getChildren().add(new Label("Year of Admission: " + (admission.getYearOfAdmission() != null ? admission.getYearOfAdmission() : "N/A")));
        
        // Disable Accept/Reject buttons if already processed
        if ("Accepted".equals(admission.getStatus()) || "Rejected".equals(admission.getStatus())) {
            acceptBtn.setDisable(true);
            rejectBtn.setDisable(true);
        }
    }
    
    @FXML
    private void handleAcceptButton(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Acceptance");
        confirmAlert.setHeaderText("Accept Admission");
        confirmAlert.setContentText("Are you sure you want to accept this admission application?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    admin.acceptAdmission(admission);
                    
                    // Close the popup
                    popupStage.close();
                    
                    // Refresh the tables
                    if (onRefresh != null) {
                        onRefresh.run();
                    }
                    
                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Admission Accepted");
                    successAlert.setContentText("The admission has been accepted and a student account has been created.");
                    successAlert.showAndWait();
                } catch (Exception ex) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Failed to Accept Admission");
                    errorAlert.setContentText("An error occurred: " + ex.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });
    }
    
    @FXML
    private void handleRejectButton(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Rejection");
        confirmAlert.setHeaderText("Reject Admission");
        confirmAlert.setContentText("Are you sure you want to reject this admission application?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    admin.rejectAdmission(admission);
                    
                    // Close the popup
                    popupStage.close();
                    
                    // Refresh the tables
                    if (onRefresh != null) {
                        onRefresh.run();
                    }
                    
                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("Admission Rejected");
                    successAlert.setContentText("The admission has been rejected.");
                    successAlert.showAndWait();
                } catch (Exception ex) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Failed to Reject Admission");
                    errorAlert.setContentText("An error occurred: " + ex.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });
    }
    
    @FXML
    private void handleCloseButton(ActionEvent event) {
        popupStage.close();
    }
    
    public static void show(Admission admission, Admin admin, Runnable onRefresh) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewAdmissionPopupController.class.getResource("/com/example/ums/ViewAdmissionPopup.fxml"));
            VBox root = loader.load();
            ViewAdmissionPopupController controller = loader.getController();
            
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Admission Details");
            
            controller.setAdmission(admission);
            controller.setPopupStage(popupStage);
            controller.setAdmin(admin);
            controller.setOnRefresh(onRefresh);
            
            Scene scene = new Scene(root, 500, 400);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

