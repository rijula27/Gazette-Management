document.addEventListener('DOMContentLoaded', function() {
    const sidebar = document.getElementById('sidebar');
    const sidebarToggle = document.getElementById('sidebar-toggle');
    const sidebarClose = document.getElementById('sidebar-close');

    // Toggle sidebar on desktop
    sidebarToggle.addEventListener('click', function() {
        sidebar.classList.toggle('open');
    });

    // Close sidebar on mobile
    sidebarClose.addEventListener('click', function() {
        sidebar.classList.remove('open');
    });

    // Close sidebar when clicking outside on mobile
    document.addEventListener('click', function(event) {
        const isClickInsideSidebar = sidebar.contains(event.target);
        const isToggleButton = sidebarToggle.contains(event.target);

        if (!isClickInsideSidebar && !isToggleButton && window.innerWidth <= 768) {
            sidebar.classList.remove('open');
        }
    });

    // Responsive handling
    window.addEventListener('resize', function() {
        if (window.innerWidth > 768) {
            sidebar.classList.remove('open');
        }
    });


    const moreActionsBtns = document.querySelectorAll('.more-actions-btn');

    moreActionsBtns.forEach(btn => {
        btn.addEventListener('click', function(event) {
            event.stopPropagation();
            
            // Close any other open dropdowns
            document.querySelectorAll('.dropdown-menu').forEach(menu => {
                if (menu !== this.nextElementSibling) {
                    menu.classList.remove('show');
                }
            });

            // Toggle current dropdown
            const dropdown = this.nextElementSibling;
            dropdown.classList.toggle('show');
        });
    });

    // Close dropdowns when clicking outside
    document.addEventListener('click', function(event) {
        const dropdowns = document.querySelectorAll('.dropdown-menu');
        dropdowns.forEach(dropdown => {
            if (dropdown.classList.contains('show') && 
                !dropdown.closest('.dropdown-container').contains(event.target)) {
                dropdown.classList.remove('show');
            }
        });
    });

});
