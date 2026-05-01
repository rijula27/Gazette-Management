// serch function
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




// Pdf file size validation

document.getElementById('editPdfFile').addEventListener('change', (event) => {
    const file = event.target.files[0];
    const maxSize = 20 * 1024 * 1024; // 20MB in bytes

    if (file) {
        if (file.size > maxSize) {
            Swal.fire({
                icon: 'error',
                title: 'File Too Large',
                text: 'The file size exceeds the 20MB limit.',
            });
            event.target.value = ''; // Clear the file input
            document.getElementById('fileLabel').textContent = 'No file selected';
        } else {
            document.getElementById('fileLabel').textContent = file.name;
        }
    }
});










// delete
var deleteId;
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



// send script
var sendId;
function sendBack_Creator(el) {
    sendId = $(el).data('id');
    Swal.fire({
        title: "Confirm Send?",
        text: "Do you want to send this to the creator?",
        icon: "question",
        showCancelButton: true,
        allowOutsideClick: false,
        allowEscapeKey: false,
        confirmButtonColor: "#28a745",
        cancelButtonColor: "#d33",
        confirmButtonText: "Yes, send it!"
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                title: "Sending...",
                text: "Please wait while the document is being sent.",
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();

                    $.ajax({
                        url: '/publisher/sendBack_tender_Creator/' + sendId,
                        type: 'GET', // Use 'POST' if needed
                        success: function (response) {
                            Swal.fire({
                                title: "Send!",
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
                                text: "Failed to send the tender.",
                                icon: "error"
                            });
                        }
                    });
                }
            });
        }
    });
}




// published script
var publishedId;
function published(el) {
    publishedId = $(el).data('id');
    Swal.fire({
        title: "Confirm Published?",
        text: "Do you want to published this tender?",
        icon: "question",
        showCancelButton: true,
        allowOutsideClick: false,
        allowEscapeKey: false,
        confirmButtonColor: "#28a745",
        cancelButtonColor: "#d33",
        confirmButtonText: "Yes, publish it!"
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                title: "Publishing...",
                text: "Please wait while the document is being publish.",
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();

                    $.ajax({
                        url: '/publisher/published_tender/' + publishedId,
                        type: 'GET', // Use 'POST' if needed
                        success: function (response) {
                            Swal.fire({
                                title: "Published!",
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
                                text: "Failed to published the tender.",
                                icon: "error"
                            });
                        }
                    });
                }
            });
        }
    });
}



// edit open
function openEditModal(el) {
    var tId = $(el).data('id');
    var title = $(el).data('title')


    $('#tenderId').val(tId);
    $('#title').val(title);


    $('#editPdfFile').val('');

    $('#editModal').modal('show');
}



// confirm edit
function confirmEdit() {
    $('#editModal').modal('hide');
    Swal.fire({
        title: "Confirm Edit?",
        text: "Do you want to update this tender?",
        icon: "info",
        showCancelButton: true,
        allowOutsideClick: false,
        allowEscapeKey: false,
        confirmButtonColor: "#17a2b8",
        cancelButtonColor: "#d33",
        confirmButtonText: "Yes, update it!"
    }).then((result) => {
        if (result.isConfirmed) {
            submitEditForm();
        }
    });
}



// submit edit
function submitEditForm() {
    var formData = new FormData($('#editForm')[0]);

    Swal.fire({
        title: "Updating...",
        text: "Please wait while the tender is being updated.",
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
            $.ajax({
                url: '/tender/edit',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    Swal.fire({
                        title: "Success!",
                        text: "tender updated successfully!",
                        icon: "success",
                        timer: 2000,
                        showConfirmButton: false
                    }).then(() => {
                        location.reload();
                    });
                },
                error: function (xhr) {
                    Swal.fire({
                        title: "Error!",
                        text: "Failed to update tender: " + xhr.responseText,
                        icon: "error"
                    });
                }
            });
        }
    });
}


function formatDate(dateString) {
    const date = new Date(dateString);
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
}



// =========================
// ACTION BUTTON EVENTS
// =========================
$(document).ready(function () {

    // Edit
    $(document).on('click', '.edit-btn', function (e) {
        e.preventDefault();
        openEditModal(this);
    });

    // Delete
    $(document).on('click', '.delete-btn', function (e) {
        e.preventDefault();
        confirmDelete(this);
    });

    // Send Back
    $(document).on('click', '.sendback-btn', function (e) {
        e.preventDefault();
        sendBack_Creator(this);
    });

    // Publish
    $(document).on('click', '.publish-btn', function (e) {
        e.preventDefault();
        published(this);
    });

    $(document).on('click', '.confirm-edit-btn', function () {
        e.preventDefault();
        confirmEdit();
    });

});