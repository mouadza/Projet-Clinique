package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaiementController {
    @FXML
    private TableView<Paiement> paiementTable;

    @FXML
    private TableColumn<Paiement, Integer> columnId;
    @FXML
    private TableColumn<Paiement, Integer> columnActeId;
    @FXML
    private TableColumn<Paiement, Integer> columnPatientId;
    @FXML
    private TableColumn<Paiement, Date> columnDatePaiement;
    @FXML
    private TableColumn<Paiement, Double> columnMontant;
    @FXML
    private TableColumn<Paiement, String> columnStatut;

    public void initialize() {
        // Initialize table columns with data
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnActeId.setCellValueFactory(new PropertyValueFactory<>("acteID"));
        columnPatientId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        columnDatePaiement.setCellValueFactory(new PropertyValueFactory<>("datePaiement"));
        columnMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        columnStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Load the data into the table
        loadPaiementData();
    }
    private void loadPaiementData() {
        ObservableList<Paiement> paiements = FXCollections.observableArrayList();

        // Use a SQL query to fetch Paiement data with Patient's prenom and nom
        String query = "SELECT p.id, p.acteID, CONCAT(pt.prenom + ' ' + pt.nom ) AS patient, p.datePaiement, p.montant, p.prix_comptabilise, " +
                "p.reste, p.paiement_method, p.statut " +
                "FROM Paiements p " +
                "INNER JOIN Patient pt ON p.patientId = pt.ID";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Loop through the result set and add each Paiement to the ObservableList
            while (rs.next()) {
                paiements.add(new Paiement(
                        rs.getInt("id"),
                        rs.getInt("acteID"),
                        rs.getString("patient"),
                        rs.getDate("datePaiement"),
                        rs.getDouble("montant"),
                        rs.getDouble("prix_comptabilise"),
                        rs.getDouble("reste"),
                        rs.getString("paiement_method"),
                        rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            showErrorAlert("Erreur lors du chargement des paiements.");
            e.printStackTrace();
        }

        // Set the items of the TableView
        paiementTable.setItems(paiements);
    }


    public void onLogoutButtonClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("hello-view.fxml");
    }
    public void HomePage(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/Home.fxml");
    }
    public void PatientPage(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/patients.fxml");
    }
    public void RendezVousPage(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/rendezvous.fxml");
    }
    public void PaiementPage(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/paiement.fxml");
    }
    public void AjoutPaiementPage(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/AjoutPaiement.fxml");
    }
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
