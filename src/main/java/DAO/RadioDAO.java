package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import metier.Radio;

import java.io.FileInputStream;
import java.sql.*;

public class RadioDAO {
    private static final Connection connection = DatabaseConnection.connect();
    public ObservableList<Radio> getRadiosByActeId(int acteId) {
        ObservableList<Radio> radios = FXCollections.observableArrayList();
        String query = """
        SELECT r.ID, r.date_radio, t.type AS type_name
        FROM Radios r
        JOIN type_radios t ON r.type_id = t.id
        WHERE r.acteID = ?
    """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, acteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    radios.add(new Radio(
                            rs.getInt("ID"),
                            rs.getString("date_radio"),
                            rs.getString("type_name")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return radios;
    }
    public void addRadio(Radio radio, FileInputStream imageStream, long imageSize) throws SQLException {
        String query = "INSERT INTO Radios (date_radio, type_id, acteID, image) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, radio.getDateRadio());
            stmt.setInt(2, radio.getTypeId());
            stmt.setInt(3, radio.getActeId());
            stmt.setBinaryStream(4, imageStream, imageSize);
            stmt.executeUpdate();
        }
    }


    public void deleteRadio(int radioId) throws SQLException {
        String query = "DELETE FROM Radios WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, radioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
