<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TripSplitter - Register</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body class="auth-page">
    <div class="auth-container">
        <div class="auth-card">
            <!-- Logo -->
            <div class="text-center mb-4">
                <div class="auth-logo">
                    <i class="bi bi-airplane-fill"></i>
                </div>
                <h2 class="auth-title">Create Account</h2>
                <p class="text-muted">Join TripSplitter today</p>
            </div>

            <!-- Messages -->
            <%@ include file="includes/messages.jsp" %>

            <!-- Register Form -->
            <form action="${pageContext.request.contextPath}/register" method="POST" novalidate>
                <div class="mb-3">
                    <label for="name" class="form-label">Full Name</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-person"></i></span>
                        <input type="text" class="form-control" id="name" name="name"
                               placeholder="Enter your full name" required
                               value="<%= request.getAttribute("name") != null ? request.getAttribute("name") : "" %>">
                    </div>
                    <% if (request.getAttribute("nameError") != null) { %>
                        <div class="text-danger small mt-1"><%= request.getAttribute("nameError") %></div>
                    <% } %>
                </div>

                <div class="mb-3">
                    <label for="email" class="form-label">Email Address</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                        <input type="email" class="form-control" id="email" name="email"
                               placeholder="Enter your email" required
                               value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>">
                    </div>
                    <% if (request.getAttribute("emailError") != null) { %>
                        <div class="text-danger small mt-1"><%= request.getAttribute("emailError") %></div>
                    <% } %>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label">Password</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-lock"></i></span>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Minimum 6 characters" required>
                    </div>
                    <% if (request.getAttribute("passwordError") != null) { %>
                        <div class="text-danger small mt-1"><%= request.getAttribute("passwordError") %></div>
                    <% } %>
                </div>

                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Confirm Password</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                               placeholder="Re-enter your password" required>
                    </div>
                    <% if (request.getAttribute("confirmError") != null) { %>
                        <div class="text-danger small mt-1"><%= request.getAttribute("confirmError") %></div>
                    <% } %>
                </div>

                <button type="submit" class="btn btn-primary w-100 btn-lg mb-3">
                    <i class="bi bi-person-plus me-2"></i> Register
                </button>
            </form>

            <div class="text-center">
                <p class="mb-0">Already have an account?
                    <a href="${pageContext.request.contextPath}/login" class="auth-link">Login here</a>
                </p>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
