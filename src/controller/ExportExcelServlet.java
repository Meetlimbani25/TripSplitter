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
import dao.TripDAO;
import dao.ExpenseDAO;
import model.Trip;
import model.Expense;

/**
 * ExportExcelServlet - Exports expenses as Excel/CSV.
 * Uses Apache POI for Excel or simple CSV format.
 * GET: Generate and download Excel/CSV file
 */
@WebServlet("/exportExcel")
public class ExportExcelServlet extends HttpServlet {

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

        String tripId = request.getParameter("tripId");
        if (tripId == null || tripId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            Trip trip = tripDAO.getTripById(tripId);
            List<Expense> expenses = expenseDAO.getExpensesByTrip(tripId);

            // Set response headers for CSV download
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition",
                "attachment; filename=\"" + trip.getName() + "_Expenses.csv\"");

            // Generate CSV content
            StringBuilder csv = new StringBuilder();
            csv.append("Title,Amount,Paid By,Date,Category\n");

            for (Expense exp : expenses) {
                csv.append("\"").append(exp.getTitle()).append("\",");
                csv.append(exp.getAmount()).append(",");
                csv.append("\"").append(exp.getPaidByName()).append("\",");
                csv.append(exp.getExpenseDate()).append(",");
                csv.append("\"").append(exp.getCategory()).append("\"\n");
            }

            response.getWriter().write(csv.toString());

        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/tripDetails?id=" + tripId);
        }
    }
}
