package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import metier.Acte;
import metier.RendezVous;

public class RendezVousDAO {
    private static Connection conn = DatabaseConnection.connect();

    // Method to add a rendezvous to the database
    public static void addRendezVous(int patientId, Timestamp datePrevue, int categorieId, int acteMedicalId, String etat) throws SQLException {
        String query = "INSERT INTO RendezVous (patient_id, date_prevue, categorie_id, numero_acte, etat) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);
            stmt.setTimestamp(2, datePrevue);
            stmt.setInt(3, categorieId);
            stmt.setInt(4, acteMedicalId);
            stmt.setString(5, etat);

            stmt.executeUpdate();
        }
    }



    public List<RendezVous> fetchRendezVous() throws SQLException {
        String query = "SELECT R.ID, DATE(R.date_prevue) AS date_pre, CI.categorie AS category, R.numero_acte, R.etat, DATE(R.date_reelle) AS date_reele, CONCAT(P.prenom, ' ', P.nom) AS full_name "
                + "FROM Rendezvous R "
                + "JOIN Patient P ON R.patient_id = P.id "
                + "JOIN categorie_intervention CI ON R.categorie_id = CI.id "
                + "WHERE DATE(R.date_prevue) = CURDATE() AND R.etat = 'Planifié'";

        List<RendezVous> rendezVousList = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                RendezVous rendezvous = new RendezVous(
                        resultSet.getInt("ID"),
                        resultSet.getString("full_name"),
                        resultSet.getString("date_pre"),
                        resultSet.getString("category"),
                        resultSet.getInt("numero_acte"),
                        resultSet.getString("etat"),
                        resultSet.getString("date_reele")
                );
                rendezVousList.add(rendezvous);
            }
        }

        return rendezVousList;
    }
    public static ObservableList<RendezVous> getInterventionsByActeId(int acteId) {
        ObservableList<RendezVous> interventions = FXCollections.observableArrayList();
        String query = """
                SELECT id, patient_id, DATE(date_prevue) AS date_prevue, categorie_id, etat, numero_acte, DATE(date_reelle) AS date_reelle
                FROM Rendezvous WHERE numero_acte = ?;
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, acteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                interventions.add(new RendezVous(
                        rs.getInt("id"),
                        rs.getString("patient_id"),
                        rs.getString("date_prevue"),
                        rs.getString("categorie_id"),
                        rs.getInt("numero_acte"),
                        rs.getString("etat"),
                        rs.getString("date_reelle")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interventions;
    }

    public void updateIntervention(int id, String dateReelle, String status) throws SQLException {
        String query = "UPDATE Rendezvous SET date_reelle = ?, etat = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dateReelle);
            stmt.setString(2, status);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    public void deleteIntervention(int id) throws SQLException {
        String query = "DELETE FROM Rendezvous WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Acte getActeAndPatientDetails(int acteId, int patientId) {
        String query = """
        SELECT 
            a.ID AS idActe, a.date_debut, a.prix_comptabilise, a.etat_de_l_acte, a.date_de_fin,
            p.id AS idPatient, p.nom, p.prenom
        FROM ActeMedicaux a
        JOIN Patient p ON a.patient_concerne = p.id
        WHERE a.ID = ? AND p.id = ?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, acteId);
            stmt.setInt(2, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Extract ActeMedicaux details
                    int idActe = rs.getInt("idActe");
                    String dateDebut = rs.getString("date_debut");
                    double prixComptabilise = rs.getDouble("prix_comptabilise");
                    String etatActe = rs.getString("etat_de_l_acte");
                    String dateFin = rs.getString("date_de_fin");

                    // Extract Patient details
                    int idPatient = rs.getInt("idPatient");
                    String patientName = rs.getString("nom") + " " + rs.getString("prenom");

                    // Return an Acte object with patient details included
                    return new Acte(idActe, dateDebut, prixComptabilise, etatActe, dateFin, idPatient, patientName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if no data is found or an error occurs
    }
    public ObservableList<RendezVous> getTodayPlanifieRendezVous() {
        ObservableList<RendezVous> rendezVousList = FXCollections.observableArrayList();
        String query = "SELECT R.ID, DATE(R.date_prevue) AS date_pre, CI.categorie AS category, " +
                "R.numero_acte, R.etat, DATE(R.date_reelle) AS date_reele, " +
                "CONCAT(P.prenom, ' ', P.nom) AS full_name " +
                "FROM Rendezvous R " +
                "JOIN Patient P ON R.patient_id = P.id " +
                "JOIN categorie_intervention CI ON R.categorie_id = CI.id " +
                "WHERE DATE(R.date_prevue) = CURDATE() AND R.etat = 'Planifié'";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                RendezVous rendezvous = new RendezVous(
                        resultSet.getInt("ID"),
                        resultSet.getString("full_name"),
                        resultSet.getString("date_pre"),
                        resultSet.getString("category"),
                        resultSet.getInt("numero_acte"),
                        resultSet.getString("etat"),
                        resultSet.getString("date_reele")
                );
                rendezVousList.add(rendezvous);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rendezVousList;
    }

    public List<RendezVous> getAllRendezVous() {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String query = "SELECT R.ID, DATE(R.date_prevue) AS date_pre, CI.categorie AS category, " +
                "R.numero_acte, R.etat, DATE(R.date_reelle) AS date_reele, CONCAT(P.prenom,' ',P.nom) AS full_name " +
                "FROM Rendezvous R " +
                "JOIN Patient P ON R.patient_id = P.id " +
                "JOIN categorie_intervention CI ON R.categorie_id = CI.id";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("ID");
                String patientName = rs.getString("full_name");
                String datePrevue = rs.getString("date_pre");
                String category = rs.getString("category");
                int medicalAct = rs.getInt("numero_acte");
                String status = rs.getString("etat");
                String realDate = rs.getString("date_reele");
                RendezVous rendezvous = new RendezVous(id, patientName, datePrevue, category, medicalAct, status, realDate);
                rendezVousList.add(rendezvous);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rendezVousList;
    }

    // Method to update the date of a rendezvous
    public boolean updateRendezVousDate(int id, Timestamp newDate) {
        String sql = "UPDATE RendezVous SET date_prevue = ? WHERE ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setTimestamp(1, newDate);
            statement.setInt(2, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to delete a rendezvous
    public boolean deleteRendezVous(int id) {
        String sql = "DELETE FROM RendezVous WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to count the total number of rendezvous
    public int countTotalRendezVous() {
        String sql = "SELECT COUNT(*) AS total_rendezvous FROM RendezVous";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total_rendezvous");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Method to count the number of "Planifié" rendezvous
    public int countPlanifieRendezVous() {
        String sql = "SELECT COUNT(*) AS planifie_rendezvous FROM RendezVous WHERE etat = 'Planifié'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("planifie_rendezvous");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Method to count the number of "Terminé" rendezvous
    public int countTermineRendezVous() {
        String sql = "SELECT COUNT(*) AS termine_rendezvous FROM RendezVous WHERE etat = 'Terminé'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("termine_rendezvous");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalRendezVousCount() {
        String query = "SELECT COUNT(*) AS total_rendezvous FROM RendezVous";
        return getCount(query);
    }

    public int getTotalPatientsCount() {
        String query = "SELECT COUNT(*) AS total_patients FROM Patient";
        return getCount(query);
    }

    public int getTodayPlanifieRendezVousCount() {
        String query = "SELECT COUNT(*) AS planifie_rendezvous_today FROM RendezVous WHERE etat = 'Planifié' AND DATE(date_prevue) = CURDATE()";
        return getCount(query);
    }

    private int getCount(String query) {
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    // Fetch counters
    public int getTotalRendezvous() throws SQLException {
        String query = "SELECT COUNT(*) AS total_rendezvous FROM RendezVous";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            return resultSet.next() ? resultSet.getInt("total_rendezvous") : 0;
        }
    }

    public int getTotalPatients() throws SQLException {
        String query = "SELECT COUNT(*) AS total_patients FROM Patient";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            return resultSet.next() ? resultSet.getInt("total_patients") : 0;
        }
    }

    public int getTotalActes() throws SQLException {
        String query = "SELECT COUNT(*) AS total_actes FROM ActeMedicaux";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            return resultSet.next() ? resultSet.getInt("total_actes") : 0;
        }
    }
}
