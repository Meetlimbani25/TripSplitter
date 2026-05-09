package controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.UserDAO;
import model.User;
import utility.ValidationUtil;
import utility.PasswordUtil;

/**
 * RegisterServlet - Handles user registration.
 * POST: Process registration form
 * GET: Show registration page
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate inputs
        boolean hasError = false;

        if (ValidationUtil.isEmpty(name)) {
            request.setAttribute("nameError", "Name is required");
            hasError = true;
        }

        if (ValidationUtil.isEmpty(email)) {
            request.setAttribute("emailError", "Email is required");
            hasError = true;
        } else if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("emailError", "Invalid email format");
            hasError = true;
        }

        if (ValidationUtil.isEmpty(password)) {
            request.setAttribute("passwordError", "Password is required");
            hasError = true;
        } else if (!ValidationUtil.isValidPassword(password)) {
            request.setAttribute("passwordError", "Password must be at least 6 characters");
            hasError = true;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("confirmError", "Passwords do not match");
            hasError = true;
        }

        if (hasError) {
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        try {
            // Check if email already exists
            if (userDAO.emailExists(email)) {
                request.setAttribute("emailError", "Email already registered");
                request.setAttribute("name", name);
                request.setAttribute("email", email);
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            // Hash password and create user
            String hashedPassword = PasswordUtil.hashPassword(password);
            User user = new User(name, email, hashedPassword);

            if (userDAO.registerUser(user)) {
                request.setAttribute("success", "Registration successful! Please login.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
