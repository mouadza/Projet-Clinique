package org.example.projetclinique;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class RendezvousController {
    @FXML
    private VBox popupReporter;

    @FXML
    private Label selectedRendezVousLabel;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TableView<Rendezvous> interventionsTable;

    @FXML
    private TableColumn<Rendezvous, Number> idColumn;
    @FXML
    private TableColumn<Rendezvous, String> patientColumn;
    @FXML
    private TableColumn<Rendezvous, String> datePrevColumn;
    @FXML
    private TableColumn<Rendezvous, String> categoryColumn;
    @FXML
    private TableColumn<Rendezvous, Number> medicalActColumn;
    @FXML
    private TableColumn<Rendezvous, String> statusColumn;
    @FXML
    private TableColumn<Rendezvous, String> realDateColumn;

    private ObservableList<Rendezvous> rendezvousList = FXCollections.observableArrayList();
    @FXML
    private Label upcomingAppointmentsLabel;

    @FXML
    private Label totalInterventionsLabel;
    @FXML
    private Label  pastInterventionsLabel;
    // Assuming you have a method to connect to the database
    private Connection conn = DatabaseConnection.connect();

    public void initialize() {
        // Set up table columns
        // Set up the table columns to show properties of the Rendezvous class
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));
        datePrevColumn.setCellValueFactory(new PropertyValueFactory<>("datePrev"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        medicalActColumn.setCellValueFactory(new PropertyValueFactory<>("medicalAct"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        realDateColumn.setCellValueFactory(new PropertyValueFactory<>("realDate"));

        loadData();
        loadAppointmentCounts();
    }

    // Method to load data into the table
    private void loadData() {
        ObservableList<Rendezvous> rendezvousList = FXCollections.observableArrayList();
        try {
            String query = "SELECT R.ID, DATE(R.date_prevue) AS date_pre, CI.categorie AS category, R.numero_acte, R.etat, DATE(R.date_reelle) AS date_reele, CONCAT(P.prenom,' ',P.nom) AS full_name "
                    + "FROM Rendezvous R "
                    + "JOIN Patient P ON R.patient_id = P.id "
                    + "JOIN categorie_intervention CI ON R.categorie_id = CI.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println("Data found: " + rs.getString("full_name"));  // Debugging line

                int id = rs.getInt("ID");
                String patientName = rs.getString("full_name");
                String datePrevue = rs.getString("date_pre");
                String category = rs.getString("category");
                int medicalAct = rs.getInt("numero_acte");
                String status = rs.getString("etat");
                String realDate = rs.getString("date_reele");
                Rendezvous rendezvous = new Rendezvous(id, patientName, datePrevue, category, medicalAct, status, realDate);
                rendezvousList.add(rendezvous);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            interventionsTable.setItems(rendezvousList);
            interventionsTable.refresh(); // Ensure UI reflects data changes
        });
    }
    @FXML
    private void onReporterClick() {
        Rendezvous selected = interventionsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedRendezVousLabel.setText("Rendez-vous ID: " + selected.getId());
            popupReporter.setVisible(true);
        } else {
            // Display an error message (optional)
            showErrorAlert("Veuillez sélectionner un rendez-vous.");
        }
    }
    @FXML
    private void onConfirmReporterClick() {
        // Get the selected Rendezvous from the table
        Rendezvous selected = interventionsTable.getSelectionModel().getSelectedItem();

        // Validate selection and date
        if (selected != null && datePicker.getValue() != null) {
            String sql = "UPDATE RendezVous SET date_prevue = ? WHERE ID = ?";

            try (Connection connection = DatabaseConnection.connect();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                // Convert LocalDate to java.sql.Timestamp
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(datePicker.getValue().atStartOfDay());

                // Set parameters for the prepared statement
                statement.setTimestamp(1, timestamp); // Use Timestamp for DATETIME field
                statement.setInt(2, selected.getId());

                // Execute the update query
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Date updated successfully for ID: " + selected.getId());
                }

                loadData();  // Reload the data to reflect the changes in the table
                popupReporter.setVisible(false); // Close the popup
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error updating date in the database.");
            }

    } else {
            showErrorAlert("Veuillez sélectionner une rendez-vous.");
        }
    }
    @FXML
    private void onCancelReporterClick() {
        popupReporter.setVisible(false);
    }
    @FXML
    private void onAnnulerClick() {
        Rendezvous selected = interventionsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Confirm deletion with a dialog (optional)
            boolean confirm = showConfirmationDialog();
            if (confirm) {
                deleteRendezvousFromDatabase(selected);
                // Remove from the table view
                interventionsTable.getItems().remove(selected);
                System.out.println("Rendez-vous annulé avec succès.");
            }
        } else {
            showErrorAlert("Veuillez sélectionner un rendez-vous.");
        }
    }

    private boolean showConfirmationDialog() {
        // You can implement a confirmation dialog here (e.g., using Alert)
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Êtes-vous sûr de vouloir annuler ce rendez-vous?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void deleteRendezvousFromDatabase(Rendezvous rendezvous) {
        String sql = "DELETE FROM RendezVous WHERE id = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, rendezvous.getId());
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Rendez-vous supprimé avec succès.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la suppression du rendez-vous.");
        }
    }
    private void loadAppointmentCounts() {
        String totalQuery = "SELECT COUNT(*) AS total_rendezvous FROM RendezVous";
        String planifieQuery = "SELECT COUNT(*) AS planifie_rendezvous FROM RendezVous WHERE etat = 'Planifié'";
        String TermineQuery = "SELECT COUNT(*) AS termine_rendezvous FROM RendezVous WHERE etat = 'Terminé'";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement totalStmt = conn.prepareStatement(totalQuery);
             PreparedStatement planifieStmt = conn.prepareStatement(planifieQuery);
             PreparedStatement TermineStmt = conn.prepareStatement(TermineQuery)) {

            // Query for total appointments
            ResultSet totalResult = totalStmt.executeQuery();
            if (totalResult.next()) {
                int totalRendezvous = totalResult.getInt("total_rendezvous");
                totalInterventionsLabel.setText(String.valueOf(totalRendezvous));
            }

            // Query for "Planifié" appointments
            ResultSet planifieResult = planifieStmt.executeQuery();
            if (planifieResult.next()) {
                int planifieRendezvous = planifieResult.getInt("planifie_rendezvous");
                upcomingAppointmentsLabel.setText(String.valueOf(planifieRendezvous));
            }
            ResultSet TermineResult = TermineStmt.executeQuery();
            if (TermineResult.next()) {
                int TermineRendezvous = TermineResult.getInt("termine_rendezvous");
                pastInterventionsLabel.setText(String.valueOf(TermineRendezvous));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur lors du chargement des données des rendez-vous.");
        }
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
                ObservableList<Rendezvous> data = interventionsTable.getItems();
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    Rendezvous rendezvous = data.get(i);

                    row.createCell(0).setCellValue(rendezvous.getId());
                    row.createCell(1).setCellValue(rendezvous.getPatient());
                    row.createCell(2).setCellValue(rendezvous.getDatePrev());
                    row.createCell(3).setCellValue(rendezvous.getCategory());
                    row.createCell(4).setCellValue(rendezvous.getMedicalAct());
                    row.createCell(5).setCellValue(rendezvous.getStatus());
                    row.createCell(6).setCellValue(rendezvous.getRealDate());
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

    public void onAddRendezVousClick(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/AjoutRendezVous.fxml");
    }
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
