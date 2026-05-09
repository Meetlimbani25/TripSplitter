<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="includes/header.jsp" %>

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h3 class="card-title mb-4">
                        <i class="bi bi-plus-circle me-2 text-primary"></i>Create New Trip
                    </h3>

                    <!-- Messages -->
                    <%@ include file="includes/messages.jsp" %>

                    <form action="${pageContext.request.contextPath}/createTrip" method="POST" novalidate>
                        <div class="mb-3">
                            <label for="name" class="form-label">Trip Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" name="name"
                                   placeholder="e.g., Goa Trip 2024" required
                                   value="<%= request.getAttribute("tripName") != null ? request.getAttribute("tripName") : "" %>">
                        </div>

                        <div class="mb-3">
                            <label for="destination" class="form-label">Destination</label>
                            <input type="text" class="form-control" id="destination" name="destination"
                                   placeholder="e.g., Goa, India"
                                   value="<%= request.getAttribute("tripDestination") != null ? request.getAttribute("tripDestination") : "" %>">
                        </div>

                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="3"
                                      placeholder="Brief description of the trip..."><%= request.getAttribute("tripDescription") != null ? request.getAttribute("tripDescription") : "" %></textarea>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label for="startDate" class="form-label">Start Date <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" id="startDate" name="startDate" required
                                       value="<%= request.getAttribute("tripStartDate") != null ? request.getAttribute("tripStartDate") : "" %>">
                            </div>
                            <div class="col-md-6">
                                <label for="endDate" class="form-label">End Date <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" id="endDate" name="endDate" required
                                       value="<%= request.getAttribute("tripEndDate") != null ? request.getAttribute("tripEndDate") : "" %>">
                            </div>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="bi bi-check-circle me-2"></i>Create Trip
                            </button>
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-2"></i>Back to Dashboard
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>
