package controller;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.TripDAO;
import model.User;

/**
 * DeleteTripServlet - Handles deleting a trip.
 * POST: Delete a trip (only creator can delete)
 */
@WebServlet("/deleteTrip")
public class DeleteTripServlet extends HttpServlet {

    private TripDAO tripDAO;

    @Override
    public void init() throws ServletException {
        tripDAO = new TripDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        String tripId = request.getParameter("tripId");

        if (tripId == null || tripId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            if (tripDAO.deleteTrip(tripId, user.getId())) {
                request.setAttribute("success", "Trip deleted successfully");
            } else {
                request.setAttribute("error", "Failed to delete trip. Only the creator can delete.");
            }
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}
