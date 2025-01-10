package Controllers;

import DAO.ActeDAO;
import DAO.MainApplication;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import metier.Acte;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActesmedicauxController {
    @FXML
    private TableView<Acte> tableActes;
    @FXML
    private TableColumn<Acte, Integer> colID;
    @FXML
    private TableColumn<Acte, String> colDateDebut;
    @FXML
    private TableColumn<Acte, Double> colPrix;
    @FXML
    private TableColumn<Acte, String> colEtat;
    @FXML
    private TableColumn<Acte, String> colDateFin;
    @FXML
    private TableColumn<Acte, String> colPatient;
    @FXML
    private Label totalActes;
    @FXML
    private Label percentageCompletedActs;
    @FXML
    private Label percentageOngoingActs;

    private final ActeDAO acteDAO = new ActeDAO(); // Use DAO instance

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixComptabilise"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etatDeLActe"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));

        loadActesFromDatabase();
        fetchActePercentages();
    }

    private void loadActesFromDatabase() {
        ObservableList<Acte> actesList = acteDAO.getAllActes();
        tableActes.setItems(actesList);
    }

    private void fetchActePercentages() {
        int total = acteDAO.getTotalActes();
        int completed = acteDAO.getCompletedActes();
        int ongoing = acteDAO.getOngoingActes();

        totalActes.setText(String.valueOf(total));
        percentageCompletedActs.setText(total > 0 ? String.format("%.2f%%", (completed * 100.0) / total) : "0%");
        percentageOngoingActs.setText(total > 0 ? String.format("%.2f%%", (ongoing * 100.0) / total) : "0%");
    }

    @FXML
    private void onCloreActeClick() {
        Acte selectedActe = tableActes.getSelectionModel().getSelectedItem();
        if (selectedActe == null) {
            showErrorAlert("Veuillez sélectionner un acte médical.");
            return;
        }

        String currentDate = java.time.LocalDate.now().toString();
        if (currentDate.compareTo(selectedActe.getDateDebut()) >= 0) {
            selectedActe.setEtatDeLActe("Terminé");
            selectedActe.setDateFin(currentDate);

            acteDAO.updateActe(selectedActe);
            tableActes.refresh();
        } else {
            showErrorAlert("La date actuelle ne peut pas être avant la date de début de l'acte.");
        }
    }
    @FXML
    private void onGererRendezvousClick() {
        Acte selectedActe = tableActes.getSelectionModel().getSelectedItem();

        if (selectedActe == null) {
            showErrorAlert("Veuillez sélectionner un acte médical.");
            return;
        }

        int acteID = selectedActe.getId();
        int patientID = selectedActe.getPatientID();

        MainApplication.oadPageWithController("gestionrendezvous.fxml", controller -> {
            if (controller instanceof GestionrendezvousController) {
                ((GestionrendezvousController) controller).setActeAndPatientDetails(acteID, patientID);
            }
        });
    }
    @FXML
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Set default file name
        fileChooser.setInitialFileName("Actes_Medicaux.xlsx");

        File file = fileChooser.showSaveDialog(tableActes.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Actes Médicaux");
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < tableActes.getColumns().size(); i++) {
                    TableColumn<?, ?> column = tableActes.getColumns().get(i);
                    if (column.isVisible()) { // Only add visible columns
                        headerRow.createCell(i).setCellValue(column.getText());
                    }
                }
                ObservableList<Acte> data = tableActes.getItems();
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    Acte acte = data.get(i);

                    row.createCell(0).setCellValue(acte.getId());
                    row.createCell(1).setCellValue(acte.getPatientName());
                    row.createCell(2).setCellValue(acte.getDateDebut().toString());
                    row.createCell(3).setCellValue(acte.getPrixComptabilise());
                    row.createCell(4).setCellValue(acte.getEtatDeLActe());
                    row.createCell(5).setCellValue(acte.getDateFin() != null ? acte.getDateFin().toString() : "");
                }

                // Resize columns to fit content
                for (int i = 0; i < tableActes.getColumns().size(); i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write the output to a file
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
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
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

    public void onAddActeClick(ActionEvent actionEvent) {
        MainApplication.loadPage("AjouterAct.fxml");

    }
}
