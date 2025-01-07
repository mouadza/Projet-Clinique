package org.example.projetclinique;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigController {

    @FXML
    private TableView<Category> tableCatInt;

    @FXML
    private TableColumn<Category, String> colCatInt;

    @FXML
    private TableColumn<Category, Double> colPrixCat;

    @FXML
    private TextField tfCatInt;

    @FXML
    private TextField tfPrixCat;

    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    @FXML
    private TextField tfIdTypeRadio;
    @FXML
    private TableColumn<RadioType, String> colId;
    @FXML
    private TextField tfTypeRadio;
    @FXML
    private TableView<RadioType> tableTypeRadio;

    @FXML
    private TableColumn<RadioType, String> colTypeRadio;
    @FXML
    private TableColumn<RadioType, String> colPrix;
    @FXML
    private TextField tfPrix;


    // Initialize method is automatically called after the FXML file is loaded
    @FXML
    private void initialize() {
        // Configure TableView columns
        colCatInt.setCellValueFactory(data -> data.getValue().categoryProperty());
        colPrixCat.setCellValueFactory(data -> data.getValue().priceProperty().asObject());

        // Load categories into the table
        loadCategories();

        // Add listener to populate inputs when a row is selected
        tableCatInt.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tfCatInt.setText(newValue.getCategory());
                tfPrixCat.setText(String.valueOf(newValue.getPrice()));
            }
        });
        // Set up the TableView columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTypeRadio.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        loadRadioTypes();

        // Add listener to selection changes
        tableTypeRadio.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> displayRadioTypeInfo(newValue)
        );
    }

    // Loads all categories from the database into the TableView
    private void loadCategories() {
        categories.clear();
        try {
            categories.addAll(getAllCategories());
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error for debugging
        }
        tableCatInt.setItems(categories);
    }

    // Retrieves all categories from the database
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categorie_intervention";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("id"),
                        rs.getString("categorie"),
                        rs.getDouble("prix")
                ));
            }
        }
        return categories;
    }

    // Adds a new category to the database
    @FXML
    private void addCategory() {
        String category = tfCatInt.getText();
        String priceText = tfPrixCat.getText();

        // Input validation
        if (category.isEmpty() || priceText.isEmpty()) {
            showErrorAlert("Category and price must not be empty.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            String query = "INSERT INTO categorie_intervention (categorie, prix) VALUES (?, ?)";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, category);
                stmt.setDouble(2, price);
                stmt.executeUpdate();
            }
            loadCategories();
            clearInputs();
        } catch (NumberFormatException e) {
            showErrorAlert("Price must be a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Updates an existing category in the database
    @FXML
    private void updateCategory() {
        Category selected = tableCatInt.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("No category selected.");
            return;
        }

        String category = tfCatInt.getText();
        String priceText = tfPrixCat.getText();

        // Input validation
        if (category.isEmpty() || priceText.isEmpty()) {
            showErrorAlert("Category and price must not be empty.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            String query = "UPDATE categorie_intervention SET categorie = ?, prix = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, category);
                stmt.setDouble(2, price);
                stmt.setInt(3, selected.getId());
                stmt.executeUpdate();
            }
            loadCategories();
            clearInputs();
        } catch (NumberFormatException e) {
            showErrorAlert("Price must be a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Deletes a selected category from the database
    @FXML
    private void deleteCategory() {
        Category selected = tableCatInt.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("No Intrevention selected.");
            return;
        }

        try {
            String query = "DELETE FROM categorie_intervention WHERE id = ?";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
            }
            loadCategories();
            clearInputs();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void displayRadioTypeInfo(RadioType selectedRadioType) {
        if (selectedRadioType != null) {
            tfTypeRadio.setText(selectedRadioType.getType());
            tfPrix.setText(String.valueOf(selectedRadioType.getPrix()));
        } else {
            tfTypeRadio.clear();
            tfPrix.clear();
        }
    }

    private void loadRadioTypes() {
        ObservableList<RadioType> radioTypes = FXCollections.observableArrayList();
        String query = "SELECT id, type, prixRadio FROM type_radios";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                int prix = rs.getInt("prixRadio");
                radioTypes.add(new RadioType(id, type, prix));
            }

            tableTypeRadio.setItems(radioTypes);

        } catch (SQLException e) {
            System.out.println("Error loading radio types: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addRadioType(ActionEvent event) {
        String typeRadio = tfTypeRadio.getText();
        int prix = Integer.parseInt(tfPrix.getText());

        // Insert into the database (make sure your database table has 'prix' column)
        String query = "INSERT INTO type_radios (type, prixRadio) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, typeRadio);
            pstmt.setInt(2, prix);

            pstmt.executeUpdate();

            // Add the new row to the TableView
            tableTypeRadio.getItems().add(new RadioType(typeRadio, prix));

            // Clear the fields after adding
            tfTypeRadio.clear();
            tfPrix.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Impossible d'ajouter le type de radio.");
        }
    }

    @FXML
    private void updateRadioType(ActionEvent event) {
        RadioType selectedRadio = tableTypeRadio.getSelectionModel().getSelectedItem();
        if (selectedRadio != null) {
            String typeRadio = tfTypeRadio.getText();
            int prix = Integer.parseInt(tfPrix.getText());

            // Update the database
            String query = "UPDATE type_radios SET type = ?, prixRadio = ? WHERE id = ?";

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, typeRadio);
                pstmt.setInt(2, prix);
                pstmt.setInt(3, selectedRadio.getId());

                pstmt.executeUpdate();

                loadRadioTypes();
                selectedRadio.setType(typeRadio);
                selectedRadio.setPrix(prix);
                tfTypeRadio.clear();
                tfPrix.clear();


                tableTypeRadio.refresh();
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Impossible de modifier le type de radio.");
            }
        }
    }

    @FXML
    private void deleteRadioType(ActionEvent event) {
        RadioType selectedRadio = tableTypeRadio.getSelectionModel().getSelectedItem();
        if (selectedRadio != null) {
            // Delete from database
            String query = "DELETE FROM type_radios WHERE id = ?";

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, selectedRadio.getId());
                pstmt.executeUpdate();

                // Remove the item from the TableView
                tableTypeRadio.getItems().remove(selectedRadio);
                loadRadioTypes();
                tfTypeRadio.clear();
                tfPrix.clear();
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Impossible de supprimer le type de radio.");
            }
        }
    }
    private void clearInputs() {
        tfCatInt.clear();
        tfPrixCat.clear();
    }
    private void clearInputsRadio() {
        tfTypeRadio.clear();
    }
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Navigation methods
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
    public void HomePage(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/Home.fxml");
    }
    public void PatientPage(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/patients.fxml");
    }
    public void RendezVousPage(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/rendezvous.fxml");
    }
    public void ConfigPage(ActionEvent actionEvent) {
        HelloApplication.loadPage("SecretairePages/Configuration.fxml");
    }
}
