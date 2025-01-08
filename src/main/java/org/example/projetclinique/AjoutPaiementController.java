package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AjoutPaiementController {
    @FXML
    private DatePicker datePaiement;

    @FXML
    private TextField montant;

    @FXML
    private ComboBox<String> methodPaiement;
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


    @FXML
    public void initialize() {
        System.out.println("Initializing Table Columns...");
        // Setup column properties
        colID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixComptabilise"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etatDeLActe"));
        colPatientID.setCellValueFactory(new PropertyValueFactory<>("patientID"));

        // Load data
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        ObservableList<Acte> actesList = FXCollections.observableArrayList();
        String query = "SELECT a.ID, a.date_debut, a.prix_comptabilise, a.etat_de_l_acte, a.date_de_fin, " +
                "CONCAT(p.prenom, ' ', p.nom) AS patient_name, p.id AS patient_id " +
                "FROM ActeMedicaux a " +
                "JOIN Patient p ON a.patient_concerne = p.id " +
                "WHERE a.prix_comptabilise > 0";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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

                // Print fetched data to console
                System.out.println("Fetched data: ID = " + patientID + ", Patient Name = " + patientName);
            }

            // Set the table data
            tableActes.setItems(actesList);

        } catch (SQLException e) {
            e.printStackTrace();  // Handle exceptions as needed
        }
    }
    @FXML
    private void ajouterPaiement() {
        // Fetch selected Acte (for example from the table)
        Acte selectedActe = tableActes.getSelectionModel().getSelectedItem();
        if (selectedActe == null) {
            showErrorAlert("Please select an Acte.");
            return;
        }

        // Get data from the form
        String datePaiementStr = datePaiement.getValue() != null ? datePaiement.getValue().toString() : null;
        String montantStr = montant.getText();
        String methodPaiementValue = methodPaiement.getValue();

        // Validate if the fields are filled
        if (datePaiementStr == null || datePaiementStr.isEmpty()) {
            showErrorAlert("Please select a payment date.");
            return;
        }

        if (montantStr == null || montantStr.isEmpty()) {
            showErrorAlert("Please enter the amount.");
            return;
        }

        if (methodPaiementValue == null || methodPaiementValue.isEmpty()) {
            showErrorAlert("Please select a payment method.");
            return;
        }

        // Parse the montant value
        double montantValue;
        try {
            montantValue = Double.parseDouble(montantStr);
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid amount. Please enter a valid number.");
            return;
        }

        // Now, insert into the database
        try (Connection conn = DatabaseConnection.connect()) {
            // Begin transaction (if your DB supports it)
            conn.setAutoCommit(false);

            // Insert into Paiements table
            String insertQuery = "INSERT INTO Paiements (acteID, patientID, datePaiement, montant, prix_comptabilise, paiement_method) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                ps.setInt(1, selectedActe.getID());
                ps.setInt(2, selectedActe.getPatientID());
                ps.setString(3, datePaiementStr);
                ps.setDouble(4, montantValue);
                ps.setDouble(5, selectedActe.getPrixComptabilise());
                ps.setString(6, methodPaiementValue);
                ps.executeUpdate();
            }

            // Commit the transaction
            conn.commit();

            // Load the payment page after success
            HelloApplication.loadPage("SecretairePages/paiement.fxml");
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error adding payment: " + e.getMessage());
            // Rollback in case of error
            try (Connection conn = DatabaseConnection.connect()) {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
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
