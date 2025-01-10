package Controllers;

import DAO.ActeDAO;
import DAO.MainApplication;
import DAO.PaiementDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import metier.Acte;
import metier.Paiement;

public class AjoutPaiementController {
    @FXML private DatePicker datePaiement;
    @FXML private TextField montant;
    @FXML private ComboBox<String> methodPaiement;
    @FXML private TableView<Acte> tableActes;
    @FXML private TableColumn<Acte, Integer> colID;
    @FXML private TableColumn<Acte, String> colPatient;
    @FXML private TableColumn<Acte, Double> colPrix;
    @FXML private TableColumn<Acte, String> colEtat;
    @FXML private TableColumn<Acte, Integer> colPatientID;
    @FXML private TextField tfSearch;

    private final ActeDAO acteDAO = new ActeDAO();
    private final PaiementDAO paiementDAO = new PaiementDAO();

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixComptabilise"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etatDeLActe"));
        colPatientID.setCellValueFactory(new PropertyValueFactory<>("patientID"));

        loadActes();
    }

    private void loadActes() {
        ObservableList<Acte> actesList = FXCollections.observableArrayList(acteDAO.getActes());
        tableActes.setItems(actesList);
    }

    @FXML
    private void ajouterPaiement() {
        Acte selectedActe = tableActes.getSelectionModel().getSelectedItem();
        if (selectedActe == null) {
            showErrorAlert("Please select an Acte.");
            return;
        }

        String datePaiementStr = datePaiement.getValue() != null ? datePaiement.getValue().toString() : null;
        String montantStr = montant.getText();
        String methodPaiementValue = methodPaiement.getValue();

        if (datePaiementStr == null || montantStr.isEmpty() || methodPaiementValue == null) {
            showErrorAlert("Please fill all fields.");
            return;
        }

        double montantValue;
        try {
            montantValue = Double.parseDouble(montantStr);
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid amount.");
            return;
        }

        Paiement paiement = new Paiement(
                selectedActe.getId(),
                selectedActe.getPatientID(),
                datePaiementStr,
                montantValue,
                selectedActe.getPrixComptabilise(),
                methodPaiementValue
        );

        if (paiementDAO.insertPaiement(paiement)) {
            MainApplication.loadPage("SecretairePages/paiement.fxml");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Payment added successfully!", ButtonType.OK);
            alert.showAndWait();
        } else {
            showErrorAlert("Failed to add payment.");
        }
    }

    @FXML
    private void onSearchKeyReleased() {
        String searchQuery = tfSearch.getText().toLowerCase();

        // Use the ActeDAO to fetch filtered data
        ActeDAO acteDAO = new ActeDAO();
        ObservableList<Acte> filteredList = acteDAO.searchActesByPatientName(searchQuery);

        // Set the filtered list to the TableView
        tableActes.setItems(filteredList);

        // If the search field is empty, reload the full data set
        if (searchQuery.isEmpty()) {
            loadActes();
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
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
}