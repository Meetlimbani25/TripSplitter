package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Singleton class for managing database connections.
 * Uses JDBC to connect to MySQL. Configure DB_URL, DB_USER, and DB_PASSWORD
 * for hosted providers such as Aiven.
 */
public class DBConnection {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        // Railway provides MYSQL_URL (mysql://...) but JDBC needs (jdbc:mysql://...)
        String railwayUrl = System.getenv("MYSQL_URL");
        if (railwayUrl != null) {
            URL = "jdbc:" + railwayUrl;
            USERNAME = System.getenv("MYSQLUSER");
            PASSWORD = System.getenv("MYSQLPASSWORD");
        } else {
            // Fallback to custom vars or localhost.
            // For Aiven MySQL, use:
            // jdbc:mysql://HOST:PORT/DB_NAME?sslMode=REQUIRED&allowPublicKeyRetrieval=true
            URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/trip_splitter";
            USERNAME = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
            PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "";
        }
    }

    private static DBConnection instance;
    private Connection connection;

    // Private constructor for singleton pattern
    private DBConnection() {
        try {
            Class.forName(DRIVER);
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + e.getMessage());
            throw new RuntimeException("Database driver not found", e);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }

    /**
     * Get the singleton instance of DBConnection.
     * @return DBConnection instance
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Get a new database connection.
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
    }

    /**
     * Close a connection safely.
     * @param conn The connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
