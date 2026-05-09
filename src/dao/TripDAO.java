package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Trip;

/**
 * TripDAO - Data Access Object for Trip operations.
 * Handles all database operations related to trips and trip members.
 */
public class TripDAO {

    /**
     * Create a new trip.
     * @param trip Trip object with details
     * @return generated trip ID or null on failure
     * @throws SQLException on database error
     */
    public String createTrip(Trip trip) throws SQLException {
        String sql = "INSERT INTO trips (name, description, destination, start_date, end_date, created_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, trip.getName());
            ps.setString(2, trip.getDescription() != null ? trip.getDescription() : "");
            ps.setString(3, trip.getDestination() != null ? trip.getDestination() : "");
            ps.setString(4, trip.getStartDate());
            ps.setString(5, trip.getEndDate());
            ps.setString(6, trip.getCreatedBy());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        String tripId = rs.getString(1);

                        // Add creator as a member with 'owner' role
                        addMember(tripId, trip.getCreatedBy(), "owner");

                        return tripId;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get a trip by ID.
     * @param tripId Trip UUID
     * @return Trip object or null
     * @throws SQLException on database error
     */
    public Trip getTripById(String tripId) throws SQLException {
        String sql = "SELECT t.*, u.name as creator_name, "
                   + "(SELECT COUNT(*) FROM trip_members WHERE trip_id = t.id) as member_count "
                   + "FROM trips t JOIN users u ON t.created_by = u.id "
                   + "WHERE t.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tripId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTrip(rs);
                }
            }
        }
        return null;
    }

    /**
     * Get all trips for a user.
     * @param userId User UUID
     * @return List of trips the user is a member of
     * @throws SQLException on database error
     */
    public List<Trip> getTripsByUser(String userId) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT t.*, u.name as creator_name, "
                   + "(SELECT COUNT(*) FROM trip_members WHERE trip_id = t.id) as member_count "
                   + "FROM trips t "
                   + "JOIN trip_members tm ON t.id = tm.trip_id "
                   + "JOIN users u ON t.created_by = u.id "
                   + "WHERE tm.user_id = ? "
                   + "ORDER BY t.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trips.add(mapTrip(rs));
                }
            }
        }
        return trips;
    }

    /**
     * Join a trip using invite code.
     * @param inviteCode The trip's invite code
     * @param userId User UUID who wants to join
     * @return true if joined successfully, false otherwise
     * @throws SQLException on database error
     */
    public boolean joinTrip(String inviteCode, String userId) throws SQLException {
        // Find the trip by invite code
        String findSql = "SELECT id FROM trips WHERE invite_code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement findPs = conn.prepareStatement(findSql)) {

            findPs.setString(1, inviteCode);

            try (ResultSet rs = findPs.executeQuery()) {
                if (rs.next()) {
                    String tripId = rs.getString("id");
                    return addMember(tripId, userId, "member");
                }
            }
        }
        return false;
    }

    /**
     * Add a member to a trip.
     * @param tripId Trip UUID
     * @param userId User UUID
     * @param role Member role (owner/member)
     * @return true if added successfully
     * @throws SQLException on database error
     */
    public boolean addMember(String tripId, String userId, String role) throws SQLException {
        String sql = "INSERT INTO trip_members (trip_id, user_id, role) VALUES (?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE role = role";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tripId);
            ps.setString(2, userId);
            ps.setString(3, role);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Get all members of a trip.
     * @param tripId Trip UUID
     * @return List of User objects
     * @throws SQLException on database error
     */
    public List<model.User> getTripMembers(String tripId) throws SQLException {
        List<model.User> members = new ArrayList<>();
        String sql = "SELECT u.id, u.name, tm.role "
                   + "FROM users u "
                   + "JOIN trip_members tm ON u.id = tm.user_id "
                   + "WHERE tm.trip_id = ? "
                   + "ORDER BY tm.joined_at";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tripId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.User user = new model.User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    members.add(user);
                }
            }
        }
        return members;
    }

    /**
     * Delete a trip.
     * @param tripId Trip UUID
     * @param userId User UUID (must be the creator)
     * @return true if deleted successfully
     * @throws SQLException on database error
     */
    public boolean deleteTrip(String tripId, String userId) throws SQLException {
        String sql = "DELETE FROM trips WHERE id = ? AND created_by = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tripId);
            ps.setString(2, userId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Get total trip count for a user.
     * @param userId User UUID
     * @return Number of trips
     * @throws SQLException on database error
     */
    public int getTripCount(String userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM trip_members WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Map a ResultSet row to a Trip object.
     */
    private Trip mapTrip(ResultSet rs) throws SQLException {
        Trip trip = new Trip();
        trip.setId(rs.getString("id"));
        trip.setName(rs.getString("name"));
        trip.setDescription(rs.getString("description"));
        trip.setDestination(rs.getString("destination"));
        trip.setStartDate(rs.getString("start_date"));
        trip.setEndDate(rs.getString("end_date"));
        trip.setInviteCode(rs.getString("invite_code"));
        trip.setCreatedBy(rs.getString("created_by"));
        trip.setCreatedAt(rs.getString("created_at"));
        trip.setCreatedByName(rs.getString("creator_name"));
        trip.setMemberCount(rs.getInt("member_count"));
        return trip;
    }
}
