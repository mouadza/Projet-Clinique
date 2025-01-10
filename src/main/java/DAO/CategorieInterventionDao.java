package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieInterventionDao {

    private static Connection conn = DatabaseConnection.connect();

    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String query = "SELECT categorie FROM categorie_intervention";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            categories.add(rs.getString("categorie"));
        }
        return categories;
    }

    public int getCategoryIdByName(String categorie) throws SQLException {
        String query = "SELECT ID FROM categorie_intervention WHERE categorie = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, categorie);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("ID");
        }
        throw new SQLException("Catégorie introuvable.");
    }
}
