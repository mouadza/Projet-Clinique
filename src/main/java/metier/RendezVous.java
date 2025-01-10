package metier;

public class RendezVous {
    private int id;
    private String patient;
    private String datePrev;
    private String category;
    private int medicalAct;
    private String etat;
    private String dateReelle;

    public RendezVous(int id, String patient, String datePrev, String category, int medicalAct, String etat, String dateReelle) {
        this.id = id;
        this.patient = patient;
        this.datePrev = datePrev;
        this.category = category;
        this.medicalAct = medicalAct;
        this.etat = etat;
        this.dateReelle = dateReelle;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getDatePrev() {
        return datePrev;
    }

    public void setDatePrev(String datePrev) {
        this.datePrev = datePrev;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMedicalAct() {
        return medicalAct;
    }

    public void setMedicalAct(int medicalAct) {
        this.medicalAct = medicalAct;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getDateReelle() {
        return dateReelle;
    }

    public void setDateReelle(String dateReelle) {
        this.dateReelle = dateReelle;
    }
}
