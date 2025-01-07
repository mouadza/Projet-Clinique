package org.example.projetclinique;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RadioType {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty type;
    private final SimpleIntegerProperty prix;

    public RadioType(int id, String type, int prix) {
        this.id = new SimpleIntegerProperty(id);
        this.type = new SimpleStringProperty(type);
        this.prix = new SimpleIntegerProperty(prix);
    }

    public RadioType(int id, String type) {
        this.id = new SimpleIntegerProperty(id);
        this.type = new SimpleStringProperty(type);
        this.prix = new SimpleIntegerProperty();
    }

    public RadioType(String type, int prix) {
        this.id = new SimpleIntegerProperty();
        this.type = new SimpleStringProperty(type);
        this.prix = new SimpleIntegerProperty(prix);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public int getPrix() {
        return prix.get();
    }

    public SimpleIntegerProperty prixProperty() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix.set(prix);
    }

    @Override
    public String toString() {
        return type.get(); // Use 'type' for ComboBox display
    }
    }
