package org.example.projetclinique;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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

    @FXML
    private TextField txtNom, txtPrenom, txtTele, txtCIN, txtUsername, txtPassword;

    @FXML
    private TableView<Secretaire> tableSecretaires;

    @FXML
    private TableColumn<Secretaire, Integer> colId;

    @FXML
    private TableColumn<Secretaire, String> nomColumn, prenomColumn, teleColumn, cinColumn, usernameColumn;

    private ObservableList<Secretaire> secretaireList;

    public void initialize() {
        // Initialize ObservableList
        secretaireList = FXCollections.observableArrayList();

        // Bind Table Columns to Secretaire properties
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        prenomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        teleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTele()));
        cinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCin()));
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));

        // Attach ObservableList to TableView
        tableSecretaires.setItems(secretaireList);

        // Load data from database
        loadSecretaires();
    }

    public void addSecretaire(ActionEvent event) {
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String tele = txtTele.getText().trim();
        String cin = txtCIN.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || tele.isEmpty() || cin.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            String query = "INSERT INTO Secretaire (nom, prenom, tele, cin, username, password) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, tele);
            stmt.setString(4, cin);
            stmt.setString(5, username);
            stmt.setString(6, password);
            stmt.executeUpdate();

            showAlert("Success", "Secretaire added successfully.", Alert.AlertType.INFORMATION);
            loadSecretaires();
        } catch (SQLException e) {
            showAlert("Error", "Could not add secretaire: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void deleteSecretaire(ActionEvent event) {
        Secretaire selected = tableSecretaires.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a secretaire to delete.", Alert.AlertType.ERROR);
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            String query = "DELETE FROM Secretaire WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();

            showAlert("Success", "Secretaire deleted successfully.", Alert.AlertType.INFORMATION);
            loadSecretaires();
        } catch (SQLException e) {
            showAlert("Error", "Could not delete secretaire: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadSecretaires() {
        secretaireList.clear();

        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT * FROM Secretaire";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                secretaireList.add(new Secretaire(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("tele"),
                        rs.getString("cin"),
                        rs.getString("username"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            showAlert("Error", "Could not load secretaires: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
