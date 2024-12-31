package org.example.projetclinique;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RadioType {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty type;

    public RadioType(int id, String type) {
        this.id = new SimpleIntegerProperty(id);
        this.type = new SimpleStringProperty(type);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }
}

