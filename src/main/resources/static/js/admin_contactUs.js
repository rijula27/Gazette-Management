// Close dropdown on outside click
window.onclick = function (event) {
  if (!event.target.matches('.dropdown-toggle')) {
    document.querySelectorAll(".dropdown-menu").forEach(d => d.style.display = "none");
  }
}

// Toggle Dropdown
function toggleDropdown(button) {
  const allDropdowns = document.querySelectorAll(".dropdown-menu");
  allDropdowns.forEach(menu => {
    if (menu !== button.nextElementSibling) {
      menu.style.display = "none";
    }
  });
  const menu = button.nextElementSibling;
  menu.style.display = (menu.style.display === "block") ? "none" : "block";
}



// Open Add Creator Modal
function openModal() {
  document.getElementById("creatorModal").style.display = "block";
  document.getElementById("modalTitle").innerText = "Add New Creator";
}

// Open Add Creator Modal
function closeAddModal() {
  document.getElementById("creatorModal").style.display = "none";
  // document.getElementById("modalTitle").innerText = "Add New Creator";
}
// ADD CREATOR SUBMIT
let formDataJson = {};
const form = document.getElementById("contactForm");
//   const modal_content = document.getElementById("modal-content");
//   const responseDiv = document.getElementById('responseMessage');

form.addEventListener("submit", function (e) {
  e.preventDefault();


  const formData = new FormData(form);
  formDataJson = {
    contactTable: formData.get("contactList")?.trim(),
    name: formData.get("userName")?.trim(),
    designation: formData.get("designation")?.trim(),
    stdCode: formData.get("stdCode")?.trim(),
    phno: formData.get("phone")?.trim(),
    mobile: formData.get("mobile")?.trim()
  };

  if (!formDataJson.name || formDataJson.name.length >= 30) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Name',
      text: 'Username maximum character limit is 30'
    });
    return;
  }

  if (!formDataJson.designation || formDataJson.designation.length >= 30) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Designation',
      text: 'Designation maximum character limit is 30'
    });
    return;
  }


  if (!formDataJson.stdCode || formDataJson.stdCode.length >= 8) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid STD code',
      text: 'STD code maximum character limit is 8'
    });
    return;
  }

  if (!formDataJson.phno || formDataJson.phno.length >= 10) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Number',
      text: 'Phone number maximum character limit is 12'
    });
    return;
  }

  if (formDataJson.mobile.length >= 12) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid mobile number',
      text: 'Mobile number maximum character limit is 12'
    });
    return;
  }

  if (!formDataJson.contactTable) {
    Swal.fire({
      icon: 'warning',
      title: 'Contact Table Required',
      text: 'Please select the contact table to confirm.'
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
        const res = await fetch("/contact/save", {
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



// Open Add Creator Modal
function closeEditModal() {
  document.getElementById("editCreatorModal").style.display = "none";
  // document.getElementById("modalTitle").innerText = "Add New Creator";
}


function openEditModal(element) {
  // Get data attributes
  const id = element.getAttribute('data-id');
  const name = element.getAttribute('data-name');
  const designation = element.getAttribute('data-designation');
  const phone = element.getAttribute('data-phno');

  // Fill form fields
  // document.getElementById('contactId').value = id;
  document.getElementById('editName').value = name;
  document.getElementById('editDesignation').value = designation;
  document.getElementById('editPhone').value = phone;


  document.getElementById('edit-form').setAttribute('data-contact-id', id);

  // Open the modal
  document.getElementById('editCreatorModal').style.display = 'block';
}





// Edit contact submit
let editFormDataJson = {};
const edit_form = document.getElementById("edit-form");
//   const modal_content = document.getElementById("modal-content");
//   const responseDiv = document.getElementById('responseMessage');

edit_form.addEventListener("submit", function (e) {
  e.preventDefault();
  const contactId = this.getAttribute('data-contact-id');

  const formData = new FormData(edit_form);
  editFormDataJson = {
    contactId: contactId,
    name: formData.get("editName")?.trim(),
    designation: formData.get("editDesignation")?.trim(),
    stdCode: formData.get("editStd")?.trim(),
    phno: formData.get("editPhone")?.trim(),
    mobile: formData.get("editMobile")?.trim()
  };

  if (!editFormDataJson.name || editFormDataJson.name.length >= 30) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Name',
      text: 'Username maximum character limit is 30'
    });
    return;
  }

  if (!editFormDataJson.designation || editFormDataJson.designation.length >= 30) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Designation',
      text: 'Designation maximum character limit is 30'
    });
    return;
  }

  if (!editFormDataJson.stdCode || editFormDataJson.stdCode.length >= 8) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid STD code',
      text: 'STD code maximum character limit is 8'
    });
    return;
  }

  if (!editFormDataJson.phno || editFormDataJson.phno.length >= 13) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Number',
      text: 'Phone number maximum character limit is 12'
    });
    return;
  }


  if (editFormDataJson.mobile.length >= 12) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid mobile number',
      text: 'Mobile number maximum character limit is 12'
    });
    return;
  }
  // Show confirmation dialog
  Swal.fire({
    title: 'Are you sure?',
    text: "Do you want to edit this contact?",
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Yes, Save',
    cancelButtonText: 'Cancel'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        const csrfToken = document.querySelector('input[name="_csrf"]')?.value || '';
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
        const res = await fetch("/contact/edit", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken
          },
          body: JSON.stringify(editFormDataJson)
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
      const redirectUrl = "/contact/delete/" + numericId;

      // Show tick animation
      Swal.fire({
        title: 'Deleted!',
        text: 'Contact has been removed.',
        icon: 'success',
        timer: 1500,
        showConfirmButton: false
      }).then(() => {
        // Redirect after animation
        window.location.replace(redirectUrl);
      });
    }
  });
}


document.addEventListener("DOMContentLoaded", function () {
  const addBtn = document.getElementById("add-btn");

  if (addBtn) {
    addBtn.addEventListener("click", openModal);
  }

    const closeBtn = document.getElementById("closeAddModalBtn");

  if (closeBtn) {
    closeBtn.addEventListener("click", closeAddModal);
  }

    const closeEditBtn = document.getElementById("closeEditModalBtn");

  if (closeEditBtn) {
    closeEditBtn.addEventListener("click", closeEditModal);
  }
});


// ADD THIS IN JS FILE

// Dropdown toggle
$(document).on("click", ".dropdown-toggle", function (e) {
  e.preventDefault();
  e.stopPropagation();
  toggleDropdown(this);
});

// Edit click
$(document).on("click", ".edit-btn", function (e) {
  e.preventDefault();
  e.stopPropagation();
  openEditModal(this);
});

// Delete click
$(document).on("click", ".delete-btn", function (e) {
  e.preventDefault();
  e.stopPropagation();
  confirmDelete(this);
});