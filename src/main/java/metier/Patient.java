package metier;

public class Patient {
    private int ID;
    private String nom;
    private String prenom;
    private String dateNaissance;
    private String telephone;
    private String CIN;
    private String adresse;
    private String email;

    public Patient(int ID,String nom, String prenom, String dateNaissance, String telephone, String CIN, String adresse, String email) {
        this.ID = ID;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.CIN = CIN;
        this.adresse = adresse;
        this.email = email;
    }
    public Patient(String nom, String prenom, String dateNaissance, String telephone, String CIN, String adresse, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.CIN = CIN;
        this.adresse = adresse;
        this.email = email;  // Set email
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    // Getters and Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getCIN() { return CIN; }
    public void setCIN(String CIN) { this.CIN = CIN; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getEmail() { return email; }  // Add getter for email
    public void setEmail(String email) { this.email = email; }  // Add setter for email
}

