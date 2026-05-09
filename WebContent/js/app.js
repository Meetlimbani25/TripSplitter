/**
 * TripSplitter - Client-side JavaScript
 * Handles form validation, UI interactions, and utility functions.
 */

// Auto-dismiss alerts after 5 seconds
document.addEventListener('DOMContentLoaded', function() {
    // Auto-dismiss alerts
    var alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Set today's date as default for expense date
    var expenseDateInput = document.getElementById('expenseDate');
    if (expenseDateInput && !expenseDateInput.value) {
        expenseDateInput.value = new Date().toISOString().split('T')[0];
    }

    // Form validation
    var forms = document.querySelectorAll('form[novalidate]');
    forms.forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Copy invite code on click
    var inviteCodes = document.querySelectorAll('.invite-code');
    inviteCodes.forEach(function(code) {
        code.style.cursor = 'pointer';
        code.title = 'Click to copy';
        code.addEventListener('click', function() {
            var text = code.textContent.trim();
            navigator.clipboard.writeText(text).then(function() {
                var original = code.textContent;
                code.textContent = 'Copied!';
                code.style.color = '#198754';
                setTimeout(function() {
                    code.textContent = original;
                    code.style.color = '';
                }, 1500);
            });
        });
    });

    // Confirm delete actions
    var deleteForms = document.querySelectorAll('form[onsubmit*="confirm"]');
    deleteForms.forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!confirm('Are you sure you want to delete this? This action cannot be undone.')) {
                event.preventDefault();
            }
        });
    });

    // Password match validation
    var confirmPassword = document.getElementById('confirmPassword');
    var password = document.getElementById('password');
    if (confirmPassword && password) {
        confirmPassword.addEventListener('input', function() {
            if (password.value !== confirmPassword.value) {
                confirmPassword.setCustomValidity('Passwords do not match');
            } else {
                confirmPassword.setCustomValidity('');
            }
        });
    }

    // Number formatting for amounts
    var amountInputs = document.querySelectorAll('input[type="number"][step="0.01"]');
    amountInputs.forEach(function(input) {
        input.addEventListener('blur', function() {
            if (input.value) {
                input.value = parseFloat(input.value).toFixed(2);
            }
        });
    });
});

/**
 * Format a number as Indian currency.
 * @param {number} amount - The amount to format
 * @returns {string} Formatted currency string
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 2
    }).format(amount);
}

/**
 * Show a toast notification.
 * @param {string} message - The message to display
 * @param {string} type - Alert type (success, danger, warning, info)
 */
function showToast(message, type) {
    type = type || 'info';
    var alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-' + type + ' alert-dismissible fade show position-fixed';
    alertDiv.style.cssText = 'top: 80px; right: 20px; z-index: 9999; min-width: 300px;';
    alertDiv.innerHTML = message +
        '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
    document.body.appendChild(alertDiv);

    setTimeout(function() {
        var bsAlert = new bootstrap.Alert(alertDiv);
        bsAlert.close();
    }, 3000);
}
