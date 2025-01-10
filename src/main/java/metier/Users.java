package metier;

public class Users {
    private int id; // Unique identifier for each user
    private String nom;
    private String prenom;
    private String tele;
    private String cin;
    private String username;
    private String password;
    private String typeUser; // Field for user type

    // Constructor for initialization with all properties
    public Users(int id, String nom, String prenom, String tele, String cin, String username, String password, String typeUser) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.tele = tele;
        this.cin = cin;
        this.username = username;
        this.password = password;
        this.typeUser = typeUser;
    }

    // Constructor for initialization without id (for new users)
    public Users(String nom, String prenom, String tele, String cin, String username, String password, String typeUser) {
        this.nom = nom;
        this.prenom = prenom;
        this.tele = tele;
        this.cin = cin;
        this.username = username;
        this.password = password;
        this.typeUser = typeUser;
    }

    // Getter and setter for ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and setter for nom
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    // Getter and setter for prenom
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    // Getter and setter for tele
    public String getTele() {
        return tele;
    }

    public void setTele(String tele) {
        this.tele = tele;
    }

    // Getter and setter for cin
    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    // Getter and setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and setter for typeUser (User type: Secretaire, Docteur, etc.)
    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }
}
