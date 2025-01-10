package DAO;

import metier.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {

    // Method to retrieve all users from the database
    public List<Users> getAllUsers() {
        List<Users> usersList = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (ResultSet rs = DatabaseConnection.executeQuery(query)) {
            while (rs.next()) {
                Users user = new Users(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("tele"),
                        rs.getString("CIN"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("TypeUser")
                );
                usersList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    // Method to add a new user to the database
    public boolean addUser(Users user) {
        String query = "INSERT INTO Users (nom, prenom, tele, CIN, username, password, TypeUser) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getNom());
            preparedStatement.setString(2, user.getPrenom());
            preparedStatement.setString(3, user.getTele());
            preparedStatement.setString(4, user.getCin());
            preparedStatement.setString(5, user.getUsername());
            preparedStatement.setString(6, user.getPassword());
            preparedStatement.setString(7, user.getTypeUser());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to delete a user from the database
    public boolean deleteUser(int id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
