package DAO;

import metier.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PatientDAO {
    private static Connection conn = DatabaseConnection.connect();

    public ObservableList<Patient> getPatients() {
        ObservableList<Patient> patients = FXCollections.observableArrayList();
        String query = "SELECT ID, nom, prenom, date_naissance, telephone, CIN, adresse, email FROM Patient";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("ID"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("CIN"),
                        rs.getString("adresse"),
                        rs.getString("email")
                );
                patients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging this exception to a file or displaying a user-friendly message.
        }
        return patients;
    }


    public Patient getPatientById(int patientId) {
        Patient patient = null;
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT ID, nom, prenom, date_naissance, telephone, CIN, adresse, email FROM Patient WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                patient = new Patient(
                        rs.getInt("ID"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("date_naissance"),
                        rs.getString("telephone"),
                        rs.getString("CIN"),
                        rs.getString("adresse"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patient;
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT ID, nom, prenom, date_naissance, telephone, CIN, adresse, email FROM Patient";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    patients.add(new Patient(
                            rs.getInt("ID"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("date_naissance"),
                            rs.getString("telephone"),
                            rs.getString("CIN"),
                            rs.getString("adresse"),
                            rs.getString("email")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    public void addPatient(Patient patient) {
        String query = "INSERT INTO Patient (nom, prenom, date_naissance, telephone, CIN, adresse, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, patient.getNom());
            stmt.setString(2, patient.getPrenom());
            stmt.setString(3, patient.getDateNaissance());
            stmt.setString(4, patient.getTelephone());
            stmt.setString(5, patient.getCIN());
            stmt.setString(6, patient.getAdresse());
            stmt.setString(7, patient.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePatient(Patient patient) {
        String query = "UPDATE Patient SET nom=?, prenom=?, date_naissance=?, telephone=?, CIN=?, adresse=?, email=? WHERE CIN=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, patient.getNom());
            stmt.setString(2, patient.getPrenom());
            stmt.setString(3, patient.getDateNaissance());
            stmt.setString(4, patient.getTelephone());
            stmt.setString(5, patient.getCIN());
            stmt.setString(6, patient.getAdresse());
            stmt.setString(7, patient.getEmail());
            stmt.setString(8, patient.getCIN());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePatient(String CIN) {
        String query = "DELETE FROM Patient WHERE CIN = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, CIN);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countTotalPatients() {
        String query = "SELECT COUNT(*) AS total FROM Patient";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countAdultPatients() {
        String query = "SELECT COUNT(*) AS total FROM Patient WHERE TIMESTAMPDIFF(YEAR, date_naissance, CURDATE()) >= 18";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }



}
