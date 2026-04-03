

  // Open Add Creator Modal
  function openModal() {
    document.getElementById("publisherModal").style.display = "block";
    document.getElementById("modalTitle").innerText = "Add New Publisher";
  }

  // Close Add Creator Modal
  function closeModal() {
    document.getElementById("publisherModal").style.display = "none";
  }

  // Close Edit Creator Modal
  function closeEditModal() {
    document.getElementById("editPublisherModal").style.display = "none";
    location.reload();
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

  // Close dropdown on outside click
  window.onclick = function (event) {
    if (!event.target.matches('.dropdown-toggle')) {
      document.querySelectorAll(".dropdown-menu").forEach(d => d.style.display = "none");
    }
  }


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
        const redirectUrl = "/admin/delete_publisher/" + numericId;

        // Show tick animation
        Swal.fire({
          title: 'Deleted!',
          text: 'Publisher has been removed.',
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
  


   // ADD CREATOR SUBMIT
let formDataJson = {};
const form = document.getElementById("publisherForm");
const modal_content = document.getElementById("modal-content");
const responseDiv = document.getElementById('responseMessage');

form.addEventListener("submit", function (e) {
  e.preventDefault();

  const formData = new FormData(form);
  formDataJson = {
    userName: formData.get("userName").trim(),
    userPassword: formData.get("userPassword").trim(),
    userConfirmPassword: formData.get("userConfirmPassword").trim(),
    adminPassword: formData.get("adminPassword").trim()
  };

  if (
    !formDataJson.userName ||
    formDataJson.userName.length <= 9 ||
    formDataJson.userName.length >= 21 ||
    !formDataJson.userName.includes("_")
  ) {
    Swal.fire({
      icon: 'warning',
      title: 'Invalid Username',
      text: 'Username must be between 10 and 20 characters long and contain an underscore ("_").'
    });
    return;
  }
  

  if (!formDataJson.userPassword || formDataJson.userPassword.length !== 12 ) {
    Swal.fire({
      icon: 'warning',
      title: 'Weak Password',
      text: 'Password must be 12 characters long.'
    });
    return;
  }
  

  if (formDataJson.userPassword !== formDataJson.userConfirmPassword) {
    Swal.fire({
      icon: 'error',
      title: 'Failed!',
      text: "Passwords do not match!"
    });
    return;
  }

  if (!formDataJson.adminPassword) {
    Swal.fire({
      icon: 'warning',
      title: 'Admin Password Required',
      text: 'Please enter the admin password to confirm.'
    });
    return;
  }

  $('#savePublisherModal').modal('show');
});


  document.getElementById("confirmSave").addEventListener("click", async function () {
    try {
      const csrfToken = document.querySelector('input[name="_csrf"]')?.value || '';
      const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
      const res = await fetch("/admin/publisher_upload", {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          [csrfHeader]: csrfToken
        },
        body: JSON.stringify(formDataJson)
      });

      const message = await res.text();

      $('#savePublisherModal').modal('hide');

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
  });

  document.getElementById("confirmCancel").addEventListener("click", function () {
    console.log("sdlkfjksdl");
    $('#savePublisherModal').modal('hide');
  });



   // Open Edit Creator Modal
  let editId;
  function editPublisher(element) {
    const userId = element.getAttribute("data-id");
    const userName = element.getAttribute("data-name");
  
    // Set the username in the existingUserName field
    document.getElementById("existingUserName").value = userName;
  
    // Show the edit modal
    $('#editPublisherModal').modal('show');
  }



// EDIT CREATOR SUBMIT
let editFormJson = {};
const edit_form = document.getElementById("edit-form");

edit_form.addEventListener("submit", function (e) {
    e.preventDefault();

    const editFormData = new FormData(edit_form);
    const newUserName = editFormData.get("newUserName");
    const newUserPassword = editFormData.get("newUserPassword");
    const userConfirmPassword = editFormData.get("confirmPassword");

    
    if (
      !newUserName ||
      newUserName.length <= 9 ||
      newUserName.length >= 21 ||
      !newUserName.includes("_")
    ) {
      Swal.fire({
        icon: 'warning',
        title: 'Invalid Username',
        text: 'Username must be between 10 and 17 characters long and contain an underscore ("_").'
      });
      return;
    }

    // New Password Validation (Must be exactly 12 characters)
    if (newUserPassword.length !== 12) {
        Swal.fire({
            icon: 'error',
            title: 'Failed!',
            text: "New password must be exactly 12 characters long."
        });
        return;
    }

    // Password Confirmation Validation
    if (newUserPassword !== userConfirmPassword) {
        Swal.fire({
            icon: 'error',
            title: 'Failed!',
            text: "Passwords do not match!"
        });
        return;
    }

    // Proceed to save if all validations pass
    editFormJson = {
        userName: document.getElementById("existingUserName").value,
        newUserName: newUserName,
        existingUserPassword: editFormData.get("existingUserPassword"),
        newUserPassword: newUserPassword,
        userConfirmPassword: userConfirmPassword,
        adminPassword: editFormData.get("adminPassword")
    };

    // Show confirmation modal for saving
    $('#saveEditPublisherModal').modal('show');
});

document.getElementById("confirmUpdatePublisher").addEventListener("click", async function () {
  console.log("enterejklsdjf ");
    try {
        const csrfToken = document.querySelector('input[name="_csrf"]')?.value || '';
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
        const res = await fetch("/admin/edit_publisher", {
            method: "POST",
            headers: { 
              "Content-Type": "application/json",
              [csrfHeader]: csrfToken
            },
            body: JSON.stringify(editFormJson)
        });

        const status = await res.text();

        $('#saveEditPublisherModal').modal('hide');

        setTimeout(() => {
            if (res.ok) {
                Swal.fire({
                    icon: 'success',
                    title: 'Success!',
                    text: status
                }).then(() => {
                    edit_form.reset();
                    location.reload();
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Failed!',
                    text: status
                });
            }
        }, 300);
    } catch (err) {
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'Request failed!'
        });
    }
});


