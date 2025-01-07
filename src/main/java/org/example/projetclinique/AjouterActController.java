package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class AjouterActController {
    @FXML
    private Label hiddenPatientId;
    @FXML
    private DatePicker dpDateDebut;
    @FXML
    private Label labelIDPatient;

    @FXML
    private TableColumn<Patient, Integer> colID; // Column for Patient ID
    @FXML
    private TableColumn<Patient, String> colNom, colPrenom, colDateNaissance, colTelephone, colCIN, colAdresse;
    @FXML
    private TableColumn<Patient, Boolean> colActif;
    @FXML
    private TableView<Patient> tablePatients;

    @FXML
    private TextField tfNom, tfPrenom, tfTelephone, tfCIN, tfAdresse;
    private ObservableList<Patient> patientList = FXCollections.observableArrayList();

    @FXML
    private DatePicker dpDateNaissance;

    private void loadPatientData() {
        try {
            patientList.clear();
            Connection conn = DatabaseConnection.connect();

            // Include ID in the query
            String query = "SELECT ID, nom, prenom, date_naissance, telephone, CIN, adresse FROM Patient";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                patientList.add(new Patient(
                        rs.getInt("ID"), // Add ID to the Patient constructor
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("CIN"),
                        rs.getString("adresse")
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

    @FXML
    public void initialize() {
        // Set up cell value factories for each column, including ID
        colID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colCIN.setCellValueFactory(new PropertyValueFactory<>("CIN"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        // Add a listener to update labels when a row is selected
        tablePatients.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                labelIDPatient.setText(newValue.getPrenom() + " " + newValue.getNom());
                hiddenPatientId.setText(String.valueOf(newValue.getID()));
            } else {
                labelIDPatient.setText("");
                hiddenPatientId.setText("");
            }
        });

        // Load patient data into the TableView
        loadPatientData();
    }

    @FXML
    private void onAjouterActeClick() {
        try {
            String dateDebut = dpDateDebut.getValue() != null ? dpDateDebut.getValue().toString() : null;
            String patientIdText = hiddenPatientId.getText();
            if(patientIdText == null || patientIdText.trim().isEmpty()){
                showErrorAlert("Veuillez selectionner patient.");
            }else if (dateDebut == null ) {
                showErrorAlert("Veuillez Selectionner date de debut.");
                return;
            }
            // Check if dateDebut is in the future
            LocalDate currentDate = LocalDate.now();
            LocalDate startDate = LocalDate.parse(dateDebut);
            if (startDate.isBefore(currentDate)) {
                showErrorAlert("La date de début ne peut pas être dans le passé.");
                return;  // Exit the method if the date is in the past
            }
            String sql = "INSERT INTO ActeMedicaux (date_debut, patient_concerne) VALUES (?, ?)";

            try (Connection connection = DatabaseConnection.connect();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setDate(1, java.sql.Date.valueOf(dateDebut));
                preparedStatement.setInt(2, Integer.parseInt(patientIdText));

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Inserting patient ID: " + patientIdText);
                    System.out.println("Acte ajouté avec succès !");
                    HelloApplication.loadPage("actesmedicaux.fxml");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Erreur lors de l'insertion dans la base de données. " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Une erreur inattendue est survenue. " + e.getMessage());
        }
    }

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

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
