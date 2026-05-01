// Search bar script
$(document).ready(function () {
    const allowedPattern = /^[0-9IXVABPDFixvabpdf\s.\-]*$/;


    $('#searchInput').on('input', function () {
        let value = $(this).val();

        // Clean input
        if (!allowedPattern.test(value)) {
            value = value.replace(/[^a-zA-Z0-9_\-\/ ]/g, '').substring(0, 150);
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
            let ref_No = $(this).find('td').eq(2).text().toLowerCase();
            let opening_Date = $(this).find('td').eq(5).text().toLowerCase();

            if (title.includes(cleanedValue) || ref_No.includes(cleanedValue) || opening_Date.includes(cleanedValue)) {
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
// pagination
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
        let value = $(this).val().replace(/[^a-zA-Z0-9_\-\/ ]/g, '').substring(0, 150);
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
            let ref_No = $(this).find('td').eq(2).text().toLowerCase();
            let opening_Date = $(this).find('td').eq(5).text().toLowerCase();

            const match = title.includes(cleanedValue) || ref_No.includes(cleanedValue) || opening_Date.includes(cleanedValue);
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
    Swal.fire({
        title: "Are you sure?",
        text: "This action cannot be undone!",
        icon: "warning",
        showCancelButton: true,
        allowOutsideClick: false,
        allowEscapeKey: false,
        confirmButtonColor: "#d33",
        cancelButtonColor: "#3085d6",
        confirmButtonText: "Yes, delete it!"
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                title: "Deleting...",
                text: "Please wait...",
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();

                    $.ajax({
                        url: '/creator/tender_delete/' + deleteId,
                        type: 'GET',
                        success: function (response) {
                            Swal.fire({
                                title: "Deleted!",
                                text: response.message, // Show success message from server
                                icon: "success",
                                timer: 2000,
                                showConfirmButton: false
                            }).then(() => {
                                location.reload(); // Refresh after success
                            });
                        },
                        error: function (xhr) {
                            Swal.fire({
                                title: "Error!",
                                text: "Failed to delete the tender.",
                                icon: "error"
                            });
                        }
                    });

                }
            });
        }
    });
}





// Dropdown toggle
$(document).on('click', '.dropdown-toggle', function (e) {
    e.preventDefault();
    e.stopPropagation();

    const menu = $(this).next('.dropdown-menu');

    $('.dropdown-menu').not(menu).hide();
    menu.toggle();
});

// Close all dropdowns when clicking outside
$(document).on('click', function () {
    $('.dropdown-menu').hide();
});

// Delete button click
$(document).on('click', '.delete-btn', function (e) {
    e.preventDefault();
    e.stopPropagation();

    confirmDelete(this);
});