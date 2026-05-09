package model;

import java.math.BigDecimal;

/**
 * Settlement Model - Represents a settlement between two users in a trip.
 * Maps to the 'settlements' table in the database.
 */
public class Settlement {

    private String id;            // UUID primary key
    private String tripId;        // UUID of the trip
    private String payerId;      // UUID of the user who pays
    private String payerName;    // Name of the payer (for display)
    private String payeeId;      // UUID of the user who receives
    private String payeeName;    // Name of the payee (for display)
    private BigDecimal amount;   // Settlement amount
    private boolean isSettled;   // Whether the settlement is completed
    private String settledAt;    // Timestamp when settled
    private String createdAt;    // Record creation timestamp

    // Default constructor
    public Settlement() {
    }

    // Constructor for creating a new settlement
    public Settlement(String tripId, String payerId, String payeeId, BigDecimal amount) {
        this.tripId = tripId;
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.amount = amount;
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

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isSettled() {
        return isSettled;
    }

    public void setSettled(boolean isSettled) {
        this.isSettled = isSettled;
    }

    public String getSettledAt() {
        return settledAt;
    }

    public void setSettledAt(String settledAt) {
        this.settledAt = settledAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Settlement{id='" + id + "', payer='" + payerName + "', payee='" + payeeName + "', amount=" + amount + "}";
    }
}
