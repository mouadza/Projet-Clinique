package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.sql.*;

public class UtilisateursController {
    public void onBtnAccueilClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("dashboard.fxml");
    }

    public void onBtnPatientsClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("patient.fxml");
    }

    public void onBtnActeMedicClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("actesmedicaux.fxml");
    }

    public void onBtnUserClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("utilisateurs.fxml");
    }

    public void onLogoutButtonClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("hello-view.fxml");
    }

    public void onBtnConfigClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("config.fxml");
    }

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField teleField;
    @FXML private TextField cinField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TableView<Secretaire> secretaireTable;
    @FXML private TableColumn<Secretaire, String> nomColumn;
    @FXML private TableColumn<Secretaire, String> prenomColumn;
    @FXML private TableColumn<Secretaire, String> teleColumn;
    @FXML private TableColumn<Secretaire, String> cinColumn;
    @FXML private TableColumn<Secretaire, String> usernameColumn;

    private ObservableList<Secretaire> secretaireList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        prenomColumn.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());
        teleColumn.setCellValueFactory(cellData -> cellData.getValue().teleProperty());
        cinColumn.setCellValueFactory(cellData -> cellData.getValue().cinProperty());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        secretaireTable.setItems(secretaireList);

        loadDataFromDatabase();
    }


    private void loadDataFromDatabase() {
        String query = "SELECT * FROM secretaire";
        ResultSet rs = DatabaseConnection.executeQuery(query);
        try {
            while (rs != null && rs.next()) {
                Secretaire secretaire = new Secretaire(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("tele"),
                        rs.getString("CIN"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                secretaireList.add(secretaire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addSecretaire() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String tele = teleField.getText();
        String cin = cinField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if any input field is empty
        if (nom.isEmpty() || prenom.isEmpty() || tele.isEmpty() || cin.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;  // Stop further execution if validation fails
        }

        // SQL query using prepared statements
        String query = "INSERT INTO secretaire (nom, prenom, tele, CIN, username, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the values for the query parameters
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, prenom);
            preparedStatement.setString(3, tele);
            preparedStatement.setString(4, cin);
            preparedStatement.setString(5, username);
            preparedStatement.setString(6, password);  // Store password as plain text

            // Execute the update and check the number of affected rows
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Secretaire newSecretaire = new Secretaire(nom, prenom, tele, cin, username, password);
                secretaireList.add(newSecretaire);
                clearForm();
                showAlert("Succès", "Le secrétaire a été ajouté avec succès.");
            } else {
                showAlert("Erreur", "Une erreur s'est produite lors de l'ajout.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de l'ajout à la base de données.");
        }
    }



    @FXML
    private void deleteSecretaire() {
        // Get the selected Secretaire
        Secretaire selectedSecretaire = secretaireTable.getSelectionModel().getSelectedItem();

        // Check if a Secretaire is selected
        if (selectedSecretaire == null) {
            showErrorAlert("Veuillez sélectionner un secrétaire à supprimer.");
            return;
        }

        // Show confirmation alert
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer le secrétaire : "
                + selectedSecretaire.getPrenom() + " " + selectedSecretaire.getNom() + " ?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Proceed with the deletion from the database
                deleteSecretaireFromDatabase(selectedSecretaire.getCin());
            }
        });
    }

    private void deleteSecretaireFromDatabase(String cin) {
        // Use the correct parameter (cin) in the query
        String query = "DELETE FROM secretaire WHERE CIN = ?";  // Use CIN as the identifier
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, cin);  // Set the CIN value in the query

            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the deletion was successful
            if (rowsAffected > 0) {
                // Remove the Secretaire from the table list (UI update)
                secretaireList.removeIf(secretaire -> secretaire.getCin().equals(cin));
                showAlert("Succès", "Le secrétaire a été supprimé avec succès.");
            } else {
                showErrorAlert("Une erreur s'est produite lors de la suppression du secrétaire.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Une erreur s'est produite lors de la suppression à la base de données.");
        }
    }



    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        teleField.clear();
        cinField.clear();
        usernameField.clear();
        passwordField.clear();
    }
    private void showErrorAlert(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Erreur");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
