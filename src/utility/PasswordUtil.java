package utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PasswordUtil - Utility class for password hashing.
 * Uses SHA-256 for password hashing.
 * In production, use bcrypt or Argon2.
 */
public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";

    /**
     * Hash a password using SHA-256.
     * @param password The plain text password
     * @return Hashed password as hex string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    /**
     * Verify a password against a hash.
     * @param password The plain text password
     * @param hash The stored hash
     * @return true if password matches
     */
    public static boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
}
