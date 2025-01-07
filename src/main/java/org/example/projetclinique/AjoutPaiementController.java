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


    public class AjoutPaiementController {

        @FXML
        private TableView<Acte> tableActes;

        @FXML
        private TableColumn<Acte, Integer> colID;

        @FXML
        private TableColumn<Acte, String> colPatient;

        @FXML
        private TableColumn<Acte, Double> colPrix;

        @FXML
        private TableColumn<Acte, String> colEtat;

        @FXML
        private TableColumn<Acte, Integer> colPatientID;

        private ObservableList<Acte> actesList = FXCollections.observableArrayList();

        @FXML
        public void initialize() {
            // Set up each column with the appropriate property
            colID.setCellValueFactory(new PropertyValueFactory<>("ID"));
            colPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
            colPrix.setCellValueFactory(new PropertyValueFactory<>("prixComptabilise"));
            colEtat.setCellValueFactory(new PropertyValueFactory<>("etatDeLActe"));
            colPatientID.setCellValueFactory(new PropertyValueFactory<>("patientID"));

            // Load data from the database
            loadDataFromDatabase();
        }

        private void loadDataFromDatabase() {
            String query = "SELECT a.ID, a.date_debut, a.prix_comptabilise, a.etat_de_l_acte, a.date_de_fin, CONCAT(p.prenom + '' + p.nom) AS patient_name, p.id AS patient_id" +
                    "FROM ActeMedicaux a " +
                    "JOIN Patient p ON a.patient_concerne = p.id";

            try (Connection conn = DatabaseConnection.connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                // Clear the current list to avoid duplication
                actesList.clear();

                // Iterate over the result set and create Acte objects
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String dateDebut = rs.getString("date_debut");
                    double prixComptabilise = rs.getDouble("prix_comptabilise");
                    String etatDeLActe = rs.getString("etat_de_l_acte");
                    String dateFin = rs.getString("date_de_fin");
                    int patientID = rs.getInt("patient_id");
                    String patientName = rs.getString("patient_name");

                    // Create a new Acte object and add it to the list
                    Acte acte = new Acte(id, dateDebut, prixComptabilise, etatDeLActe, dateFin, patientID, patientName);
                    actesList.add(acte);
                }

                // Set the table data
                tableActes.setItems(actesList);

            } catch (SQLException e) {
                e.printStackTrace();  // Handle exceptions as needed
            }
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
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    }
