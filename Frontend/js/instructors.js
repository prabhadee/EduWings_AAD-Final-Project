// Backend API configuration
        const API_BASE_URL = 'http://localhost:8080/api';
        
        // DOM elements
        const loadingState = document.getElementById('loadingState');
        const errorState = document.getElementById('errorState');
        const emptyState = document.getElementById('emptyState');
        const instructorsGrid = document.getElementById('instructorsGrid');
        const errorMessage = document.getElementById('errorMessage');
        const instructorSearch = document.getElementById('instructorSearch');
        const courseFilter = document.getElementById('courseFilter');
        const mobileMenuBtn = document.getElementById('mobileMenu');
        const mobileNav = document.getElementById('mobileNav');
        const closeMobileNavBtn = document.getElementById('closeMobileNav');
        const overlay = document.getElementById('overlay');

        // Store all instructors and courses for filtering
        let allInstructors = [];
        let allCourses = [];
        let filteredInstructors = [];
        const token = localStorage.getItem('accessToken');

        // Load instructors when page loads
        document.addEventListener('DOMContentLoaded', () => {
            // Mobile navigation toggle
            mobileMenuBtn.addEventListener("click", () => {
                mobileNav.classList.add("active");
                overlay.classList.add("active");
                document.body.style.overflow = "hidden";
            });

            closeMobileNavBtn.addEventListener("click", closeMobileNav);
            overlay.addEventListener("click", closeMobileNav);

            // Navbar scroll effect
            window.addEventListener('scroll', () => {
                const navbar = document.getElementById('navbar');
                if (window.scrollY > 50) {
                    navbar.classList.add('scrolled');
                } else {
                    navbar.classList.remove('scrolled');
                }
            });

            // Set up search and filter event listeners
            instructorSearch.addEventListener('input', filterInstructors);
            courseFilter.addEventListener('change', filterInstructors);

            // Load instructors and courses
            Promise.all([loadInstructors(), loadCourses()])
                .then(() => {
                    populateCourseFilter();
                    filterInstructors(); // Apply initial filters
                })
                .catch(error => {
                    console.error('Error loading data:', error);
                    showError(error.message);
                });
        });

        function closeMobileNav() {
            mobileNav.classList.remove("active");
            overlay.classList.remove("active");
            document.body.style.overflow = "auto";
        }

        // Function to load instructors from backend API
        async function loadInstructors() {
            showLoading();
            
            try {
                const response = await fetch(`${API_BASE_URL}/instructors/getall`, {
                    method: "GET",
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                });
                
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                
                const instructors = await response.json();
                
                if (instructors && instructors.length > 0) {
                    allInstructors = instructors;
                    return instructors;
                } else {
                    showEmptyState();
                    return [];
                }
            } catch (error) {
                console.error('Error loading instructors:', error);
                showError(error.message);
                return [];
            }
        }

        // Function to load courses from backend API (for the filter dropdown)
        async function loadCourses() {
            try {
                const response = await fetch(`${API_BASE_URL}/courses/getall`, {
                    method: "GET",
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                });
                
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                
                const courses = await response.json();
                
                if (courses && courses.length > 0) {
                    allCourses = courses;
                    return courses;
                } else {
                    return [];
                }
            } catch (error) {
                console.error('Error loading courses:', error);
                return [];
            }
        }

        // Populate course filter dropdown
        function populateCourseFilter() {
            // Clear existing options except the first one
            while (courseFilter.options.length > 1) {
                courseFilter.remove(1);
            }
            
            // Add courses to dropdown
            allCourses.forEach(course => {
                const option = document.createElement('option');
                option.value = course.id;
                option.textContent = course.courseName;
                courseFilter.appendChild(option);
            });
        }

        // Function to filter instructors based on search and course
        function filterInstructors() {
            const searchTerm = instructorSearch.value.toLowerCase();
            const selectedCourseId = courseFilter.value;
            
            filteredInstructors = allInstructors.filter(instructor => {
                const matchesSearch = instructor.name.toLowerCase().includes(searchTerm) || 
                                     instructor.email.toLowerCase().includes(searchTerm);
                
                const matchesCourse = !selectedCourseId || instructor.courseId == selectedCourseId;
                
                return matchesSearch && matchesCourse;
            });
            
            if (filteredInstructors.length > 0) {
                displayInstructors(filteredInstructors);
            } else {
                showEmptyState();
            }
        }

        // Function to display instructors
        function displayInstructors(instructors) {
            hideAllStates();
            instructorsGrid.style.display = 'grid';
            instructorsGrid.innerHTML = '';

            instructors.forEach((instructor, index) => {
                const instructorCard = createInstructorCard(instructor, index);
                instructorsGrid.appendChild(instructorCard);
            });

            // Add staggered animation
            const cards = instructorsGrid.querySelectorAll('.instructor-card');
            cards.forEach((card, index) => {
                card.style.animationDelay = `${index * 0.1}s`;
            });
        }

        // Function to create an instructor card
        function createInstructorCard(instructor, index) {
            const card = document.createElement('div');
            card.className = 'instructor-card';
            
            // Find course name
            const course = allCourses.find(c => c.id === instructor.courseId);
            const courseName = course ? course.courseName : 'Not assigned';
            
            // Use a placeholder image if no photo is available
            const photoUrl = instructor.photo || `https://randomuser.me/api/portraits/${index % 2 === 0 ? 'men' : 'women'}/${index + 1}.jpg`;
            
            card.innerHTML = `
                <img src="${photoUrl}" alt="${instructor.name}" class="instructor-photo">
                <h3 class="instructor-name">${instructor.name}</h3>
                <div class="instructor-subject">${courseName}</div>
                <div class="instructor-contact">
                    <div><i class="fas fa-envelope"></i> ${instructor.email}</div>
                    <div><i class="fas fa-phone"></i> ${instructor.phone || 'Not provided'}</div>
                </div>
                <div class="instructor-btn">View Batches</div>
            `;

            // Add click event to the card
            card.addEventListener('click', () => {
                openBatchModal(instructor.id, instructor.name);
            });

            return card;
        }

        // Utility functions for state management
        function showLoading() {
            hideAllStates();
            loadingState.style.display = 'flex';
        }

        function showError(message) {
            hideAllStates();
            errorMessage.textContent = message || 'An unexpected error occurred.';
            errorState.style.display = 'block';
        }

        function showEmptyState() {
            hideAllStates();
            emptyState.style.display = 'block';
        }

        function hideAllStates() {
            loadingState.style.display = 'none';
            errorState.style.display = 'none';
            emptyState.style.display = 'none';
        }

        // Global variables for modal
        let currentInstructorId = null;
        let currentInstructorName = null;

        // Function to open the batch modal
        function openBatchModal(instructorId, instructorName) {
            currentInstructorId = instructorId;
            currentInstructorName = instructorName;
            
            const modal = document.getElementById('batchModal');
            const modalInstructorName = document.getElementById('modalInstructorName');
            
            modalInstructorName.textContent = `${instructorName}'s Batches`;
            modal.style.display = 'flex';
            setTimeout(() => modal.classList.add('show'), 10);
            
            loadBatches();
        }

        // Function to close the modal
        function closeModal() {
            const modal = document.getElementById('batchModal');
            modal.classList.remove('show');
            setTimeout(() => modal.style.display = 'none', 300);
        }

        // Function to load batches for the current instructor
        async function loadBatches() {
            const modalLoading = document.getElementById('modalLoading');
            const modalError = document.getElementById('modalError');
            const batchesList = document.getElementById('batchesList');
            const noBatchesMessage = document.getElementById('noBatchesMessage');
            
            // Show loading, hide others
            modalLoading.style.display = 'flex';
            modalError.style.display = 'none';
            batchesList.style.display = 'none';
            noBatchesMessage.style.display = 'none';
            
            try {
                const response = await fetch(`${API_BASE_URL}/batches/instructor/${currentInstructorId}`, {
                    method: "GET",
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                });
                
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                
                const batches = await response.json();
                
                if (batches && batches.length > 0) {
                    displayBatches(batches);
                } else {
                    showNoBatches();
                }
            } catch (error) {
                console.error('Error loading batches:', error);
                showModalError(error.message);
            }
        }

        // Function to display batches in the modal
        function displayBatches(batches) {
            const modalLoading = document.getElementById('modalLoading');
            const batchesList = document.getElementById('batchesList');
            const noBatchesMessage = document.getElementById('noBatchesMessage');
            
            modalLoading.style.display = 'none';
            batchesList.style.display = 'block';
            noBatchesMessage.style.display = 'none';
            
            batchesList.innerHTML = '';
            
            batches.forEach(batch => {
                const batchItem = document.createElement('div');
                batchItem.className = 'batch-item';
                
                // Find course name
                const course = allCourses.find(c => c.id === batch.courseId);
                const courseName = course ? course.courseName : 'Unknown Course';
                
                batchItem.innerHTML = `
                    <h3 class="batch-name">${batch.batchName}</h3>
                    <div class="batch-details">
                        <div class="batch-detail">
                            <span class="batch-label">Course</span>
                            <span class="batch-value">${courseName}</span>
                        </div>
                        <div class="batch-detail">
                            <span class="batch-label">Monthly Fee</span>
                            <span class="batch-value">Rs. ${batch.monthlyFee.toFixed(2)}</span>
                        </div>
                    </div>
                    <button class="select-batch-btn" onclick="selectBatch(${batch.batchId})">Select This Batch</button>
                `;
                
                batchesList.appendChild(batchItem);
            });
        }

        // Function to show no batches message
        function showNoBatches() {
            const modalLoading = document.getElementById('modalLoading');
            const batchesList = document.getElementById('batchesList');
            const noBatchesMessage = document.getElementById('noBatchesMessage');
            
            modalLoading.style.display = 'none';
            batchesList.style.display = 'none';
            noBatchesMessage.style.display = 'block';
        }

        // Function to show modal error
        function showModalError(message) {
            const modalLoading = document.getElementById('modalLoading');
            const modalError = document.getElementById('modalError');
            const modalErrorMessage = document.getElementById('modalErrorMessage');
            
            modalLoading.style.display = 'none';
            modalError.style.display = 'block';
            modalErrorMessage.textContent = message || 'Failed to load batches';
        }

        // Function to select a batch and redirect to enrollment page
        function selectBatch(batchId) {
            // Store batch ID in localStorage for the next page
            localStorage.setItem('selectedBatchId', batchId);
            
            // Redirect to enrollment page
            window.location.href = '/pages/student/enrollment.html';
        }

        // Add event listeners for modal close
        document.addEventListener('DOMContentLoaded', () => {
            // Add modal close event listeners
            document.querySelector('.close').addEventListener('click', closeModal);
            document.getElementById('batchModal').addEventListener('click', (e) => {
                if (e.target === document.getElementById('batchModal')) {
                    closeModal();
                }
            });
        });
