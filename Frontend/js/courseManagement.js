document.addEventListener("DOMContentLoaded", () => {
            const openModalBtn = document.getElementById("openModal");
            const closeModalBtn = document.getElementById("closeModal");
            const modal = document.getElementById("courseModal");
            const courseForm = document.getElementById("courseForm");
            const courseList = document.getElementById("courseList");
            const token = localStorage.getItem('accessToken');

            // Mobile menu toggle
            const menuToggle = document.getElementById('menuToggle');
            const sidebar = document.getElementById('sidebar');
            
            if (menuToggle) {
                menuToggle.addEventListener('click', () => {
                    sidebar.classList.toggle('active');
                });
            }

            // Open Modal
            openModalBtn.addEventListener("click", () => {
                modal.style.display = "flex";
                document.getElementById("courseForm").onsubmit = addNewCourse;
            });

            // Close Modal
            closeModalBtn.addEventListener("click", () => {
                modal.style.display = "none";
                courseForm.reset();
            });

            // Close modal when clicking outside
            window.addEventListener("click", (e) => {
                if (e.target === modal) {
                    modal.style.display = "none";
                    courseForm.reset();
                }
            });

            // Load all courses
            function loadAllCourses() {
                fetch("http://localhost:8080/api/courses/getall", {
                    method: "GET",
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                })
                .then(response => {
                    if (!response.ok) {
                        if (response.status === 403) {
                            throw new Error("Access Denied: You do not have permission.");
                        } else {
                            throw new Error(`Failed to fetch courses. Status: ${response.status}`);
                        }
                    }
                    return response.json();
                })
                .then(courses => {
                    courseList.innerHTML = "";
                    if (!courses || courses.length === 0) {
                        courseList.innerHTML = `
                            <tr>
                                <td colspan="4" class="empty-state">
                                    <i class="fas fa-book-open"></i>
                                    <p>No courses found. Add your first course to get started.</p>
                                </td>
                            </tr>`;
                        return;
                    }

                    courses.forEach(course => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td>${course.id}</td>
                            <td>${course.courseName}</td>
                            <td>${course.description}</td>
                            <td>
                                <button class="action-btn update" data-id="${course.id}">
                                    <i class="fas fa-edit"></i> Edit
                                </button>
                                <button class="action-btn delete" data-id="${course.id}">
                                    <i class="fas fa-trash"></i> Delete
                                </button>
                            </td>
                        `;
                        courseList.appendChild(row);
                    });

                    // Add event listeners to action buttons
                    document.querySelectorAll('.action-btn.delete').forEach(btn => {
                        btn.addEventListener('click', deleteCourse);
                    });
                    
                    document.querySelectorAll('.action-btn.update').forEach(btn => {
                        btn.addEventListener('click', updateCourse);
                    });
                })
                .catch(err => {
                    console.error("Error loading courses:", err);
                    courseList.innerHTML = `
                        <tr>
                            <td colspan="4" style="text-align: center; color: #ff6b6b;">
                                ${err.message}
                            </td>
                        </tr>`;
                });
            }

            // Add New Course
            function addNewCourse(e) {
                e.preventDefault();

                const courseData = {
                    courseName: document.getElementById("courseName").value,
                    description: document.getElementById("courseDesc").value
                };

                fetch("http://localhost:8080/api/courses/addcourse", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },
                    body: JSON.stringify(courseData)
                })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    }
                    throw new Error(`Failed to add course: ${response.status}`);
                })
                .then(data => {
                    Swal.fire({
                        icon: 'success',
                        title: 'Course added successfully!',
                        showConfirmButton: false,
                        timer: 1500
                    });
                    modal.style.display = "none";
                    courseForm.reset();
                    loadAllCourses();
                })
                .catch(error => {
                    Swal.fire({
                        icon: 'error',
                        title: 'Failed to add course',
                        text: error.message
                    });
                });
            }

            // Delete Course
            function deleteCourse(e) {
                const courseId = e.target.closest('button').dataset.id;
                
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
                        fetch(`http://localhost:8080/api/courses/${courseId}`, {
                            method: "DELETE",
                            headers: {
                                "Authorization": `Bearer ${token}`
                            }
                        })
                        .then(response => {
                            if (response.ok) {
                                Swal.fire(
                                    'Deleted!',
                                    'Course has been deleted.',
                                    'success'
                                );
                                loadAllCourses();
                            } else {
                                throw new Error(`Failed to delete course: ${response.status}`);
                            }
                        })
                        .catch(error => {
                            Swal.fire('Error', error.message, 'error');
                        });
                    }
                });
            }

            // Update Course
            function updateCourse(e) {
                const courseId = e.target.closest('button').dataset.id;
                const row = e.target.closest('tr');
                const courseName = row.children[1].textContent;
                const courseDesc = row.children[2].textContent;

                // Fill the modal with current data
                document.getElementById("courseName").value = courseName;
                document.getElementById("courseDesc").value = courseDesc;
                
                // Show modal
                modal.style.display = "flex";
                
                // Change form submission handler to update instead of add
                courseForm.onsubmit = function(e) {
                    e.preventDefault();
                    
                    const updatedData = {
                        courseName: document.getElementById("courseName").value,
                        description: document.getElementById("courseDesc").value
                    };

                    fetch(`http://localhost:8080/api/courses/${courseId}`, {
                        method: "PUT",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${token}`
                        },
                        body: JSON.stringify(updatedData)
                    })
                    .then(response => {
                        if (response.ok) {
                            Swal.fire({
                                icon: 'success',
                                title: 'Course updated successfully!',
                                showConfirmButton: false,
                                timer: 1500
                            });
                            modal.style.display = "none";
                            courseForm.reset();
                            courseForm.onsubmit = addNewCourse;
                            loadAllCourses();
                        } else {
                            throw new Error(`Failed to update course: ${response.status}`);
                        }
                    })
                    .catch(error => {
                        Swal.fire({
                            icon: 'error',
                            title: 'Failed to update course',
                            text: error.message
                        });
                    });
                };
            }

            // Initialize
            loadAllCourses();
            courseForm.onsubmit = addNewCourse;
        });