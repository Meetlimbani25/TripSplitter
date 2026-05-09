<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.math.BigDecimal" %>
<%@ page import="model.User, model.Trip, model.Expense, model.Balance, model.Settlement" %>
<%
    Trip trip = (Trip) request.getAttribute("trip");
    List<User> members = (List<User>) request.getAttribute("members");
    List<Expense> expenses = (List<Expense>) request.getAttribute("expenses");
    List<Balance> balances = (List<Balance>) request.getAttribute("balances");
    List<Settlement> settlements = (List<Settlement>) request.getAttribute("settlements");
    List<Settlement> pendingSettlements = (List<Settlement>) request.getAttribute("pendingSettlements");
    BigDecimal totalExpenses = (BigDecimal) request.getAttribute("totalExpenses");
    BigDecimal perPerson = (BigDecimal) request.getAttribute("perPerson");
    User currentUser = (User) session.getAttribute("user");
%>
<%@ include file="includes/header.jsp" %>

<div class="container-fluid py-4">
    <!-- Messages -->
    <%@ include file="includes/messages.jsp" %>

    <% if (trip != null) { %>
    <!-- Trip Header -->
    <div class="trip-header-card mb-4">
        <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
            <div>
                <h2 class="mb-1"><i class="bi bi-airplane me-2"></i><%= trip.getName() %></h2>
                <% if (trip.getDestination() != null && !trip.getDestination().isEmpty()) { %>
                    <p class="mb-1"><i class="bi bi-geo-alt me-1"></i><%= trip.getDestination() %></p>
                <% } %>
                <p class="mb-0 text-muted small">
                    <%= trip.getStartDate() %> to <%= trip.getEndDate() %> |
                    Created by <%= trip.getCreatedByName() %>
                </p>
                <div class="mt-2">
                    <span class="badge bg-light text-dark">Invite Code: <code><%= trip.getInviteCode() %></code></span>
                </div>
            </div>
            <div class="d-flex gap-2 flex-wrap">
                <a href="${pageContext.request.contextPath}/addExpense?tripId=<%= trip.getId() %>"
                   class="btn btn-success">
                    <i class="bi bi-plus-circle me-1"></i> Add Expense
                </a>
                <a href="${pageContext.request.contextPath}/settle?tripId=<%= trip.getId() %>"
                   class="btn btn-warning">
                    <i class="bi bi-cash-stack me-1"></i> Settle Up
                </a>
                <a href="${pageContext.request.contextPath}/exportPdf?tripId=<%= trip.getId() %>"
                   class="btn btn-outline-danger">
                    <i class="bi bi-file-pdf me-1"></i> PDF
                </a>
                <a href="${pageContext.request.contextPath}/exportExcel?tripId=<%= trip.getId() %>"
                   class="btn btn-outline-success">
                    <i class="bi bi-file-earmark-excel me-1"></i> CSV
                </a>
            </div>
        </div>
    </div>

    <!-- Stats Row -->
    <div class="row g-3 mb-4">
        <div class="col-md-3">
            <div class="stat-card stat-card-primary mini">
                <div class="stat-content">
                    <p class="stat-label">Total Expenses</p>
                    <h4 class="stat-value"><%= totalExpenses != null ? totalExpenses.toString() : "0" %></h4>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stat-card stat-card-success mini">
                <div class="stat-content">
                    <p class="stat-label">Members</p>
                    <h4 class="stat-value"><%= members != null ? members.size() : 0 %></h4>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stat-card stat-card-warning mini">
                <div class="stat-content">
                    <p class="stat-label">Per Person Share</p>
                    <h4 class="stat-value"><%= perPerson != null ? perPerson.toString() : "0" %></h4>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stat-card stat-card-info mini">
                <div class="stat-content">
                    <p class="stat-label">Pending Settlements</p>
                    <h4 class="stat-value"><%= pendingSettlements != null ? pendingSettlements.size() : 0 %></h4>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <!-- Members Section -->
        <div class="col-lg-4">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="bi bi-people me-2"></i>Members</h5>
                </div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        <% if (members != null) { %>
                            <% for (User member : members) { %>
                                <li class="list-group-item d-flex align-items-center">
                                    <div class="member-avatar me-3">
                                        <%= member.getName().substring(0, 1).toUpperCase() %>
                                    </div>
                                    <div>
                                        <h6 class="mb-0"><%= member.getName() %></h6>
                                        <small class="text-muted">Member</small>
                                    </div>
                                </li>
                            <% } %>
                        <% } %>
                    </ul>
                </div>
            </div>
        </div>

        <!-- Expenses Section -->
        <div class="col-lg-8">
            <div class="card shadow-sm">
                <div class="card-header bg-white d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="bi bi-receipt me-2"></i>Expenses</h5>
                    <a href="${pageContext.request.contextPath}/addExpense?tripId=<%= trip.getId() %>"
                       class="btn btn-sm btn-success">
                        <i class="bi bi-plus me-1"></i> Add
                    </a>
                </div>
                <div class="card-body p-0">
                    <% if (expenses != null && !expenses.isEmpty()) { %>
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>Title</th>
                                        <th>Amount</th>
                                        <th>Paid By</th>
                                        <th>Date</th>
                                        <th>Category</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (Expense exp : expenses) { %>
                                        <tr>
                                            <td><strong><%= exp.getTitle() %></strong></td>
                                            <td><span class="text-primary fw-bold"><%= exp.getAmount() %></span></td>
                                            <td><%= exp.getPaidByName() %></td>
                                            <td><%= exp.getExpenseDate() %></td>
                                            <td>
                                                <span class="badge bg-light text-dark"><%= exp.getCategory() %></span>
                                            </td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/deleteExpense" method="POST"
                                                      onsubmit="return confirm('Are you sure you want to delete this expense?')">
                                                    <input type="hidden" name="expenseId" value="<%= exp.getId() %>">
                                                    <input type="hidden" name="tripId" value="<%= trip.getId() %>">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } else { %>
                        <div class="text-center py-4">
                            <i class="bi bi-receipt text-muted" style="font-size: 2rem;"></i>
                            <p class="text-muted mt-2">No expenses yet. Add your first expense!</p>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>

    <!-- Balance Sheet Section -->
    <div class="row g-4 mt-1">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="bi bi-calculator me-2"></i>Balance Sheet</h5>
                </div>
                <div class="card-body p-0">
                    <% if (balances != null && !balances.isEmpty()) { %>
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>Member</th>
                                        <th>Total Paid</th>
                                        <th>Share</th>
                                        <th>Balance</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (Balance bal : balances) { %>
                                        <tr>
                                            <td><strong><%= bal.getUserName() %></strong></td>
                                            <td><%= bal.getPaid() %></td>
                                            <td><%= bal.getShare() %></td>
                                            <td>
                                                <% if (bal.getBalance().compareTo(BigDecimal.ZERO) > 0) { %>
                                                    <span class="text-success fw-bold">+<%= bal.getBalance() %></span>
                                                <% } else if (bal.getBalance().compareTo(BigDecimal.ZERO) < 0) { %>
                                                    <span class="text-danger fw-bold"><%= bal.getBalance() %></span>
                                                <% } else { %>
                                                    <span class="text-muted">0.00</span>
                                                <% } %>
                                            </td>
                                            <td>
                                                <% if (bal.getBalance().compareTo(BigDecimal.ZERO) > 0) { %>
                                                    <span class="badge bg-success">Gets back</span>
                                                <% } else if (bal.getBalance().compareTo(BigDecimal.ZERO) < 0) { %>
                                                    <span class="badge bg-danger">Owes</span>
                                                <% } else { %>
                                                    <span class="badge bg-secondary">Settled</span>
                                                <% } %>
                                            </td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } else { %>
                        <div class="text-center py-4">
                            <p class="text-muted">No balance data available.</p>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>

    <!-- Settlements Section -->
    <% if (settlements != null && !settlements.isEmpty()) { %>
    <div class="row g-4 mt-1">
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="bi bi-cash-stack me-2"></i>Settlements</h5>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Payer</th>
                                    <th>Receiver</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Settlement set : settlements) { %>
                                    <tr>
                                        <td><%= set.getPayerName() %></td>
                                        <td><%= set.getPayeeName() %></td>
                                        <td class="fw-bold"><%= set.getAmount() %></td>
                                        <td>
                                            <% if (set.isSettled()) { %>
                                                <span class="badge bg-success">Settled</span>
                                            <% } else { %>
                                                <span class="badge bg-warning text-dark">Pending</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if (!set.isSettled()) { %>
                                                <form action="${pageContext.request.contextPath}/settle" method="POST">
                                                    <input type="hidden" name="action" value="settle">
                                                    <input type="hidden" name="settlementId" value="<%= set.getId() %>">
                                                    <input type="hidden" name="tripId" value="<%= trip.getId() %>">
                                                    <button type="submit" class="btn btn-sm btn-success"
                                                            onclick="return confirm('Mark this as paid?')">
                                                        <i class="bi bi-check me-1"></i>Mark Paid
                                                    </button>
                                                </form>
                                            <% } else { %>
                                                <span class="text-muted small">Completed</span>
                                            <% } %>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <% } %>

    <!-- Delete Trip -->
    <div class="row mt-4">
        <div class="col-12">
            <% if (trip.getCreatedBy().equals(currentUser.getId())) { %>
                <form action="${pageContext.request.contextPath}/deleteTrip" method="POST"
                      onsubmit="return confirm('Are you sure you want to delete this trip? This cannot be undone!')">
                    <input type="hidden" name="tripId" value="<%= trip.getId() %>">
                    <button type="submit" class="btn btn-outline-danger">
                        <i class="bi bi-trash me-2"></i>Delete Trip
                    </button>
                </form>
            <% } %>
        </div>
    </div>

    <% } else { %>
        <div class="text-center py-5">
            <i class="bi bi-exclamation-circle text-muted" style="font-size: 3rem;"></i>
            <h4>Trip not found</h4>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Back to Dashboard</a>
        </div>
    <% } %>
</div>

<%@ include file="includes/footer.jsp" %>
