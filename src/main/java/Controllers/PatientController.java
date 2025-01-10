package Controllers;

import DAO.MainApplication;
import DAO.PatientDAO;
import metier.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PatientController {

    private PatientDAO patientDAO = new PatientDAO();
    private ObservableList<Patient> patientList = FXCollections.observableArrayList();
    private Patient selectedPatient;

    @FXML
    private Label lblTotal, lblTotalAdults;

    @FXML
    private TableView<Patient> tablePatients;

    @FXML
    private TableColumn<Patient, String> colNom, colPrenom, colDateNaissance, colTelephone, colCIN, colAdresse, colEmail;

    @FXML
    private TextField tfNom, tfPrenom, tfTelephone, tfCIN, tfAdresse, tfEmail, tfSearch;

    @FXML
    private DatePicker dpDateNaissance;

    @FXML
    private Button btnAjouter;

    @FXML
    public void initialize() {
        setupTable();
        loadPatientData();
        loadStatistics();
    }

    private void setupTable() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colCIN.setCellValueFactory(new PropertyValueFactory<>("CIN"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tablePatients.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedPatient = newSelection;
            }
        });
    }

    private void loadPatientData() {
        List<Patient> patients = patientDAO.getAllPatients();
        patientList.setAll(patients);
        tablePatients.setItems(patientList);
    }

    private void loadStatistics() {
        int totalPatients = patientDAO.countTotalPatients();
        int adultPatients = patientDAO.countAdultPatients();
        lblTotal.setText(String.valueOf(totalPatients));
        lblTotalAdults.setText(String.valueOf(adultPatients));
    }

    @FXML
    public void onSearchKeyReleased() {
        String searchQuery = tfSearch.getText().toLowerCase();
        ObservableList<Patient> filteredList = FXCollections.observableArrayList();

        for (Patient patient : patientList) {
            if (patient.getNom().toLowerCase().contains(searchQuery) || patient.getPrenom().toLowerCase().contains(searchQuery)) {
                filteredList.add(patient);
            }
        }

        tablePatients.setItems(filteredList);
    }
    @FXML
    private void onModifierButtonClick() {
        if (selectedPatient == null) {
            showErrorAlert("Veuillez sélectionner un patient à modifier.");
            return;
        }
        tfNom.setText(selectedPatient.getNom());
        tfPrenom.setText(selectedPatient.getPrenom());
        dpDateNaissance.setValue(selectedPatient.getDateNaissance() != null ? LocalDate.parse(selectedPatient.getDateNaissance()) : null);
        tfTelephone.setText(selectedPatient.getTelephone());
        tfCIN.setText(selectedPatient.getCIN());
        tfAdresse.setText(selectedPatient.getAdresse());
        tfEmail.setText(selectedPatient.getEmail());
        btnAjouter.setText("Enregistrer");
    }


    @FXML
    private void onSavePatientClick() {
        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String dateNaissance = dpDateNaissance.getValue() != null ? dpDateNaissance.getValue().toString() : null;
        String telephone = tfTelephone.getText();
        String cin = tfCIN.getText();
        String adresse = tfAdresse.getText();
        String email = tfEmail.getText();

        if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || cin.isEmpty()) {
            showErrorAlert("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        try {
            if (selectedPatient == null) {
                Patient patient = new Patient(nom, prenom, dateNaissance, telephone, cin, adresse, email);
                patientDAO.addPatient(patient);
                selectedPatient = null;
                showSuccessAlert("Patient ajouté avec succès.");
            } else {
                selectedPatient.setNom(nom);
                selectedPatient.setPrenom(prenom);
                selectedPatient.setDateNaissance(dateNaissance);
                selectedPatient.setTelephone(telephone);
                selectedPatient.setCIN(cin);
                selectedPatient.setAdresse(adresse);
                selectedPatient.setEmail(email);
                patientDAO.updatePatient(selectedPatient);
                btnAjouter.setText("Ajouter");
                selectedPatient = null;
                showSuccessAlert("Patient modifié avec succès.");
            }
            clearFormFields();
            loadPatientData();
            loadStatistics();
        } catch (Exception e) {
            showErrorAlert("Erreur lors de l'enregistrement du patient.");
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
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce patient ?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    patientDAO.deletePatient(selectedPatient.getCIN());
                    loadPatientData();
                    loadStatistics();
                    clearFormFields();
                    selectedPatient = null;
                    showSuccessAlert("Patient supprimé avec succès.");
                } catch (Exception e) {
                    showErrorAlert("Erreur lors de la suppression du patient.");
                }
            }
        });
    }

    public void exportToExcel() {
        Workbook workbook = new XSSFWorkbook();  // Create a new Excel workbook
        Sheet sheet = workbook.createSheet("Patients");  // Create a new sheet named "Patients"

        // Create the header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Nom", "Prenom", "Date Naissance", "Telephone", "CIN", "Adresse", "Email"};

        // Add columns to the header row
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Fill the sheet with patient data
        int rowNum = 1;  // Start from the second row
        for (Patient patient : patientList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(patient.getID());
            row.createCell(1).setCellValue(patient.getNom());
            row.createCell(2).setCellValue(patient.getPrenom());
            row.createCell(3).setCellValue(patient.getDateNaissance());
            row.createCell(4).setCellValue(patient.getTelephone());
            row.createCell(5).setCellValue(patient.getCIN());
            row.createCell(6).setCellValue(patient.getAdresse());
            row.createCell(7).setCellValue(patient.getEmail());
        }

        // Create the output file
        try (FileOutputStream fileOut = new FileOutputStream(new File("Patients.xlsx"))) {
            workbook.write(fileOut);  // Write the workbook to the file
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();  // Close the workbook
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Export to Excel completed!");
    }
    private void clearFormFields() {
        tfNom.clear();
        tfPrenom.clear();
        tfTelephone.clear();
        tfCIN.clear();
        tfAdresse.clear();
        tfEmail.clear();
        dpDateNaissance.setValue(null);
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onBtnAccueilClick(ActionEvent actionEvent) {
        MainApplication.loadPage("dashboard.fxml");
    }

    public void onBtnPatientsClick(ActionEvent actionEvent) {
        MainApplication.loadPage("patient.fxml");
    }

    public void onBtnActeMedicClick(ActionEvent actionEvent) {
        System.out.println("Actes Medicaux openned");
        MainApplication.loadPage("actesmedicaux.fxml");
    }

    public void onBtnUserClick(ActionEvent actionEvent) {
        MainApplication.loadPage("utilisateurs.fxml");
    }

    public void onLogoutButtonClick(ActionEvent actionEvent) {
        MainApplication.loadPage("hello-view.fxml");
    }

    public void onBtnConfigClick(ActionEvent actionEvent) {
        MainApplication.loadPage("config.fxml");
    }
    public void HomePage(ActionEvent actionEvent) {
        MainApplication.loadPage("SecretairePages/Home.fxml");
    }

    public void PatientPage(ActionEvent actionEvent) {
        MainApplication.loadPage("SecretairePages/patients.fxml");
    }

    public void RendezVousPage(ActionEvent actionEvent) {
        MainApplication.loadPage("SecretairePages/rendezvous.fxml");
    }

    public void PaiementPage(ActionEvent actionEvent) {
        MainApplication.loadPage("SecretairePages/paiement.fxml");
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
