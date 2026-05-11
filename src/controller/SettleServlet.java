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
import jakarta.servlet.http.HttpSession;
import dao.SettlementDAO;
import dao.TripDAO;
import dao.ExpenseDAO;
import model.Balance;
import model.Settlement;
import model.User;

/**
 * SettleServlet - Handles payment settlements.
 * GET: Show settlement page
 * POST: Process settlement (mark as paid or create new settlement)
 */
@WebServlet("/settle")
public class SettleServlet extends HttpServlet {

    private SettlementDAO settlementDAO;
    private TripDAO tripDAO;
    private ExpenseDAO expenseDAO;

    @Override
    public void init() throws ServletException {
        settlementDAO = new SettlementDAO();
        tripDAO = new TripDAO();
        expenseDAO = new ExpenseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tripId = request.getParameter("tripId");
        if (tripId == null || tripId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            // Get balances for the trip
            List<Balance> balances = expenseDAO.getTripBalances(tripId);
            request.setAttribute("balances", balances);

            // Get pending settlements
            List<Settlement> pendingSettlements = settlementDAO.getPendingSettlements(tripId);
            request.setAttribute("pendingSettlements", pendingSettlements);

            // Get all settlements
            List<Settlement> allSettlements = settlementDAO.getSettlementsByTrip(tripId);
            request.setAttribute("allSettlements", allSettlements);

            // Get trip members
            List<User> members = tripDAO.getTripMembers(tripId);
            request.setAttribute("members", members);

            request.setAttribute("tripId", tripId);
            request.getRequestDispatcher("/balance.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/balance.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String tripId = request.getParameter("tripId");

        if ("settle".equals(action)) {
            // Mark an existing settlement as paid
            String settlementId = request.getParameter("settlementId");
            try {
                if (settlementDAO.settlePayment(settlementId)) {
                    request.setAttribute("success", "Payment settled successfully!");
                } else {
                    request.setAttribute("error", "Failed to settle payment");
                }
            } catch (SQLException e) {
                request.setAttribute("error", "Database error: " + e.getMessage());
            }
        } else if ("create".equals(action)) {
            // Create a new settlement
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute("user");

            String payerId = request.getParameter("payerId");
            String payeeId = request.getParameter("payeeId");
            String amountStr = request.getParameter("amount");

            try {
                BigDecimal amount = new BigDecimal(amountStr);
                Settlement settlement = new Settlement(tripId, payerId, payeeId, amount);

                if (settlementDAO.createSettlement(settlement)) {
                    request.setAttribute("success", "Settlement recorded successfully!");
                } else {
                    request.setAttribute("error", "Failed to record settlement");
                }
            } catch (SQLException e) {
                request.setAttribute("error", "Database error: " + e.getMessage());
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid amount");
            }
        }

        // Redirect back to the balance page
        response.sendRedirect(request.getContextPath() + "/settle?tripId=" + tripId);
    }
}
