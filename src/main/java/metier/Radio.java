package metier;

public class Radio {
    private int id;
    private String dateRadio;
    private String type;
    private int typeId;
    private String cheminImage; // Optional: can be null if not used
    private int acteId; // New field to link the radio with an Acte

    // Full constructor including ID and acteId
    public Radio(int id, String dateRadio, String type, String cheminImage, int acteId) {
        this.id = id;
        this.dateRadio = dateRadio;
        this.type = type;
        this.cheminImage = cheminImage;
        this.acteId = acteId;
    }
    public Radio(int id, String dateRadio, String type) {
        this.id = id;
        this.dateRadio = dateRadio;
        this.type = type;
    }
    public Radio(String dateRadio, int typeId, int acteId) {
        this.dateRadio = dateRadio;
        this.typeId = typeId;
        this.cheminImage = cheminImage;
        this.acteId = acteId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getDateRadio() {
        return dateRadio;
    }

    public void setDateRadio(String dateRadio) {
        this.dateRadio = dateRadio;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheminImage() {
        return cheminImage;
    }

    public void setCheminImage(String cheminImage) {
        this.cheminImage = cheminImage;
    }

    public int getActeId() {
        return acteId;
    }

    public void setActeId(int acteId) {
        this.acteId = acteId;
    }

    @Override
    public String toString() {
        return "Radio{" +
                "id=" + id +
                ", dateRadio='" + dateRadio + '\'' +
                ", type='" + type + '\'' +
                ", cheminImage='" + cheminImage + '\'' +
                ", acteId=" + acteId +
                '}';
    }
}
