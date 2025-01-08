package org.example.projetclinique;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Paiement {
    private String id;
    private String acteId;
    private String patient_name; // This must match the TableColumn binding
    private String datePaiement;
    private String montant;
    private String prixComptabilise;
    private String reste;
    private String paiementMethod;
    private String statut;

    // Constructor
    public Paiement(String id, String acteId, String patient_name, String datePaiement, String montant,
                    String prixComptabilise, String reste, String paiementMethod, String statut) {
        this.id = id;
        this.acteId = acteId;
        this.patient_name = patient_name;
        this.datePaiement = datePaiement;
        this.montant = montant;
        this.prixComptabilise = prixComptabilise;
        this.reste = reste;
        this.paiementMethod = paiementMethod;
        this.statut = statut;
    }

    // Getters
    public String getId() { return id; }
    public String getActeId() { return acteId; }
    public String getPatient_name() { return patient_name; } // Getter matches "patient_name"
    public String getDatePaiement() { return datePaiement; }
    public String getMontant() { return montant; }
    public String getPrixComptabilise() { return prixComptabilise; }
    public String getReste() { return reste; }
    public String getPaiementMethod() { return paiementMethod; }
    public String getStatut() { return statut; }
}
