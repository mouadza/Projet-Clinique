package org.example.projetclinique;

import javafx.beans.property.*;

public class Category {
    private final IntegerProperty id;
    private final StringProperty category;
    private final DoubleProperty price;

    // Constructor
    public Category(int id, String category, double price) {
        this.id = new SimpleIntegerProperty(id);
        this.category = new SimpleStringProperty(category);
        this.price = new SimpleDoubleProperty(price);
    }

    // Getter for ID
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    // Getter for Category
    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    // Getter for Price
    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    // Setter for ID
    public void setId(int id) {
        this.id.set(id);
    }

    // Setter for Category
    public void setCategory(String category) {
        this.category.set(category);
    }

    // Setter for Price
    public void setPrice(double price) {
        this.price.set(price);
    }
}
