package model;

/**
 * Trip Model - Represents a trip in the TripSplitter system.
 * Maps to the 'trips' table in the database.
 */
public class Trip {

    private String id;           // UUID primary key
    private String name;         // Trip name
    private String description;  // Trip description
    private String destination;  // Trip destination
    private String startDate;    // Trip start date
    private String endDate;      // Trip end date
    private String inviteCode;   // Unique invite code for joining
    private String createdBy;    // UUID of the user who created the trip
    private String createdAt;   // Trip creation timestamp
    private String createdByName; // Name of the creator (for display)
    private int memberCount;     // Number of members in the trip

    // Default constructor
    public Trip() {
    }

    // Constructor for creating a new trip
    public Trip(String name, String description, String destination, String startDate, String endDate, String createdBy) {
        this.name = name;
        this.description = description;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
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

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    @Override
    public String toString() {
        return "Trip{id='" + id + "', name='" + name + "', destination='" + destination + "'}";
    }
}
