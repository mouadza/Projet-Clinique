package Controllers;

import DAO.CategoryDAO;
import DAO.MainApplication;
import DAO.RadioTypeDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import metier.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import metier.RadioType;

import java.sql.SQLException;
import java.util.List;

public class ConfigController {

    @FXML
    private TableView<RadioType> tableTypeRadio;

    @FXML
    private TableColumn<RadioType, Integer> colId;

    @FXML
    private TableColumn<RadioType, String> colTypeRadio;

    @FXML
    private TableColumn<RadioType, Integer> colPrix;

    @FXML
    private TextField tfTypeRadio;

    @FXML
    private TextField tfPrix;
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
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ObservableList<RadioType> radioTypes = FXCollections.observableArrayList();
    private final RadioTypeDAO radioTypeDAO = new RadioTypeDAO();

    @FXML
    private void initialize() {
        // Configure TableView columns with proper property bindings
        colCatInt.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        colPrixCat.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        // Add a listener to update text fields when an item is selected
        tableCatInt.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tfCatInt.setText(newValue.getCategory());
                tfPrixCat.setText(String.valueOf(newValue.getPrice()));
            }
        });

        // Use SimpleObjectProperty for ID and Prix columns (assuming they are generic objects)
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        colTypeRadio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        colPrix.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPrix()));

        // Add a listener to update radio type information when a radio type is selected
        tableTypeRadio.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> displayRadioTypeInfo(newValue)
        );

        // Load data for radio types
        loadRadioTypes();
        loadCategories();
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
    private void loadCategories() {
        categories.clear();
        try {
            categories.addAll(CategoryDAO.getAllCategories());
        } catch (SQLException e) {
            showErrorAlert("Error loading categories from database.");
        }
        tableCatInt.setItems(categories);
    }

    // Adds a new category
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
            Category newCategory = new Category(category, price);
            categoryDAO.addCategory(newCategory);
            loadCategories();
            clearInputs();
        } catch (NumberFormatException e) {
            showErrorAlert("Price must be a valid number.");
        } catch (SQLException e) {
            showErrorAlert("Error adding category to database.");
        }
    }
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
            selected.setCategory(category);
            selected.setPrice(price);
            categoryDAO.updateCategory(selected);
            loadCategories();
            clearInputs();
        } catch (NumberFormatException e) {
            showErrorAlert("Price must be a valid number.");
        } catch (SQLException e) {
            showErrorAlert("Error updating category in database.");
        }
    }


    private void loadRadioTypes() {
        radioTypes.clear();
        List<RadioType> fetchedRadioTypes = radioTypeDAO.getAllRadioTypes();
        if (fetchedRadioTypes.isEmpty()) {
            System.out.println("No radio types found in the database.");
        } else {
            System.out.println("Loaded radio types from database.");
        }
        radioTypes.addAll(fetchedRadioTypes);
        tableTypeRadio.setItems(radioTypes);
    }


    @FXML
    private void addRadioType() {
        String type = tfTypeRadio.getText();
        String prixText = tfPrix.getText();

        if (type.isEmpty() || prixText.isEmpty()) {
            showErrorAlert("Type and price must not be empty.");
            return;
        }

        try {
            int prix = Integer.parseInt(prixText);
            RadioType newRadioType = new RadioType(type, prix);
            radioTypeDAO.addRadioType(newRadioType);
            loadRadioTypes();
            tfTypeRadio.clear();
            tfPrix.clear();
        } catch (NumberFormatException e) {
            showErrorAlert("Price must be a valid number.");
        } catch (SQLException e) {
            showErrorAlert("Error adding radio type to database.");
        }
    }

    @FXML
    private void updateRadioType() {
        RadioType selected = tableTypeRadio.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("No radio type selected.");
            return;
        }

        String type = tfTypeRadio.getText();
        String prixText = tfPrix.getText();

        if (type.isEmpty() || prixText.isEmpty()) {
            showErrorAlert("Type and price must not be empty.");
            return;
        }

        try {
            int prix = Integer.parseInt(prixText);
            selected.setType(type);
            selected.setPrix(prix);
            radioTypeDAO.updateRadioType(selected);
            loadRadioTypes();
            tfTypeRadio.clear();
            tfPrix.clear();
        } catch (NumberFormatException e) {
            showErrorAlert("Price must be a valid number.");
        } catch (SQLException e) {
            showErrorAlert("Error updating radio type in database.");
        }
    }

    @FXML
    private void deleteRadioType() {
        RadioType selected = tableTypeRadio.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("No radio type selected.");
            return;
        }

        try {
            radioTypeDAO.deleteRadioType(selected.getId());
            loadRadioTypes();
            clearInputs();
        } catch (SQLException e) {
            showErrorAlert("Error deleting radio type from database.");
        }
    }


    // Deletes a selected category
    @FXML
    private void deleteCategory() {
        Category selected = tableCatInt.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("No category selected.");
            return;
        }

        try {
            categoryDAO.deleteCategory(selected.getId());
            loadCategories();
            clearInputs();
        } catch (SQLException e) {
            showErrorAlert("Error deleting category from database.");
        }
    }

    // Clears the input fields
    private void clearInputs() {
        tfCatInt.clear();
        tfPrixCat.clear();
    }

    // Displays an error alert
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods (unchanged)
    public void onBtnAccueilClick(ActionEvent actionEvent) {
        MainApplication.loadPage("dashboard.fxml");
    }

    public void onBtnPatientsClick(ActionEvent actionEvent) {
        MainApplication.loadPage("patient.fxml");
    }

    public void onBtnActeMedicClick(ActionEvent actionEvent) {
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

    public void HomePage(ActionEvent actionEvent) {
        MainApplication.loadPage("SecretairePages/Home.fxml");
    }

    public void PatientPage(ActionEvent actionEvent) {
        MainApplication.loadPage("SecretairePages/patients.fxml");
    }

    public void RendezVousPage(ActionEvent actionEvent) {
        MainApplication.loadPage("SecretairePages/rendezvous.fxml");
    }

    public void ConfigPage(ActionEvent actionEvent) {
        MainApplication.loadPage("SecretairePages/Configuration.fxml");
    }
}
