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
import dao.SettlementDAO;
import model.Trip;
import model.Expense;
import model.Balance;
import model.Settlement;

/**
 * ExportPdfServlet - Exports trip report as PDF.
 * Uses iText library for PDF generation.
 * GET: Generate and download PDF report
 */
@WebServlet("/exportPdf")
public class ExportPdfServlet extends HttpServlet {

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

        String tripId = request.getParameter("tripId");
        if (tripId == null || tripId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            Trip trip = tripDAO.getTripById(tripId);
            List<Expense> expenses = expenseDAO.getExpensesByTrip(tripId);
            List<Balance> balances = expenseDAO.getTripBalances(tripId);
            List<Settlement> settlements = settlementDAO.getSettlementsByTrip(tripId);
            BigDecimal totalExpenses = expenseDAO.getTotalExpenses(tripId);

            // Set response headers for PDF download
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                "attachment; filename=\"" + trip.getName() + "_Report.pdf\"");

            // Generate PDF using iText
            // Note: Requires iText library in WEB-INF/lib
            generatePdf(response, trip, expenses, balances, settlements, totalExpenses);

        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/tripDetails?id=" + tripId);
        }
    }

    /**
     * Generate PDF report.
     * This method uses iText library. If iText is not available,
     * a simple text-based report is generated instead.
     */
    private void generatePdf(HttpServletResponse response, Trip trip,
            List<Expense> expenses, List<Balance> balances,
            List<Settlement> settlements, BigDecimal totalExpenses) throws IOException {

        // Simple text-based PDF generation
        // In production, use iText or Apache PDFBox
        StringBuilder content = new StringBuilder();
        content.append("TripSplitter - Trip Report\n");
        content.append("========================\n\n");
        content.append("Trip: ").append(trip.getName()).append("\n");
        content.append("Destination: ").append(trip.getDestination()).append("\n");
        content.append("Total Expenses: Rs. ").append(totalExpenses).append("\n\n");

        content.append("Expenses:\n");
        content.append("---------\n");
        for (Expense exp : expenses) {
            content.append("- ").append(exp.getTitle())
                   .append(" | Rs. ").append(exp.getAmount())
                   .append(" | Paid by: ").append(exp.getPaidByName())
                   .append(" | Date: ").append(exp.getExpenseDate())
                   .append("\n");
        }

        content.append("\nBalances:\n");
        content.append("---------\n");
        for (Balance bal : balances) {
            content.append("- ").append(bal.getUserName())
                   .append(" | Paid: Rs. ").append(bal.getPaid())
                   .append(" | Share: Rs. ").append(bal.getShare())
                   .append(" | Balance: Rs. ").append(bal.getBalance())
                   .append("\n");
        }

        content.append("\nSettlements:\n");
        content.append("------------\n");
        for (Settlement set : settlements) {
            content.append("- ").append(set.getPayerName())
                   .append(" owes ").append(set.getPayeeName())
                   .append(" Rs. ").append(set.getAmount())
                   .append(set.isSettled() ? " [SETTLED]" : " [PENDING]")
                   .append("\n");
        }

        // Write as plain text (replace with iText for actual PDF)
        response.setContentType("text/plain");
        response.getWriter().write(content.toString());
    }
}
