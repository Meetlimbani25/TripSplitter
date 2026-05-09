package model;

import java.math.BigDecimal;

/**
 * Expense Model - Represents an expense in the TripSplitter system.
 * Maps to the 'expenses' table in the database.
 */
public class Expense {

    private String id;            // UUID primary key
    private String tripId;        // UUID of the trip this expense belongs to
    private String title;        // Expense title (e.g., "Hotel", "Food")
    private BigDecimal amount;   // Expense amount
    private String paidBy;       // UUID of the user who paid
    private String paidByName;   // Name of the payer (for display)
    private String expenseDate;  // Date of the expense
    private String category;     // Expense category (hotel, food, transport, etc.)
    private String createdBy;    // UUID of the user who created the record
    private String createdAt;    // Record creation timestamp
    private BigDecimal perPersonShare; // Calculated share per person

    // Default constructor
    public Expense() {
    }

    // Constructor for creating a new expense
    public Expense(String tripId, String title, BigDecimal amount, String paidBy, String expenseDate, String category, String createdBy) {
        this.tripId = tripId;
        this.title = title;
        this.amount = amount;
        this.paidBy = paidBy;
        this.expenseDate = expenseDate;
        this.category = category;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getPaidByName() {
        return paidByName;
    }

    public void setPaidByName(String paidByName) {
        this.paidByName = paidByName;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getPerPersonShare() {
        return perPersonShare;
    }

    public void setPerPersonShare(BigDecimal perPersonShare) {
        this.perPersonShare = perPersonShare;
    }

    @Override
    public String toString() {
        return "Expense{id='" + id + "', title='" + title + "', amount=" + amount + "}";
    }
}
