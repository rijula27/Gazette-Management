// === Form Validation Function ===
function validateTenderForm() {
    const title = document.getElementById('title').value.trim();
    const refNo = document.getElementById('ref_No').value.trim();
    const announcementDate = new Date(document.getElementById('announcement_Date').value);
    const lastDate = new Date(document.getElementById('last_Date').value);
    const openingDate = new Date(document.getElementById('opening_Date').value);
    const keywords = document.getElementById('keywords').value.trim();
    const fileInput = document.getElementById('pdfFile');
    const announcementDateObj = new Date(announcementDate);
    const openingDateObj = new Date(openingDate);
    const lastDateObj = new Date(lastDate);
    const file = fileInput.files[0];

    if (!title || title.length > 150) {
        Swal.fire('Invalid Title', 'Please enter a valid tender title (max 150 characters).', 'warning');
        return false;
    }

    // const refPattern = /^[A-Za-z0-9_\-\s\/.]{1,100}$/;
    if (!refNo || refNo.length > 100) {
        Swal.fire('Invalid Reference Number', 'Please enter a valid tender reference number (max 100 characters).', 'warning');
        return false;
    }



    const today = new Date();
    today.setHours(0, 0, 0, 0);

    // if (announcementDate >= today) {
    //     Swal.fire('Invalid Date', 'Announcement date cannot be in the future.', 'warning');
    //     return false;
    // }
    if (lastDateObj < announcementDateObj) {
        Swal.fire('Invalid Date', 'Last date for submission must be after the announcement date.', 'warning');
        return false;
    }
    if (openingDateObj < lastDateObj) {
        Swal.fire('Invalid Date', 'Opening date must be same or after the last date for submission.', 'warning');
        return false;
    }

    if (!file) {
        Swal.fire('File Required', 'Please select a PDF file to upload.', 'warning');
        return false;
    }
    if (file.size > 20 * 1024 * 1024) {
        Swal.fire('File Too Large', 'The selected file exceeds the 20MB limit.', 'warning');
        return false;
    }

    if (keywords.length > 0 && !/^([a-zA-Z0-9\s]+,?)*$/.test(keywords)) {
        Swal.fire('Invalid Keywords', 'Keywords must be comma-separated words only.', 'warning');
        return false;
    }

    return true;
}

// === Main Logic ===
document.addEventListener("DOMContentLoaded", () => {
    // === File Picker Logic ===
    const fileInput = document.getElementById('pdfFile');
    const fileLabel = document.getElementById('fileLabel');
    const maxSize = 20 * 1024 * 1024; // 20MB

    document.getElementById('selectFileBtn').addEventListener('click', () => {
        fileInput.click();
    });

    fileInput.addEventListener('change', (event) => {
        const file = event.target.files[0];
        if (file) {
            if (file.size > maxSize) {
                Swal.fire({
                    icon: 'error',
                    title: 'File Too Large',
                    text: 'The file size exceeds the 20MB limit.',
                });
                event.target.value = '';
                fileLabel.textContent = 'No file selected';
            } else {
                fileLabel.textContent = file.name;
            }
        } else {
            fileLabel.textContent = 'No file selected';
        }
    });


    // === Submit Logic with Validation and Confirmation ===
    document.getElementById('saveTender').addEventListener('click', function () {
        if (!validateTenderForm()) return;

        Swal.fire({
            title: 'Are you sure?',
            text: 'Do you want to submit this tender?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, submit it!'
        }).then((result) => {
            if (result.isConfirmed) {
                // Show loading
                Swal.fire({
                    title: 'Uploading...',
                    text: 'Please wait while your tender is being uploaded.',
                    icon: 'info',
                    allowOutsideClick: false,
                    showConfirmButton: false,
                    didOpen: () => {
                        Swal.showLoading();
                    }
                });

                const formData = new FormData();
                formData.append("tender", new Blob([JSON.stringify({
                    title: document.getElementById('title').value,
                    ref_No: document.getElementById('ref_No').value,
                    announcement_Date: document.getElementById('announcement_Date').value,
                    last_Date: document.getElementById('last_Date').value,
                    opening_Date: document.getElementById('opening_Date').value,
                    keywords: document.getElementById('keywords').value
                })], { type: 'application/json' }));

                formData.append("pdfFile", document.getElementById('pdfFile').files[0]);

                // Add CSRF token
                const csrfToken = document.querySelector('input[name="_csrf"]')?.value || '';
                formData.append('_csrf', csrfToken);

                setTimeout(() => {
                    fetch('/tender/uploadTender', {
                        method: 'POST',
                        body: formData
                    })
                        .then(response => response.json()) // Parse the response as JSON
                        .then(data => {
                            if (data.message) { // Check if a success message is present
                                Swal.fire({
                                    title: 'Upload Successful!',
                                    icon: 'success',
                                    timer: 3000,
                                    showConfirmButton: false
                                }).then(() => {
                                    // Reset form fields after success
                                    document.getElementById('tender_form').reset(); // Reset form inputs
                                    fileLabel.textContent = 'No file selected'; // Reset file label
                                });
                            } else {
                                // If response contains an error message
                                Swal.fire({
                                    title: 'Upload Failed!',
                                    icon: 'error',
                                    text: data.error || 'Something went wrong while uploading.', // Show backend error or default message
                                    confirmButtonColor: '#d33'
                                });
                            }
                        })
                        .catch(error => {
                            console.error("Error:", error);
                            Swal.fire({
                                title: 'Upload Error!',
                                text: error.message,
                                icon: 'error'
                            });
                        });
                }, 1500);


            }
        });
    });

    // === Display Success/Error from URL Parameters ===
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('success')) {
        Swal.fire({
            title: 'Upload Successful!',
            text: urlParams.get('success'),
            icon: 'success',
            confirmButtonColor: '#28a745',
            timer: 3000,
            showConfirmButton: false
        });
    }

    if (urlParams.has('error')) {
        Swal.fire({
            title: 'Upload Failed!',
            text: urlParams.get('error'),
            icon: 'error',
            confirmButtonColor: '#d33'
        });
    }

    // Clean up URL
    const newUrl = window.location.origin + window.location.pathname;
    window.history.replaceState({}, document.title, newUrl);
});
