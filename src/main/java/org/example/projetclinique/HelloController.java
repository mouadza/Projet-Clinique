package org.example.projetclinique;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;
    @FXML
    private void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Step 1: Check if the fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and Password cannot be empty.");
            return;
        }

        // Step 3: Attempt to connect to the database
        try (Connection connection = DatabaseConnection.connect()) {
            if (connection != null) {
                // Step 4: Prepare SQL query for authentication
                String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);

                // Step 5: Execute the query and check if a result is returned
                ResultSet resultSet = statement.executeQuery();

                // Step 6: Debugging - Print if any result is returned
                if (resultSet.next()) {
                    HelloApplication.loadPage("dashboard.fxml");
                    System.out.println("Login Successfull!!");
                } else {
                    System.out.println("Login failed. Invalid username or password.");
                    errorLabel.setText("Invalid username or password.");
                }
            } else {
                errorLabel.setText("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            // Step 7: Handle any database errors
            e.printStackTrace();
            errorLabel.setText("Error occurred while connecting to the database.");
        }
    }



}
