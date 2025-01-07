package org.example.projetclinique;

import java.util.Date;

public class Paiement {
    private int id;
    private int acteID;
    private String patient;
    private Date datePaiement;
    private double prix_comptabilise;
    private double montant;
    private double reste;
    private String paiementMethod;
    private String statut;

    // Constructor


    public Paiement(int id, int acteID, String patient, Date datePaiement, double prix_comptabilise, double montant, double reste, String paiementMethod, String statut) {
        this.id = id;
        this.acteID = acteID;
        this.patient = patient;
        this.datePaiement = datePaiement;
        this.prix_comptabilise = prix_comptabilise;
        this.montant = montant;
        this.reste = reste;
        this.paiementMethod = paiementMethod;
        this.statut = statut;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActeID() {
        return acteID;
    }

    public void setActeID(int acteID) {
        this.acteID = acteID;
    }



    public Date getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(Date datePaiement) {
        this.datePaiement = datePaiement;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }


}
