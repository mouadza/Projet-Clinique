package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import metier.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class CategoryDAO {
    private static Connection conn = DatabaseConnection.connect();

    public void addCategory(Category category) throws SQLException {
        String query = "INSERT INTO categorie_intervention (categorie, prix) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, category.getCategory());
            stmt.setDouble(2, category.getPrice());
            stmt.executeUpdate();
        }
    }
    public static List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categorie_intervention";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("id"),
                        rs.getString("categorie"),
                        rs.getDouble("prix")
                ));
            }
        }
        return categories;
    }

    public static ObservableList<String> getCategories() throws SQLException {
        ObservableList<String> categoriesList = FXCollections.observableArrayList();

        String query = "SELECT categorie FROM categorie_intervention";
        try (Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                categoriesList.add(rs.getString("categorie"));
            }
        }

        return categoriesList;
    }
    public void updateCategory(Category category) throws SQLException {
        String query = "UPDATE categorie_intervention SET categorie = ?, prix = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, category.getCategory());
            stmt.setDouble(2, category.getPrice());
            stmt.setInt(3, category.getId());
            stmt.executeUpdate();
        }
    }

    public static int getCategorieIdByName(String categorie) throws SQLException {
        String query = "SELECT ID FROM categorie_intervention WHERE categorie = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, categorie);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return  rs.getInt("ID");
            } else {
                throw new SQLException("Catégorie introuvable.");
            }
        }
    }
    public void deleteCategory(int categoryId) throws SQLException {
        String query = "DELETE FROM categorie_intervention WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate();
        }
    }
}
