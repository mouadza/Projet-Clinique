package Controllers;

import DAO.MainApplication;
import DAO.PaiementDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import metier.Paiement;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PaiementController {
    @FXML
    private TableView<Paiement> paiementTable;

    @FXML
    private TableColumn<Paiement, String> columnId, columnActeId, columnPatientId,
            columnDatePaiement, columnMontant,
            columnPrixComptabilise, columnReste,
            columnPaiementMethod, columnStatut;

    @FXML
    private TextField searchTextField;

    @FXML
    private Label lblTotalPaiements, lblTotalPaid, lblOutstandingBalance;

    private final ObservableList<Paiement> paiementList = FXCollections.observableArrayList();
    private PaiementDAO paiementDAO;

    @FXML
    public void initialize() {
        paiementDAO = new PaiementDAO();

        setupTableColumns();
        loadPaiements();
        loadPaiementStatistics();
    }

    private void setupTableColumns() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnActeId.setCellValueFactory(new PropertyValueFactory<>("acteId"));
        columnPatientId.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        columnDatePaiement.setCellValueFactory(new PropertyValueFactory<>("datePaiement"));
        columnMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        columnPrixComptabilise.setCellValueFactory(new PropertyValueFactory<>("prixComptabilise"));
        columnReste.setCellValueFactory(new PropertyValueFactory<>("reste"));
        columnPaiementMethod.setCellValueFactory(new PropertyValueFactory<>("paiementMethod"));
        columnStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    private void loadPaiements() {
        List<Paiement> paiements = paiementDAO.getAllPaiements();
        paiementList.setAll(paiements);
        paiementTable.setItems(paiementList);
    }

    private void loadPaiementStatistics() {
        lblTotalPaiements.setText(String.valueOf(paiementDAO.getTotalPaiements()));
        lblTotalPaid.setText(String.format("%.2f", paiementDAO.getTotalPaidAmount()));
        lblOutstandingBalance.setText(String.format("%.2f", paiementDAO.getTotalOutstandingBalance()));
    }

    @FXML
    private void onSearchKeyReleased() {
        String searchText = searchTextField.getText().trim().toLowerCase();
        ObservableList<Paiement> filteredList = FXCollections.observableArrayList();

        for (Paiement paiement : paiementList) {
            if (paiement.getPatient_name().toLowerCase().contains(searchText)) {
                filteredList.add(paiement);
            }
        }

        paiementTable.setItems(filteredList.isEmpty() ? paiementList : filteredList);
    }

    @FXML
    private void exportToExcelPaiement() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("Paiements.xlsx");
        File file = fileChooser.showSaveDialog(paiementTable.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Paiements");
                Row headerRow = sheet.createRow(0);

                for (int i = 0; i < paiementTable.getColumns().size(); i++) {
                    headerRow.createCell(i).setCellValue(paiementTable.getColumns().get(i).getText());
                }

                ObservableList<Paiement> data = paiementTable.getItems();
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    Paiement paiement = data.get(i);

                    row.createCell(0).setCellValue(paiement.getId());
                    row.createCell(1).setCellValue(paiement.getActeId());
                    row.createCell(2).setCellValue(paiement.getPatient_name());
                    row.createCell(3).setCellValue(paiement.getDatePaiement());
                    row.createCell(4).setCellValue(paiement.getMontant());
                    row.createCell(5).setCellValue(paiement.getPrixComptabilise());
                    row.createCell(6).setCellValue(paiement.getReste());
                    row.createCell(7).setCellValue(paiement.getPaiementMethod());
                    row.createCell(8).setCellValue(paiement.getStatut());
                }

                for (int i = 0; i < paiementTable.getColumns().size(); i++) {
                    sheet.autoSizeColumn(i);
                }

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
    public void AjoutPaiementPage(ActionEvent actionEvent) {
        MainApplication.loadPage("SecretairePages/AjoutPaiement.fxml");
    }

}
