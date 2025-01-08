package org.example.projetclinique;

import javax.mail.*;
import javax.mail.internet.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Properties;
import java.sql.*;
import java.time.LocalDateTime;


public class AjoutRendezVousController {

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
    private DatePicker dpDatePrevue;
    @FXML
    private TableColumn<Patient, String> colEmail;

    @FXML
    private ComboBox<String> cbActeMedical; // ComboBox for Acte Médical
    @FXML
    private ComboBox<String> cbCategorie; // ComboBox for Categories

    private ObservableList<Patient> patientList = FXCollections.observableArrayList();

    @FXML
    private DatePicker dpDateNaissance;
    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colCIN.setCellValueFactory(new PropertyValueFactory<>("CIN"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Set ComboBox to not interfere with row selection
        cbActeMedical.setFocusTraversable(false); // Prevent ComboBox from interrupting row selection

        tablePatients.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // When a row is selected, enable ComboBox
                cbActeMedical.setDisable(false);
                labelIDPatient.setText(newValue.getPrenom() + " " + newValue.getNom());
                System.out.println("Selected row: " + newValue.getID());

                // Load Acte Medical Data for the selected patient
                loadActeMedicalData(newValue.getID());
            } else {
                // If no row is selected, disable ComboBox
                cbActeMedical.setDisable(true);
                labelIDPatient.setText("");
                hiddenPatientId.setText("");
            }
        });

        loadPatientData();
        loadCategories();
    }



    private void loadPatientData() {
        try {
            patientList.clear();
            Connection conn = DatabaseConnection.connect();

            String query = "SELECT ID, nom, prenom, date_naissance, telephone, CIN, adresse, email FROM Patient";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                patientList.add(new Patient(
                        rs.getInt("ID"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("CIN"),
                        rs.getString("adresse"),
                        rs.getString("email")
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
    private void loadActeMedicalData(int patientId) {
        if (patientId == 0) {
            showErrorAlert("ID de patient invalide.");
            return;
        }
        System.out.println("Selected row: " + patientId);
        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                ObservableList<String> acteMedicalList = FXCollections.observableArrayList();
                Connection conn = DatabaseConnection.connect();

                String query = "SELECT ID FROM ActeMedicaux WHERE patient_concerne = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, patientId); // Set the patient_id as a parameter

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    int acteId = rs.getInt("ID");  // Assuming 'ID' is the correct column
                    acteMedicalList.add(String.valueOf(acteId)); // Add acte_id to the list
                }

                rs.close();
                pstmt.close();
                conn.close();

                return acteMedicalList; // Return the list of acte_ids
            }
        };

        task.setOnSucceeded(event -> {
            if (task.getValue().isEmpty()) {
                showErrorAlert("Aucun acte médical trouvé pour ce patient.");
            } else {
                cbActeMedical.setItems(task.getValue()); // Populate ComboBox with options
            }
        });

        task.setOnFailed(event -> {
            showErrorAlert("Erreur lors du chargement des actes médicaux.");
        });

        new Thread(task).start();
    }




    // Method to load Categories data asynchronously
    private void loadCategories() {
        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                ObservableList<String> categoriesList = FXCollections.observableArrayList();
                Connection conn = DatabaseConnection.connect();
                String query = "SELECT categorie FROM categorie_intervention";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    categoriesList.add(rs.getString("categorie"));
                }

                rs.close();
                stmt.close();
                conn.close();
                return categoriesList;
            }
        };

        // When the data is fetched, update the ComboBox on the JavaFX Application thread
        task.setOnSucceeded(event -> {
            cbCategorie.setItems(task.getValue());
        });

        task.setOnFailed(event -> {
            showErrorAlert("Erreur lors du chargement des catégories.");
        });

        // Start the task
        new Thread(task).start();
    }

    @FXML
    private void onAjouterRendezvousClick() {
        try {
            Patient selectedPatient = tablePatients.getSelectionModel().getSelectedItem();
            if (selectedPatient == null) {
                showErrorAlert("No patient selected!");
                return;
            }

            int patientId = selectedPatient.getID(); // Get the selected patient ID
            System.out.println("Selected Patient ID: " + patientId);

            // Validate inputs
            if (labelIDPatient.getText().equals(".....")) {
                showErrorAlert("Sélectionnez un patient dans la table.");
                return;
            }

            if (cbActeMedical.getValue() == null || cbCategorie.getValue() == null || dpDatePrevue.getValue() == null) {
                showErrorAlert("Veuillez remplir tous les champs.");
                return;
            }

            Connection conn = DatabaseConnection.connect();
            String acteMedical = cbActeMedical.getValue();
            String categorie = cbCategorie.getValue();
            LocalDateTime datePrevue = dpDatePrevue.getValue().atStartOfDay(); // Adjust as needed for time input

            // Insert into database
            String query = "INSERT INTO RendezVous (patient_id, date_prevue, categorie_id, numero_acte, etat) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, patientId);
                statement.setTimestamp(2, Timestamp.valueOf(datePrevue));
                statement.setInt(3, getCategorieIdByName(categorie)); // Implement helper method
                statement.setInt(4, Integer.parseInt(acteMedical)); // Set Acte as null initially
                statement.setString(5, "Planifié"); // Default status

                statement.executeUpdate();

                // Send email after adding the rendez-vous
                String emailSubject = "Rendez-vous Planifié";
                String emailContent = "Bonjour " + selectedPatient.getPrenom() + " " + selectedPatient.getNom() + ",\n\n" +
                        "Nous vous informons que votre rendez-vous a été planifié avec succès. Voici les détails :\n\n" +
                        "Date : " + datePrevue.toLocalDate() + "\n" +
                        "Acte médical : " + acteMedical + "\n" +
                        "Catégorie : " + categorie + "\n\n" +
                        "Nous vous prions d’arriver à l’heure prévue et de bien vouloir nous contacter en cas d’empêchement.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe de la CliniDent";
                sendEmail(selectedPatient.getEmail(), emailSubject, emailContent);


                HelloApplication.loadPage("SecretairePages/rendezvous.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur lors de l'ajout du rendez-vous : " + e.getMessage());
        }
    }

    // Helper method to send email
    @FXML
    private void sendEmail(String toEmail, String subject, String messageText) {
        // SMTP server information
        String host = "smtp.gmail.com";  // Change to your SMTP host
        String user = "moaad.za12@gmail.com";  // Sender's email
        String password = "eljs etir olhy cgxo";  // Sender's email password

        // Set up properties for the mail session
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");  // Use port 587 for TLS
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Authenticate and send email
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });


        try {
            // Create a new email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));  // Corrected access
            message.setSubject(subject);
            message.setText(messageText);

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email.");
        }
    }


    // Helper method to get category ID by name
    private int getCategorieIdByName(String categorie) throws SQLException {
        Connection conn = DatabaseConnection.connect();
        String query = "SELECT ID FROM categorie_intervention WHERE categorie = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, categorie);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("ID");
            }
            throw new SQLException("Catégorie introuvable.");
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

    public void onAddRendezVousClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/AjoutRendezVous.fxml");
    }

    public void onReporterClick(ActionEvent actionEvent) {
        System.out.println("Reporter Rendezvous");
    }

    public void onAnnulerClick(ActionEvent actionEvent) {
        System.out.println("Annuler Rendezvous");
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
