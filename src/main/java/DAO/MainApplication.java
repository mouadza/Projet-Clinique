package DAO;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class MainApplication extends Application {
    public static Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage; // Assign the primary stage
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("CliniDent");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void loadPage(String fxmlFile) {
        try {
            System.out.println("Loading FXML file: " + fxmlFile);
            Parent root = FXMLLoader.load(MainApplication.class.getResource(fxmlFile));
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
    public static void oadPageWithController(String fxmlFile, Consumer<Object> controllerHandler) {
        try {
            System.out.println("Loading FXML file: " + fxmlFile);

            // Use FXMLLoader manually to load the FXML
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlFile));
            Parent root = loader.load();
            System.out.println("FXML file loaded successfully.");
            Object controller = loader.getController();

            // Apply the controller handler logic
            controllerHandler.accept(controller);

            // Proceed with the existing loadPage logic to set the scene
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
