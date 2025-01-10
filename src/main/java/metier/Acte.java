package metier;

public class Acte {
    private Integer id;
    private String dateDebut;
    private double prixComptabilise;
    private String etatDeLActe;
    private String dateFin;
    private int patientID;
    private String patientName;

    public Acte(Integer id, String dateDebut, double prixComptabilise, String etatDeLActe, String dateFin, int patientID, String patientName) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.prixComptabilise = prixComptabilise;
        this.etatDeLActe = etatDeLActe;
        this.dateFin = dateFin;
        this.patientID = patientID;
        this.patientName = patientName;
    }

    public Acte(String dateDebut, double prixComptabilise, String etatDeLActe, String dateFin, int patientID) {
        this.dateDebut = dateDebut;
        this.prixComptabilise = prixComptabilise;
        this.etatDeLActe = etatDeLActe;
        this.dateFin = dateFin;
        this.patientID = patientID;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDateDebut() { return dateDebut; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }

    public double getPrixComptabilise() { return prixComptabilise; }
    public void setPrixComptabilise(double prixComptabilise) { this.prixComptabilise = prixComptabilise; }

    public String getEtatDeLActe() { return etatDeLActe; }
    public void setEtatDeLActe(String etatDeLActe) { this.etatDeLActe = etatDeLActe; }

    public String getDateFin() { return dateFin; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }

    public int getPatientID() { return patientID; }
    public void setPatientID(int patientID) { this.patientID = patientID; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
}
