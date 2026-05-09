package utility;

/**
 * ValidationUtil - Utility class for input validation.
 * Provides static methods for validating user input.
 */
public class ValidationUtil {

    /**
     * Check if a string is null or empty.
     * @param value The string to check
     * @return true if null or empty
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Validate email format.
     * @param email The email to validate
     * @return true if valid format
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }

    /**
     * Validate password strength.
     * Minimum 6 characters required.
     * @param password The password to validate
     * @return true if valid
     */
    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) return false;
        return password.length() >= 6;
    }

    /**
     * Validate name - only letters and spaces allowed.
     * @param name The name to validate
     * @return true if valid
     */
    public static boolean isValidName(String name) {
        if (isEmpty(name)) return false;
        return name.trim().length() >= 2;
    }

    /**
     * Sanitize input to prevent XSS.
     * Removes HTML tags and special characters.
     * @param input The input to sanitize
     * @return Sanitized string
     */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    /**
     * Validate amount - must be a positive number.
     * @param amountStr The amount string to validate
     * @return true if valid positive number
     */
    public static boolean isValidAmount(String amountStr) {
        if (isEmpty(amountStr)) return false;
        try {
            double amount = Double.parseDouble(amountStr);
            return amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate date format (YYYY-MM-DD).
     * @param dateStr The date string to validate
     * @return true if valid format
     */
    public static boolean isValidDate(String dateStr) {
        if (isEmpty(dateStr)) return false;
        String regex = "^\\d{4}-\\d{2}-\\d{2}$";
        return dateStr.matches(regex);
    }
}
