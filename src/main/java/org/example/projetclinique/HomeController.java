package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Label;


public class HomeController {
    @FXML
    private TableView<Rendezvous> interventionsTable;
    @FXML
    private TableColumn<Rendezvous, Integer> idColumn;
    @FXML
    private TableColumn<Rendezvous, String> patientColumn;
    @FXML
    private TableColumn<Rendezvous, String> dateColumn;
    @FXML
    private TableColumn<Rendezvous, String> categoryColumn;
    @FXML
    private TableColumn<Rendezvous, String> medicalActColumn;

    @FXML
    private Label totalRendezvousLabel;
    @FXML
    private Label totalPatient;
    @FXML
    private Label todayRendezvousLabel;

    private ObservableList<Rendezvous> rendezVousList = FXCollections.observableArrayList();
    public void initialize() {
        setupTable();
        fetchRendezVous();
        fetchCounters();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("datePrev"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        medicalActColumn.setCellValueFactory(new PropertyValueFactory<>("medicalAct"));
    }

    private void fetchRendezVous() {
        // SQL query to fetch today's and "Planifié" rendezvous
        String query = "SELECT R.ID, DATE(R.date_prevue) AS date_pre, CI.categorie AS category, R.numero_acte, R.etat, DATE(R.date_reelle) AS date_reele, CONCAT(P.prenom, ' ', P.nom) AS full_name "
                + "FROM Rendezvous R "
                + "JOIN Patient P ON R.patient_id = P.id "
                + "JOIN categorie_intervention CI ON R.categorie_id = CI.id "
                + "WHERE DATE(R.date_prevue) = CURDATE() AND R.etat = 'Planifié'";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            ObservableList<Rendezvous> rendezvousList = FXCollections.observableArrayList();

            while (resultSet.next()) {
                Rendezvous rendezvous = new Rendezvous(
                        resultSet.getInt("ID"),
                        resultSet.getString("full_name"),
                        resultSet.getString("date_pre"),
                        resultSet.getString("category"),
                        resultSet.getInt("numero_acte"),
                        resultSet.getString("etat"),
                        resultSet.getString("date_reele")
                );
                rendezvousList.add(rendezvous);
            }

            // Set the fetched list into the TableView
            interventionsTable.setItems(rendezvousList);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des rendez-vous planifiés d'aujourd'hui.");
        }
    }



    private void fetchCounters() {
        // Queries
        String totalRendezvousQuery = "SELECT COUNT(*) AS total_rendezvous FROM RendezVous";
        String totalPatientsQuery = "SELECT COUNT(*) AS total_patients FROM Patient";
        String todayPlanifieQuery = "SELECT COUNT(*) AS planifie_rendezvous_today FROM RendezVous WHERE etat = 'Planifié' AND DATE(date_prevue) = CURDATE()";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement totalRendezvousStmt = conn.prepareStatement(totalRendezvousQuery);
             PreparedStatement totalPatientsStmt = conn.prepareStatement(totalPatientsQuery);
             PreparedStatement todayPlanifieStmt = conn.prepareStatement(todayPlanifieQuery)) {

            // Fetch total rendezvous
            ResultSet totalRendezvousResult = totalRendezvousStmt.executeQuery();
            if (totalRendezvousResult.next()) {
                int totalRendezvous = totalRendezvousResult.getInt("total_rendezvous");
                totalRendezvousLabel.setText(String.valueOf(totalRendezvous));
            }

            // Fetch total patients
            ResultSet totalPatientsResult = totalPatientsStmt.executeQuery();
            if (totalPatientsResult.next()) {
                int totalPatients = totalPatientsResult.getInt("total_patients");
                totalPatient.setText(String.valueOf(totalPatients));
            }

            // Fetch today's "Planifié" rendezvous
            ResultSet todayPlanifieResult = todayPlanifieStmt.executeQuery();
            if (todayPlanifieResult.next()) {
                int planifieRendezvousToday = todayPlanifieResult.getInt("planifie_rendezvous_today");
                todayRendezvousLabel.setText(String.valueOf(planifieRendezvousToday));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des données des rendez-vous.");
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
}
