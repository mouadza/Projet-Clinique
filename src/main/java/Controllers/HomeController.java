package Controllers;

import DAO.MainApplication;
import DAO.RendezVousDAO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import metier.RendezVous;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HomeController {
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
    private Label lblCurrentDate;
    @FXML
    private Label totalRendezvousLabel;
    @FXML
    private Label totalPatient;
    @FXML
    private Label todayRendezvousLabel;

    private final RendezVousDAO rendezVousDAO = new RendezVousDAO();

    public void initialize() {
        setupTable();
        loadTodayPlanifieRendezVous();
        loadCounters();
        setCurrentDate();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("datePrev"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        medicalActColumn.setCellValueFactory(new PropertyValueFactory<>("medicalAct"));
    }

    private void loadTodayPlanifieRendezVous() {
        ObservableList<RendezVous> rendezVousList = rendezVousDAO.getTodayPlanifieRendezVous();
        interventionsTable.setItems(rendezVousList);
    }

    private void loadCounters() {
        totalRendezvousLabel.setText(String.valueOf(rendezVousDAO.getTotalRendezVousCount()));
        totalPatient.setText(String.valueOf(rendezVousDAO.getTotalPatientsCount()));
        todayRendezvousLabel.setText(String.valueOf(rendezVousDAO.getTodayPlanifieRendezVousCount()));
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

    private void setCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
        lblCurrentDate.setText("La liste des rendez-vous aujourd'hui, le " + currentDate.format(formatter));
    }
}
