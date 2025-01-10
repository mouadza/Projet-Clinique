package metier;

public class Category {
    private int id;
    private String category;
    private double price;
    public Category(int id, String category, double price) {
        this.id = id;
        this.category = category;
        this.price = price;
    }
    public Category(String category, double price) {
        this.category = category;
        this.price = price;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // Getter for Category
    public String getCategory() {
        return category;
    }

    // Setter for Category
    public void setCategory(String category) {
        this.category = category;
    }

    // Getter for Price
    public double getPrice() {
        return price;
    }

    // Setter for Price
    public void setPrice(double price) {
        this.price = price;
    }
}
