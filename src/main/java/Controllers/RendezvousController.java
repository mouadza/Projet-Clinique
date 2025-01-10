package Controllers;

import DAO.MainApplication;
import DAO.RendezVousDAO;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import metier.RendezVous;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class RendezvousController {

    @FXML
    private VBox popupReporter;
    @FXML
    private Label selectedRendezVousLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<RendezVous> interventionsTable;
    @FXML
    private TableColumn<RendezVous, Number> idColumn;
    @FXML
    private TableColumn<RendezVous, String> patientColumn;
    @FXML
    private TableColumn<RendezVous, String> datePrevColumn;
    @FXML
    private TableColumn<RendezVous, String> categoryColumn;
    @FXML
    private TableColumn<RendezVous, Number> medicalActColumn;
    @FXML
    private TableColumn<RendezVous, String> statusColumn;
    @FXML
    private TableColumn<RendezVous, String> realDateColumn;
    @FXML
    private Label upcomingAppointmentsLabel;
    @FXML
    private Label totalInterventionsLabel;
    @FXML
    private Label pastInterventionsLabel;
    @FXML
    private TextField searchTextField;

    private ObservableList<RendezVous> rendezVousList = FXCollections.observableArrayList();
    private RendezVousDAO rendezVousDAO = new RendezVousDAO();

    public void initialize() {
        // Set up the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));
        datePrevColumn.setCellValueFactory(new PropertyValueFactory<>("datePrev"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        medicalActColumn.setCellValueFactory(new PropertyValueFactory<>("medicalAct"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        realDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateReelle"));

        loadData();
        loadAppointmentCounts();
    }

    private void loadData() {
        List<RendezVous> rendezVousListFromDB = rendezVousDAO.getAllRendezVous();
        rendezVousList.clear();
        rendezVousList.addAll(rendezVousListFromDB);
        interventionsTable.setItems(rendezVousList);
    }

    @FXML
    private void onSearchKeyReleased() {
        String searchText = searchTextField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            interventionsTable.setItems(rendezVousList);
        } else {
            ObservableList<RendezVous> filteredList = FXCollections.observableArrayList();
            for (RendezVous rdv : rendezVousList) {
                if (rdv.getPatient().toLowerCase().contains(searchText)) {
                    filteredList.add(rdv);
                }
            }
            interventionsTable.setItems(filteredList);
        }
    }

    @FXML
    private void onReporterClick() {
        RendezVous selected = interventionsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedRendezVousLabel.setText("Rendez-vous ID: " + selected.getId());
            popupReporter.setVisible(true);
        } else {
            showErrorAlert("Veuillez sélectionner un rendez-vous.");
        }
    }

    @FXML
    private void onConfirmReporterClick() {
        RendezVous selected = interventionsTable.getSelectionModel().getSelectedItem();
        if (selected != null && datePicker.getValue() != null) {
            Timestamp newDate = Timestamp.valueOf(datePicker.getValue().atStartOfDay());
            boolean success = rendezVousDAO.updateRendezVousDate(selected.getId(), newDate);
            if (success) {
                loadData();
                popupReporter.setVisible(false);
            } else {
                showErrorAlert("Erreur lors de la mise à jour du rendez-vous.");
            }
        } else {
            showErrorAlert("Veuillez sélectionner un rendez-vous et une date.");
        }
    }

    @FXML
    private void onCancelReporterClick() {
        popupReporter.setVisible(false);
    }

    @FXML
    private void onAnnulerClick() {
        RendezVous selected = interventionsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean confirm = showConfirmationDialog();
            if (confirm) {
                boolean success = rendezVousDAO.deleteRendezVous(selected.getId());
                if (success) {
                    interventionsTable.getItems().remove(selected);
                }
            }
        } else {
            showErrorAlert("Veuillez sélectionner un rendez-vous.");
        }
    }

    private boolean showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Êtes-vous sûr de vouloir annuler ce rendez-vous?");
        return alert.showAndWait().get() == ButtonType.OK;
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void loadAppointmentCounts() {
        totalInterventionsLabel.setText(String.valueOf(rendezVousDAO.countTotalRendezVous()));
        upcomingAppointmentsLabel.setText(String.valueOf(rendezVousDAO.countPlanifieRendezVous()));
        pastInterventionsLabel.setText(String.valueOf(rendezVousDAO.countTermineRendezVous()));
    }

    @FXML
    private void exportToExcel() {
        // Create a FileChooser to allow the user to choose the file path and name
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Set the default file name
        fileChooser.setInitialFileName("Rendezvous_Data.xlsx");

        // Open the save dialog and get the chosen file
        File file = fileChooser.showSaveDialog(interventionsTable.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                // Create a new sheet in the workbook
                Sheet sheet = workbook.createSheet("Rendezvous Data");

                // Create the header row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("ID");
                headerRow.createCell(1).setCellValue("Patient");
                headerRow.createCell(2).setCellValue("Date prévue");
                headerRow.createCell(3).setCellValue("Catégorie");
                headerRow.createCell(4).setCellValue("Numéro d'acte");
                headerRow.createCell(5).setCellValue("État");
                headerRow.createCell(6).setCellValue("Date réelle");

                // Add the data rows from the TableView
                ObservableList<RendezVous> data = interventionsTable.getItems();
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    RendezVous rendezvous = data.get(i);

                    row.createCell(0).setCellValue(rendezvous.getId());
                    row.createCell(1).setCellValue(rendezvous.getPatient());
                    row.createCell(2).setCellValue(rendezvous.getDatePrev());
                    row.createCell(3).setCellValue(rendezvous.getCategory());
                    row.createCell(4).setCellValue(rendezvous.getMedicalAct());
                    row.createCell(5).setCellValue(rendezvous.getEtat());
                    row.createCell(6).setCellValue(rendezvous.getDateReelle());
                }

                // Resize columns to fit content
                for (int i = 0; i < 7; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write the workbook to the selected file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                System.out.println("Exported to Excel successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error exporting to Excel.");
            }
        }
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
}
