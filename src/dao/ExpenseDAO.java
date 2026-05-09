package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Balance;
import model.Expense;

/**
 * ExpenseDAO - Data Access Object for Expense operations.
 * Handles all database operations related to expenses and expense splits.
 */
public class ExpenseDAO {

    /**
     * Add a new expense to a trip.
     * @param expense Expense object with details
     * @return true if added successfully
     * @throws SQLException on database error
     */
    public boolean addExpense(Expense expense) throws SQLException {
        String sql = "INSERT INTO expenses (trip_id, title, amount, paid_by, expense_date, category, created_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, expense.getTripId());
            ps.setString(2, expense.getTitle());
            ps.setBigDecimal(3, expense.getAmount());
            ps.setString(4, expense.getPaidBy());
            ps.setString(5, expense.getExpenseDate());
            ps.setString(6, expense.getCategory() != null ? expense.getCategory() : "general");
            ps.setString(7, expense.getCreatedBy());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        String expenseId = rs.getString(1);

                        // Create equal splits for all trip members
                        createEqualSplits(conn, expenseId, expense.getTripId(), expense.getAmount());

                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Create equal expense splits for all trip members.
     * @param conn Active database connection
     * @param expenseId Expense UUID
     * @param tripId Trip UUID
     * @param totalAmount Total expense amount
     * @throws SQLException on database error
     */
    private void createEqualSplits(Connection conn, String expenseId, String tripId, BigDecimal totalAmount) throws SQLException {
        // Get all trip members
        String memberSql = "SELECT user_id FROM trip_members WHERE trip_id = ?";
        List<String> memberIds = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(memberSql)) {
            ps.setString(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    memberIds.add(rs.getString("user_id"));
                }
            }
        }

        if (memberIds.isEmpty()) return;

        // Calculate per-person share
        BigDecimal share = totalAmount.divide(new BigDecimal(memberIds.size()), 2, RoundingMode.HALF_UP);

        // Insert splits for each member
        String splitSql = "INSERT INTO expense_splits (expense_id, user_id, share_amount, is_settled) "
                        + "VALUES (?, ?, ?, false)";

