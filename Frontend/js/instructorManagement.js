document.addEventListener("DOMContentLoaded", () => {
    const openModalBtn = document.getElementById("openModal");
    const closeModalBtn = document.getElementById("closeModal");
    const modal = document.getElementById("instructorModal");
    const instructorForm = document.getElementById("instructorForm");
    const instructorList = document.getElementById("instructorList");
    const instructorCourseDropdown = document.getElementById("instructorCourse");
    const modalTitle = document.getElementById("modalTitle");
    const submitButton = document.getElementById("submitButton");
    const instructorIdInput = document.getElementById("instructorId");
    const token = localStorage.getItem('accessToken');
    
    // Mobile menu toggle
    const menuToggle = document.getElementById('menuToggle');
    const sidebar = document.getElementById('sidebar');
    
    if (menuToggle) {
        menuToggle.addEventListener('click', () => {
            sidebar.classList.toggle('active');
        });
    }

    // Check authentication
    if (!token) {
        Swal.fire({
            icon: 'error',
            title: 'Authentication Required',
            text: 'Please login to access this page',
            willClose: () => {
                window.location.href = '/welcome.html';
            }
        });
        return;
    }

    // Load all courses from DB into dropdown
    function loadCourses() {
        return fetch("http://localhost:8080/api/courses/getall", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        })
        .then(response => {
            if (!response.ok) {
                if (response.status === 401 || response.status === 403) {
                    throw new Error("Authentication failed. Please login again.");
                }
                throw new Error(`Failed to fetch courses: ${response.status}`);
            }
            return response.json();
        })
        .then(courses => {
            instructorCourseDropdown.innerHTML = '<option value="">Select Course</option>';
            courses.forEach(course => {
                const option = document.createElement("option");
                option.value = course.id;
                option.textContent = course.courseName;
                instructorCourseDropdown.appendChild(option);
            });
            return courses; // Return courses for chaining
        })
        .catch(error => {
            console.error("Error loading courses:", error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: error.message
            });
            throw error;
        });
    }

    // Load all instructors from DB
    function loadInstructors() {
        Promise.all([
            fetch("http://localhost:8080/api/instructors/getall", {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }),
            fetch("http://localhost:8080/api/courses/getall", {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            })
        ])
        .then(responses => Promise.all(responses.map(r => {
            if (!r.ok) {
                if (r.status === 401 || r.status === 403) {
                    throw new Error("Authentication failed. Please login again.");
                }
                throw new Error(`Failed to fetch data: ${r.status}`);
            }
            return r.json();
        })))
        .then(([instructors, courses]) => {
            instructorList.innerHTML = "";
            
            // Create a mapping of course IDs to course names
            const courseMap = {};
            courses.forEach(course => {
                courseMap[course.id] = course.courseName;
            });
            
            if (!instructors || instructors.length === 0) {
                instructorList.innerHTML = `
                    <tr>
                        <td colspan="7" class="empty-state">
                            <i class="fas fa-user-graduate"></i>
                            <p>No instructors found. Add your first instructor to get started.</p>
                        </td>
                    </tr>`;
                return;
            }

            instructors.forEach(instructor => {
                // Add courseName to instructor object
                instructor.courseName = instructor.courseId ? courseMap[instructor.courseId] : 'Not assigned';
                addInstructorRow(instructor);
            });
        })
        .catch(error => {
            console.error("Error loading instructors:", error);
            instructorList.innerHTML = `
                <tr>
                    <td colspan="7" style="text-align: center; color: #ff6b6b;">
                        ${error.message}
                    </td>
                </tr>`;
        });
    }

    // Add instructor row to table with improved photo handling
    function addInstructorRow(instructor) {
        const row = document.createElement("tr");
        
        // Safe photo handling - check if photo exists and is valid
        let photoHtml;
        if (instructor.photo && instructor.photo.trim() !== '' && instructor.photo !== 'null') {
            // Validate base64 data URL format
            if (instructor.photo.startsWith('data:image/')) {
                photoHtml = `<img src="${instructor.photo}" class="instructor-photo" alt="${instructor.name}" onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                            <div class="instructor-photo" style="background: var(--accent-gradient); display: none; align-items: center; justify-content: center;">
                                <i class="fas fa-user" style="color: white;"></i>
                            </div>`;
            } else {
                // Invalid photo data, show placeholder
                photoHtml = `<div class="instructor-photo" style="background: var(--accent-gradient); display: flex; align-items: center; justify-content: center;">
                                <i class="fas fa-user" style="color: white;"></i>
                            </div>`;
            }
        } else {
            // No photo, show placeholder
            photoHtml = `<div class="instructor-photo" style="background: var(--accent-gradient); display: flex; align-items: center; justify-content: center;">
                            <i class="fas fa-user" style="color: white;"></i>
                        </div>`;
        }

        row.innerHTML = `
            <td>${instructor.id}</td>
            <td>${photoHtml}</td>
            <td>${instructor.name}</td>
            <td>${instructor.email}</td>
            <td>${instructor.phone || 'N/A'}</td>
            <td class="course-cell" data-course-id="${instructor.courseId || ''}">
                ${instructor.courseName || 'Not assigned'}
            </td>
            <td>
                <button class="action-btn update" data-id="${instructor.id}">
                    <i class="fas fa-edit"></i> Edit
                </button>
                <button class="action-btn delete" data-id="${instructor.id}">
                    <i class="fas fa-trash"></i> Delete
                </button>
            </td>
        `;
        instructorList.appendChild(row);
    }

    // Convert image to Base64 with validation
    function getBase64(file) {
        return new Promise((resolve, reject) => {
            // Check file type
            if (!file.type.startsWith('image/')) {
                reject(new Error('Please select a valid image file'));
                return;
            }
            
            // Check file size (limit to 5MB)
            const maxSize = 5 * 1024 * 1024; // 5MB in bytes
            if (file.size > maxSize) {
                reject(new Error('Image size must be less than 5MB'));
                return;
            }

            const reader = new FileReader();
            reader.onload = () => resolve(reader.result);
            reader.onerror = error => reject(error);
            reader.readAsDataURL(file);
        });
    }

    // Open Modal
    openModalBtn.addEventListener("click", () => {
        loadCourses();
        modalTitle.textContent = "Add Instructor";
        submitButton.textContent = "Save Instructor";
        instructorIdInput.value = "";
        modal.style.display = "flex";
        instructorForm.reset();
        // Reset file input label
        document.getElementById('fileInputLabel').innerHTML = '<i class="fas fa-upload"></i> Choose Instructor Photo';
        instructorForm.onsubmit = addInstructorHandler;
    });

    // Close Modal
    function closeModal() {
        modal.style.display = "none";
        instructorForm.reset();
        // Reset file input label
        document.getElementById('fileInputLabel').innerHTML = '<i class="fas fa-upload"></i> Choose Instructor Photo';
    }
    
    closeModalBtn.addEventListener("click", closeModal);
    window.addEventListener("click", (e) => { 
        if (e.target === modal) closeModal(); 
    });

    // Add New Instructor Handler with improved error handling
    async function addInstructorHandler(e) {
    e.preventDefault();

    const name = document.getElementById("instructorName").value.trim();
    const email = document.getElementById("instructorEmail").value.trim().toLowerCase();
    const phone = document.getElementById("instructorPhone").value.trim();
    const courseId = instructorCourseDropdown.value;
    const photoFile = document.getElementById("instructorPhoto").files[0];

    // Validation
    if (!name) {
        Swal.fire("Error", "Please enter instructor name", "error");
        return;
    }
    if (!email) {
        Swal.fire("Error", "Please enter instructor email", "error");
        return;
    }
    if (!courseId) {
        Swal.fire("Error", "Please select a course", "error");
        return;
    }

    // Use FormData to send multipart/form-data
    const formData = new FormData();
    formData.append("name", name);
    formData.append("email", email);
    formData.append("phone", phone);
    formData.append("courseId", courseId);

    if (photoFile) {
        formData.append("photo", photoFile); // match @RequestParam("photo")
    }

    try {
        const response = await fetch("http://localhost:8080/api/instructors/create", {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}` // DO NOT set Content-Type for FormData
            },
            body: formData
        });

        if (!response.ok) {
            const errText = await response.text();
            throw new Error(errText || "Failed to add instructor");
        }

        Swal.fire({
            icon: "success",
            title: "Instructor added successfully",
            showConfirmButton: false,
            timer: 1500
        });

        closeModal();
        loadInstructors(); // Reload table

    } catch (error) {
        console.error("Error adding instructor:", error);
        Swal.fire("Error", error.message, "error");
    }
}

    // Handle Edit & Delete Actions
    instructorList.addEventListener("click", (e) => {
        const target = e.target.closest('button');
        if (!target) return;
        
        const instructorId = target.dataset.id;
        const row = target.closest("tr");
        
        if (target.classList.contains("delete")) {
            // Delete Instructor
            Swal.fire({
                title: 'Are you sure?',
                text: "You won't be able to revert this!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes, delete it!'
            }).then((result) => {
                if (result.isConfirmed) {
                    fetch(`http://localhost:8080/api/instructors/${instructorId}`, { 
                        method: "DELETE",
                        headers: {
                            "Authorization": `Bearer ${token}`
                        }
                    })
                    .then(response => {
                        if (!response.ok) {
                            if (response.status === 401 || response.status === 403) {
                                throw new Error("Authentication failed. Please login again.");
                            }
                            throw new Error(`Failed to delete instructor: ${response.status}`);
                        }
                        Swal.fire(
                            'Deleted!',
                            'Instructor has been deleted.',
                            'success'
                        );
                        loadInstructors();
                    })
                    .catch(error => {
                        console.error("Error deleting instructor:", error);
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: error.message
                        });
                    });
                }
            });

        } else if (target.classList.contains("update")) {
            // Edit Instructor - Get the course ID from the data attribute
            const courseCell = row.querySelector('.course-cell');
            const courseId = courseCell.dataset.courseId;

            document.getElementById("instructorId").value = instructorId;
            document.getElementById("instructorName").value = row.children[2].textContent;
            document.getElementById("instructorEmail").value = row.children[3].textContent;
            document.getElementById("instructorPhone").value = row.children[4].textContent;

            // First load courses, then set the selected value
            loadCourses().then(courses => {
                if (courseId) {
                    instructorCourseDropdown.value = courseId;
                }
            });

            modalTitle.textContent = "Edit Instructor";
            submitButton.textContent = "Update Instructor";
            modal.style.display = "flex";

            instructorForm.onsubmit = updateInstructorHandler;
        }
    });

    // Update Instructor Handler
    async function updateInstructorHandler(e) {
        e.preventDefault();

        const instructorId = instructorIdInput.value;
        const name = document.getElementById("instructorName").value.trim();
        const email = document.getElementById("instructorEmail").value.trim().toLowerCase();
        const phone = document.getElementById("instructorPhone").value.trim();
        const courseId = instructorCourseDropdown.value;
        const photoFile = document.getElementById("instructorPhoto").files[0];

        if (!courseId) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Please select a course'
            });
            return;
        }

        let photoBase64 = null;
        if (photoFile) {
            try {
                photoBase64 = await getBase64(photoFile);
            } catch (error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Image Error',
                    text: error.message
                });
                return;
            }
        }

        const instructorData = { 
            id: parseInt(instructorId),
            name, 
            email, 
            phone, 
            photo: photoBase64, 
            courseId: parseInt(courseId)
        };

        try {
            const response = await fetch(`http://localhost:8080/api/instructors/${instructorId}`, {
                method: "PUT",
                headers: { 
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(instructorData)
            });

            if (!response.ok) {
                if (response.status === 401 || response.status === 403) {
                    throw new Error("Authentication failed. Please login again.");
                }
                throw new Error(`Failed to update instructor: ${response.status}`);
            }

            const data = await response.json();

            Swal.fire({
                icon: 'success',
                title: 'Success!',
                text: 'Instructor updated successfully',
                showConfirmButton: false,
                timer: 1500
            });
            
            closeModal();
            loadInstructors();

        } catch (error) {
            console.error("Error updating instructor:", error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: error.message
            });
        }
    }

    // File input change handler with validation
    document.getElementById('instructorPhoto').addEventListener('change', function(e) {
        const file = e.target.files[0];
        const label = document.getElementById('fileInputLabel');
        
        if (file) {
            // Validate file type
            if (!file.type.startsWith('image/')) {
                Swal.fire({
                    icon: 'error',
                    title: 'Invalid File',
                    text: 'Please select a valid image file'
                });
                e.target.value = ''; // Clear the input
                label.innerHTML = '<i class="fas fa-upload"></i> Choose Instructor Photo';
                return;
            }
            
            // Validate file size (5MB limit)
            const maxSize = 5 * 1024 * 1024;
            if (file.size > maxSize) {
                Swal.fire({
                    icon: 'error',
                    title: 'File Too Large',
                    text: 'Image size must be less than 5MB'
                });
                e.target.value = ''; // Clear the input
                label.innerHTML = '<i class="fas fa-upload"></i> Choose Instructor Photo';
                return;
            }
            
            label.innerHTML = `<i class="fas fa-check"></i> ${file.name}`;
        } else {
            label.innerHTML = '<i class="fas fa-upload"></i> Choose Instructor Photo';
        }
    });

    // Initial load
    loadCourses();
    loadInstructors();
    instructorForm.onsubmit = addInstructorHandler;
});