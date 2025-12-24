package com.example.ums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AddMaterialController {
    @FXML
    private TextField materialNameField;
    @FXML
    private TextField materialFileField;
    @FXML
    private ListView<String> materialsListView;

    private int courseId;
    private Instructor currentUser;
    private Runnable onSaveCallback;
    private Stage stage;
    private String selectedFilePath = null;
    private ArrayList<Material> materials = new ArrayList<>();
    
    public static void show(int courseId, Instructor currentUser, Runnable onSaveCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(AddMaterialController.class.getResource("AddMaterial.fxml"));
            Scene scene = new Scene(loader.load());
            
            AddMaterialController controller = loader.getController();
            controller.courseId = courseId;
            controller.currentUser = currentUser;
            controller.onSaveCallback = onSaveCallback;
            
            Stage stage = new Stage();
            controller.stage = stage;
            stage.setTitle("Add Material");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBrowseMaterial(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Material File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Documents", "*.doc", "*.docx")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            selectedFilePath = file.getAbsolutePath();
            materialFileField.setText(file.getName());
        }
    }

    @FXML
    private void handleAddMaterial(ActionEvent event) {
        String materialName = materialNameField.getText().trim();
        
        if (materialName.isEmpty() || selectedFilePath == null) {
            return;
        }
        
        Material material = new Material(materialName, selectedFilePath);
        materials.add(material);
        
        ObservableList<String> items = FXCollections.observableArrayList(materialsListView.getItems());
        items.add(materialName + " - " + new File(selectedFilePath).getName());
        materialsListView.setItems(items);
        
        materialNameField.clear();
        materialFileField.clear();
        selectedFilePath = null;
    }

    @FXML
    public void handleSaveButton(ActionEvent event) {
        if (materials.isEmpty()) {
            showAlert("Validation Error", "Please add at least one material.");
            return;
        }
        
        for (Material material : materials) {
            currentUser.addMaterial(courseId, material);
        }
        
        if (onSaveCallback != null) {
            onSaveCallback.run();
        }
        
        stage.close();
    }

    @FXML
    public void handleCancelButton(ActionEvent event) {
        stage.close();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
