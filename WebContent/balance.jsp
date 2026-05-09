<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.math.BigDecimal" %>
<%@ page import="model.User, model.Balance, model.Settlement" %>
<%
    String tripId = (String) request.getAttribute("tripId");
    List<Balance> balances = (List<Balance>) request.getAttribute("balances");
    List<Settlement> pendingSettlements = (List<Settlement>) request.getAttribute("pendingSettlements");
    List<Settlement> allSettlements = (List<Settlement>) request.getAttribute("allSettlements");
    List<User> members = (List<User>) request.getAttribute("members");
    User currentUser = (User) session.getAttribute("user");
%>
<%@ include file="includes/header.jsp" %>

<div class="container py-4">
    <!-- Messages -->
    <%@ include file="includes/messages.jsp" %>

    <h3 class="mb-4"><i class="bi bi-calculator me-2"></i>Balance & Settlement</h3>

    <!-- Balance Sheet -->
    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white">
            <h5 class="mb-0"><i class="bi bi-scale me-2"></i>Who Owes Whom</h5>
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
                                    <td>Rs. <%= bal.getPaid() %></td>
                                    <td>Rs. <%= bal.getShare() %></td>
                                    <td>
                                        <% if (bal.getBalance().compareTo(BigDecimal.ZERO) > 0) { %>
                                            <span class="text-success fw-bold">+Rs. <%= bal.getBalance() %></span>
                                        <% } else if (bal.getBalance().compareTo(BigDecimal.ZERO) < 0) { %>
                                            <span class="text-danger fw-bold">-Rs. <%= bal.getBalance().abs() %></span>
                                        <% } else { %>
                                            <span class="text-muted">Rs. 0.00</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <% if (bal.getBalance().compareTo(BigDecimal.ZERO) > 0) { %>
                                            <span class="badge bg-success">Gets Back</span>
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

    <!-- Create Settlement -->
    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white">
            <h5 class="mb-0"><i class="bi bi-plus-circle me-2"></i>Record Settlement</h5>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/settle" method="POST">
                <input type="hidden" name="action" value="create">
                <input type="hidden" name="tripId" value="<%= tripId %>">

                <div class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label">Payer (owes money)</label>
                        <select class="form-select" name="payerId" required>
                            <option value="">-- Select --</option>
                            <% if (members != null) { %>
                                <% for (User member : members) { %>
                                    <option value="<%= member.getId() %>"><%= member.getName() %></option>
                                <% } %>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Receiver (is owed money)</label>
                        <select class="form-select" name="payeeId" required>
                            <option value="">-- Select --</option>
                            <% if (members != null) { %>
                                <% for (User member : members) { %>
                                    <option value="<%= member.getId() %>"><%= member.getName() %></option>
                                <% } %>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Amount (Rs.)</label>
                        <input type="number" class="form-control" name="amount"
                               step="0.01" min="0.01" required placeholder="0.00">
                    </div>
                    <div class="col-md-1 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="bi bi-check"></i>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Pending Settlements -->
    <% if (pendingSettlements != null && !pendingSettlements.isEmpty()) { %>
    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white">
            <h5 class="mb-0"><i class="bi bi-clock me-2"></i>Pending Settlements</h5>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>Payer</th>
                            <th>Receiver</th>
                            <th>Amount</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Settlement set : pendingSettlements) { %>
                            <tr>
                                <td><%= set.getPayerName() %></td>
                                <td><%= set.getPayeeName() %></td>
                                <td class="fw-bold">Rs. <%= set.getAmount() %></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/settle" method="POST">
                                        <input type="hidden" name="action" value="settle">
                                        <input type="hidden" name="settlementId" value="<%= set.getId() %>">
                                        <input type="hidden" name="tripId" value="<%= tripId %>">
                                        <button type="submit" class="btn btn-sm btn-success"
                                                onclick="return confirm('Mark this payment as settled?')">
                                            <i class="bi bi-check-circle me-1"></i>Mark as Paid
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <% } %>

    <!-- Settlement History -->
    <% if (allSettlements != null && !allSettlements.isEmpty()) { %>
    <div class="card shadow-sm">
        <div class="card-header bg-white">
            <h5 class="mb-0"><i class="bi bi-clock-history me-2"></i>Settlement History</h5>
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
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Settlement set : allSettlements) { %>
                            <tr>
                                <td><%= set.getPayerName() %></td>
                                <td><%= set.getPayeeName() %></td>
                                <td>Rs. <%= set.getAmount() %></td>
                                <td>
                                    <% if (set.isSettled()) { %>
                                        <span class="badge bg-success">Settled</span>
                                    <% } else { %>
                                        <span class="badge bg-warning text-dark">Pending</span>
                                    <% } %>
                                </td>
                                <td><%= set.getCreatedAt() != null ? set.getCreatedAt().substring(0, 10) : "" %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <% } %>

    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/tripDetails?id=<%= tripId %>" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-2"></i>Back to Trip
        </a>
    </div>
</div>

<%@ include file="includes/footer.jsp" %>
