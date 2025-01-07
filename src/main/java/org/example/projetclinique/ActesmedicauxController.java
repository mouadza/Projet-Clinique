package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Cell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class ActesmedicauxController {
    public Button btnAddActe;
    public Button btnCloreActe;
    public Button btnGererInterventions;
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
    private TableColumn<Acte, Integer> colPatientID;
    @FXML
    private Label totalActes;
    @FXML
    private Label percentageCompletedActs;
    @FXML
    private Label percentageOngoingActs;
    @FXML
    private void onAddActeClick() {

        HelloApplication.loadPage("AjouterAct.fxml");
    }

    @FXML
    private void onGererRendezvousClick() {
        Acte selectedActe = tableActes.getSelectionModel().getSelectedItem();

        if (selectedActe == null) {
            showErrorAlert("Veuillez sélectionner un acte médical.");
            return;
        }

        int acteID = selectedActe.getID();
        int patientID = selectedActe.getPatientID();

        HelloApplication.oadPageWithController("gestionrendezvous.fxml", controller -> {
            if (controller instanceof GestionrendezvousController) {
                ((GestionrendezvousController) controller).setActeAndPatientDetails(acteID, patientID);
            }
        });
    }
    private ObservableList<Acte> actesList;
    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixComptabilise"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etatDeLActe"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));

        // For the Patient column, show the patientName instead of patientID
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colPatientID.setCellValueFactory(new PropertyValueFactory<>("patientID"));
        loadActesFromDatabase();
        fetchActePercentages();
    }
    private void loadActesFromDatabase() {
        actesList = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a.ID, a.date_debut, a.prix_comptabilise, a.etat_de_l_acte, a.date_de_fin, a.patient_concerne AS patientID, CONCAT(p.prenom, ' ', p.nom) AS patientName FROM ActeMedicaux a JOIN Patient p ON a.patient_concerne = p.id")) {

            while (rs.next()) {
                actesList.add(new Acte(
                        rs.getInt("ID"),
                        rs.getString("date_debut"),
                        rs.getDouble("prix_comptabilise"),
                        rs.getString("etat_de_l_acte"),
                        rs.getString("date_de_fin"),
                        rs.getInt("patientID"),
                        rs.getString("patientName")
                ));
            }
        } catch (SQLException e) {
            showErrorAlert("Erreur lors du chargement des actes médicaux.");
            e.printStackTrace();
        }

        tableActes.setItems(actesList);
    }
    @FXML
    private void onCloreActeClick() {
        // Get the selected acte from the TableView
        Acte selectedActe = tableActes.getSelectionModel().getSelectedItem();

        // Check if an acte is selected
        if (selectedActe != null) {
            // Get the current date and the date_debut of the selected acte
            String currentDate = java.time.LocalDate.now().toString(); // Get current date in "yyyy-MM-dd"
            String dateDebut = selectedActe.getDateDebut(); // Get the start date of the selected acte

            // Compare dates: ensure current date is after the date_debut
            if (currentDate.compareTo(dateDebut) >= 0) {
                // Set the 'etat' to "clôturé" and 'date_de_fin' to the current date
                selectedActe.setEtatDeLActe("Terminé");
                selectedActe.setDateDeFin(currentDate);

                // Update the acte in the database
                updateActeInDatabase(selectedActe);

                // Refresh the TableView to show updated data
                tableActes.refresh();

                System.out.println("Acte médical clôturé avec succès !");
            } else {
                // Show error if the current date is before the date_debut
                showErrorAlert("La date actuelle ne peut pas être avant la date de début de l'acte.");
            }
        } else {
            showErrorAlert("Veuillez sélectionner un acte médical à clore.");
        }
    }

    private void updateActeInDatabase(Acte acte) {
        String updateQuery = "UPDATE actemedicaux SET etat_de_l_acte = ?, date_de_fin = ? WHERE ID = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, acte.getEtatDeLActe());
            stmt.setString(2, acte.getDateDeFin());
            stmt.setInt(3, acte.getID());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Acte médical mis à jour dans la base de données !");
            } else {
                showErrorAlert("Erreur lors de la mise à jour de l'acte médical.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur lors de l'accès à la base de données.");
        }
    }
    private void fetchActePercentages() {
        // Queries
        String totalActesQuery = "SELECT COUNT(*) AS total_actes FROM ActeMedicaux";
        String completedActesQuery = "SELECT COUNT(*) AS completed_actes FROM ActeMedicaux WHERE etat_de_l_acte = 'Terminé'";
        String ongoingActesQuery = "SELECT COUNT(*) AS ongoing_actes FROM ActeMedicaux WHERE etat_de_l_acte = 'En cours'";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement totalActesStmt = conn.prepareStatement(totalActesQuery);
             PreparedStatement completedActesStmt = conn.prepareStatement(completedActesQuery);
             PreparedStatement ongoingActesStmt = conn.prepareStatement(ongoingActesQuery)) {

            // Fetch total actes
            ResultSet totalActesResult = totalActesStmt.executeQuery();
            int RtotalActes = 0;
            if (totalActesResult.next()) {
                RtotalActes = totalActesResult.getInt("total_actes");
                totalActes.setText(String.valueOf(RtotalActes));
            }

            // Fetch completed actes
            ResultSet completedActesResult = completedActesStmt.executeQuery();
            int completedActes = 0;
            if (completedActesResult.next()) {
                completedActes = completedActesResult.getInt("completed_actes");
            }

            // Fetch ongoing actes
            ResultSet ongoingActesResult = ongoingActesStmt.executeQuery();
            int ongoingActes = 0;
            if (ongoingActesResult.next()) {
                ongoingActes = ongoingActesResult.getInt("ongoing_actes");
            }

            // Calculate percentages
            String completedPercentage = RtotalActes > 0 ? String.format("%.2f%%", (completedActes * 100.0) / RtotalActes) : "0%";
            String ongoingPercentage = RtotalActes > 0 ? String.format("%.2f%%", (ongoingActes * 100.0) / RtotalActes) : "0%";


            percentageCompletedActs.setText(completedPercentage);
            percentageOngoingActs.setText(ongoingPercentage);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des données des actes.");
        }
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

                // Add headers
                for (int i = 0; i < tableActes.getColumns().size(); i++) {
                    TableColumn<?, ?> column = tableActes.getColumns().get(i);
                    if (column.isVisible()) { // Only add visible columns
                        headerRow.createCell(i).setCellValue(column.getText());
                    }
                }

                // Add data rows
                ObservableList<Acte> data = tableActes.getItems();
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    Acte acte = data.get(i);

                    row.createCell(0).setCellValue(acte.getID());
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
    }

    public void onLogoutButtonClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("hello-view.fxml");
    }

    public void onBtnConfigClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("config.fxml");
    }
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
