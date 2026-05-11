package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.TripDAO;
import dao.ExpenseDAO;
import dao.SettlementDAO;
import model.Trip;
import model.Expense;
import model.Balance;
import model.User;
import model.Settlement;

/**
 * TripDetailsServlet - Shows trip details including members, expenses, and balances.
 * GET: Display trip details page
 */
@WebServlet("/tripDetails")
public class TripDetailsServlet extends HttpServlet {

    private TripDAO tripDAO;
    private ExpenseDAO expenseDAO;
    private SettlementDAO settlementDAO;

    @Override
    public void init() throws ServletException {
        tripDAO = new TripDAO();
        expenseDAO = new ExpenseDAO();
        settlementDAO = new SettlementDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tripId = request.getParameter("id");

        if (tripId == null || tripId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            // Get trip details
            Trip trip = tripDAO.getTripById(tripId);
            if (trip == null) {
                request.setAttribute("error", "Trip not found");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }
            request.setAttribute("trip", trip);

            // Get trip members
            List<User> members = tripDAO.getTripMembers(tripId);
            request.setAttribute("members", members);

            // Get trip expenses
            List<Expense> expenses = expenseDAO.getExpensesByTrip(tripId);
            request.setAttribute("expenses", expenses);

            // Get total expenses
            BigDecimal totalExpenses = expenseDAO.getTotalExpenses(tripId);
            request.setAttribute("totalExpenses", totalExpenses);

            // Get balances
            List<Balance> balances = expenseDAO.getTripBalances(tripId);
            request.setAttribute("balances", balances);

            // Get settlements
            List<Settlement> settlements = settlementDAO.getSettlementsByTrip(tripId);
            request.setAttribute("settlements", settlements);

            // Get pending settlements
            List<Settlement> pendingSettlements = settlementDAO.getPendingSettlements(tripId);
            request.setAttribute("pendingSettlements", pendingSettlements);

            // Calculate per-person share
            if (members.size() > 0 && totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal perPerson = totalExpenses.divide(
                    new BigDecimal(members.size()), 2, BigDecimal.ROUND_HALF_UP);
                request.setAttribute("perPerson", perPerson);
            }

            request.getRequestDispatcher("/tripDetails.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/tripDetails.jsp").forward(request, response);
        }
    }
}
