
  
  let formDataJson = {};
  const form = document.getElementById("addSectionForm");


  form.addEventListener("submit", function (e) {
    e.preventDefault();
  
   
  const formData = new FormData(form);
  formDataJson = {
    sectionHeading: formData.get("sectionHeading")?.trim(),
    sectionContent: formData.get("sectionParagraph")?.trim()
  };



  const headingRegex = /^[a-zA-Z0-9\- ]+$/;

  if (!formDataJson.sectionHeading || formDataJson.sectionHeading.length >= 150) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid heading',
      text: 'Heading maximum character limit is 150'
    });
    return;
  }
  
  if (!headingRegex.test(formDataJson.sectionHeading)) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid heading format',
      text: 'Heading can only contain letters, numbers, hyphens, and spaces.'
    });
    return;
  }
  

  const allowedRegex = /^[a-zA-Z0-9./\-@#$&!():;,_? \n\r]+$/;

  if (!formDataJson.sectionContent || formDataJson.sectionContent.length >= 2000) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Content',
      text: 'Content maximum character limit is 2000'
    });
    return;
  }
  
  if (!allowedRegex.test(formDataJson.sectionContent)) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Characters',
      text: 'Content contains unsupported characters.'
    });
    return;
  }
  



   // Show confirmation dialog
  Swal.fire({
    title: 'Are you sure?',
    text: "Do you want to save this contact?",
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Yes, Save',
    cancelButtonText: 'Cancel'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        const csrfToken = document.querySelector('input[name="_csrf"]')?.value || '';
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
        const res = await fetch("/about/save", {
          method: "POST",
          headers: { 
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken
          },
          body: JSON.stringify(formDataJson)
        });

        const message = await res.text();

        if (res.ok) {
          Swal.fire({
            icon: 'success',
            title: 'Success!',
            text: message
          }).then(() => {
            form.reset();
            location.reload();
          });
        } else {
          Swal.fire({
            icon: 'error',
            title: 'Failed!',
            text: message
          });
        }
      } catch (err) {
        Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: 'Request failed!'
        });
      }
    }
  });
});




// Confirm Delete
function confirmDelete(el) {
  const deleteId = $(el).data('id');

  Swal.fire({
    title: 'Are you sure?',
    text: "You won't be able to revert this!",
    icon: 'warning',
    showCancelButton: true,
    allowOutsideClick: false,
    allowEscapeKey: false,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Yes, delete it!',
    cancelButtonText: 'Cancel'
  }).then((result) => {
    if (result.isConfirmed) {
       if (!deleteId || !/^\d+$/.test(String(deleteId))) {
            console.error("Invalid delete ID:", deleteId);
            return;
        }

        const numericId = parseInt(deleteId, 10); 
        const redirectUrl = "/about/delete/" + numericId;


      // Show tick animation
      Swal.fire({
        title: 'Deleted!',
        text: 'Section has been removed.',
        icon: 'success',
        timer: 1500,
        showConfirmButton: false,
        willClose: () => {
          // Redirect after animation
          window.location.replace(redirectUrl);
        }
      });
    }
  });
}






function enableEditing(btn) {
  const container = btn.closest('.content-display');
  const title = container.querySelector('.section-title');
  const content = container.querySelector('.section-content');
  const updateBtn = container.querySelector('.update-btn');
  const editBtn = container.querySelector('.edit-btn');
  const deleteBtn = container.querySelector(".delete-btn");
  const cancelBtn = container.querySelector(".cancel-btn");




  // Enable editing
  title.contentEditable = true;
  content.contentEditable = true;

  // Add visual cue
  title.style.border = "1px solid #ccc";
  content.style.border = "1px solid #ccc";

  // Show the update button
  updateBtn.style.display = "inline-block";
  cancelBtn.style.display = "inline-block";

  editBtn.style.display = "none";
  deleteBtn.style.display = "none";



  // Optional: change button text to "Editing..." or disable it
  btn.disabled = true;
}

function submitUpdate(btn) {
  const container = btn.closest('.content-display');
  const title = container.querySelector('.section-title').innerText.trim();
  const content = container.querySelector('.section-content').innerText.trim();
  const id = btn.getAttribute("data-id");

  // Regex patterns
  const headingPattern = /^[a-zA-Z0-9\- ]+$/;
  const contentPattern = /^[a-zA-Z0-9./\-@#$&!():;,_? \n\r]+$/;

  // Validation checks
  if (!title || title.length > 150 || !headingPattern.test(title)) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Heading',
      text: 'Heading must be under 150 characters and only include letters, numbers, spaces, and hyphens.'
    });
    return;
  }

  if (!content || content.length > 2000 || !contentPattern.test(content)) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Content',
      text: 'Content must be under 2000 characters and contain only allowed characters.'
    });
    return;
  }

  const payload = {
    sectionId: id,
    sectionHeading: title,
    sectionContent: content
  };

  const csrfToken = document.querySelector('input[name="_csrf"]')?.value || '';
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
  fetch('/about/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      [csrfHeader]: csrfToken
    },
    body: JSON.stringify(payload)
  }).then(response => {
    if (response.ok) {
      Swal.fire({
        icon: 'success',
        title: 'Updated!',
        text: 'Content has been successfully updated.',
        timer: 1500,
        showConfirmButton: false
      }).then(() => {
        location.reload();
      });
    } else {
      Swal.fire({
        icon: 'error',
        title: 'Update Failed',
        text: 'Something went wrong. Please try again later.'
      });
    }
  }).catch(error => {
    Swal.fire({
      icon: 'error',
      title: 'Error',
      text: 'Failed to send request. Check your internet connection.'
    });
  });
}

function cancelUpdate(btn) {
  location.reload();
}