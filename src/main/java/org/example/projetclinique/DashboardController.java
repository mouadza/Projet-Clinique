package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        System.out.println("Utilisateurs");
    }

    public void onLogoutButtonClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("hello-view.fxml");
    }

    public void onBtnConfigClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("config.fxml");
    }
    @FXML
    private Label lblPatientsActifs;

    @FXML
    private Label lblRendezVousAujourdHui;

    @FXML
    private TableView<RendezVous> tableRendezVous;
    @FXML
    private TableColumn<RendezVous, String> colNomPatient;
    @FXML
    private TableColumn<RendezVous, String> colHeure;
    @FXML
    private TableColumn<RendezVous, String> colService;
    @FXML
    private Label lblCurrentDate;
    public void initialize() {
        loadStatistics();
        setupTableColumns();
        loadRendezVousTable();
        setCurrentDate();
    }
    private void setCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDate = currentDate.format(formatter);

        lblCurrentDate.setText("La Liste des Rendez-vous Aujourd'hui le " + formattedDate);
    }
    private void setupTableColumns() {
        colNomPatient.setCellValueFactory(new PropertyValueFactory<>("nomPatient"));
        colHeure.setCellValueFactory(new PropertyValueFactory<>("heure"));
        colService.setCellValueFactory(new PropertyValueFactory<>("service"));
    }
    private void loadRendezVousTable() {
        ObservableList<RendezVous> rendezVousList = FXCollections.observableArrayList();
        Connection connection = DatabaseConnection.connect();
        if (connection == null) {
            System.err.println("Failed to establish a database connection.");
            return;
        }
        try (Statement statement = connection.createStatement()) {
            String query = """
            SELECT CONCAT(p.prenom, ' ', p.nom) AS nom_patient, 
                   TIME(r.date_reele) AS heure_reele, 
                   s.nom AS service
            FROM RendezVous r
            JOIN Patient p ON r.id_Patient = p.id_patient
            JOIN Service s ON r.service_id = s.id_service
            WHERE DATE(r.date_prevu) = CURDATE()
        """;
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String nomPatient = resultSet.getString("nom_patient");
                String heure = resultSet.getString("heure_reele");
                String service = resultSet.getString("service");

                rendezVousList.add(new RendezVous(nomPatient, heure, service));
            }

            tableRendezVous.setItems(rendezVousList);

        } catch (SQLException e) {
            System.err.println("Error loading TableView: " + e.getMessage());
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



    private void loadStatistics() {
        Connection connection = DatabaseConnection.connect();

        if (connection == null) {
            System.err.println("Failed to establish a database connection.");
            return;
        }

        try (Statement statement = connection.createStatement()) {
            // Requête pour compter les patients actifs
            String queryPatientsActifs = "SELECT COUNT(*) AS total FROM Patient WHERE actif = TRUE";
            ResultSet rsPatients = statement.executeQuery(queryPatientsActifs);
            if (rsPatients.next()) {
                lblPatientsActifs.setText(String.valueOf(rsPatients.getInt("total")));
            }

            // Requête pour compter les rendez-vous du jour
            String queryRendezVous = """
                SELECT COUNT(*) AS total
                FROM RendezVous
                WHERE DATE(date_rendezvous) = CURDATE()
            """;
            ResultSet rsRendezVous = statement.executeQuery(queryRendezVous);
            if (rsRendezVous.next()) {
                lblRendezVousAujourdHui.setText(String.valueOf(rsRendezVous.getInt("total")));
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
}
