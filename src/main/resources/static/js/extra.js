
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



    // Open Edit Creator Modal
  let editAdminId;
  function editAdmin(element) {
    // const userId = element.getAttribute("data-id");
    const userName = element.getAttribute("data-name");
  
    console.log("sjfdl",userName);

    // Set the username in the existingUserName field
    document.getElementById("existingAdminUserName").value = userName;
  
    // Show the edit modal
    $('#editAdminModal').modal('show');
  }





    // EDIT CREATOR SUBMIT
    let editAdminFormJson = {};
    const edit_admin_form = document.getElementById("edit-form-admin");

    edit_admin_form.addEventListener("submit", function (e) {
        e.preventDefault();
    
        const editFormData = new FormData(edit_admin_form);
        const newUserName = editFormData.get("newAdminUserName");
        const newUserPassword = editFormData.get("newAdminUserPassword");
        const userConfirmPassword = editFormData.get("confirmAdminPassword");
    

        if (
          !newUserName ||
          newUserName.length <= 9 ||
          newUserName.length >= 17 ||
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
        editAdminFormJson = {
            userName: document.getElementById("existingAdminUserName").value,
            newUserName: newUserName,
            existingUserPassword: editFormData.get("existingAdminUserPassword"),
            newUserPassword: newUserPassword,
            userConfirmPassword: userConfirmPassword,
            adminPassword: editFormData.get("adminPassword")
        };
      
        // Show confirmation modal for saving
        $('#saveEditAdminModal').modal('show');
    });

    document.getElementById("confirmUpdate").addEventListener("click", async function () {
        try {
            const csrfToken = document.querySelector('input[name="_csrf"]')?.value || '';
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
            const res = await fetch("/admin/edit_admin", {
                method: "POST",
                headers: { 
                  "Content-Type": "application/json",
                  [csrfHeader]: csrfToken
                },
                body: JSON.stringify(editAdminFormJson)
            });
          
            const status = await res.text();
          
            $('#saveEditAdminModal').modal('hide');
          
            setTimeout(() => {
                if (res.ok) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Success!',
                        text: status
                    }).then(() => {
                        edit_admin_form.reset();
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



      // Close Add Creator Modal
  function closeModal() {
    document.getElementById("adminModal").style.display = "none";
  }

  // Close Edit Creator Modal
  function closeEditModal() {
    document.getElementById("editAdminModal").style.display = "none";
    location.reload();
  }


 


       // Close Add Creator Modal
  function closeModal() {
    document.getElementById("creatorModal").style.display = "none";
  }

  // Close Edit Creator Modal
  function closeEditModal() {
    document.getElementById("editCreatorModal").style.display = "none";
    location.reload();
  }


 