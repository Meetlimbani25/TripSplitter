package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Settlement;

/**
 * SettlementDAO - Data Access Object for Settlement operations.
 * Handles all database operations related to settlements.
 */
public class SettlementDAO {

    /**
     * Create a new settlement record.
     * @param settlement Settlement object with details
     * @return true if created successfully
     * @throws SQLException on database error
     */
    public boolean createSettlement(Settlement settlement) throws SQLException {
        String sql = "INSERT INTO settlements (trip_id, payer_id, payee_id, amount, is_settled) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, settlement.getTripId());
            ps.setString(2, settlement.getPayerId());
            ps.setString(3, settlement.getPayeeId());
            ps.setBigDecimal(4, settlement.getAmount());
            ps.setBoolean(5, settlement.isSettled());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mark a settlement as settled/paid.
     * @param settlementId Settlement UUID
     * @return true if updated successfully
     * @throws SQLException on database error
     */
    public boolean settlePayment(String settlementId) throws SQLException {
        String sql = "UPDATE settlements SET is_settled = true, settled_at = NOW() WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, settlementId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Get all settlements for a trip.
     * @param tripId Trip UUID
     * @return List of Settlement objects
     * @throws SQLException on database error
     */
    public List<Settlement> getSettlementsByTrip(String tripId) throws SQLException {
        List<Settlement> settlements = new ArrayList<>();
        String sql = "SELECT s.*, "
                   + "u1.name as payer_name, "
                   + "u2.name as payee_name "
                   + "FROM settlements s "
                   + "JOIN users u1 ON s.payer_id = u1.id "
                   + "JOIN users u2 ON s.payee_id = u2.id "
                   + "WHERE s.trip_id = ? "
                   + "ORDER BY s.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tripId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    settlements.add(mapSettlement(rs));
                }
            }
        }
        return settlements;
    }

    /**
     * Get pending (unsettled) settlements for a trip.
     * @param tripId Trip UUID
     * @return List of pending Settlement objects
     * @throws SQLException on database error
     */
    public List<Settlement> getPendingSettlements(String tripId) throws SQLException {
        List<Settlement> settlements = new ArrayList<>();
        String sql = "SELECT s.*, "
                   + "u1.name as payer_name, "
                   + "u2.name as payee_name "
                   + "FROM settlements s "
                   + "JOIN users u1 ON s.payer_id = u1.id "
                   + "JOIN users u2 ON s.payee_id = u2.id "
                   + "WHERE s.trip_id = ? AND s.is_settled = false "
                   + "ORDER BY s.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tripId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    settlements.add(mapSettlement(rs));
                }
            }
        }
        return settlements;
    }

    /**
     * Get settlements where a user is the payer.
     * @param userId User UUID
     * @return List of Settlement objects
     * @throws SQLException on database error
     */
    public List<Settlement> getSettlementsByPayer(String userId) throws SQLException {
        List<Settlement> settlements = new ArrayList<>();
        String sql = "SELECT s.*, "
                   + "u1.name as payer_name, "
                   + "u2.name as payee_name "
                   + "FROM settlements s "
                   + "JOIN users u1 ON s.payer_id = u1.id "
                   + "JOIN users u2 ON s.payee_id = u2.id "
                   + "WHERE s.payer_id = ? "
                   + "ORDER BY s.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    settlements.add(mapSettlement(rs));
                }
            }
        }
        return settlements;
    }

    /**
     * Get total settled amount for a trip.
     * @param tripId Trip UUID
     * @return Total settled amount
     * @throws SQLException on database error
     */
    public BigDecimal getTotalSettled(String tripId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM settlements "
                   + "WHERE trip_id = ? AND is_settled = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tripId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Delete a settlement.
     * @param settlementId Settlement UUID
     * @return true if deleted successfully
     * @throws SQLException on database error
     */
    public boolean deleteSettlement(String settlementId) throws SQLException {
        String sql = "DELETE FROM settlements WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, settlementId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Map a ResultSet row to a Settlement object.
     */
    private Settlement mapSettlement(ResultSet rs) throws SQLException {
        Settlement settlement = new Settlement();
        settlement.setId(rs.getString("id"));
        settlement.setTripId(rs.getString("trip_id"));
        settlement.setPayerId(rs.getString("payer_id"));
        settlement.setPayerName(rs.getString("payer_name"));
        settlement.setPayeeId(rs.getString("payee_id"));
        settlement.setPayeeName(rs.getString("payee_name"));
        settlement.setAmount(rs.getBigDecimal("amount"));
        settlement.setSettled(rs.getBoolean("is_settled"));
        settlement.setSettledAt(rs.getString("settled_at"));
        settlement.setCreatedAt(rs.getString("created_at"));
        return settlement;
    }
}
