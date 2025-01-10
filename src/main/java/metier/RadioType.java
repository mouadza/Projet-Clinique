package metier;

public class RadioType {
    private int id;
    private String type;
    private int prix;

    // Constructors
    public RadioType(int id, String type, int prix) {
        this.id = id;
        this.type = type;
        this.prix = prix;
    }

    public RadioType(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public RadioType(String type, int prix) {
        this.type = type;
        this.prix = prix;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }
    @Override
    public String toString() {
        return type; // Use 'type' for ComboBox display
    }
}
