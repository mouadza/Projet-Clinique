package Controllers;

import DAO.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import metier.Patient;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

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
    private ComboBox<String> cbActeMedical;
    @FXML
    private ComboBox<String> cbCategorie;

    private ObservableList<Patient> patientList = FXCollections.observableArrayList();
    @FXML
    private TextField tfSearch;

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

        cbActeMedical.setFocusTraversable(false);

        tablePatients.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cbActeMedical.setDisable(false);
                labelIDPatient.setText(newValue.getPrenom() + " " + newValue.getNom());
                loadActeMedicalData(newValue.getID());
            } else {
                cbActeMedical.setDisable(true);
                labelIDPatient.setText("");
                hiddenPatientId.setText("");
            }
        });

        loadPatientData();
        loadCategories();
    }

    @FXML
    private void onSearchKeyReleased() {
        String searchText = tfSearch.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            tablePatients.setItems(patientList);
        } else {
            ObservableList<Patient> filteredList = FXCollections.observableArrayList();

            for (Patient patient : patientList) {
                if (patient.getNom().toLowerCase().contains(searchText) ||
                        patient.getPrenom().toLowerCase().contains(searchText) ||
                        patient.getCIN().toLowerCase().contains(searchText)) {
                    filteredList.add(patient);
                }
            }

            tablePatients.setItems(filteredList);
        }
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

        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                return ActeDAO.getActesMedicalsForPatient(patientId);
            }
        };

        task.setOnSucceeded(event -> {
            if (task.getValue().isEmpty()) {
                showErrorAlert("Aucun acte médical trouvé pour ce patient.");
            } else {
                cbActeMedical.setItems(task.getValue());
            }
        });

        task.setOnFailed(event -> {
            showErrorAlert("Erreur lors du chargement des actes médicaux.");
        });

        new Thread(task).start();
    }

    private void loadCategories() {
        Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                return CategoryDAO.getCategories();
            }
        };

        task.setOnSucceeded(event -> {
            cbCategorie.setItems(task.getValue());
        });

        task.setOnFailed(event -> {
            showErrorAlert("Erreur lors du chargement des catégories.");
        });

        new Thread(task).start();
    }

    @FXML
    private void onAjouterRendezvousClick() {
        try {
            Patient selectedPatient = tablePatients.getSelectionModel().getSelectedItem();
            if (selectedPatient == null) {
                showErrorAlert("Aucun patient sélectionné!");
                return;
            }

            int patientId = selectedPatient.getID();
            if (labelIDPatient.getText().equals(".....")) {
                showErrorAlert("Sélectionnez un patient dans la table.");
                return;
            }

            if (cbActeMedical.getValue() == null || cbCategorie.getValue() == null || dpDatePrevue.getValue() == null) {
                showErrorAlert("Veuillez remplir tous les champs.");
                return;
            }

            String acteMedical = cbActeMedical.getValue();
            String categorie = cbCategorie.getValue();
            LocalDateTime datePrevue = dpDatePrevue.getValue().atStartOfDay();

            // Insert the rendezvous using DAO
            RendezVousDAO.addRendezVous(patientId, Timestamp.valueOf(datePrevue), CategoryDAO.getCategorieIdByName(categorie), Integer.parseInt(acteMedical), "Planifié");

            // Send email after adding the rendez-vous
            String emailSubject = "Rendez-vous Planifié";
            String emailContent = "Bonjour " + selectedPatient.getPrenom() + " " + selectedPatient.getNom() + ",\n\n" +
                    "Votre rendez-vous a été planifié avec succès.\n\n" +
                    "Date : " + datePrevue.toLocalDate() + "\n" +
                    "Acte médical : " + acteMedical + "\n" +
                    "Catégorie : " + categorie + "\n\n" +
                    "Cordialement,\nL'équipe de la CliniDent";
            sendEmail(selectedPatient.getEmail(), emailSubject, emailContent);

            MainApplication.loadPage("SecretairePages/rendezvous.fxml");

        } catch (Exception e) {
            showErrorAlert("Erreur lors de l'ajout du rendez-vous : " + e.getMessage());
        }
    }

    @FXML
    private void sendEmail(String recipient, String subject, String content) {
        // SMTP server information
        String fromEmail = "moaad.za12@gmail.com";  // Sender's email
        String fromName = "CliniDent";  // Sender's name
        String host = "smtp.gmail.com";  // SMTP server for Gmail
        String username = "moaad.za12@gmail.com";  // Your email username
        String password = "eljs etir olhy cgxo";  // Your email password

        // Set properties for the mail session
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");  // Use port 587 for TLS
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Create a session with authentication
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a new email message
            Message message = new MimeMessage(session);

            // Set the "From" field with a display name
            message.setFrom(new InternetAddress(fromEmail, fromName));

            // Set the recipient's email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

            // Set the subject and content
            message.setSubject(subject);
            message.setText(content);

            // Send the email
            Transport.send(message);

            System.out.println("Email sent successfully to " + recipient);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email.");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void onLogoutButtonClick(ActionEvent actionEvent) {
        MainApplication.loadPage("hello-view.fxml");
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

    public void onAddRendezVousClick(ActionEvent actionEvent) {
        MainApplication.loadPage("SecretairePages/AjoutRendezVous.fxml");
    }

    public void onReporterClick(ActionEvent actionEvent) {
        System.out.println("Reporter Rendezvous");
    }

    public void onAnnulerClick(ActionEvent actionEvent) {
        System.out.println("Annuler Rendezvous");
    }
}
