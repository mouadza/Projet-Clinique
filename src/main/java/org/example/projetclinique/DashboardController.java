package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class DashboardController {
    @FXML
    private Label lblCurrentDate;
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
    private Label totalActe;

    private ObservableList<Rendezvous> rendezVousList = FXCollections.observableArrayList();
    public void initialize() {
        setupTable();
        fetchRendezVous();
        fetchCounters();
        setCurrentDate();
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
        String actemedicauxQuery = "SELECT COUNT(*) AS total_actes FROM ActeMedicaux";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement totalRendezvousStmt = conn.prepareStatement(totalRendezvousQuery);
             PreparedStatement totalPatientsStmt = conn.prepareStatement(totalPatientsQuery);
             PreparedStatement actemedicauxStmt = conn.prepareStatement(actemedicauxQuery)) {

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
            ResultSet actemedicauxResult = actemedicauxStmt.executeQuery();
            if (actemedicauxResult.next()) {
                int planifieRendezvousToday = actemedicauxResult.getInt("total_actes");
                totalActe.setText(String.valueOf(planifieRendezvousToday));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des données des rendez-vous.");
        }
    }

    public void onBtnAccueilClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("dashboard.fxml");
    }

    public void onBtnPatientsClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("patient.fxml");
    }

    public void onBtnActeMedicClick(ActionEvent actionEvent) {
        System.out.println("Actes Medicaux openned");
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

    private void setCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
        String formattedDate = currentDate.format(formatter);

        lblCurrentDate.setText("La liste des rendez-vous aujourd'hui, le " + formattedDate);
    }

}
