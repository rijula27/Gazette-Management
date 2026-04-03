// Search bar script
$(document).ready(function () {
    const allowedPattern = /^[0-9IXVABPDFixvabpdf\s.\-]*$/;

    $('#searchInput').on('input', function () {
        let value = $(this).val();

        // Clean input
        if (!allowedPattern.test(value)) {
            value = value.replace(/[^0-9IXVABPDFixvabpdf\s.\-]/g, '');
            $(this).val(value);
        }

        let cleanedValue = value.trim().toLowerCase();

        if (cleanedValue === '') {
            $('#noResultsRow').show();
            resetTable();
            resetPagination();
            return;
        }

        let hasResults = false;
        $('.dashboard-table tbody tr').each(function () {
            if (this.id === 'noResultsRow') return;

            let title = $(this).find('td').eq(1).text().toLowerCase();
            let date = $(this).find('td').eq(2).text().toLowerCase();
            let part = $(this).find('td').eq(3).text().toLowerCase();

            if (title.includes(cleanedValue) || date.includes(cleanedValue) || part.includes(cleanedValue)) {
                $(this).show();
                hasResults = true;
            } else {
                $(this).hide();
            }
        });

        $('#noResultsRow').toggle(!hasResults);
    });

    $('#searchInput').on('keydown', function (e) {
        if (e.key === 'Backspace') {
            setTimeout(() => {
                let val = $(this).val().trim();
                if (val === '') {
                    $('#noResultsRow').show();
                    resetTable();
                    resetPagination();
                }
            }, 0);
        }
    });

    function resetTable() {
        $('.dashboard-table tbody tr').each(function () {
            if (this.id !== 'noResultsRow') {
                $(this).show();
            }
        });
        $('#noResultsRow').hide();
    }

    function resetPagination() {
        // Replace this with actual pagination logic
        console.log('Pagination reset');
    }
});







// Client-side pagination (adjust rows per page as needed)
$(document).ready(function () {
    const rowsPerPage = 5;
    let currentPage = 0;
    const paginationControls = $('#paginationControls');

    function showPage(rows, page) {
        rows.hide();
        rows.slice(page * rowsPerPage, (page + 1) * rowsPerPage).show();
    }

    function createPagination(filteredRows) {
        const pageCount = Math.ceil(filteredRows.length / rowsPerPage);
        paginationControls.empty();

        if (pageCount > 1) {
            const prevBtn = $('<button class="page-link">Previous</button>');
            const nextBtn = $('<button class="page-link">Next</button>');

            function updateButtons() {
                prevBtn.prop('disabled', currentPage === 0);
                nextBtn.prop('disabled', currentPage === pageCount - 1);
            }

            prevBtn.on('click', function () {
                if (currentPage > 0) {
                    currentPage--;
                    showPage(filteredRows, currentPage);
                    updateButtons();
                }
            });

            nextBtn.on('click', function () {
                if (currentPage < pageCount - 1) {
                    currentPage++;
                    showPage(filteredRows, currentPage);
                    updateButtons();
                }
            });

            paginationControls.append(prevBtn, nextBtn);
            updateButtons();
        }
    }

    function applyPagination() {
        const visibleRows = $('#gazetteTableBody tr:visible').not('#noResultsRow');
        if (visibleRows.length > 0) {
            $('#noResultsRow').hide();
            currentPage = 0;
            showPage(visibleRows, currentPage);
            createPagination(visibleRows);
        } else {
            $('#noResultsRow').show();
            paginationControls.empty();
        }
    }

    applyPagination();






    // Search input
    $('#searchInput').on('input', function () {
        let value = $(this).val().replace(/[^0-9IXVABPDFixvabpdf\s.\-]/g, '');
        $(this).val(value);
        const cleanedValue = value.trim().toLowerCase();

        if (cleanedValue === '') {
            $('#noResultsRow').show();
            $('#gazetteTableBody tr').not('#noResultsRow').show();
            applyPagination();
            return;
        }

        let hasResults = false;
        $('#gazetteTableBody tr').each(function () {
            if (this.id === 'noResultsRow') return;

            let title = $(this).find('td').eq(1).text().toLowerCase();
            let date = $(this).find('td').eq(2).text().toLowerCase();
            let part = $(this).find('td').eq(3).text().toLowerCase();

            const match = title.includes(cleanedValue) || date.includes(cleanedValue) || part.includes(cleanedValue);
            $(this).toggle(match);
            if (match) hasResults = true;
        });

        $('#noResultsRow').toggle(!hasResults);
        applyPagination(); // Reapply pagination to new filtered results
    });

    $('#searchInput').on('keydown', function (e) {
        if (e.key === 'Backspace') {
            setTimeout(() => {
                if ($(this).val().trim() === '') {
                    $('#noResultsRow').show();
                    $('#gazetteTableBody tr').not('#noResultsRow').show();
                    applyPagination();
                }
            }, 0);
        }
    });
});



var deleteId; // Store the gazette ID for deletion

function confirmDelete(el) {
deleteId = $(el).data('id');

// Show SweetAlert confirmation dialog
Swal.fire({
    title: "Are you sure?",
    text: "You won't be able to revert this!",
    icon: "warning",
    showCancelButton: true,
    allowOutsideClick: false,
    allowEscapeKey: false,
    confirmButtonColor: "#d33",
    cancelButtonColor: "#3085d6",
    confirmButtonText: "Yes, delete it!",
    cancelButtonText: "Cancel"
}).then((result) => {
    if (result.isConfirmed) {
        if (!deleteId || !/^\d+$/.test(String(deleteId))) {
            console.error("Invalid delete ID:", deleteId);
            return;
        }

        const numericId = parseInt(deleteId, 10); 
        const redirectUrl = "/admin/admin_delete/" + numericId;

        // Show success animation with slight delay before redirection
        Swal.fire({
            title: "Deleted!",
            text: "The gazette has been successfully removed.",
            icon: "success",
            timer: 1500,
            showConfirmButton: false,
            didClose: () => {
                // Redirect after SweetAlert closes
                window.location.replace(redirectUrl);
            }
        });
    }
});
}






function toggleDropdown(button) {
    const menu = button.nextElementSibling;
    const isVisible = menu.style.display === 'block';
    
    // Close all dropdowns
    document.querySelectorAll('.dropdown-menu').forEach(drop => drop.style.display = 'none');

    // Toggle current
    menu.style.display = isVisible ? 'none' : 'block';
}

// Optional: Close dropdowns if clicked outside
window.onclick = function(event) {
    if (!event.target.matches('.dropdown-toggle')) {
        document.querySelectorAll('.dropdown-menu').forEach(drop => drop.style.display = 'none');
    }
};