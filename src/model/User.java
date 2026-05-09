package model;

/**
 * User Model - Represents a user in the TripSplitter system.
 * Maps to the 'users' table in the database.
 */
public class User {

    private String id;          // UUID primary key
    private String name;        // User's full name
    private String email;       // User's email (unique)
    private String password;    // User's password (hashed)
    private String avatarUrl;   // Avatar URL
    private String createdAt;   // Account creation timestamp

    // Default constructor
    public User() {
    }

    // Constructor for registration
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Full constructor
    public User(String id, String name, String email, String avatarUrl, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
}
