package org.example.projetclinique;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;

public class Rendezvous {
    private final IntegerProperty id;
    private final StringProperty patient;
    private final StringProperty datePrev; // Changed to ObjectProperty<LocalDate>
    private final StringProperty category;
    private final IntegerProperty medicalAct;
    private final StringProperty status;
    private final StringProperty realDate; // Changed to ObjectProperty<LocalDate>

    public Rendezvous(int id, String patient, String datePrev, String category, int medicalAct, String status, String realDate) {
        this.id = new SimpleIntegerProperty(id);
        this.patient = new SimpleStringProperty(patient);
        this.datePrev = new SimpleStringProperty(datePrev); // Initialize with LocalDate
        this.category = new SimpleStringProperty(category);
        this.medicalAct = new SimpleIntegerProperty(medicalAct);
        this.status = new SimpleStringProperty(status);
        this.realDate = new SimpleStringProperty(realDate); // Initialize with LocalDate
    }
    // Getters and properties for binding
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty patientProperty() {
        return patient;
    }

    public StringProperty datePrevProperty() {
        return datePrev;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public IntegerProperty medicalActProperty() {
        return medicalAct;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty realDateProperty() {
        return realDate;
    }

    // Getters and Setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getPatient() {
        return patient.get();
    }

    public void setPatient(String patient) {
        this.patient.set(patient);
    }

    public String getDatePrev() {
        return datePrev.get();
    }

    public void setDatePrev(String datePrev) {
        this.datePrev.set(datePrev);
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public int getMedicalAct() {
        return medicalAct.get();
    }

    public void setMedicalAct(int medicalAct) {
        this.medicalAct.set(medicalAct);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getRealDate() {
        return realDate.get();
    }

    public void setRealDate(String realDate) {
        this.realDate.set(realDate);
    }
}
