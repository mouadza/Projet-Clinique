package org.example.projetclinique;

import javafx.beans.property.*;

public class Secretaire {
    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty prenom;
    private final StringProperty tele;
    private final StringProperty cin;
    private final StringProperty username;
    private final StringProperty password;

    public Secretaire(int id, String nom, String prenom, String tele, String cin, String username, String password) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.tele = new SimpleStringProperty(tele);
        this.cin = new SimpleStringProperty(cin);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getNom() { return nom.get(); }
    public StringProperty nomProperty() { return nom; }

    public String getPrenom() { return prenom.get(); }
    public StringProperty prenomProperty() { return prenom; }

    public String getTele() { return tele.get(); }
    public StringProperty teleProperty() { return tele; }

    public String getCin() { return cin.get(); }
    public StringProperty cinProperty() { return cin; }

    public String getUsername() { return username.get(); }
    public StringProperty usernameProperty() { return username; }

    public String getPassword() { return password.get(); }
    public StringProperty passwordProperty() { return password; }
}

