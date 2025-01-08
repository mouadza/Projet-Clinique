package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaiementController {
    @FXML
    private TableView<Paiement> paiementTable;

    @FXML
    private TableColumn<Paiement, String> columnId;

    @FXML
    private TableColumn<Paiement, String> columnActeId;

    @FXML
    private TableColumn<Paiement, String> columnPatientId;

    @FXML
    private TableColumn<Paiement, String> columnDatePaiement;

    @FXML
    private TableColumn<Paiement, String> columnMontant;

    @FXML
    private TableColumn<Paiement, String> columnPrixComptabilise;

    @FXML
    private TableColumn<Paiement, String> columnReste;

    @FXML
    private TableColumn<Paiement, String> columnPaiementMethod;

    @FXML
    private TableColumn<Paiement, String> columnStatut;

    @FXML
    private TextField searchTextField;

     // Original list of data (fetched from the database)
    @FXML
    private FilteredList<Paiement> filteredActeList;

    @FXML
    private Label lblTotalPaiements;        // Total number of paiements
    @FXML
    private Label lblTotalPaid;            // Total amount paid
    @FXML
    private Label lblOutstandingBalance;

    private final ObservableList<Paiement> paiementList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnActeId.setCellValueFactory(new PropertyValueFactory<>("acteId"));
        columnPatientId.setCellValueFactory(new PropertyValueFactory<>("patienId"));
        columnDatePaiement.setCellValueFactory(new PropertyValueFactory<>("datePaiement"));
        columnMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        columnPrixComptabilise.setCellValueFactory(new PropertyValueFactory<>("prixComptabilise"));
        columnReste.setCellValueFactory(new PropertyValueFactory<>("reste"));
        columnPaiementMethod.setCellValueFactory(new PropertyValueFactory<>("paiementMethod"));
        columnStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        filteredActeList = new FilteredList<>(paiementList, p -> true);

        // Bind the filtered list to the TableView
        paiementTable.setItems(filteredActeList);

        // Add a listener to the search text field
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredActeList.setPredicate(acte -> {
                // If the search text is empty, show all items
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare the patient name with the search text
                String lowerCaseFilter = newValue.toLowerCase();
                return acte.getPatientId().toLowerCase().contains(lowerCaseFilter); // Update to match your getter for patient name
            });
        });

        loadPaiements();
        loadPaiementStatistics();
    }
    private void loadPaiementStatistics() {
        Connection connection = DatabaseConnection.connect();

        if (connection == null) {
            System.err.println("Failed to establish a database connection.");
            return;
        }

        try (Statement statement = connection.createStatement()) {
            // Query for total paiements
            String queryTotalPaiements = """
                SELECT COUNT(*) AS total
                FROM Paiements
            """;
            ResultSet rsTotalPaiements = statement.executeQuery(queryTotalPaiements);
            if (rsTotalPaiements.next()) {
                lblTotalPaiements.setText(String.valueOf(rsTotalPaiements.getInt("total")));
            }

            // Query for total amount paid
            String queryTotalPaid = """
                SELECT SUM(montant) AS totalPaid
                FROM Paiements
            """;
            ResultSet rsTotalPaid = statement.executeQuery(queryTotalPaid);
            if (rsTotalPaid.next()) {
                lblTotalPaid.setText(String.format("%.2f", rsTotalPaid.getDouble("totalPaid")));
            }

            // Query for total outstanding balance
            String queryOutstandingBalance = """
                SELECT SUM(reste) AS totalBalance
                FROM Paiements
            """;
            ResultSet rsOutstandingBalance = statement.executeQuery(queryOutstandingBalance);
            if (rsOutstandingBalance.next()) {
                lblOutstandingBalance.setText(String.format("%.2f", rsOutstandingBalance.getDouble("totalBalance")));
            }

        } catch (SQLException e) {
            System.err.println("Error while fetching paiement statistics: " + e.getMessage());
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
    private void loadPaiements() {
        String query = "SELECT * FROM Paiements";

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                // Add each row to the observable list
                paiementList.add(new Paiement(
                        resultSet.getString("id"),
                        resultSet.getString("acteID"),
                        resultSet.getString("patientId"),
                        resultSet.getString("datePaiement"),
                        resultSet.getString("montant"),
                        resultSet.getString("prix_comptabilise"),
                        resultSet.getString("reste"),
                        resultSet.getString("paiement_method"),
                        resultSet.getString("statut")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Bind the list to the TableView
        paiementTable.setItems(paiementList);
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

                // Add headers
                for (int i = 0; i < paiementTable.getColumns().size(); i++) {
                    headerRow.createCell(i).setCellValue(paiementTable.getColumns().get(i).getText());
                }

                // Add data rows
                ObservableList<Paiement> data = paiementTable.getItems();
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    Paiement paiement = data.get(i);

                    row.createCell(0).setCellValue(paiement.getId());
                    row.createCell(1).setCellValue(paiement.getActeId());
                    row.createCell(2).setCellValue(paiement.getPatientId());
                    row.createCell(3).setCellValue(paiement.getDatePaiement());
                    row.createCell(4).setCellValue(paiement.getMontant());
                    row.createCell(5).setCellValue(paiement.getPrixComptabilise());
                    row.createCell(6).setCellValue(paiement.getReste());
                    row.createCell(7).setCellValue(paiement.getPaiementMethod());
                    row.createCell(8).setCellValue(paiement.getStatut());
                }

                // Resize columns to fit content
                for (int i = 0; i < paiementTable.getColumns().size(); i++) {
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
    public void AjoutPaiementPage(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/AjoutPaiement.fxml");
    }
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
