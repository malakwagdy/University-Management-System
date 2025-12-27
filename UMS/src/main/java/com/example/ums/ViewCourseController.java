package com.example.ums;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;
import java.util.ArrayList;

public class ViewCourseController {
    @FXML
    private Label courseIdLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label bylawLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label professorLabel;
    @FXML
    private Label taLabel;
    @FXML
    private VBox materialsBox;
    @FXML
    private Button saveBtn;
    @FXML
    private Button closeBtn;

    private Course course;
    private Stage popupStage;

    public static void show(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewCourseController.class.getResource("ViewCourse.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("View Course");
            stage.initModality(Modality.APPLICATION_MODAL);

            ViewCourseController controller = loader.getController();
            controller.setCourse(course);
            controller.setPopupStage(stage);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCourse(Course course) {
        this.course = course;
        populateCourseDetails();
    }

    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }

    @FXML
    private void initialize() {
        saveBtn.setVisible(false);
        saveBtn.setManaged(false);
    }

    private void populateCourseDetails() {
        if (course == null)
            return;

        courseIdLabel.setText(String.valueOf(course.getCourseId()));
        nameLabel.setText(course.getCourseName());
        bylawLabel.setText(course.getYear());
        descriptionLabel.setText(course.getCourseDescription());

        ArrayList<Instructor> instructors = Course.getCourseInstructors(course.getCourseId());
        StringBuilder professors = new StringBuilder();
        StringBuilder tas = new StringBuilder();

        for (Instructor instructor : instructors) {
            if ("Professor".equals(instructor.getRole())) {
                if (professors.length() > 0)
                    professors.append(", ");
                professors.append(instructor.getName());
            } else if ("Teaching Assistant".equals(instructor.getRole())) {
                if (tas.length() > 0)
                    tas.append(", ");
                tas.append(instructor.getName());
            }
        }

        professorLabel.setText(professors.length() > 0 ? professors.toString() : "N/A");
        taLabel.setText(tas.length() > 0 ? tas.toString() : "N/A");

        // Populate materialsBox with a row per material: [Label: name] [Hyperlink: url]
        materialsBox.getChildren().clear();
        ArrayList<Material> materials = course.getCourseMaterials(course.getCourseId());
        if (materials != null && !materials.isEmpty()) {
            for (Material material : materials) {
                HBox row = new HBox(10);
                // Make the material name itself the clickable hyperlink
                Hyperlink link = new Hyperlink(material.getMaterialName());
                // show actual URL on hover
                link.setTooltip(new Tooltip(material.geturl()));
                link.setOnAction(evt -> {
                    String urlStr = material.geturl();
                    // Open in system browser with cross-platform fallback
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(new URI(urlStr));
                        } else {
                            String os = System.getProperty("os.name").toLowerCase();
                            if (os.contains("mac")) {
                                Runtime.getRuntime().exec(new String[] { "open", urlStr });
                            } else if (os.contains("win")) {
                                Runtime.getRuntime()
                                        .exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", urlStr });
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
                row.getChildren().add(link);
                materialsBox.getChildren().add(row);
            }
        } else {
            materialsBox.getChildren().add(new Label("N/A"));
        }
    }

    @FXML
    private void handleSaveButton() {
        // Not used in view mode
    }

    @FXML
    private void handleCloseButton() {
        if (popupStage != null) {
            popupStage.close();
        }
    }
}
