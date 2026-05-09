<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.User" %>
<%
    String tripId = (String) request.getAttribute("tripId");
    List<User> members = (List<User>) request.getAttribute("members");
%>
<%@ include file="includes/header.jsp" %>

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h3 class="card-title mb-4">
                        <i class="bi bi-plus-circle me-2 text-success"></i>Add Expense
                    </h3>

                    <!-- Messages -->
                    <%@ include file="includes/messages.jsp" %>

                    <form action="${pageContext.request.contextPath}/addExpense" method="POST" novalidate>
                        <input type="hidden" name="tripId" value="<%= tripId %>">

                        <div class="mb-3">
                            <label for="title" class="form-label">Expense Title <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="title" name="title"
                                   placeholder="e.g., Hotel, Food, Petrol" required
                                   value="<%= request.getAttribute("expTitle") != null ? request.getAttribute("expTitle") : "" %>">
                            <% if (request.getAttribute("titleError") != null) { %>
                                <div class="text-danger small mt-1"><%= request.getAttribute("titleError") %></div>
                            <% } %>
                        </div>

                        <div class="mb-3">
                            <label for="amount" class="form-label">Amount (Rs.) <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-currency-rupee"></i></span>
                                <input type="number" class="form-control" id="amount" name="amount"
                                       placeholder="0.00" step="0.01" min="0.01" required
                                       value="<%= request.getAttribute("expAmount") != null ? request.getAttribute("expAmount") : "" %>">
                            </div>
                            <% if (request.getAttribute("amountError") != null) { %>
                                <div class="text-danger small mt-1"><%= request.getAttribute("amountError") %></div>
                            <% } %>
                        </div>

                        <div class="mb-3">
                            <label for="paidBy" class="form-label">Paid By <span class="text-danger">*</span></label>
                            <select class="form-select" id="paidBy" name="paidBy" required>
                                <option value="">-- Select Member --</option>
                                <% if (members != null) { %>
                                    <% for (User member : members) { %>
                                        <option value="<%= member.getId() %>"><%= member.getName() %></option>
                                    <% } %>
                                <% } %>
                            </select>
                            <% if (request.getAttribute("paidByError") != null) { %>
                                <div class="text-danger small mt-1"><%= request.getAttribute("paidByError") %></div>
                            <% } %>
                        </div>

                        <div class="mb-3">
                            <label for="expenseDate" class="form-label">Expense Date <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="expenseDate" name="expenseDate" required>
                            <% if (request.getAttribute("dateError") != null) { %>
                                <div class="text-danger small mt-1"><%= request.getAttribute("dateError") %></div>
                            <% } %>
                        </div>

                        <div class="mb-3">
                            <label for="category" class="form-label">Category</label>
                            <select class="form-select" id="category" name="category">
                                <option value="general">General</option>
                                <option value="hotel">Hotel</option>
                                <option value="food">Food</option>
                                <option value="transport">Transport</option>
                                <option value="tickets">Tickets</option>
                                <option value="shopping">Shopping</option>
                                <option value="petrol">Petrol</option>
                                <option value="other">Other</option>
                            </select>
                        </div>

                        <div class="alert alert-info">
                            <i class="bi bi-info-circle me-2"></i>
                            Expense will be split equally among all trip members.
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-success btn-lg">
                                <i class="bi bi-check-circle me-2"></i>Add Expense
                            </button>
                            <a href="${pageContext.request.contextPath}/tripDetails?id=<%= tripId %>"
                               class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-2"></i>Back to Trip
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>
