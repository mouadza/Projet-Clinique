package DAO;

import metier.Paiement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaiementDAO {
    private static Connection conn = DatabaseConnection.connect();

    public boolean insertPaiement(Paiement paiement) {
        String insertQuery = "INSERT INTO Paiements (acteID, patientID, datePaiement, montant, prix_comptabilise, reste, paiement_method, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setInt(1, paiement.getActeId());
            ps.setInt(2, paiement.getPatientID());
            ps.setString(3, paiement.getDatePaiement());
            ps.setDouble(4, paiement.getMontant());
            ps.setDouble(5, paiement.getPrixComptabilise());
            ps.setDouble(6, paiement.getReste());
            ps.setString(7, paiement.getPaiementMethod());
            ps.setString(8, paiement.getStatut());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Paiement> getAllPaiements() {
        List<Paiement> paiements = new ArrayList<>();
        String query = """
            SELECT p.id, p.acteID, CONCAT(pt.prenom, ' ', pt.nom) AS patientName, 
                   p.datePaiement, p.montant, p.prix_comptabilise, p.reste, 
                   p.paiement_method, p.statut
            FROM Paiements p
            JOIN Patient pt ON p.patientID = pt.id
        """;

        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                paiements.add(new Paiement(
                        resultSet.getInt("id"),
                        resultSet.getInt("acteID"),
                        resultSet.getString("patientName"),
                        resultSet.getString("datePaiement"),
                        resultSet.getDouble("montant"),
                        resultSet.getDouble("prix_comptabilise"),
                        resultSet.getString("paiement_method"),
                        resultSet.getDouble("reste"),
                        resultSet.getString("statut")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paiements;
    }

    public int getTotalPaiements() {
        String query = "SELECT COUNT(*) AS total FROM Paiements";
        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalPaidAmount() {
        String query = "SELECT SUM(montant) AS totalPaid FROM Paiements";
        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getDouble("totalPaid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalOutstandingBalance() {
        String query = "SELECT SUM(reste) AS totalBalance FROM Paiements";
        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getDouble("totalBalance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
