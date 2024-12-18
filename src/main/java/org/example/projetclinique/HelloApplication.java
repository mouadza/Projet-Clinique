package org.example.projetclinique;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public static Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage; // Assign the primary stage
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Clinique Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void loadPage(String fxmlFile) {
        try {
            System.out.println("Loading FXML file: " + fxmlFile);
            Parent root = FXMLLoader.load(HelloApplication.class.getResource(fxmlFile));
            System.out.println("FXML file loaded successfully.");

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();

            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }
        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }

}

