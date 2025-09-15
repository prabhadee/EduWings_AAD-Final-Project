document.addEventListener("DOMContentLoaded", () => {
            const courseGrid = document.getElementById("courseGrid");
            const instructorModal = document.getElementById("instructorModal");
            const closeModalBtn = document.getElementById("closeModal");
            const instructorGrid = document.getElementById("instructorGrid");
            const modalCourseTitle = document.getElementById("modalCourseTitle");
            const mobileMenuBtn = document.getElementById("mobileMenu");
            const mobileNav = document.getElementById("mobileNav");
            const closeMobileNavBtn = document.getElementById("closeMobileNav");
            const overlay = document.getElementById("overlay");
            const token = localStorage.getItem('accessToken');

            // Mobile navigation toggle
            mobileMenuBtn.addEventListener("click", () => {
                mobileNav.classList.add("active");
                overlay.classList.add("active");
                document.body.style.overflow = "hidden";
            });

            closeMobileNavBtn.addEventListener("click", closeMobileNav);
            overlay.addEventListener("click", closeMobileNav);

            function closeMobileNav() {
                mobileNav.classList.remove("active");
                overlay.classList.remove("active");
                document.body.style.overflow = "auto";
            }

            // Navbar scroll effect
            window.addEventListener('scroll', () => {
                const navbar = document.getElementById('navbar');
                if (window.scrollY > 50) {
                    navbar.classList.add('scrolled');
                } else {
                    navbar.classList.remove('scrolled');
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
                    courseGrid.innerHTML = "";
                    
                    if (!courses || courses.length === 0) {
                        courseGrid.innerHTML = `
                            <div class="empty-state">
                                <i class="fas fa-book-open"></i>
                                <p>No courses available at the moment. Please check back later.</p>
                            </div>`;
                        return;
                    }

                    courses.forEach(course => {
                        const icons = [
                            "fas fa-code", "fas fa-calculator", "fas fa-flask", 
                            "fas fa-paint-brush", "fas fa-globe", "fas fa-chart-line",
                            "fas fa-briefcase", "fas fa-music", "fas fa-camera"
                        ];
                        const randomIcon = icons[Math.floor(Math.random() * icons.length)];
                        
                        const card = document.createElement('div');
                        card.className = 'course-card';
                        card.dataset.courseId = course.id;
                        card.innerHTML = `
                            <div class="course-image">
                                <i class="${randomIcon}"></i>
                            </div>
                            <div class="course-content">
                                <h3 class="course-title">${course.courseName}</h3>
                                <p class="course-description">${course.description}</p>
                                <button class="view-btn view-instructors">
                                    <i class="fas fa-users"></i> View Instructors
                                </button>
                            </div>
                        `;
                        courseGrid.appendChild(card);
                    });

                    // Add event listeners to view buttons
                    document.querySelectorAll('.view-instructors').forEach(btn => {
                        btn.addEventListener('click', function(e) {
                            const courseId = this.closest('.course-card').dataset.courseId;
                            const courseName = this.closest('.course-card').querySelector('.course-title').textContent;
                            openInstructorsModal(courseId, courseName);
                        });
                    });
                })
                .catch(err => {
                    console.error("Error loading courses:", err);
                    courseGrid.innerHTML = `
                        <div class="empty-state">
                            <i class="fas fa-exclamation-circle"></i>
                            <p>${err.message}</p>
                        </div>`;
                });
            }

            // Open instructors modal
            function openInstructorsModal(courseId, courseName) {
                modalCourseTitle.textContent = `Instructors for ${courseName}`;
                instructorGrid.innerHTML = '<div class="loader"><div class="loader-circle"></div></div>';
                instructorModal.style.display = "flex";

                // Fetch instructors for this course
                fetch(`http://localhost:8080/api/instructors/course/${courseId}`, {
                    method: "GET",
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                })
                .then(response => {
                    if (!response.ok) {
                        if (response.status === 404) {
                            throw new Error("No instructors found for this course.");
                        } else {
                            throw new Error(`Failed to fetch instructors. Status: ${response.status}`);
                        }
                    }
                    return response.json();
                })
                .then(instructors => {
                    instructorGrid.innerHTML = "";
                    
                    if (!instructors || instructors.length === 0) {
                        instructorGrid.innerHTML = `
                            <div class="empty-state">
                                <i class="fas fa-user-graduate"></i>
                                <p>No instructors assigned to this course yet.</p>
                            </div>`;
                        return;
                    }

                    instructors.forEach(instructor => {
                        const colors = [
                            "#667eea", "#764ba2", "#f093fb", "#f5576c", 
                            "#4facfe", "#00f2fe", "#fa709a", "#fee140"
                        ];
                        const randomColor = colors[Math.floor(Math.random() * colors.length)];
                        
                        const card = document.createElement('div');
                        card.className = 'instructor-card';
                        card.innerHTML = `
                            <div class="instructor-avatar" style="background: linear-gradient(135deg, ${randomColor} 0%, ${randomColor}77 100%)">
                                <i class="fas fa-user"></i>
                            </div>
                            <h3 class="instructor-name">${instructor.name}</h3>
                            <p class="instructor-bio">${instructor.bio || 'Experienced instructor with expertise in this field.'}</p>
                        `;
                        instructorGrid.appendChild(card);
                    });
                })
                .catch(error => {
                    console.error("Error loading instructors:", error);
                    instructorGrid.innerHTML = `
                        <div class="empty-state">
                            <i class="fas fa-exclamation-circle"></i>
                            <p>${error.message}</p>
                        </div>`;
                });
            }

            // Close Modal
            closeModalBtn.addEventListener("click", () => {
                instructorModal.style.display = "none";
            });

            // Close modal when clicking outside
            window.addEventListener("click", (e) => {
                if (e.target === instructorModal) {
                    instructorModal.style.display = "none";
                }
            });

            window.logout = function(){
                localStorage.removeItem("accessToken");
                localStorage.removeItem("enrollmentData");
                localStorage.removeItem("__paypal_storage__");
                localStorage.removeItem("selectedBatchId");
                localStorage.removeItem("userRole");
            }

            // Initialize
            loadAllCourses();
        });