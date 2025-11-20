package com.example.ums;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class UMSApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneController.showInitialScene(stage, "Login.fxml", "University Management System - Login", true);
    }
}
