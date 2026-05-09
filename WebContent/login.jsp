<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TripSplitter - Login</title>
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
                <h2 class="auth-title">TripSplitter</h2>
                <p class="text-muted">Smart Trip Expense Sharing</p>
            </div>

            <!-- Messages -->
            <%@ include file="includes/messages.jsp" %>

            <!-- Login Form -->
            <form action="${pageContext.request.contextPath}/login" method="POST" novalidate>
                <div class="mb-3">
                    <label for="email" class="form-label">Email Address</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                        <input type="email" class="form-control" id="email" name="email"
                               placeholder="Enter your email" required
                               value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>">
                    </div>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label">Password</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-lock"></i></span>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Enter your password" required>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary w-100 btn-lg mb-3">
                    <i class="bi bi-box-arrow-in-right me-2"></i> Login
                </button>
            </form>

            <div class="text-center">
                <p class="mb-0">Don't have an account?
                    <a href="${pageContext.request.contextPath}/register" class="auth-link">Register here</a>
                </p>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
