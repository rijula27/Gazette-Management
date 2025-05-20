
    const toggleBtn = document.getElementById('menu-toggle');
    const sidebar = document.getElementById('sidebar');

    toggleBtn.addEventListener('click', () => {
      sidebar.classList.toggle('collapsed');
    });


    document.getElementById('logout-link').addEventListener('click', function(event) {
        event.preventDefault(); // Prevent the default logout
    
        Swal.fire({
            title: 'Are you sure?',
            text: "You will be logged out of your session.",
            icon: 'warning',
            showCancelButton: true,
            allowOutsideClick: false,
            allowEscapeKey: false,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Yes, logout'
        }).then((result) => {
            if (result.isConfirmed) {
                // Redirect to logout if confirmed
                window.location.href = '/logout';
            }
        });
    });