        try (PreparedStatement ps = conn.prepareStatement(splitSql)) {
            for (String memberId : memberIds) {
                ps.setString(1, expenseId);
                ps.setString(2, memberId);
                ps.setBigDecimal(3, share);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Get all expenses for a trip.
     * @param tripId Trip UUID
     * @return List of Expense objects
     * @throws SQLException on database error
     */
    public List<Expense> getExpensesByTrip(String tripId) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT e.*, u.name as payer_name "
                   + "FROM expenses e "
                   + "JOIN users u ON e.paid_by = u.id "
                   + "WHERE e.trip_id = ? "
                   + "ORDER BY e.expense_date DESC, e.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tripId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapExpense(rs));
                }
            }
        }
        return expenses;
    }

    /**
     * Get an expense by ID.
     * @param expenseId Expense UUID
     * @return Expense object or null
     * @throws SQLException on database error
     */
    public Expense getExpenseById(String expenseId) throws SQLException {
        String sql = "SELECT e.*, u.name as payer_name "
                   + "FROM expenses e "
                   + "JOIN users u ON e.paid_by = u.id "
                   + "WHERE e.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, expenseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapExpense(rs);
                }
            }
        }
        return null;
    }

    /**
     * Delete an expense.
     * @param expenseId Expense UUID
     * @return true if deleted successfully
     * @throws SQLException on database error
     */
    public boolean deleteExpense(String expenseId) throws SQLException {
        // Delete splits first (cascade should handle this, but being explicit)
        String splitSql = "DELETE FROM expense_splits WHERE expense_id = ?";
        String expenseSql = "DELETE FROM expenses WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(splitSql)) {
                ps.setString(1, expenseId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(expenseSql)) {
                ps.setString(1, expenseId);
                return ps.executeUpdate() > 0;
            }
        }
    }

    /**
     * Get total expenses for a trip.
     * @param tripId Trip UUID
     * @return Total amount
     * @throws SQLException on database error
     */
    public BigDecimal getTotalExpenses(String tripId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM expenses WHERE trip_id = ?";

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
     * Get total expenses for a user across all trips.
     * @param userId User UUID
     * @return Total amount
     * @throws SQLException on database error
     */
    public BigDecimal getTotalExpensesByUser(String userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(e.amount), 0) as total "
                   + "FROM expenses e "
                   + "JOIN trip_members tm ON e.trip_id = tm.trip_id "
                   + "WHERE tm.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calculate balance sheet for a trip.
     * Shows who owes whom and how much.
     * @param tripId Trip UUID
     * @return List of Balance objects
     * @throws SQLException on database error
     */
    public List<Balance> getTripBalances(String tripId) throws SQLException {
        List<Balance> balances = new ArrayList<>();
        Map<String, BigDecimal> paidMap = new HashMap<>();
        Map<String, BigDecimal> shareMap = new HashMap<>();
        Map<String, String> nameMap = new HashMap<>();

        // Get all members
        String memberSql = "SELECT u.id, u.name FROM users u "
                         + "JOIN trip_members tm ON u.id = tm.user_id "
                         + "WHERE tm.trip_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(memberSql)) {

            ps.setString(1, tripId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String userId = rs.getString("id");
                    String name = rs.getString("name");
                    paidMap.put(userId, BigDecimal.ZERO);
                    shareMap.put(userId, BigDecimal.ZERO);
                    nameMap.put(userId, name);
                }
            }
        }

        // Calculate total paid by each user
        String paidSql = "SELECT paid_by, SUM(amount) as total FROM expenses "
                       + "WHERE trip_id = ? GROUP BY paid_by";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(paidSql)) {

            ps.setString(1, tripId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String userId = rs.getString("paid_by");
                    BigDecimal total = rs.getBigDecimal("total");
                    paidMap.put(userId, total);
                }
            }
        }

        // Calculate each user's share from expense_splits
        String shareSql = "SELECT es.user_id, SUM(es.share_amount) as total "
                        + "FROM expense_splits es "
                        + "JOIN expenses e ON es.expense_id = e.id "
                        + "WHERE e.trip_id = ? "
                        + "GROUP BY es.user_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(shareSql)) {

            ps.setString(1, tripId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String userId = rs.getString("user_id");
                    BigDecimal total = rs.getBigDecimal("total");
                    shareMap.put(userId, total);
                }
            }
        }

        // Build balance list
        for (String userId : nameMap.keySet()) {
            BigDecimal paid = paidMap.getOrDefault(userId, BigDecimal.ZERO);
            BigDecimal share = shareMap.getOrDefault(userId, BigDecimal.ZERO);
            BigDecimal balance = paid.subtract(share);

            balances.add(new Balance(userId, nameMap.get(userId), paid, share, balance));
        }

        return balances;
    }

    /**
     * Get expense count for a user.
     * @param userId User UUID
     * @return Number of expenses
     * @throws SQLException on database error
     */
    public int getExpenseCount(String userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM expenses e "
                   + "JOIN trip_members tm ON e.trip_id = tm.trip_id "
                   + "WHERE tm.user_id = ?";

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
     * Map a ResultSet row to an Expense object.
     */
    private Expense mapExpense(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getString("id"));
        expense.setTripId(rs.getString("trip_id"));
        expense.setTitle(rs.getString("title"));
        expense.setAmount(rs.getBigDecimal("amount"));
        expense.setPaidBy(rs.getString("paid_by"));
        expense.setPaidByName(rs.getString("payer_name"));
        expense.setExpenseDate(rs.getString("expense_date"));
        expense.setCategory(rs.getString("category"));
        expense.setCreatedBy(rs.getString("created_by"));
        expense.setCreatedAt(rs.getString("created_at"));
        return expense;
    }
}
