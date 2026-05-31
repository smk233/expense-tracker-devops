/* ============================================================
   EXPENSE TRACKER - MAIN INTERACTIVE JAVASCRIPT
   ============================================================ */

document.addEventListener('DOMContentLoaded', function() {
    // 1. MOBILE SIDEBAR TOGGLE & OVERLAY
    const sidebar = document.getElementById('sidebar');
    const toggleBtn = document.getElementById('sidebarToggleMobile');
    
    if (sidebar && toggleBtn) {
        // Create overlay element dynamically if it doesn't exist
        let overlay = document.querySelector('.sidebar-overlay');
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.className = 'sidebar-overlay';
            document.body.appendChild(overlay);
        }

        // Toggle Sidebar & Overlay
        toggleBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            sidebar.classList.toggle('show');
            overlay.classList.toggle('show');
        });

        // Close sidebar when clicking outside or on overlay
        overlay.addEventListener('click', function() {
            sidebar.classList.remove('show');
            overlay.classList.remove('show');
        });

        // Close sidebar on mobile when resizing to desktop
        window.addEventListener('resize', function() {
            if (window.innerWidth >= 992) {
                sidebar.classList.remove('show');
                overlay.classList.remove('show');
            }
        });
    }

    // 2. AUTO-HIDE ALERT MESSAGES
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            // Check if Bootstrap's alert instance exists and trigger close
            if (typeof bootstrap !== 'undefined' && bootstrap.Alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            } else {
                // Fallback for raw fade/removal
                alert.style.transition = 'opacity 0.5s ease';
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 500);
            }
        }, 5000); // Hide after 5 seconds
    });
});
