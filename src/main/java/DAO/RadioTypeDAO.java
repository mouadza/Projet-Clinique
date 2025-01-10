package DAO;

import metier.RadioType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RadioTypeDAO {
    private static Connection conn = DatabaseConnection.connect();

    public List<RadioType> getAllRadioTypes() {
        String query = "SELECT id, type, prixRadio FROM type_radios";
        List<RadioType> radioTypes = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("type");
                int Prix = rs.getInt("prixRadio");

                // Create and add RadioType object to the list
                radioTypes.add(new RadioType(id, name, Prix));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return radioTypes;
    }


    // Add a new radio type
    public void addRadioType(RadioType radioType) throws SQLException {
        String query = "INSERT INTO type_radios (type, prixRadio) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, radioType.getType());
            pstmt.setInt(2, radioType.getPrix());
            pstmt.executeUpdate();
        }
    }

    // Update an existing radio type
    public void updateRadioType(RadioType radioType) throws SQLException {
        String query = "UPDATE type_radios SET type = ?, prixRadio = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, radioType.getType());
            pstmt.setInt(2, radioType.getPrix());
            pstmt.setInt(3, radioType.getId());
            pstmt.executeUpdate();
        }
    }

    // Delete a radio type by ID
    public void deleteRadioType(int id) throws SQLException {
        String query = "DELETE FROM type_radios WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
