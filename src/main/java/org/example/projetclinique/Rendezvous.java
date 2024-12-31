package org.example.projetclinique;

import javafx.beans.property.SimpleStringProperty;

public class RendezVous {
    private final SimpleStringProperty nomPatient;
    private final SimpleStringProperty heure;
    private final SimpleStringProperty service;

    public RendezVous(String nomPatient, String heure, String service) {
        this.nomPatient = new SimpleStringProperty(nomPatient);
        this.heure = new SimpleStringProperty(heure);
        this.service = new SimpleStringProperty(service);
    }

    public String getNomPatient() {
        return nomPatient.get();
    }

    public String getHeure() {
        return heure.get();
    }

    public String getService() {
        return service.get();
    }
}
