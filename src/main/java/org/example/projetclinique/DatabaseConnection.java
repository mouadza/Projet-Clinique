package org.example.projetclinique;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Update the driver class to the newer version
    private static final String URL = "jdbc:mysql://localhost:3306/ProjetClinique";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Use environment variables for production

    public static Connection connect() {
        try {
            // Load the MySQL JDBC driver (for newer versions)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Include the JDBC driver in your classpath.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Connection to the database failed. Check your database URL, username, and password.");
            e.printStackTrace();
            return null;
        }
    }
}
