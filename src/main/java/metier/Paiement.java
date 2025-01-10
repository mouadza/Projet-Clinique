package metier;

public class Paiement {
    private int id;                // Unique identifier for the payment
    private int acteID;            // ID of the associated act
    private int patientID;         // ID of the associated patient
    private String patientName;    // Name of the patient
    private String datePaiement;   // Payment date
    private double montant;        // Payment amount
    private double prixComptabilise; // Total calculated price
    private double reste;          // Remaining amount to be paid
    private String paiementMethod; // Payment method
    private String statut;         // Payment status

    // Constructor matching the requirements
    public Paiement(int id, int acteID, String patientName, String datePaiement, double montant,
                    double prixComptabilise, String paiementMethod, double reste, String statut) {
        this.id = id;
        this.acteID = acteID;
        this.patientName = patientName;
        this.datePaiement = datePaiement;
        this.montant = montant;
        this.prixComptabilise = prixComptabilise;
        this.paiementMethod = paiementMethod;
        this.reste = reste;
        this.statut = statut;
    }

    public Paiement(int acteID, int patientID, String datePaiement, double montant, double prixComptabilise, String paiementMethod) {
        this.acteID = acteID;
        this.patientID = patientID;
        this.datePaiement = datePaiement;
        this.montant = montant;
        this.prixComptabilise = prixComptabilise;
        this.paiementMethod = paiementMethod;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getActeId() {
        return acteID;
    }

    public int getPatientID() {
        return patientID;
    }

    public String getPatient_name() {
        return patientName;
    }

    public String getDatePaiement() {
        return datePaiement;
    }

    public double getMontant() {
        return montant;
    }

    public double getPrixComptabilise() {
        return prixComptabilise;
    }

    public double getReste() {
        return reste;
    }

    public String getPaiementMethod() {
        return paiementMethod;
    }

    public String getStatut() {
        return statut;
    }

    // Setters (if needed)
    public void setId(int id) {
        this.id = id;
    }

    public void setActeID(int acteID) {
        this.acteID = acteID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDatePaiement(String datePaiement) {
        this.datePaiement = datePaiement;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public void setPrixComptabilise(double prixComptabilise) {
        this.prixComptabilise = prixComptabilise;
    }

    public void setReste(double reste) {
        this.reste = reste;
    }

    public void setPaiementMethod(String paiementMethod) {
        this.paiementMethod = paiementMethod;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
