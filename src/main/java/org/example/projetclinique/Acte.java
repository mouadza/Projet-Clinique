package org.example.projetclinique;

public class Acte {
    private Integer ID;
    private String dateDebut;
    private Double prixComptabilise;
    private String etatDeLActe;
    private String dateFin;
    private int patientID; // Not displayed in the table
    private String patientName; // Displayed in the table

    public Acte(int id, String dateDebut, double prixComptabilise, String etatDeLActe, String dateFin, int patientID, String patientName) {
        this.ID = id;
        this.dateDebut = dateDebut;
        this.prixComptabilise = prixComptabilise;
        this.etatDeLActe = etatDeLActe;
        this.dateFin = dateFin;
        this.patientID = patientID;
        this.patientName = patientName; // Displayed in the table
    }

    public int getPatientID() {
        return patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    // Getters and setters...
    public Integer getID() {
        return ID;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public Double getPrixComptabilise() {
        return prixComptabilise;
    }

    public String getEtatDeLActe() {
        return etatDeLActe;
    }

    public String getDateFin() {
        return dateFin;
    }
    public void setEtatDeLActe(String etatDeLActe) {
        this.etatDeLActe = etatDeLActe;
    }

    public String getDateDeFin() {
        return dateFin;
    }

    public void setDateDeFin(String dateDeFin) {
        this.dateFin = dateDeFin;
    }
}
