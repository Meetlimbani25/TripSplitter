package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import dao.TripDAO;
import dao.ExpenseDAO;
import model.Trip;
import model.User;

/**
 * DashboardServlet - Handles the main dashboard.
 * Shows user's trips, total expenses, and recent activity.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private TripDAO tripDAO;
    private ExpenseDAO expenseDAO;

    @Override
    public void init() throws ServletException {
        tripDAO = new TripDAO();
        expenseDAO = new ExpenseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        try {
            // Get user's trips
            List<Trip> trips = tripDAO.getTripsByUser(user.getId());
            request.setAttribute("trips", trips);

            // Get statistics
            int tripCount = tripDAO.getTripCount(user.getId());
            int expenseCount = expenseDAO.getExpenseCount(user.getId());
            BigDecimal totalExpenses = expenseDAO.getTotalExpensesByUser(user.getId());

            request.setAttribute("tripCount", tripCount);
            request.setAttribute("expenseCount", expenseCount);
            request.setAttribute("totalExpenses", totalExpenses);

            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }
}
