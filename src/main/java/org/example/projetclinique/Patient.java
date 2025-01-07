package org.example.projetclinique;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.time.LocalDate;
import java.time.Period;

public class Patient {
    private IntegerProperty ID;
    private String nom;
    private String prenom;
    private String dateNaissance;
    private String telephone;
    private String CIN;
    private String adresse;
    private int age;

    public Patient( String nom, String prenom, String dateNaissance, String telephone, String CIN, String adresse) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.CIN = CIN;
        this.adresse = adresse;
    }
    public Patient(int ID, String nom, String prenom, String dateNaissance, String telephone, String CIN, String adresse) {
        this.ID = new SimpleIntegerProperty(ID);
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.CIN = CIN;
        this.adresse = adresse;
    }

    // Getter and Property for ID
    public int getID() {
        return ID.get();
    }

    public void setID(int ID) {
        this.ID.set(ID);
    }

    public IntegerProperty IDProperty() {
        return ID;
    }

    // Getters and Setters for other fields
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCIN() {
        return CIN;
    }

    public void setCIN(String CIN) {
        this.CIN = CIN;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    private int calculateAge(LocalDate dateOfBirth, LocalDate currentDate) {
        return Period.between(dateOfBirth, currentDate).getYears();
    }
}
