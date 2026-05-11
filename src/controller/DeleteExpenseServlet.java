package controller;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.ExpenseDAO;

/**
 * DeleteExpenseServlet - Handles deleting an expense.
 * POST: Delete an expense
 */
@WebServlet("/deleteExpense")
public class DeleteExpenseServlet extends HttpServlet {

    private ExpenseDAO expenseDAO;

    @Override
    public void init() throws ServletException {
        expenseDAO = new ExpenseDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String expenseId = request.getParameter("expenseId");
        String tripId = request.getParameter("tripId");

        if (expenseId == null || expenseId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            if (expenseDAO.deleteExpense(expenseId)) {
                request.setAttribute("success", "Expense deleted successfully");
            } else {
                request.setAttribute("error", "Failed to delete expense");
            }

            if (tripId != null && !tripId.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/tripDetails?id=" + tripId);
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }

        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}
