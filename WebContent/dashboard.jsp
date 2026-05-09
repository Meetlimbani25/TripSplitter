<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.math.BigDecimal" %>
<%@ page import="model.User, model.Trip" %>
<%
    User currentUser = (User) session.getAttribute("user");
    List<Trip> trips = (List<Trip>) request.getAttribute("trips");
    int tripCount = (Integer) request.getAttribute("tripCount");
    int expenseCount = (Integer) request.getAttribute("expenseCount");
    BigDecimal totalExpenses = (BigDecimal) request.getAttribute("totalExpenses");
%>
<%@ include file="includes/header.jsp" %>

<div class="container-fluid py-4">
    <!-- Welcome Section -->
    <div class="row mb-4">
        <div class="col-12">
            <div class="welcome-card">
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                    <div>
                        <h1 class="welcome-title">Welcome back, <%= currentUser.getName() %>!</h1>
                        <p class="welcome-subtitle">Manage your trips and expenses all in one place.</p>
                    </div>
                    <div class="d-flex gap-2">
                        <a href="${pageContext.request.contextPath}/createTrip" class="btn btn-primary btn-lg">
                            <i class="bi bi-plus-circle me-2"></i> New Trip
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Statistics Cards -->
    <div class="row g-4 mb-4">
        <div class="col-md-4">
            <div class="stat-card stat-card-primary">
                <div class="stat-icon">
                    <i class="bi bi-airplane"></i>
                </div>
                <div class="stat-content">
                    <h3 class="stat-value"><%= tripCount %></h3>
                    <p class="stat-label">Total Trips</p>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="stat-card stat-card-success">
                <div class="stat-icon">
                    <i class="bi bi-receipt"></i>
                </div>
                <div class="stat-content">
                    <h3 class="stat-value"><%= expenseCount %></h3>
                    <p class="stat-label">Total Expenses</p>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="stat-card stat-card-warning">
                <div class="stat-icon">
                    <i class="bi bi-currency-rupee"></i>
                </div>
                <div class="stat-content">
                    <h3 class="stat-value"><%= totalExpenses != null ? totalExpenses.toString() : "0" %></h3>
                    <p class="stat-label">Total Amount</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Join Trip Section -->
    <div class="row mb-4">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title"><i class="bi bi-person-plus me-2"></i>Join a Trip</h5>
                    <p class="text-muted">Enter the invite code shared by a trip member to join their trip.</p>
                    <form action="${pageContext.request.contextPath}/joinTrip" method="POST" class="row g-3">
                        <div class="col-md-6">
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-key"></i></span>
                                <input type="text" class="form-control" name="inviteCode"
                                       placeholder="Enter invite code (e.g., ABC12345)" required>
                                <button type="submit" class="btn btn-outline-primary">Join Trip</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Messages -->
    <%@ include file="includes/messages.jsp" %>

    <!-- My Trips -->
    <div class="row">
        <div class="col-12">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4><i class="bi bi-suitcase-lg me-2"></i>My Trips</h4>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <% if (trips != null && !trips.isEmpty()) { %>
            <% for (Trip trip : trips) { %>
                <div class="col-md-6 col-lg-4">
                    <div class="card trip-card shadow-sm h-100">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <h5 class="card-title mb-0"><%= trip.getName() %></h5>
                                <span class="badge bg-primary"><%= trip.getMemberCount() %> members</span>
                            </div>
                            <% if (trip.getDestination() != null && !trip.getDestination().isEmpty()) { %>
                                <p class="text-muted mb-2">
                                    <i class="bi bi-geo-alt me-1"></i><%= trip.getDestination() %>
                                </p>
                            <% } %>
                            <% if (trip.getDescription() != null && !trip.getDescription().isEmpty()) { %>
                                <p class="card-text small"><%= trip.getDescription() %></p>
                            <% } %>
                            <div class="trip-dates mb-3">
                                <% if (trip.getStartDate() != null) { %>
                                    <span class="badge bg-light text-dark">
                                        <i class="bi bi-calendar me-1"></i><%= trip.getStartDate() %>
                                    </span>
                                <% } %>
                                <% if (trip.getEndDate() != null) { %>
                                    <span class="text-muted mx-1">to</span>
                                    <span class="badge bg-light text-dark">
                                        <%= trip.getEndDate() %>
                                    </span>
                                <% } %>
                            </div>
                            <div class="d-flex align-items-center text-muted small mb-3">
                                <i class="bi bi-person me-1"></i>
                                Created by <%= trip.getCreatedByName() %>
                            </div>
                            <div class="invite-code-box mb-3">
                                <small class="text-muted">Invite Code:</small>
                                <code class="invite-code"><%= trip.getInviteCode() %></code>
                            </div>
                        </div>
                        <div class="card-footer bg-transparent border-top-0">
                            <a href="${pageContext.request.contextPath}/tripDetails?id=<%= trip.getId() %>"
                               class="btn btn-primary w-100">
                                <i class="bi bi-eye me-2"></i>View Details
                            </a>
                        </div>
                    </div>
                </div>
            <% } %>
        <% } else { %>
            <div class="col-12">
                <div class="empty-state text-center py-5">
                    <i class="bi bi-suitcase-lg empty-icon"></i>
                    <h4>No Trips Yet</h4>
                    <p class="text-muted">Create your first trip or join an existing one using an invite code.</p>
                    <a href="${pageContext.request.contextPath}/createTrip" class="btn btn-primary btn-lg">
                        <i class="bi bi-plus-circle me-2"></i>Create Your First Trip
                    </a>
                </div>
            </div>
        <% } %>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>
