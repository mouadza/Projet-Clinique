package Controllers;

import DAO.UsersDAO;
import DAO.MainApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import metier.Users;

import java.util.List;

public class UtilisateursController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField teleField;
    @FXML private TextField cinField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> typeUserComboBox;
    @FXML private TableView<Users> secretaireTable;
    @FXML private TableColumn<Users, String> nomColumn;
    @FXML private TableColumn<Users, String> prenomColumn;
    @FXML private TableColumn<Users, String> teleColumn;
    @FXML private TableColumn<Users, String> cinColumn;
    @FXML private TableColumn<Users, String> usernameColumn;
    @FXML private TableColumn<Users, String> typeUserColumn;

    private ObservableList<Users> secretaireList = FXCollections.observableArrayList();
    private UsersDAO usersDAO = new UsersDAO();  // DAO instance

    @FXML
    public void initialize() {
        // Bind table columns to Users properties
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        prenomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        teleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTele()));
        cinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCin()));
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        typeUserColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTypeUser()));


        secretaireTable.setItems(secretaireList);

        // Initialize ComboBox options
        typeUserComboBox.setItems(FXCollections.observableArrayList("Docteur", "Secretaire"));

        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        List<Users> usersList = usersDAO.getAllUsers(); // Use DAO to load users
        secretaireList.setAll(usersList);
    }

    @FXML
    public void addSecretaire() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String tele = teleField.getText();
        String cin = cinField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String typeUser = typeUserComboBox.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || tele.isEmpty() || cin.isEmpty() || username.isEmpty() || password.isEmpty() || typeUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        Users newSecretaire = new Users(nom, prenom, tele, cin, username, password, typeUser);
        boolean success = usersDAO.addUser(newSecretaire);  // Use DAO to add user

        if (success) {
            secretaireList.add(newSecretaire);
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le secrétaire a été ajouté avec succès.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de l'ajout.");
        }
    }

    @FXML
    private void deleteSecretaire() {
        Users selectedSecretaire = secretaireTable.getSelectionModel().getSelectedItem();

        if (selectedSecretaire != null) {
            int id = selectedSecretaire.getId();
            boolean success = usersDAO.deleteUser(id);  // Use DAO to delete user

            if (success) {
                secretaireList.remove(selectedSecretaire);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le secrétaire a été supprimé avec succès.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de la suppression.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un utilisateur à supprimer.");
        }
    }

    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        teleField.clear();
        cinField.clear();
        usernameField.clear();
        passwordField.clear();
        typeUserComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods
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
}
