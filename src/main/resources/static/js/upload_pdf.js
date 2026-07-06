//  // File Picker Logic
//  document.getElementById('selectFileBtn').addEventListener('click', () => {
//     document.getElementById('pdfFile').click();
// });

// document.getElementById('pdfFile').addEventListener('change', (event) => {
//     const fileName = event.target.files[0] ? event.target.files[0].name : 'No file selected';
//     document.getElementById('fileLabel').textContent = fileName;
// });





// Pdf file size validation

document.getElementById('pdfFile').addEventListener('change', (event) => {
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





// loading and confirmation
document.getElementById('saveButton').addEventListener('click', function () {
    const title = document.getElementById('title').value;

    if (!validatePdfTitle(title)) {
        return;
    }
    Swal.fire({
        title: 'Are you sure?',
        text: 'Do you want to submit this pdf?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, submit it!'
    }).then((result) => {
        if (result.isConfirmed) {
            // Show loading animation
            Swal.fire({
                title: 'Uploading...',
                text: 'Please wait while your pdf is being uploaded.',
                icon: 'info',
                allowOutsideClick: false,
                showConfirmButton: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            setTimeout(() => {
                document.getElementById('pdfForm').submit();
            }, 1500);
        }
    });
});


// api call display
document.addEventListener("DOMContentLoaded", () => {
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

    const newUrl = window.location.origin + window.location.pathname;
    window.history.replaceState({}, document.title, newUrl);
});



function validatePdfTitle(title) {

    title = title.trim();

    if (title.length === 0) {
        Swal.fire("Invalid Title", "PDF title is required.", "warning");
        return false;
    }

    if (title.length > 150) {
        Swal.fire("Invalid Title", "PDF title cannot exceed 150 characters.", "warning");
        return false;
    }

    // Reject HTML tags
    if (/<[^>]*>/g.test(title)) {
        Swal.fire(
            "Invalid Title",
            "HTML or script tags are not allowed.",
            "warning"
        );
        return false;
    }

    // Allow only expected characters
    const pattern = /^[A-Za-z0-9\s.,()&\/\-_:]+$/;

    if (!pattern.test(title)) {
        Swal.fire(
            "Invalid Title",
            "Title contains invalid characters.",
            "warning"
        );
        return false;
    }

    return true;
}


const titleInput = document.getElementById("title");

titleInput.addEventListener("input", function () {

    let value = this.value;

    value = value.replace(/<[^>]*>/g, "");

    value = value.replace(/[^A-Za-z0-9\s.,()&\/\-_:]/g, "");

    if (value.length > 150) {
        value = value.substring(0, 150);
    }

    this.value = value;
});