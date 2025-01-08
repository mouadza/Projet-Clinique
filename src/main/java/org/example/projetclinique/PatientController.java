package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Label lblTotal;
    @FXML
    private TableView<Patient> tablePatients;

    @FXML
    private TableColumn<Patient, String> colNom, colPrenom, colDateNaissance, colTelephone, colCIN, colAdresse;

    @FXML
    private TableColumn<Patient, String> colEmail;


    @FXML
    private TextField tfNom, tfPrenom, tfTelephone, tfCIN, tfAdresse;

    @FXML
    private DatePicker dpDateNaissance;

    @FXML
    private TextField tfEmail;

    @FXML
    private Button btnAjouter; // Ensure this button is declared in the FXML

    private ObservableList<Patient> patientList = FXCollections.observableArrayList();
    private Patient selectedPatient;
    @FXML
    private Label lblTotalAdults;
    @FXML
    private TextField tfSearch;


    @FXML
    public void initialize() {
        // Set up TableView columns
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colCIN.setCellValueFactory(new PropertyValueFactory<>("CIN"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));



        // Load initial data
        loadPatientData();
        loadStatistics();
    }

    @FXML
    public void onSearchKeyReleased() {
        String searchQuery = tfSearch.getText().toLowerCase();
        ObservableList<Patient> filteredList = FXCollections.observableArrayList();

        for (Patient patient : patientList) {
            if (patient.getNom().toLowerCase().contains(searchQuery) ||
                    patient.getPrenom().toLowerCase().contains(searchQuery)) {
                filteredList.add(patient);
            }
        }

        tablePatients.setItems(filteredList);
    }
    private void loadStatistics() {
        Connection connection = DatabaseConnection.connect();

        if (connection == null) {
            System.err.println("Failed to establish a database connection.");
            return;
        }

        try (Statement statement = connection.createStatement()) {
            // Query for total patients (existing logic)
            String queryTotalPatients = """
                SELECT COUNT(*) AS total
                FROM Patient
            """;
            ResultSet rsTotalPatients = statement.executeQuery(queryTotalPatients);
            if (rsTotalPatients.next()) {
                lblTotal.setText(String.valueOf(rsTotalPatients.getInt("total")));
            }

            // Query for adult patients (assuming age column exists)
            String queryAdultPatients = """
            SELECT COUNT(*) AS total
            FROM Patient
            WHERE TIMESTAMPDIFF(YEAR, date_naissance, CURDATE()) >= 18
        """;
            ResultSet rsAdultPatients = statement.executeQuery(queryAdultPatients);
            if (rsAdultPatients.next()) {
                lblTotalAdults.setText(String.valueOf(rsAdultPatients.getInt("total")));
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

            String query = "SELECT ID, nom, prenom, date_naissance, telephone, CIN, adresse FROM Patient";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                patientList.add(new Patient(
                        rs.getInt("ID"), // Correct type for ID
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("CIN"),
                        rs.getString("adresse"),
                        null // Assuming email is optional
                ));
            }

            tablePatients.setItems(patientList); // Populate the TableView
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
        tfEmail.setText(patient.getEmail());  // Add this line to set the email
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
            String email = tfEmail.getText();  // Get the email input value

            // Validate required fields
            if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || cin.isEmpty()) {
                showErrorAlert("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Add or update the patient
            if (selectedPatient == null) {
                // Add new patient
                addPatientToDatabase(nom, prenom, dateNaissance, telephone, cin, adresse, email);  // Pass email to the method
            } else {
                // Update existing patient
                updatePatientInDatabase(selectedPatient.getCIN(), nom, prenom, dateNaissance, telephone, cin, adresse, email);  // Pass email to the method
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
                                      String telephone, String cin, String adresse, String email) {
        try {
            Connection conn = DatabaseConnection.connect();
            String query = "INSERT INTO Patient (nom, prenom, date_naissance, telephone, CIN, adresse, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, dateNaissance);
            stmt.setString(4, telephone);
            stmt.setString(5, cin);
            stmt.setString(6, adresse);
            stmt.setString(7, email);  // Include email

            stmt.executeUpdate();
            loadStatistics();
            conn.close();
            showInfoAlert("Patient ajouté avec succès!");
        } catch (Exception e) {
            showErrorAlert("Erreur lors de l'ajout du patient.");
            e.printStackTrace();
        }
    }


    private void updatePatientInDatabase(String oldCin, String nom, String prenom, String dateNaissance,
                                         String telephone, String cin, String adresse, String email) {
        try {
            Connection conn = DatabaseConnection.connect();
            String query = "UPDATE Patient SET nom=?, prenom=?, date_naissance=?, telephone=?, CIN=?, adresse=?, email=? WHERE CIN=?";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, dateNaissance);
            stmt.setString(4, telephone);
            stmt.setString(5, cin);
            stmt.setString(6, adresse);
            stmt.setString(7, email);  // Include email
            stmt.setString(8, oldCin);

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

    private void deletePatientFromDatabase(String Id) {
        try {
            Connection conn = DatabaseConnection.connect();
            String query = "DELETE FROM Patient WHERE Id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, Id);

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
        tfEmail.clear();  // Clear email field
        dpDateNaissance.setValue(null);
    }

    @FXML
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("Patients.xlsx");
        File file = fileChooser.showSaveDialog(tablePatients.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Patients");
                Row headerRow = sheet.createRow(0);

                // Add headers, including Email
                for (int i = 0; i < tablePatients.getColumns().size(); i++) {
                    headerRow.createCell(i).setCellValue(tablePatients.getColumns().get(i).getText());
                }
                headerRow.createCell(tablePatients.getColumns().size()).setCellValue("Email");  // Add Email header

                // Add data rows, including Email
                ObservableList<Patient> data = tablePatients.getItems();
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    Patient patient = data.get(i);

                    row.createCell(0).setCellValue(patient.getPrenom());
                    row.createCell(1).setCellValue(patient.getNom());
                    row.createCell(2).setCellValue(patient.getDateNaissance().toString());
                    row.createCell(3).setCellValue(patient.getTelephone());
                    row.createCell(4).setCellValue(patient.getCIN());
                    row.createCell(5).setCellValue(patient.getAdresse());
                    row.createCell(6).setCellValue(patient.getEmail());  // Add Email data
                }

                // Resize columns to fit content
                for (int i = 0; i < tablePatients.getColumns().size() + 1; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write the output to a file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                System.out.println("Exported to Excel successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error exporting to Excel.");
            }
        }
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
}
