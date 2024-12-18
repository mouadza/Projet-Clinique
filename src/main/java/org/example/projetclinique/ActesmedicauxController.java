package org.example.projetclinique;

import javafx.event.ActionEvent;

public class ActesmedicauxController {
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
}
