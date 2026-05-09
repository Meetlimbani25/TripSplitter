package model;

import java.math.BigDecimal;

/**
 * Balance Model - Represents the balance between two users in a trip.
 * Used for the balance sheet calculation.
 * This is a computed model, not directly mapped to a database table.
 */
public class Balance {

    private String userId;        // UUID of the user
    private String userName;     // Name of the user
    private BigDecimal paid;     // Total amount paid by this user
    private BigDecimal share;    // Total share of this user
    private BigDecimal balance;  // Net balance (positive = owed money, negative = owes money)

    // Default constructor
    public Balance() {
    }

    public Balance(String userId, String userName, BigDecimal paid, BigDecimal share, BigDecimal balance) {
        this.userId = userId;
        this.userName = userName;
        this.paid = paid;
        this.share = share;
        this.balance = balance;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getPaid() {
        return paid;
    }

    public void setPaid(BigDecimal paid) {
        this.paid = paid;
    }

    public BigDecimal getShare() {
        return share;
    }

    public void setShare(BigDecimal share) {
        this.share = share;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Balance{user='" + userName + "', paid=" + paid + ", share=" + share + ", balance=" + balance + "}";
    }
}
