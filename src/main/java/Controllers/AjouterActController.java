package Controllers;

import DAO.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import metier.Acte;
import metier.Patient;

import java.sql.Connection;
import java.time.LocalDate;

public class AjouterActController {
    @FXML
    private Label hiddenPatientId;
    @FXML
    private DatePicker dpDateDebut;
    @FXML
    private Label labelIDPatient;

    @FXML
    private TableColumn<Patient, Integer> colID;
    @FXML
    private TableColumn<Patient, String> colNom, colPrenom, colDateNaissance, colTelephone, colCIN, colAdresse;
    @FXML
    private TableView<Patient> tablePatients;

    @FXML
    private TextField tfSearch;
    private Connection conn = DatabaseConnection.connect();
    private PatientDAO patientDao = new PatientDAO();
    private ActeDAO acteMedicauxDao = new ActeDAO();

    // Declare patientList at the class level
    private ObservableList<Patient> patientList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up table columns
        colID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colCIN.setCellValueFactory(new PropertyValueFactory<>("CIN"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        // Add listener for patient selection
        tablePatients.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                labelIDPatient.setText(newValue.getPrenom() + " " + newValue.getNom());
                hiddenPatientId.setText(String.valueOf(newValue.getID()));
            } else {
                labelIDPatient.setText("");
                hiddenPatientId.setText("");
            }
        });

        // Load patient data using DAO
        loadPatientData();
    }

    private void loadPatientData() {
        patientList.clear(); // Clear the list to avoid duplicates
        patientList.setAll(patientDao.getPatients()); // Fetch all patients from the DAO
        tablePatients.setItems(patientList); // Bind the list to the TableView
    }

    @FXML
    private void onAjouterActeClick() {
        String dateDebut = dpDateDebut.getValue() != null ? dpDateDebut.getValue().toString() : null;
        String patientIdText = hiddenPatientId.getText();

        if (patientIdText == null || patientIdText.trim().isEmpty()) {
            showErrorAlert("Veuillez sélectionner un patient.");
            return;
        }

        if (dateDebut == null) {
            showErrorAlert("Veuillez sélectionner une date de début.");
            return;
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = LocalDate.parse(dateDebut);
        if (startDate.isBefore(currentDate)) {
            showErrorAlert("La date de début ne peut pas être dans le passé.");
            return;
        }

        double prixComptabilise = 0.0; // Default or calculated value
        String etatDeLActe = "en cours"; // Default status
        String dateFin = null; // Default or calculated value

        Acte acte = new Acte(dateDebut, prixComptabilise, etatDeLActe, dateFin, Integer.parseInt(patientIdText));

        boolean isAdded = acteMedicauxDao.addActe(acte);
        if (isAdded) {
            showInfoAlert("Acte ajouté avec succès !");
            MainApplication.loadPage("actesmedicaux.fxml");
        } else {
            showErrorAlert("Erreur lors de l'ajout de l'acte.");
        }
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

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
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
}
