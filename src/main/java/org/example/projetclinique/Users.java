package org.example.projetclinique;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

public class Users {
    private final IntegerProperty id; // Unique identifier for each user
    private final StringProperty nom;
    private final StringProperty prenom;
    private final StringProperty tele;
    private final StringProperty cin;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty newField; // Replace "newField" with your actual field name

    // Constructor
    public Users(int id, String nom, String prenom, String tele, String cin, String username, String password, String newField) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.tele = new SimpleStringProperty(tele);
        this.cin = new SimpleStringProperty(cin);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.newField = new SimpleStringProperty(newField);
    }
    public Users(String nom, String prenom, String tele, String cin, String username, String password, String newField) {
        this.id = new SimpleIntegerProperty();
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.tele = new SimpleStringProperty(tele);
        this.cin = new SimpleStringProperty(cin);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.newField = new SimpleStringProperty(newField);
    }

    // Getter and setter for ID
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    // Getter and setter for nom
    public String getNom() {
        return nom.get();
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    // Getter and setter for prenom
    public String getPrenom() {
        return prenom.get();
    }

    public StringProperty prenomProperty() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom.set(prenom);
    }

    // Getter and setter for tele
    public String getTele() {
        return tele.get();
    }

    public StringProperty teleProperty() {
        return tele;
    }

    public void setTele(String tele) {
        this.tele.set(tele);
    }

    // Getter and setter for cin
    public String getCin() {
        return cin.get();
    }

    public StringProperty cinProperty() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin.set(cin);
    }

    // Getter and setter for username
    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    // Getter and setter for password
    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    // Getter and setter for newField
    public String getNewField() {
        return newField.get();
    }

    public StringProperty newFieldProperty() {
        return newField;
    }

    public void setNewField(String newField) {
        this.newField.set(newField);
    }
}
