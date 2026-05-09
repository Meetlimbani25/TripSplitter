package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.User;
import utility.PasswordUtil;

/**
 * UserDAO - Data Access Object for User operations.
 * Handles all database operations related to users.
 */
public class UserDAO {

    /**
     * Register a new user.
     * @param user User object with registration details
     * @return true if registration successful, false otherwise
     * @throws SQLException on database error
     */
    public boolean registerUser(User user) throws SQLException {
        // Check if email already exists
        if (emailExists(user.getEmail())) {
            return false;
        }

        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getString(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Validate user login credentials.
     * @param email User's email
     * @param password User's password
     * @return User object if valid, null otherwise
     * @throws SQLException on database error
     */
    public User validateUser(String email, String password) throws SQLException {
        String sql = "SELECT id, name, email, password, created_at FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verifyPassword(password, rs.getString("password"))) {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setCreatedAt(rs.getString("created_at"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Get user by ID.
     * @param userId User's UUID
     * @return User object if found, null otherwise
     * @throws SQLException on database error
     */
    public User getUserById(String userId) throws SQLException {
        String sql = "SELECT id, name, email, created_at FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setCreatedAt(rs.getString("created_at"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Check if email already exists in the database.
     * @param email The email to check
     * @return true if exists, false otherwise
     * @throws SQLException on database error
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Get user name by ID.
     * @param userId User's UUID
     * @return User's name or "Unknown"
     * @throws SQLException on database error
     */
    public String getUserName(String userId) throws SQLException {
        String sql = "SELECT name FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return "Unknown";
    }
}
