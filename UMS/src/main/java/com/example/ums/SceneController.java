package com.example.ums;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneController {

    private static Stage primaryStage;
    private static boolean maximizeByDefault = true;

    public static void switchScene(ActionEvent event, String fileName, String title) throws IOException {
        // Convert to URL and load
        String absolutePath = GlobalData.path + fileName;
        FXMLLoader fxmlLoader = new FXMLLoader(new java.io.File(absolutePath).toURI().toURL());
        Parent root = fxmlLoader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        // Save current maximized state
        boolean wasMaximized = stage.isMaximized();
        
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
        
        // Restore maximized state if it was maximized or if maximizeByDefault is true
        if (wasMaximized || maximizeByDefault) {
            Platform.runLater(() -> stage.setMaximized(true));
        }
    }

    public static void Popup(ActionEvent event, String fileName, String title) throws IOException {
        try {
            String absolutePath = GlobalData.path + fileName;
            FXMLLoader loader = new FXMLLoader(new java.io.File(absolutePath).toURI().toURL());
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
            popupStage.setTitle(title);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait(); // Wait until the popup is closed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void refreshPage(ActionEvent event, String fileName) throws IOException {
        // Refresh the page
        String absolutePath = GlobalData.path + fileName;

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        
        // Save current maximized state
        boolean wasMaximized = stage.isMaximized();
        
        FXMLLoader loader = new FXMLLoader(new java.io.File(absolutePath).toURI().toURL());
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        
        // Restore maximized state if it was maximized or if maximizeByDefault is true
        if (wasMaximized || maximizeByDefault) {
            Platform.runLater(() -> stage.setMaximized(true));
        }
    }

    // ===== New primary-stage helpers (replacement for StageManager) =====

    public static void initializePrimaryStage(Stage stage, boolean maximizeByDefaultSetting) {
        primaryStage = stage;
        maximizeByDefault = maximizeByDefaultSetting;
    }

    public static void showInitialScene(Stage stage, String resource, String title, boolean maximizeWindow) throws IOException {
        initializePrimaryStage(stage, maximizeWindow);
        Scene scene = buildSceneFromResource(resource);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
        if (maximizeByDefault) {
            Platform.runLater(() -> stage.setMaximized(true));
        }
    }

    public static void switchScene(String resource, String title) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not initialized. Call initializePrimaryStage first.");
        }
        boolean wasMaximized = primaryStage.isMaximized();
        Scene scene = buildSceneFromResource(resource);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        if (wasMaximized || maximizeByDefault) {
            Platform.runLater(() -> primaryStage.setMaximized(true));
        }
    }

    public static Stage openWindow(String resource, String title, boolean maximize) throws IOException {
        Scene scene = buildSceneFromResource(resource);
        Stage newStage = new Stage();
        newStage.setTitle(title);
        newStage.setScene(scene);
        newStage.show();
        if (maximize) {
            Platform.runLater(() -> newStage.setMaximized(true));
        }
        return newStage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static Scene buildSceneFromResource(String resource) throws IOException {
        URL resourceUrl = SceneController.class.getResource(resource);
        if (resourceUrl == null) {
            throw new IOException("FXML resource not found: " + resource);
        }
        FXMLLoader loader = new FXMLLoader(resourceUrl);
        Parent root = loader.load();
        return new Scene(root);
    }

    public static void switchTo(String resource) {
        try {
            switchScene(resource, "UMS System");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: Could not switch to " + resource);
        }
    }

}

