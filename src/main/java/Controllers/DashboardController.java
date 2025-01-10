package Controllers;

import DAO.RendezVousDAO;
import DAO.MainApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import metier.RendezVous;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DashboardController {

    @FXML
    private Label lblCurrentDate;
    @FXML
    private TableView<RendezVous> interventionsTable;
    @FXML
    private TableColumn<RendezVous, Integer> idColumn;
    @FXML
    private TableColumn<RendezVous, String> patientColumn;
    @FXML
    private TableColumn<RendezVous, String> dateColumn;
    @FXML
    private TableColumn<RendezVous, String> categoryColumn;
    @FXML
    private TableColumn<RendezVous, String> medicalActColumn;

    @FXML
    private Label totalRendezvousLabel;
    @FXML
    private Label totalPatient;
    @FXML
    private Label totalActe;

    private RendezVousDAO rendezVousDAO = new RendezVousDAO();

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
        try {
            List<RendezVous> rendezVousList = rendezVousDAO.fetchRendezVous();
            ObservableList<RendezVous> observableList = FXCollections.observableArrayList(rendezVousList);
            interventionsTable.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des rendez-vous planifiés d'aujourd'hui.");
        }
    }

    private void fetchCounters() {
        try {
            totalRendezvousLabel.setText(String.valueOf(rendezVousDAO.getTotalRendezvous()));
            totalPatient.setText(String.valueOf(rendezVousDAO.getTotalPatients()));
            totalActe.setText(String.valueOf(rendezVousDAO.getTotalActes()));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des données des rendez-vous.");
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

    private void setCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
        String formattedDate = currentDate.format(formatter);
        lblCurrentDate.setText("La liste des rendez-vous aujourd'hui, le " + formattedDate);
    }
}
