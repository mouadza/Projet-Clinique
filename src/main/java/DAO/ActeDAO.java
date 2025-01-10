package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import metier.Acte;
import java.sql.*;

public class ActeDAO {
    private static Connection conn = DatabaseConnection.connect();

    public ObservableList<Acte> getAllActes() {
        ObservableList<Acte> actesList = FXCollections.observableArrayList();
        String query = "SELECT a.ID, a.date_debut, a.prix_comptabilise, a.etat_de_l_acte, " +
                "a.date_de_fin, a.patient_concerne AS patientID, " +
                "CONCAT(p.prenom, ' ', p.nom) AS patientName " +
                "FROM ActeMedicaux a JOIN Patient p ON a.patient_concerne = p.id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                actesList.add(new Acte(
                        rs.getInt("ID"),
                        rs.getString("date_debut"),
                        rs.getDouble("prix_comptabilise"),
                        rs.getString("etat_de_l_acte"),
                        rs.getString("date_de_fin"),
                        rs.getInt("patientID"),
                        rs.getString("patientName")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actesList;
    }
    public ObservableList<Acte> getActes() {
        ObservableList<Acte> actesList = FXCollections.observableArrayList();
        String query = "SELECT a.ID, a.date_debut, a.prix_comptabilise, a.etat_de_l_acte, " +
                "a.date_de_fin, a.patient_concerne AS patientID, " +
                "CONCAT(p.prenom, ' ', p.nom) AS patientName " +
                "FROM ActeMedicaux a JOIN Patient p ON a.patient_concerne = p.id" +
                "WHERE a.prix_comptabilise > 0"  ;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                actesList.add(new Acte(
                        rs.getInt("ID"),
                        rs.getString("date_debut"),
                        rs.getDouble("prix_comptabilise"),
                        rs.getString("etat_de_l_acte"),
                        rs.getString("date_de_fin"),
                        rs.getInt("patientID"),
                        rs.getString("patientName")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actesList;
    }

    // Method to update an Acte
    public void updateActe(Acte acte) {
        String updateQuery = "UPDATE ActeMedicaux SET etat_de_l_acte = ?, date_de_fin = ? WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, acte.getEtatDeLActe());
            stmt.setString(2, acte.getDateFin());
            stmt.setInt(3, acte.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ObservableList<Acte> searchActesByPatientName(String searchQuery) {
        ObservableList<Acte> actesList = FXCollections.observableArrayList();
        String query = "SELECT a.ID, a.date_debut, a.prix_comptabilise, a.etat_de_l_acte, a.date_de_fin, " +
                "CONCAT(p.prenom, ' ', p.nom) AS patient_name, p.id AS patient_id " +
                "FROM ActeMedicaux a " +
                "JOIN Patient p ON a.patient_concerne = p.id " +
                "WHERE LOWER(CONCAT(p.prenom, ' ', p.nom)) LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {

            // Set the search query parameter
            ps.setString(1, "%" + searchQuery + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String dateDebut = rs.getString("date_debut");
                    double prixComptabilise = rs.getDouble("prix_comptabilise");
                    String etatDeLActe = rs.getString("etat_de_l_acte");
                    String dateFin = rs.getString("date_de_fin");
                    int patientID = rs.getInt("patient_id");
                    String patientName = rs.getString("patient_name");
                    Acte acte = new Acte(id, dateDebut, prixComptabilise, etatDeLActe, dateFin, patientID, patientName);
                    actesList.add(acte);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return actesList;
    }
    public int getTotalActes() {
        String query = "SELECT COUNT(*) AS total_actes FROM ActeMedicaux";
        return getCount(query);
    }
    public int getCompletedActes() {
        String query = "SELECT COUNT(*) AS completed_actes FROM ActeMedicaux WHERE etat_de_l_acte = 'Terminé'";
        return getCount(query);
    }
    public int getOngoingActes() {
        String query = "SELECT COUNT(*) AS ongoing_actes FROM ActeMedicaux WHERE etat_de_l_acte = 'En cours'";
        return getCount(query);
    }
    private int getCount(String query) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public boolean addActe(Acte acte) {
        String insertQuery = "INSERT INTO ActeMedicaux (date_debut, prix_comptabilise, etat_de_l_acte, date_de_fin, patient_concerne) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, acte.getDateDebut());
            stmt.setDouble(2, acte.getPrixComptabilise());
            stmt.setString(3, acte.getEtatDeLActe());
            stmt.setString(4, acte.getDateFin());
            stmt.setInt(5, acte.getPatientID());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static ObservableList<String> getActesMedicalsForPatient(int patientId) throws SQLException {
        ObservableList<String> acteMedicalList = FXCollections.observableArrayList();

        String query = "SELECT ID FROM ActeMedicaux WHERE patient_concerne = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                acteMedicalList.add(String.valueOf(rs.getInt("ID")));
            }
        }

        return acteMedicalList;
    }
}
