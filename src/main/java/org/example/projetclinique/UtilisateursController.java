package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UtilisateursController {
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField teleField;
    @FXML private TextField cinField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> typeUserComboBox;
    @FXML private TableView<Users> secretaireTable;
    @FXML private TableColumn<Users, String> nomColumn;
    @FXML private TableColumn<Users, String> prenomColumn;
    @FXML private TableColumn<Users, String> teleColumn;
    @FXML private TableColumn<Users, String> cinColumn;
    @FXML private TableColumn<Users, String> usernameColumn;
    @FXML private TableColumn<Users, String> typeUserColumn;

    private ObservableList<Users> secretaireList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind table columns to Users properties
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        prenomColumn.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());
        teleColumn.setCellValueFactory(cellData -> cellData.getValue().teleProperty());
        cinColumn.setCellValueFactory(cellData -> cellData.getValue().cinProperty());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        typeUserColumn.setCellValueFactory(cellData -> cellData.getValue().newFieldProperty());

        secretaireTable.setItems(secretaireList);

        // Initialize ComboBox options
        typeUserComboBox.setItems(FXCollections.observableArrayList("Docteur", "Secretaire"));

        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        String query = "SELECT * FROM users";
        try (ResultSet rs = DatabaseConnection.executeQuery(query)) {
            while (rs != null && rs.next()) {
                Users secretaire = new Users(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("tele"),
                        rs.getString("CIN"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("TypeUser") // Update this field based on your database column
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
        String typeUser = typeUserComboBox.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || tele.isEmpty() || cin.isEmpty() || username.isEmpty() || password.isEmpty() || typeUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        String query = "INSERT INTO Users (nom, prenom, tele, CIN, username, password, TypeUser) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, prenom);
            preparedStatement.setString(2, nom);
            preparedStatement.setString(3, tele);
            preparedStatement.setString(4, cin);
            preparedStatement.setString(5, username);
            preparedStatement.setString(6, password);
            preparedStatement.setString(7, typeUser);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Users newSecretaire = new Users(nom, prenom, tele, cin, username, password, typeUser);
                secretaireList.add(newSecretaire);
                clearForm();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le secrétaire a été ajouté avec succès.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de l'ajout.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de l'ajout à la base de données.");
        }
    }
    @FXML
    private void deleteSecretaire() {
        // Get the selected Secretaire from the TableView
        Users selectedSecretaire = secretaireTable.getSelectionModel().getSelectedItem();

        if (selectedSecretaire != null) {
            // If a Secretaire is selected, get the CIN and proceed with deletion
            int id = selectedSecretaire.getId();
            deleteSecretaireFromDatabase(id);  // Call the delete method with the CIN
        } else {
            // If no Secretaire is selected, show an alert
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un secrétaire à supprimer.");
        }
    }
    private void deleteSecretaireFromDatabase(int id) {
        // Use the correct parameter (cin) in the query
        String query = "DELETE FROM users WHERE id = ?";  // Use CIN as the identifier
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);  // Set the CIN value in the query

            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the deletion was successful
            if (rowsAffected > 0) {
                // Remove the Secretaire from the table list (UI update)
                secretaireList.removeIf(secretaire -> secretaire.getCin().equals(id));
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le secrétaire a été supprimé avec succès.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de la suppression du secrétaire.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur","Une erreur s'est produite lors de la suppression à la base de données.");
        }
    }

    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        teleField.clear();
        cinField.clear();
        usernameField.clear();
        passwordField.clear();
        typeUserComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods
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
}
