package Controllers;

import DAO.DatabaseConnection;
import DAO.MainApplication;
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

        // Step 2: Attempt to connect to the database
        try (Connection connection = DatabaseConnection.connect()) {
            if (connection != null) {
                // Query to check for user credentials and role
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String userType = resultSet.getString("TypeUser"); // Assuming "type" column indicates the role
                    if (userType.equals("Docteur")) {
                        MainApplication.loadPage("dashboard.fxml");
                    }
                    if (userType.equals("Secretaire")) {
                        MainApplication.loadPage("SecretairePages/Home.fxml");
                    }
                    System.out.println(userType + " login successful!");
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
