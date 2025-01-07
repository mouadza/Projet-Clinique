package org.example.projetclinique;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Secretaire {
    private StringProperty nom;
    private StringProperty prenom;
    private StringProperty tele;
    private StringProperty cin;
    private StringProperty username;
    private StringProperty password;

    public Secretaire(String nom, String prenom, String tele, String cin, String username, String password) {
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.tele = new SimpleStringProperty(tele);
        this.cin = new SimpleStringProperty(cin);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public StringProperty prenomProperty() {
        return prenom;
    }

    public StringProperty teleProperty() {
        return tele;
    }

    public StringProperty cinProperty() {
        return cin;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    // Getters
    public String getNom() {
        return nom.get();
    }

    public String getPrenom() {
        return prenom.get();
    }

    public String getTele() {
        return tele.get();
    }

    public String getCin() {
        return cin.get();
    }

    public String getUsername() {
        return username.get();
    }

    public String getPassword() {
        return password.get();
    }
}
