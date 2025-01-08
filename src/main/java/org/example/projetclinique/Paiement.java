package org.example.projetclinique;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Paiement {
    private final StringProperty id;
    private final StringProperty acteId;
    private final StringProperty patientId;
    private final StringProperty datePaiement;
    private final StringProperty montant;
    private final StringProperty prixComptabilise;
    private final StringProperty reste;
    private final StringProperty paiementMethod;
    private final StringProperty statut;

    public Paiement(String id, String acteId, String patientId, String datePaiement, String montant,
                    String prixComptabilise, String reste, String paiementMethod, String statut) {
        this.id = new SimpleStringProperty(id);
        this.acteId = new SimpleStringProperty(acteId);
        this.patientId = new SimpleStringProperty(patientId);
        this.datePaiement = new SimpleStringProperty(datePaiement);
        this.montant = new SimpleStringProperty(montant);
        this.prixComptabilise = new SimpleStringProperty(prixComptabilise);
        this.reste = new SimpleStringProperty(reste);
        this.paiementMethod = new SimpleStringProperty(paiementMethod);
        this.statut = new SimpleStringProperty(statut);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getActeId() {
        return acteId.get();
    }

    public StringProperty acteIdProperty() {
        return acteId;
    }

    public String getPatientId() {
        return patientId.get();
    }

    public StringProperty patientIdProperty() {
        return patientId;
    }

    public String getDatePaiement() {
        return datePaiement.get();
    }

    public StringProperty datePaiementProperty() {
        return datePaiement;
    }

    public String getMontant() {
        return montant.get();
    }

    public StringProperty montantProperty() {
        return montant;
    }

    public String getPrixComptabilise() {
        return prixComptabilise.get();
    }

    public StringProperty prixComptabiliseProperty() {
        return prixComptabilise;
    }

    public String getReste() {
        return reste.get();
    }

    public StringProperty resteProperty() {
        return reste;
    }

    public String getPaiementMethod() {
        return paiementMethod.get();
    }

    public StringProperty paiementMethodProperty() {
        return paiementMethod;
    }

    public String getStatut() {
        return statut.get();
    }

    public StringProperty statutProperty() {
        return statut;
    }
}
