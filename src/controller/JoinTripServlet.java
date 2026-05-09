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
import model.User;
import utility.ValidationUtil;

/**
 * JoinTripServlet - Handles joining a trip using an invite code.
 * POST: Process join trip form
 */
@WebServlet("/joinTrip")
public class JoinTripServlet extends HttpServlet {

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

        String inviteCode = request.getParameter("inviteCode");

        if (ValidationUtil.isEmpty(inviteCode)) {
            request.setAttribute("error", "Invite code is required");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            boolean joined = tripDAO.joinTrip(inviteCode.trim(), user.getId());

            if (joined) {
                request.setAttribute("success", "Successfully joined the trip!");
            } else {
                request.setAttribute("error", "Invalid invite code or already a member");
            }
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}
