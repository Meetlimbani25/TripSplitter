package controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import dao.TripDAO;
import model.Trip;
import model.User;
import utility.ValidationUtil;

/**
 * CreateTripServlet - Handles creating new trips.
 * GET: Show create trip form
 * POST: Process create trip form
 */
@WebServlet("/createTrip")
public class CreateTripServlet extends HttpServlet {

    private TripDAO tripDAO;

    @Override
    public void init() throws ServletException {
        tripDAO = new TripDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/createTrip.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String destination = request.getParameter("destination");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        // Validate inputs
        if (ValidationUtil.isEmpty(name)) {
            request.setAttribute("error", "Trip name is required");
            preserveFormData(request, name, description, destination, startDate, endDate);
            request.getRequestDispatcher("/createTrip.jsp").forward(request, response);
            return;
        }

        if (ValidationUtil.isEmpty(startDate) || ValidationUtil.isEmpty(endDate)) {
            request.setAttribute("error", "Start and end dates are required");
            preserveFormData(request, name, description, destination, startDate, endDate);
            request.getRequestDispatcher("/createTrip.jsp").forward(request, response);
            return;
        }

        try {
            Trip trip = new Trip(name, description, destination, startDate, endDate, user.getId());
            String tripId = tripDAO.createTrip(trip);

            if (tripId != null) {
                response.sendRedirect(request.getContextPath() + "/tripDetails?id=" + tripId);
            } else {
                request.setAttribute("error", "Failed to create trip");
                preserveFormData(request, name, description, destination, startDate, endDate);
                request.getRequestDispatcher("/createTrip.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            preserveFormData(request, name, description, destination, startDate, endDate);
            request.getRequestDispatcher("/createTrip.jsp").forward(request, response);
        }
    }

    private void preserveFormData(HttpServletRequest request, String name, String description,
                                   String destination, String startDate, String endDate) {
        request.setAttribute("tripName", name);
        request.setAttribute("tripDescription", description);
        request.setAttribute("tripDestination", destination);
        request.setAttribute("tripStartDate", startDate);
        request.setAttribute("tripEndDate", endDate);
    }
}
