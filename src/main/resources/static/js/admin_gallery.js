// Preview selected images
document.getElementById('galleryImages').addEventListener('change', function (e) {
    const files = e.target.files;
    const previewContainer = document.createElement('div');
    previewContainer.className = 'mt-3 d-flex flex-wrap gap-2';

    // Remove existing preview if present
    const existingPreview = document.querySelector('.preview-container');
    if (existingPreview) existingPreview.remove();

    // Add preview-container class for future reference
    previewContainer.classList.add('preview-container');

    // Iterate through selected files and create preview
    Array.from(files).forEach(file => {
        if (file.type.startsWith('image/')) {
            const reader = new FileReader();
            const preview = document.createElement('div');
            preview.className = 'preview-image';
            preview.style.width = '100px';
            preview.style.height = '100px';
            preview.style.overflow = 'hidden';
            preview.style.borderRadius = '8px';
            preview.style.border = '1px solid #dee2e6';

            reader.onload = function (e) {
                const img = document.createElement('img');
                img.src = e.target.result;
                img.style.width = '100%';
                img.style.height = '100%';
                img.style.objectFit = 'cover';
                preview.appendChild(img);
            };

            reader.readAsDataURL(file);
            previewContainer.appendChild(preview);
        }
    });

    // Insert preview after the file input field
    e.target.parentElement.appendChild(previewContainer);
});

// Form submission and validation with "Are you sure?" confirmation
document.querySelector('.form-upload').addEventListener('submit', function (e) {
    e.preventDefault();  // Prevent default form submission

    const imageInput = document.getElementById('galleryImages');
    const descInput = document.getElementById('imageDescription');

    if (!imageInput.files.length) {
        Swal.fire({
            icon: 'warning',
            title: 'No Image Selected',
            text: 'Please select an image to upload.',
        });
        return;
    }

    if (!descInput.value.trim()) {
        Swal.fire({
            icon: 'warning',
            title: 'Description Required',
            text: 'Please enter a description for the image.',
        });
        return;
    }

    // Show confirmation dialog before proceeding with the form submission
    Swal.fire({
        title: 'Are you sure?',
        text: 'You are about to upload this image. Do you want to proceed?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, upload it!',
        cancelButtonText: 'No, cancel!',
        reverseButtons: true
    }).then(result => {
        if (result.isConfirmed) {
            // Proceed with form submission if confirmed
            const formData = new FormData();
            formData.append('image', imageInput.files[0]);
            formData.append('description', descInput.value.trim());
            // Add CSRF token
            const csrfToken = document.querySelector('input[name="_csrf"]')?.value || '';
            formData.append('_csrf', csrfToken);

            fetch('/gallery/upload', {
                method: 'POST',
                body: formData
            })
                .then(res => {
                    return res.text().then(message => {
                        if (!res.ok) {
                            throw new Error(message || 'Upload failed');
                        }
                        return message;
                    });
                })
                .then(message => {
                    Swal.fire({
                        icon: 'success',
                        title: 'Uploaded',
                        text: message || 'Image uploaded successfully!'
                    }).then(() => {
                        location.reload();  // Reload the page after the alert is closed
                    });

                    // Optionally reset the form
                    document.querySelector('.form-upload').reset();
                })
                .catch(error => {
                    Swal.fire({
                        icon: 'error',
                        title: 'Upload Error',
                        text: error.message || 'Something went wrong.'
                    });
                });
        } else {
            // Do nothing if the user cancels the action
            console.log('Upload cancelled');
        }
    });
});


function deleteImage(imageId) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    Swal.fire({
        title: 'Are you sure?',
        text: "This image will be deleted permanently!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`/gallery/delete/${imageId}`, {
                method: 'DELETE',
                headers: {
                    [csrfHeader]: csrfToken
                }
            })
                .then(response => {
                    if (!response.ok) throw new Error('Failed to delete image');
                    return response.text();
                })
                .then(message => {
                    Swal.fire('Deleted!', message, 'success').then(() => location.reload());
                })
                .catch(err => {
                    Swal.fire('Error!', err.message, 'error');
                });
        }
    });
}
