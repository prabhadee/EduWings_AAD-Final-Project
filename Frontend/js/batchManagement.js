document.addEventListener("DOMContentLoaded", () => {
            // Create animated background particles
            function createParticles() {
                const particlesContainer = document.getElementById('particles');
                const particleCount = 15;
                
                for (let i = 0; i < particleCount; i++) {
                    const particle = document.createElement('div');
                    particle.className = 'particle';
                    
                    // Random size and position
                    const size = Math.random() * 8 + 4;
                    particle.style.width = `${size}px`;
                    particle.style.height = `${size}px`;
                    particle.style.left = `${Math.random() * 100}%`;
                    particle.style.top = `${Math.random() * 100}%`;
                    
                    // Random animation delay
                    particle.style.animationDelay = `${Math.random() * 20}s`;
                    particle.style.animationDuration = `${Math.random() * 10 + 15}s`;
                    
                    particlesContainer.appendChild(particle);
                }
            }
            
            createParticles();

            // Mobile menu toggle with enhanced animation
            const menuToggle = document.getElementById('menuToggle');
            const sidebar = document.getElementById('sidebar');
            
            if (menuToggle) {
                menuToggle.addEventListener('click', () => {
                    sidebar.classList.toggle('active');
                    const icon = menuToggle.querySelector('i');
                    
                    if (sidebar.classList.contains('active')) {
                        icon.className = 'fas fa-times';
                        menuToggle.style.background = 'var(--secondary-gradient)';
                    } else {
                        icon.className = 'fas fa-bars';
                        menuToggle.style.background = 'var(--neon-gradient)';
                    }
                });
            }

            // Enhanced scroll animations with intersection observer
            const animateOnScroll = () => {
                const elements = document.querySelectorAll('.fade-in, .slide-in');
                
                const observer = new IntersectionObserver((entries) => {
                    entries.forEach(entry => {
                        if (entry.isIntersecting) {
                            entry.target.style.animationPlayState = 'running';
                            entry.target.style.opacity = '1';
                            
                            // Add extra sparkle effect
                            setTimeout(() => {
                                entry.target.style.filter = 'drop-shadow(0 0 20px rgba(102, 126, 234, 0.3))';
                            }, 500);
                        }
                    });
                }, { threshold: 0.1 });
                
                elements.forEach(element => {
                    element.style.animationPlayState = 'paused';
                    observer.observe(element);
                });
            };

            animateOnScroll();

            // Your existing JavaScript code for batch management
            const apiBase = "http://localhost:8080/api/batches";
            const batchTableBody = document.getElementById("batchTableBody");
            const batchModal = document.getElementById("batchModal");
            const openModalBtn = document.getElementById("openModalBtn");
            const closeModalBtn = document.getElementById("closeModalBtn");
            const batchForm = document.getElementById("batchForm");

            const batchIdField = document.getElementById("batchId");
            const batchNameField = document.getElementById("batchName");
            const monthlyFeeField = document.getElementById("monthlyFee");
            const courseSelect = document.getElementById("courseSelect");
            const instructorSelect = document.getElementById("instructorSelect");

            const token = localStorage.getItem('accessToken');
            let isEdit = false;

            // Open Modal
            openModalBtn.addEventListener("click", () => {
                isEdit = false;
                batchForm.reset();
                batchIdField.value = "";
                document.getElementById("modalTitle").textContent = "Add New Batch";
                batchModal.style.display = "flex";
                loadDropdowns(); // Load dropdowns when opening modal
            });

            // Close Modal
            closeModalBtn.addEventListener("click", () => {
                batchModal.style.display = "none";
            });

            // Close modal when clicking outside
            window.addEventListener("click", (e) => {
                if (e.target === batchModal) {
                    batchModal.style.display = "none";
                }
            });

            // Fetch and populate dropdowns
            async function loadDropdowns() {
                try {
                    // Fetch courses
                    const coursesRes = await fetch("http://localhost:8080/api/courses/getall", {
                        headers: {
                            "Authorization": `Bearer ${token}`
                        }
                    });

                    // Fetch instructors
                    const instructorsRes = await fetch("http://localhost:8080/api/instructors/getall", {
                        headers: {
                            "Authorization": `Bearer ${token}`
                        }
                    });

                    if (!coursesRes.ok || !instructorsRes.ok) {
                        throw new Error("Failed to fetch dropdown data");
                    }

                    const courses = await coursesRes.json();
                    const instructors = await instructorsRes.json();

                    // Populate courses dropdown
                    courseSelect.innerHTML = '<option value="">--Select Course--</option>';
                    courses.forEach(course => {
                        courseSelect.innerHTML += `<option value="${course.id}">${course.courseName}</option>`;
                    });

                    // Populate instructors dropdown
                    instructorSelect.innerHTML = '<option value="">--Select Instructor--</option>';
                    instructors.forEach(instructor => {
                        instructorSelect.innerHTML += `<option value="${instructor.id}">${instructor.name}</option>`;
                    });

                } catch (error) {
                    console.error("Error loading dropdowns:", error);
                    Swal.fire("Error!", "Failed to load dropdown data", "error");
                }
            }

            // Load all batches
            async function loadBatches() {
                try {
                    const res = await fetch(`${apiBase}/all`, {
                        headers: {
                            "Authorization": `Bearer ${token}`
                        }
                    });

                    if (!res.ok) {
                        if (res.status === 403) {
                            throw new Error("Access Denied: You do not have permission.");
                        } else {
                            throw new Error("Failed to fetch batches");
                        }
                    }

                    const batches = await res.json();
                    batchTableBody.innerHTML = "";

                    if (batches.length === 0) {
                        batchTableBody.innerHTML = `
                            <tr>
                                <td colspan="6" class="empty-state">
                                    <i class="fas fa-layer-group"></i>
                                    <p>No batches found. Add a new batch to get started.</p>
                                </td>
                            </tr>
                        `;
                        return;
                    }

                    // Fetch additional data to display names instead of IDs
                    const coursesRes = await fetch("http://localhost:8080/api/courses/getall", {
                        headers: {
                            "Authorization": `Bearer ${token}`
                        }
                    });
                    
                    const instructorsRes = await fetch("http://localhost:8080/api/instructors/getall", {
                        headers: {
                            "Authorization": `Bearer ${token}`
                        }
                    });
                    
                    const courses = await coursesRes.json();
                    const instructors = await instructorsRes.json();
                    
                    // Create lookup objects for quick access
                    const courseLookup = {};
                    courses.forEach(course => {
                        courseLookup[course.id] = course.courseName;
                    });
                    
                    const instructorLookup = {};
                    instructors.forEach(instructor => {
                        instructorLookup[instructor.id] = instructor.name;
                    });

                    // Display batches with names instead of IDs
                    batches.forEach(batch => {
                        const row = document.createElement("tr");
                        row.innerHTML = `
                            <td>${batch.batchId}</td>
                            <td>${batch.batchName}</td>
                            <td>$${batch.monthlyFee.toFixed(2)}</td>
                            <td>${courseLookup[batch.courseId] || 'Unknown'}</td>
                            <td>${instructorLookup[batch.instructorId] || 'Unknown'}</td>
                            <td>
                                <button class="action-btn update" onclick="editBatch(${batch.batchId})">
                                    <i class="fas fa-edit"></i> Edit
                                </button>
                                <button class="action-btn delete" onclick="deleteBatch(${batch.batchId})">
                                    <i class="fas fa-trash"></i> Delete
                                </button>
                            </td>
                        `;
                        batchTableBody.appendChild(row);
                    });
                } catch (error) {
                    console.error("Error loading batches:", error);
                    batchTableBody.innerHTML = `
                        <tr>
                            <td colspan="6" style="text-align:center; color:#ff6b6b;">
                                ${error.message}
                            </td>
                        </tr>
                    `;
                }
            }

            // Add/Edit Batch
            batchForm.addEventListener("submit", async (e) => {
                e.preventDefault();

                const saveBtn = document.getElementById("saveBatchBtn");
                saveBtn.disabled = true;
                saveBtn.textContent = "Saving...";

                try {
                    const batchData = {
                        batchName: batchNameField.value,
                        monthlyFee: parseFloat(monthlyFeeField.value),
                        courseId: parseInt(courseSelect.value),
                        instructorId: parseInt(instructorSelect.value)
                    };

                    let url, method;
                    
                    if (isEdit) {
                        url = `${apiBase}/update/${batchIdField.value}`;
                        method = "PUT";
                        batchData.batchId = parseInt(batchIdField.value);
                    } else {
                        url = `${apiBase}/create`;
                        method = "POST";
                    }

                    const res = await fetch(url, {
                        method,
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${token}`
                        },
                        body: JSON.stringify(batchData)
                    });

                    if (res.ok) {
                        Swal.fire("Success!", `Batch ${isEdit ? "updated" : "added"} successfully`, "success");
                        batchModal.style.display = "none";
                        loadBatches();
                    } else {
                        const errorText = await res.text();
                        throw new Error(errorText || "Something went wrong");
                    }
                } catch (error) {
                    console.error("Error saving batch:", error);
                    Swal.fire("Error!", error.message || "Failed to save batch", "error");
                } finally {
                    saveBtn.disabled = false;
                    saveBtn.textContent = "Save Batch";
                }
            });

            // Edit Batch
            window.editBatch = async (id) => {
                try {
                    isEdit = true;
                    document.getElementById("modalTitle").textContent = "Edit Batch";

                    const res = await fetch(`${apiBase}/${id}`, {
                        headers: {
                            "Authorization": `Bearer ${token}`
                        }
                    });

                    if (!res.ok) {
                        throw new Error("Failed to fetch batch details");
                    }

                    const batch = await res.json();

                    batchIdField.value = batch.batchId;
                    batchNameField.value = batch.batchName;
                    monthlyFeeField.value = batch.monthlyFee;
                    
                    // Load dropdowns first to ensure options are available
                    await loadDropdowns();
                    
                    // Set values after a small delay to ensure dropdowns are populated
                    setTimeout(() => {
                        courseSelect.value = batch.courseId;
                        instructorSelect.value = batch.instructorId;
                    }, 100);

                    batchModal.style.display = "flex";
                } catch (error) {
                    console.error("Error editing batch:", error);
                    Swal.fire("Error!", "Failed to load batch details", "error");
                }
            };

            // Delete Batch
            window.deleteBatch = async (id) => {
                const confirm = await Swal.fire({
                    title: "Are you sure?",
                    text: "You won't be able to revert this!",
                    icon: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#d33",
                    cancelButtonColor: "#3085d6",
                    confirmButtonText: "Yes, delete it!"
                });

                if (confirm.isConfirmed) {
                    try {
                        const res = await fetch(`${apiBase}/delete/${id}`, {
                            method: "DELETE",
                            headers: {
                                "Authorization": `Bearer ${token}`
                            }
                        });

                        if (res.ok) {
                            Swal.fire("Deleted!", "Batch has been deleted.", "success");
                            loadBatches();
                        } else {
                            throw new Error("Failed to delete batch");
                        }
                    } catch (error) {
                        console.error("Error deleting batch:", error);
                        Swal.fire("Error!", "Failed to delete batch", "error");
                    }
                }
            };

            // Initial Load
            if (!token) {
                Swal.fire("Unauthorized!", "Please login first to access batches.", "error");
                window.location.href = "/login.html"; // Redirect to login
                return;
            }
            loadBatches();

            console.log('🚀 Extraordinary Batch Management Loaded Successfully!');
        });
