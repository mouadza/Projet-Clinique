package org.example.projetclinique;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Radio {
    private final IntegerProperty id;
    private final StringProperty dateRadio;
    private final StringProperty type;
    private final StringProperty cheminImage;

    // Constructor
    public Radio(int id, String dateRadio, String type) {
        this.id = new SimpleIntegerProperty(id);
        this.dateRadio = new SimpleStringProperty(dateRadio);
        this.type = new SimpleStringProperty(type);
        this.cheminImage = new SimpleStringProperty();
    }

    // Getters and Setters for the properties
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getDateRadio() {
        return dateRadio.get();
    }

    public void setDateRadio(String dateRadio) {
        this.dateRadio.set(dateRadio);
    }

    public StringProperty dateRadioProperty() {
        return dateRadio;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public StringProperty typeProperty() {
        return type;
    }

    public String getCheminImage() {
        return cheminImage.get();
    }

    public void setCheminImage(String cheminImage) {
        this.cheminImage.set(cheminImage);
    }

    public StringProperty cheminImageProperty() {
        return cheminImage;
    }
}
