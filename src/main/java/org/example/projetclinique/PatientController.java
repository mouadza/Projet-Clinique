package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class PatientController {
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
    private Label lblPatientsActifs;

    @FXML
    private Label lblTotal;
    @FXML
    private TableView<Patient> tablePatients;

    @FXML
    private TableColumn<Patient, String> colNom, colPrenom, colDateNaissance, colTelephone, colCIN, colAdresse;

    @FXML
    private TableColumn<Patient, Boolean> colActif;

    @FXML
    private TextField tfNom, tfPrenom, tfTelephone, tfCIN, tfAdresse;

    @FXML
    private DatePicker dpDateNaissance;

    @FXML
    private CheckBox cbActif;

    @FXML
    private Button btnAjouter; // Ensure this button is declared in the FXML

    private ObservableList<Patient> patientList = FXCollections.observableArrayList();
    private Patient selectedPatient;

    @FXML
    public void initialize() {
        // Set up TableView columns
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colCIN.setCellValueFactory(new PropertyValueFactory<>("CIN"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colActif.setCellValueFactory(new PropertyValueFactory<>("actif"));

        // Table row selection listener
        tablePatients.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedPatient = newSelection;
            }
        });

        loadPatientData();
        loadStatistics();
    }
    private void loadStatistics() {
        Connection connection = DatabaseConnection.connect();

        if (connection == null) {
            System.err.println("Failed to establish a database connection.");
            return;
        }

        try (Statement statement = connection.createStatement()) {
            // Requête pour compter les patients actifs
            String queryPatientsActifs = "SELECT COUNT(*) AS total FROM Patient WHERE actif = TRUE";
            ResultSet rsPatients = statement.executeQuery(queryPatientsActifs);
            if (rsPatients.next()) {
                lblPatientsActifs.setText(String.valueOf(rsPatients.getInt("total")));
            }

            // Requête pour compter les rendez-vous du jour
            String queryRendezVous = """
                SELECT COUNT(*) AS total
                FROM Patient
            """;
            ResultSet rsRendezVous = statement.executeQuery(queryRendezVous);
            if (rsRendezVous.next()) {
                lblTotal.setText(String.valueOf(rsRendezVous.getInt("total")));
            }

        } catch (SQLException e) {
            System.err.println("Error while fetching statistics: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Failed to close the database connection.");
                e.printStackTrace();
            }
        }
    }

    private void loadPatientData() {
        try {
            patientList.clear();
            Connection conn = DatabaseConnection.connect();

            String query = "SELECT nom, prenom, date_naissance, telephone, CIN, adresse, actif FROM Patient";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                patientList.add(new Patient(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("CIN"),
                        rs.getString("adresse"),
                        rs.getBoolean("actif")
                ));
            }

            tablePatients.setItems(patientList);
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            showErrorAlert("Erreur lors du chargement des données.");
            e.printStackTrace();
        }
    }

    private void populateFormWithPatient(Patient patient) {
        tfNom.setText(patient.getNom());
        tfPrenom.setText(patient.getPrenom());
        dpDateNaissance.setValue(LocalDate.parse(patient.getDateNaissance()));
        tfTelephone.setText(patient.getTelephone());
        tfCIN.setText(patient.getCIN());
        tfAdresse.setText(patient.getAdresse());
        cbActif.setSelected(patient.isActif());
    }
    @FXML
    private void onModifierButtonClick() {
        if (selectedPatient != null) {
            // Populate form fields with selected patient's data
            populateFormWithPatient(selectedPatient);
            btnAjouter.setText("Enregistrer"); // Change button text to "Enregistrer"
        } else {
            showErrorAlert("Veuillez sélectionner un patient à modifier.");
        }
    }

    @FXML
    private void onSavePatientClick() {
        try {
            // Gather input values
            String nom = tfNom.getText();
            String prenom = tfPrenom.getText();
            String dateNaissance = dpDateNaissance.getValue() != null ? dpDateNaissance.getValue().toString() : null;
            String telephone = tfTelephone.getText();
            String cin = tfCIN.getText();
            String adresse = tfAdresse.getText();
            boolean actif = cbActif.isSelected();

            // Validate required fields
            if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || cin.isEmpty()) {
                showErrorAlert("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Add or update the patient
            if (selectedPatient == null) {
                // Add new patient
                addPatientToDatabase(nom, prenom, dateNaissance, telephone, cin, adresse, actif);
            } else {
                // Update existing patient
                updatePatientInDatabase(selectedPatient.getCIN(), nom, prenom, dateNaissance, telephone, cin, adresse, actif);
                selectedPatient = null; // Reset selection
                btnAjouter.setText("Ajouter"); // Reset button text
            }

            clearFormFields();  // Reset the form
            loadPatientData();  // Refresh patient list
        } catch (Exception e) {
            showErrorAlert("Erreur lors de la sauvegarde du patient.");
            e.printStackTrace();
        }
    }

    private void addPatientToDatabase(String nom, String prenom, String dateNaissance,
                                      String telephone, String cin, String adresse, boolean actif) {
        try {
            Connection conn = DatabaseConnection.connect();
            String query = "INSERT INTO Patient (nom, prenom, date_naissance, telephone, CIN, adresse, actif) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, dateNaissance);
            stmt.setString(4, telephone);
            stmt.setString(5, cin);
            stmt.setString(6, adresse);
            stmt.setBoolean(7, actif);

            stmt.executeUpdate();
            loadStatistics();
            conn.close();
            showInfoAlert("Patient ajouté avec succès!");
        } catch (Exception e) {
            showErrorAlert("Erreur lors de l'ajout du patient.");
            e.printStackTrace();
        }
    }

    private void updatePatientInDatabase(String oldCIN, String nom, String prenom, String dateNaissance,
                                         String telephone, String cin, String adresse, boolean actif) {
        try {
            Connection conn = DatabaseConnection.connect();
            String query = "UPDATE Patient SET nom=?, prenom=?, date_naissance=?, telephone=?, CIN=?, adresse=?, actif=? WHERE CIN=?";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, dateNaissance);
            stmt.setString(4, telephone);
            stmt.setString(5, cin);
            stmt.setString(6, adresse);
            stmt.setBoolean(7, actif);
            stmt.setString(8, oldCIN);

            stmt.executeUpdate();
            loadStatistics();
            conn.close();
            showInfoAlert("Patient modifié avec succès!");
        } catch (Exception e) {
            showErrorAlert("Erreur de mise à jour du patient.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onSupprimerButtonClick() {
        if (selectedPatient == null) {
            showErrorAlert("Veuillez sélectionner un patient à supprimer.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer le patient : " + selectedPatient.getPrenom() + " " + selectedPatient.getNom() +" ?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deletePatientFromDatabase(selectedPatient.getCIN());
            }
        });
    }

    private void deletePatientFromDatabase(String cin) {
        try {
            Connection conn = DatabaseConnection.connect();
            String query = "DELETE FROM Patient WHERE CIN = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, cin);

            stmt.executeUpdate();
            conn.close();
            showInfoAlert("Patient supprimé avec succès!");
            loadPatientData();
            loadStatistics();
            clearFormFields();
            selectedPatient = null;
            btnAjouter.setText("Ajouter");
        } catch (Exception e) {
            showErrorAlert("Erreur lors de la suppression du patient.");
            e.printStackTrace();
        }
    }

    private void clearFormFields() {
        tfNom.clear();
        tfPrenom.clear();
        tfTelephone.clear();
        tfCIN.clear();
        tfAdresse.clear();
        dpDateNaissance.setValue(null);
        cbActif.setSelected(false);
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onLogoutButtonClick() {
        HelloApplication.loadPage("hello-view.fxml");
    }


}
