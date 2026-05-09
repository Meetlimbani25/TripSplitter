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
import dao.ExpenseDAO;
import dao.TripDAO;
import model.Expense;
import model.User;
import utility.ValidationUtil;

/**
 * AddExpenseServlet - Handles adding expenses to a trip.
 * GET: Show add expense form
 * POST: Process add expense form
 */
@WebServlet("/addExpense")
public class AddExpenseServlet extends HttpServlet {

    private ExpenseDAO expenseDAO;
    private TripDAO tripDAO;

    @Override
    public void init() throws ServletException {
        expenseDAO = new ExpenseDAO();
        tripDAO = new TripDAO();
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
            // Get trip members for "Paid By" dropdown
            List<User> members = tripDAO.getTripMembers(tripId);
            request.setAttribute("members", members);
            request.setAttribute("tripId", tripId);

            request.getRequestDispatcher("/addExpense.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        String tripId = request.getParameter("tripId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String amountStr = request.getParameter("amount");
        String paidBy = request.getParameter("paidBy");
        String expenseDate = request.getParameter("expenseDate");
        String category = request.getParameter("category");

        // Validate inputs
        boolean hasError = false;

        if (ValidationUtil.isEmpty(title)) {
            request.setAttribute("titleError", "Expense title is required");
            hasError = true;
        }

        if (!ValidationUtil.isValidAmount(amountStr)) {
            request.setAttribute("amountError", "Valid amount is required");
            hasError = true;
        }

        if (ValidationUtil.isEmpty(paidBy)) {
            request.setAttribute("paidByError", "Please select who paid");
            hasError = true;
        }

        if (ValidationUtil.isEmpty(expenseDate)) {
            request.setAttribute("dateError", "Expense date is required");
            hasError = true;
        }

        if (hasError) {
            try {
                List<User> members = tripDAO.getTripMembers(tripId);
                request.setAttribute("members", members);
            } catch (SQLException e) {
                // Ignore
            }
            request.setAttribute("tripId", tripId);
            request.setAttribute("expTitle", title);
            request.setAttribute("expAmount", amountStr);
            request.getRequestDispatcher("/addExpense.jsp").forward(request, response);
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            Expense expense = new Expense(tripId, title, amount, paidBy, expenseDate, category, user.getId());

            if (expenseDAO.addExpense(expense)) {
                response.sendRedirect(request.getContextPath() + "/tripDetails?id=" + tripId);
            } else {
                request.setAttribute("error", "Failed to add expense");
                request.setAttribute("tripId", tripId);
                request.getRequestDispatcher("/addExpense.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.setAttribute("tripId", tripId);
            request.getRequestDispatcher("/addExpense.jsp").forward(request, response);
        }
    }
}
