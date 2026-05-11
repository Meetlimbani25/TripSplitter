package controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
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

            generatePdf(response, trip, expenses, balances, settlements, totalExpenses);

        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/tripDetails?id=" + tripId);
        }
    }

    /**
     * Generate a simple valid PDF report without external libraries.
     */
    private void generatePdf(HttpServletResponse response, Trip trip,
            List<Expense> expenses, List<Balance> balances,
            List<Settlement> settlements, BigDecimal totalExpenses) throws IOException {

        List<String> lines = new ArrayList<>();
        lines.add("TripSplitter - Trip Report");
        lines.add("==========================");
        lines.add("");
        lines.add("Trip: " + valueOrEmpty(trip.getName()));
        lines.add("Destination: " + valueOrEmpty(trip.getDestination()));
        lines.add("Total Expenses: Rs. " + valueOrZero(totalExpenses));
        lines.add("");

        lines.add("Expenses");
        lines.add("--------");
        for (Expense exp : expenses) {
            addWrappedLine(lines, "- " + valueOrEmpty(exp.getTitle())
                    + " | Rs. " + valueOrZero(exp.getAmount())
                    + " | Paid by: " + valueOrEmpty(exp.getPaidByName())
                    + " | Date: " + exp.getExpenseDate());
        }

        lines.add("");
        lines.add("Balances");
        lines.add("--------");
        for (Balance bal : balances) {
            addWrappedLine(lines, "- " + valueOrEmpty(bal.getUserName())
                    + " | Paid: Rs. " + valueOrZero(bal.getPaid())
                    + " | Share: Rs. " + valueOrZero(bal.getShare())
                    + " | Balance: Rs. " + valueOrZero(bal.getBalance()));
        }

        lines.add("");
        lines.add("Settlements");
        lines.add("-----------");
        for (Settlement set : settlements) {
            addWrappedLine(lines, "- " + valueOrEmpty(set.getPayerName())
                    + " owes " + valueOrEmpty(set.getPayeeName())
                    + " Rs. " + valueOrZero(set.getAmount())
                    + (set.isSettled() ? " [SETTLED]" : " [PENDING]"));
        }

        byte[] pdfBytes = createPdf(lines);
        response.setContentType("application/pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    private byte[] createPdf(List<String> lines) {
        final int linesPerPage = 46;
        int pageCount = Math.max(1, (lines.size() + linesPerPage - 1) / linesPerPage);
        int fontObjectId = 3 + (pageCount * 2);

        List<String> objects = new ArrayList<>();
        objects.add("<< /Type /Catalog /Pages 2 0 R >>");

        StringBuilder kids = new StringBuilder();
        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            kids.append(3 + (pageIndex * 2)).append(" 0 R ");
        }
        objects.add("<< /Type /Pages /Kids [" + kids + "] /Count " + pageCount + " >>");

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            int pageObjectId = 3 + (pageIndex * 2);
            int contentObjectId = pageObjectId + 1;
            objects.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] "
                    + "/Resources << /Font << /F1 " + fontObjectId + " 0 R >> >> "
                    + "/Contents " + contentObjectId + " 0 R >>");

            String stream = createPageStream(lines, pageIndex * linesPerPage,
                    Math.min(lines.size(), (pageIndex + 1) * linesPerPage));
            objects.add("<< /Length " + stream.getBytes(StandardCharsets.ISO_8859_1).length
                    + " >>\nstream\n" + stream + "endstream");
        }

        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
        return serializePdf(objects);
    }

    private String createPageStream(List<String> lines, int start, int end) {
        StringBuilder stream = new StringBuilder();
        stream.append("BT\n/F1 11 Tf\n14 TL\n50 790 Td\n");
        for (int i = start; i < end; i++) {
            stream.append("(").append(escapePdfText(lines.get(i))).append(") Tj\nT*\n");
        }
        stream.append("ET\n");
        return stream.toString();
    }

    private byte[] serializePdf(List<String> objects) {
        StringBuilder pdf = new StringBuilder();
        List<Integer> offsets = new ArrayList<>();

        pdf.append("%PDF-1.4\n");
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.length());
            pdf.append(i + 1).append(" 0 obj\n")
               .append(objects.get(i)).append("\n")
               .append("endobj\n");
        }

        int xrefOffset = pdf.length();
        pdf.append("xref\n0 ").append(objects.size() + 1).append("\n");
        pdf.append("0000000000 65535 f \n");
        for (Integer offset : offsets) {
            pdf.append(String.format("%010d 00000 n \n", offset));
        }
        pdf.append("trailer\n<< /Size ").append(objects.size() + 1)
           .append(" /Root 1 0 R >>\n")
           .append("startxref\n").append(xrefOffset).append("\n%%EOF\n");

        return pdf.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private void addWrappedLine(List<String> lines, String text) {
        int maxLength = 92;
        String remaining = text;
        while (remaining.length() > maxLength) {
            lines.add(remaining.substring(0, maxLength));
            remaining = "  " + remaining.substring(maxLength);
        }
        lines.add(remaining);
    }

    private String escapePdfText(String value) {
        return toPdfSafeText(valueOrEmpty(value))
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private String toPdfSafeText(String value) {
        StringBuilder safe = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            safe.append(ch >= 32 && ch <= 126 ? ch : '?');
        }
        return safe.toString();
    }

    private String valueOrEmpty(Object value) {
        return value == null ? "" : value.toString();
    }

    private String valueOrZero(BigDecimal value) {
        return value == null ? "0.00" : value.toString();
    }
}
