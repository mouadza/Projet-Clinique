package Controllers;

import DAO.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import metier.Radio;
import metier.RadioType;
import metier.RendezVous;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class GestionrendezvousController {

    @FXML private Label idActe, dateDebutSoin, etatActe, dateFinSoin, idPatient, nomPatient, prenomPatient, agePatient, prixComptabilise;
    @FXML private TableView<RendezVous> tableInterventions;
    @FXML private TableColumn<RendezVous, Integer> coliDIntervention;
    @FXML private TableColumn<RendezVous, String> colDatePrevue, colDateReelle, colCategorieIntervention, colEtatRV;
    @FXML private TableView<Radio> tableRadios;
    @FXML private TableColumn<Radio, Integer> coliDRadio;
    @FXML private TableColumn<Radio, String> colDateRadio, colTypeRadio;
    @FXML private ComboBox<RadioType> cbTypeRadio;
    @FXML private DatePicker dpDateRadio, dpConfirmer;
    @FXML private Label labelSelectedFile;
    @FXML private ImageView imageRadio;

    private File selectedImageFile;
    private int acteId;
    private int patientId;
    @FXML
    public Label labelIDSupprimer;

    @FXML
    private Label labelIDConfirmer;

    private final RendezVousDAO rendezVousDAO = new RendezVousDAO();
    private final RadioTypeDAO radioTypeDAO = new RadioTypeDAO();
    private final RadioDAO radioDAO = new RadioDAO();

    public void setActeAndPatientDetails(int acteID, int patientID) {
        this.acteId = acteID;
        this.patientId = patientID;

        loadData();
        loadInterventions();
        loadRadios();
        updatePrixComptabilise();
    }

    public void initialize() {
        coliDIntervention.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDatePrevue.setCellValueFactory(new PropertyValueFactory<>("datePrev"));
        colDateReelle.setCellValueFactory(new PropertyValueFactory<>("dateReelle"));
        colCategorieIntervention.setCellValueFactory(new PropertyValueFactory<>("category"));
        colEtatRV.setCellValueFactory(new PropertyValueFactory<>("etat"));
        coliDRadio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colDateRadio.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateRadio()));
        colTypeRadio.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));

        tableInterventions.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                labelIDConfirmer.setText(String.valueOf(newSelection.getId()));
                labelIDSupprimer.setText(String.valueOf(newSelection.getId()));
            }
        });

        tableRadios.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showImageForSelectedRadio(newValue);
            }
        });

        loadRadioTypes();
    }

    private void loadData() {
        try {
            var data = rendezVousDAO.getActeAndPatientDetails(acteId, patientId);
            if (data != null) {
                idActe.setText(String.valueOf(data.getId()));
                dateDebutSoin.setText(data.getDateDebut());
                etatActe.setText(data.getEtatDeLActe());
                dateFinSoin.setText(data.getDateFin());
                prixComptabilise.setText(String.format("%.2f", data.getPrixComptabilise()));
                idPatient.setText(String.valueOf(data.getPatientID()));

                String[] patientNameParts = data.getPatientName().split(" ", 2);
                nomPatient.setText(patientNameParts.length > 0 ? patientNameParts[0] : "");
                prenomPatient.setText(patientNameParts.length > 1 ? patientNameParts[1] : "");
                String sqlQuery = "SELECT * FROM Patient WHERE id = ?";
                Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(sqlQuery);
                statement.setInt(1, patientId); // Use the correct patientId for the query
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    LocalDate dateNaissance = rs.getDate("date_naissance") != null
                            ? rs.getDate("date_naissance").toLocalDate()
                            : null;
                    nomPatient.setText(patientNameParts.length > 0 ? patientNameParts[0] : "");
                    prenomPatient.setText(patientNameParts.length > 1 ? patientNameParts[1] : "");

                    if (dateNaissance != null) {
                        int age = calculateAge(dateNaissance, LocalDate.now());
                        agePatient.setText(age + " ans");
                    } else {
                        agePatient.setText("N/A");
                    }
                }
                rs.close();
                statement.close();
            } else {
                showErrorAlert("No data found for the specified Acte and Patient IDs.");
            }
        } catch (Exception e) {
            showErrorAlert("Error loading Acte and Patient details: " + e.getMessage());
        }
    }

    private void loadInterventions() {
        try {
            ObservableList<RendezVous> interventions = FXCollections.observableArrayList(rendezVousDAO.getInterventionsByActeId(acteId));
            tableInterventions.setItems(interventions);
        } catch (Exception e) {
            showErrorAlert("Error loading interventions: " + e.getMessage());
        }
    }
    public void handleConfirmerIntervention() {
        try {
            // Check if the ID is selected
            if (labelIDConfirmer.getText() == null || labelIDConfirmer.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez sélectionner une intervention.");
                return;
            }

            int id = Integer.parseInt(labelIDConfirmer.getText());
            LocalDate dateReelle = dpConfirmer.getValue();

            // Check if a date is selected
            if (dateReelle == null) {
                showAlert("Erreur", "Veuillez sélectionner une date réelle.");
                return;
            }

            LocalDate dateActuelle = LocalDate.now();
            if (dateReelle.isBefore(dateActuelle)) {
                showAlert("Erreur", "La date réelle est antérieure à la date actuelle.");
                return;
            }

            // Format the date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateReelleString = dateReelle.format(formatter);

            // Update intervention
            rendezVousDAO.updateIntervention(id, dateReelleString, "Terminé");

            // Refresh the data and clear fields
            loadInterventions();
            calculatePrixComptabilise(acteId);
            ClearFields();
            showSuccessAlert("Intervention confirmée avec succès !");
        } catch (NumberFormatException e) {
            showAlert("Erreur", "ID invalide. Veuillez sélectionner une intervention.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de confirmer l'intervention.");
        }
    }

    public void handleSupprimerRendezVous() {
        try {
            // Check if the ID is selected
            if (labelIDSupprimer.getText() == null || labelIDSupprimer.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez sélectionner un rendez-vous.");
                return;
            }

            int id = Integer.parseInt(labelIDSupprimer.getText());

            // Delete the rendezvous
            rendezVousDAO.deleteIntervention(id);

            // Refresh the data and clear fields
            loadInterventions();
            calculatePrixComptabilise(acteId);
            ClearFields();
            showAlert("Succès", "Rendez-vous supprimé avec succès !");
        } catch (NumberFormatException e) {
            showAlert("Erreur", "ID invalide. Veuillez sélectionner un rendez-vous.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer le rendez-vous.");
        }
    }

    private void ClearFields() {
        labelIDConfirmer.setText("");
        labelIDSupprimer.setText("");
        dpConfirmer.setValue(null);
    }

    private void loadRadioTypes() {
        try {
            // Get all radio types from the DAO
            ObservableList<RadioType> radioTypes = FXCollections.observableArrayList(radioTypeDAO.getAllRadioTypes());
            cbTypeRadio.setItems(radioTypes);
        } catch (Exception e) {
            showErrorAlert("Error loading radio types: " + e.getMessage());
        }
    }


    public void handleFileSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(null);
        labelSelectedFile.setText(selectedImageFile != null ? selectedImageFile.getName() : "No file selected");
    }

    public void handleAddRadio() {
        if (selectedImageFile == null) {
            showErrorAlert("Please select an image file.");
            return;
        }
        if (cbTypeRadio.getValue() == null) {
            showErrorAlert("Please select a valid radio type.");
            return;
        }

        try (FileInputStream imageStream = new FileInputStream(selectedImageFile)) {
            long imageSize = selectedImageFile.length();

            // Validate selected type
            RadioType selectedType = cbTypeRadio.getValue();
            if (selectedType == null || selectedType.getId() == 0) { // Assuming 0 is invalid
                showErrorAlert("Invalid radio type selected.");
                return;
            }

            // Create the radio object
            Radio radio = new Radio(
                    dpDateRadio.getValue().toString(),
                    selectedType.getId(),
                    acteId
            );

            // Add the radio
            radioDAO.addRadio(radio, imageStream, imageSize);

            // Refresh UI
            loadRadios();
            updatePrixComptabilise();
            showSuccessAlert("Radio added successfully!");
        } catch (IOException | SQLException e) {
            showErrorAlert("Error adding radio: " + e.getMessage());
        }
    }


    public void handleDeleteRadio() {
        Radio selectedRadio = tableRadios.getSelectionModel().getSelectedItem();
        if (selectedRadio == null) {
            showErrorAlert("Please select a radio to delete.");
            return;
        }

        try {
            radioDAO.deleteRadio(selectedRadio.getId());
            loadRadios();
            updatePrixComptabilise();
            showAlert("Success", "Radio deleted successfully!");
        } catch (Exception e) {
            showErrorAlert("Error deleting radio: " + e.getMessage());
        }
    }

    private void loadRadios() {
        try {
            ObservableList<Radio> radios = FXCollections.observableArrayList(radioDAO.getRadiosByActeId(acteId));
            tableRadios.setItems(radios);
        } catch (Exception e) {
            showErrorAlert("Error loading radios: " + e.getMessage());
        }
    }

    private void updatePrixComptabilise() {
        calculatePrixComptabilise(acteId);
    }

    public void calculatePrixComptabilise(int numeroActe) {
        // Query to fetch prices from Rendezvous
        String rendezvousQuery = """
SELECT r.categorie_id, c.prix 
FROM Rendezvous r
JOIN categorie_intervention c ON r.categorie_id = c.id
WHERE r.numero_acte = ? AND r.etat = 'Terminé';
""";

        // Query to fetch prices from Radios
        String radiosQuery = """
SELECT t.prixRadio 
FROM Radios r
JOIN type_radios t ON r.type_id = t.ID
WHERE r.acteID = ?;
""";

        // Query to update ActeMedicaux
        String updateQuery = """
UPDATE ActeMedicaux 
SET prix_comptabilise = ? 
WHERE ID = ?;
""";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement rendezvousPstmt = conn.prepareStatement(rendezvousQuery);
             PreparedStatement radiosPstmt = conn.prepareStatement(radiosQuery);
             PreparedStatement updatePstmt = conn.prepareStatement(updateQuery)) {

            // Calculate total from Rendezvous
            rendezvousPstmt.setInt(1, numeroActe);
            ResultSet rendezvousRs = rendezvousPstmt.executeQuery();
            double totalPrix = 0.0;
            while (rendezvousRs.next()) {
                double prix = rendezvousRs.getDouble("prix");
                totalPrix += prix;
            }

            // Calculate total from Radios
            radiosPstmt.setInt(1, numeroActe);
            ResultSet radiosRs = radiosPstmt.executeQuery();
            while (radiosRs.next()) {
                double prix = radiosRs.getDouble("prixRadio");
                totalPrix += prix;
            }

            // Update the price in ActeMedicaux table
            prixComptabilise.setText(String.format("%.2f", totalPrix));
            updatePstmt.setDouble(1, totalPrix);
            updatePstmt.setInt(2, numeroActe);
            updatePstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de calculer le prix comptabilisé.");
        }
    }

    private int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        return Period.between(birthDate, currentDate).getYears();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showImageForSelectedRadio(Radio selectedRadio) {
        String query = "SELECT image FROM Radios WHERE ID = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, selectedRadio.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    byte[] imageData = rs.getBytes("image");
                    if (imageData != null) {
                        InputStream inputStream = new ByteArrayInputStream(imageData);
                        Image image = new Image(inputStream);
                        imageRadio.setImage(image); // Set the image in the ImageView
                    } else {
                        imageRadio.setImage(null); // Clear the ImageView if no image is available
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error loading image for the selected radio.");
        }
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

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
