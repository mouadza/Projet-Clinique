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

public class    HelloController {

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

        // Step 2: Attempt to connect to the database
        try (Connection connection = DatabaseConnection.connect()) {
            if (connection != null) {
                // Step 3: Check for `admin` credentials
                String adminQuery = "SELECT * FROM admin WHERE username = ? AND password = ?";
                PreparedStatement adminStatement = connection.prepareStatement(adminQuery);
                adminStatement.setString(1, username);
                adminStatement.setString(2, password);

                ResultSet adminResultSet = adminStatement.executeQuery();

                if (adminResultSet.next()) {
                    HelloApplication.loadPage("dashboard.fxml");
                    System.out.println("Admin login successful!");
                    return;
                }

                // Step 4: Check for `secretaire` credentials
                String secretaireQuery = "SELECT * FROM secretaire WHERE username = ? AND password = ?";
                PreparedStatement secretaireStatement = connection.prepareStatement(secretaireQuery);
                secretaireStatement.setString(1, username);
                secretaireStatement.setString(2, password);

                ResultSet secretaireResultSet = secretaireStatement.executeQuery();

                if (secretaireResultSet.next()) {
                    HelloApplication.loadPage("SecretairePages/Home.fxml");
                    System.out.println("Secretaire login successful!");
                    return;
                }

                // Step 5: If no match found
                errorLabel.setText("Invalid username or password.");
                System.out.println("Login failed. Invalid username or password.");
            } else {
                errorLabel.setText("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            // Step 6: Handle any database errors
            e.printStackTrace();
            errorLabel.setText("Error occurred while connecting to the database.");
        }
    }




}
