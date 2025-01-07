package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import kotlin.random.Random;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class GestionrendezvousController {
    private DatabaseConnection dbConnection = new DatabaseConnection();
    @FXML
    private Label idActe; // This should match fx:id="idActe"

    @FXML
    private Label dateDebutSoin; // Match fx:id="dateDebutSoin"

    @FXML
    private Label etatActe; // Match fx:id="etatActe"

    @FXML
    private Label dateFinSoin; // Match fx:id="dateFinSoin"

    @FXML
    private Label idPatient; // Match fx:id="idPatient"

    @FXML
    private Label nomPatient; // Match fx:id="nomPatient"

    @FXML
    private Label prenomPatient;
    @FXML
    private Label agePatient;
    @FXML
    private Label prixComptabilise;

    private int acteId; // To store Acte ID
    private int patientId;
    @FXML private TableView<Radio> tableRadios;
    @FXML private TableColumn<Radio, Integer> coliDRadio;
    @FXML private TableColumn<Radio, String> colDateRadio;
    @FXML private TableColumn<Radio, String> colTypeRadio;
    @FXML private TableColumn<Radio, String> colChemin;
    @FXML private ComboBox<RadioType> cbTypeRadio;
    @FXML private DatePicker dpDateRadio;
    @FXML
    private File selectedImageFile;
    @FXML
    private Label labelSelectedFile;
    @FXML
    private ImageView imageRadio;

    // This method is called after the FXML is loaded and data is passed
    public void setActeAndPatientDetails(int acteID, int patientID) {
        this.acteId = acteID;
        this.patientId = patientID;

        System.out.println("SetActeAndPatientDetails - Acte ID: " + acteId);
        System.out.println("SetActeAndPatientDetails - Patient ID: " + patientId);

        // Now that IDs are set, initialize data that depends on them
        loadData();
        loadInterventions();
        calculatePrixComptabilise(acteId);
    }
    public void initialize() {
        // Initialize the columns of the TableView
        coliDIntervention.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDatePrevue.setCellValueFactory(new PropertyValueFactory<>("datePrev"));
        colDateReelle.setCellValueFactory(new PropertyValueFactory<>("realDate"));
        colCategorieIntervention.setCellValueFactory(new PropertyValueFactory<>("category"));
        colEtatRV.setCellValueFactory(new PropertyValueFactory<>("status"));
        coliDRadio.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colDateRadio.setCellValueFactory(cellData -> cellData.getValue().dateRadioProperty());
        colTypeRadio.setCellValueFactory(cellData -> cellData.getValue().typeProperty());

        // Load the radios when the view is initialized
        loadRadios();
        loadRadioTypes();

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

        TableColumn<Radio, Image> colImage = new TableColumn<>("Image");
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));


    }

    private void loadData() {
        if (acteId == 0 || patientId == 0) {
            showErrorAlert("Les IDs de l'acte ou du patient ne sont pas correctement définis.");
            return;
        }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT a.ID AS idActe, a.date_debut, a.prix_comptabilise, a.etat_de_l_acte, a.date_de_fin, " +
                             "p.id AS idPatient, p.nom, p.prenom, p.date_naissance " +
                             "FROM ActeMedicaux a " +
                             "JOIN Patient p ON a.patient_concerne = p.id " +
                             "WHERE a.ID = ? AND p.id = ?")) {

            stmt.setInt(1, acteId);
            stmt.setInt(2, patientId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idActe.setText(String.valueOf(rs.getInt("idActe")));
                dateDebutSoin.setText(rs.getString("date_debut"));
                etatActe.setText(rs.getString("etat_de_l_acte"));
                dateFinSoin.setText(rs.getString("date_de_fin"));
                prixComptabilise.setText(String.format("%.2f", rs.getDouble("prix_comptabilise")));

                idPatient.setText(String.valueOf(rs.getInt("idPatient")));
                nomPatient.setText(rs.getString("nom"));
                prenomPatient.setText(rs.getString("prenom"));

                if (rs.getDate("date_naissance") != null) {
                    LocalDate dateNaissance = rs.getDate("date_naissance").toLocalDate();
                    int age = calculateAge(dateNaissance, LocalDate.now());
                    agePatient.setText(age + " ans");
                } else {
                    agePatient.setText("N/A");
                }
            } else {
                System.out.println("Aucune donnée trouvée pour Acte ID: " + acteId + ", Patient ID: " + patientId);
                showErrorAlert("Aucune donnée trouvée pour ces identifiants.");
            }
        } catch (SQLException e) {
            showErrorAlert("Erreur lors de la requête SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private TableView<Rendezvous> tableInterventions;

    @FXML
    private TableColumn<Rendezvous, Integer> coliDIntervention;
    @FXML
    private TableColumn<Rendezvous, String> colDatePrevue;
    @FXML
    private TableColumn<Rendezvous, String> colDateReelle;
    @FXML
    private TableColumn<Rendezvous, String> colCategorieIntervention;
    @FXML
    private TableColumn<Rendezvous, String> colEtatRV;

    private void loadInterventions() {
        System.out.println("Id de l'acte est: " + acteId);
        String query = "SELECT id, patient_id, DATE(date_prevue) AS date_prevue, categorie_id, etat, numero_acte, DATE(date_reelle) AS date_reelle " +
                "FROM Rendezvous WHERE numero_acte = " + acteId;
        ResultSet rs = DatabaseConnection.executeQuery(query);
        ObservableList<Rendezvous> interventionsList = FXCollections.observableArrayList();

        try {
            while (rs.next()) {
                int id = rs.getInt("id");
                String patient = rs.getString("patient_id");
                String datePrev = rs.getString("date_prevue");
                String category = rs.getString("categorie_id");
                int medicalAct = rs.getInt("numero_acte");
                String status = rs.getString("etat");
                String realDate = rs.getString("date_reelle");

                Rendezvous r = new Rendezvous(id, patient, datePrev, category, medicalAct, status, realDate);
                interventionsList.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableInterventions.setItems(interventionsList);
    }
    @FXML
    private DatePicker dpConfirmer;

    @FXML
    private Button btnConfirmerIntervention;
    @FXML
    public Label labelIDSupprimer;

    @FXML
    private Label labelIDConfirmer;

    public void handleConfirmerIntervention() {
        try {
            int id = Integer.parseInt(labelIDConfirmer.getText());
            LocalDate dateReelle = dpConfirmer.getValue(); // Utiliser LocalDate directement depuis le DatePicker
            LocalDate dateActuelle = LocalDate.now();

            // Validation de la date
            if (dateReelle.isBefore(dateActuelle)) {
                showAlert("Erreur", "La date réelle est antérieure à la date actuelle.");
                return;
            }

            String query = "UPDATE Rendezvous SET date_reelle = ?, etat = ? WHERE id = ?";
            dbConnection.executeUpdate(query, dateReelle, "Terminé", id);

            // Refresh the table
            loadInterventions();
            calculatePrixComptabilise(acteId);
            ClearFields();
            showAlert("Success", "Intervention confirmed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to confirm the intervention.");
        }
    }

    // Delete intervention by ID
    public void handleSupprimerRendezVous() {
        try {
            int id = Integer.parseInt(labelIDSupprimer.getText());

            String query = "DELETE FROM Rendezvous WHERE id = ?";
            dbConnection.executeUpdate(query, id);

            // Refresh the table
            loadInterventions();
            calculatePrixComptabilise(acteId);
            ClearFields();
            showAlert("Success", "Rendezvous deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to delete the rendezvous.");
        }
    }
    private void ClearFields() {
        labelIDConfirmer.setText("");
        labelIDSupprimer.setText("");
        dpConfirmer.setValue(null);
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

    public void loadRadios() {
        String query = "SELECT * FROM Radios";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            ObservableList<Radio> radios = FXCollections.observableArrayList();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String type = rs.getString("type_id");
                String date = rs.getString("date_radio");
                radios.add(new Radio(id, date, type));
            }

            // Update the TableView
            tableRadios.setItems(radios);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les radios.");
        }
    }
    private void loadRadioTypes() {
        String query = "SELECT ID, type, prixRadio FROM type_radios";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            ObservableList<RadioType> radioTypes = FXCollections.observableArrayList();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String type = rs.getString("type");
                int prix = rs.getInt("prixRadio");
                radioTypes.add(new RadioType(id, type, prix));
            }

            // Example usage with ComboBox:
            cbTypeRadio.setItems(radioTypes);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void handleFileSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            labelSelectedFile.setText(file.getName()); // Show file name instead of path
        } else {
            selectedImageFile = null;
            labelSelectedFile.setText("No file selected");
        }
    }

    public void handleAddRadio() {
        if (selectedImageFile == null) {
            showErrorAlert("Please select an image file.");
            return;
        }

        String dateRadio = dpDateRadio.getValue().toString();
        RadioType selectedType = cbTypeRadio.getSelectionModel().getSelectedItem();

        if (selectedType == null || dateRadio.isEmpty()) {
            showErrorAlert("Please fill in all fields and select a type.");
            return;
        }

        int typeId = selectedType.getId();

        String query = "INSERT INTO Radios (type_id, date_radio, image, acteID) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             FileInputStream fis = new FileInputStream(selectedImageFile)) {

            pstmt.setInt(1, typeId);
            pstmt.setDate(2, Date.valueOf(dateRadio));
            pstmt.setBinaryStream(3, fis, (int) selectedImageFile.length());
            pstmt.setInt(4, acteId);

            pstmt.executeUpdate();


            loadRadios(); // Reload the radios after adding
            calculatePrixComptabilise(acteId);
            dpDateRadio.setValue(null);
            cbTypeRadio.getSelectionModel().clearSelection();
            labelSelectedFile.setText("No file selected");
            showAlert("Success", "Radio added successfully!");
        } catch (SQLException | IOException e) {
            showAlert("Error", "Unable to add the radio." + e.getMessage());
        }
    }


    public void handleDeleteRadio() {
        Radio selectedRadio = tableRadios.getSelectionModel().getSelectedItem();
        if (selectedRadio != null) {
            String query = "DELETE FROM Radios WHERE ID = ?";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, selectedRadio.getId());
                pstmt.executeUpdate();

                loadRadios();  // Reload the radios after deletion
                dpDateRadio.setValue(null);
                cbTypeRadio.getSelectionModel().clearSelection();
                calculatePrixComptabilise(acteId);
                showAlert("Success", "Radio deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de supprimer la radio.");
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner une radio à supprimer.");
        }
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





    private int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        return Period.between(birthDate, currentDate).getYears();
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
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
