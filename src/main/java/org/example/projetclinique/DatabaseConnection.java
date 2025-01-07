package org.example.projetclinique;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ProjetClinique";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Use environment variables for production

    // Establish and return the connection
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

    // Method to safely execute SELECT queries (using PreparedStatement for security)
    public static ResultSet executeQuery(String query, Object... params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = connect();
            stmt = conn.prepareStatement(query);

            // Set parameters dynamically for PreparedStatement
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            rs = stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    // Method to safely execute INSERT, UPDATE, DELETE queries (using PreparedStatement)
    public static int executeUpdate(String query, Object... params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;

        try {
            conn = connect();
            stmt = conn.prepareStatement(query);

            // Set parameters dynamically for PreparedStatement
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    // Method to close resources (Connection, PreparedStatement, ResultSet)
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
