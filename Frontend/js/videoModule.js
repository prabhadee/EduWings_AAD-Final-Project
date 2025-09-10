// const API_BASE_URL = 'http://localhost:8080/api';

// let modules = [];
// let months = [];
// let isEditing = false;

// // Get token from localStorage
// const token = localStorage.getItem('accessToken');

// // DOM Elements
// const moduleList = document.getElementById('moduleList');
// const moduleModal = document.getElementById('moduleModal');
// const moduleForm = document.getElementById('moduleForm');
// const openModalBtn = document.getElementById('openModal');
// const closeModalBtn = document.getElementById('closeModal');
// const monthFilter = document.getElementById('monthFilter');
// const monthSelect = document.getElementById('monthSelect');
// const modalTitle = document.getElementById('modalTitle');
// const submitBtn = document.getElementById('submitBtn');
// const menuToggle = document.getElementById('menuToggle');
// const sidebar = document.getElementById('sidebar');
// const videoFilesInput = document.getElementById('videoFiles');

// // Initialize
// document.addEventListener('DOMContentLoaded', function () {
//     if (!token) {
//         showErrorAlert('Access denied. Please log in first.');
//         return;
//     }

//     loadMonths();
//     loadModules();
//     initializeEventListeners();
// });

// // Event Listeners
// function initializeEventListeners() {
//     openModalBtn.addEventListener('click', openAddModal);
//     closeModalBtn.addEventListener('click', closeModal);
//     moduleForm.addEventListener('submit', handleFormSubmit);
//     monthFilter.addEventListener('change', filterModules);

//     if (menuToggle) {
//         menuToggle.addEventListener('click', function () {
//             sidebar.classList.toggle('active');
//         });
//     }

//     moduleModal.addEventListener('click', function (e) {
//         if (e.target === moduleModal) {
//             closeModal();
//         }
//     });

//     document.addEventListener('click', function (e) {
//         if (window.innerWidth <= 768 && !sidebar.contains(e.target) && !menuToggle.contains(e.target)) {
//             sidebar.classList.remove('active');
//         }
//     });

//     document.addEventListener('keydown', function (e) {
//         if (e.key === 'Escape' && moduleModal.style.display === 'flex') {
//             closeModal();
//         }
//     });
// }

// // Headers with token
// function getAuthHeaders() {
//     return {
//         'Authorization': `Bearer ${token}`
//     };
// }

// // Handle auth errors
// function handleAuthError(response) {
//     if (response.status === 403 || response.status === 401) {
//         showErrorAlert('Access Denied: You do not have permission or your session has expired.');
//         return true;
//     }
//     return false;
// }

// // Load months
// async function loadMonths() {
//     try {
//         const response = await fetch(`${API_BASE_URL}/months/all`, {
//             method: 'GET',
//             headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' }
//         });

//         if (handleAuthError(response)) return;

//         if (response.ok) {
//             months = await response.json();
//         } else {
//             months = Array.from({ length: 12 }, (_, i) => ({ monthId: i + 1 }));
//         }
//     } catch {
//         months = Array.from({ length: 12 }, (_, i) => ({ monthId: i + 1 }));
//         showErrorAlert('Failed to load months, using available data');
//     }
//     populateMonthDropdowns();
// }

// // Populate month dropdowns
// function populateMonthDropdowns() {
//     monthSelect.innerHTML = '<option value="">Select Month ID</option>';
//     monthFilter.innerHTML = '<option value="">All Months</option>';

//     months.forEach(month => {
//         const option = document.createElement('option');
//         option.value = month.monthId;
//         option.textContent = `Month ${month.monthId}`;
//         monthSelect.appendChild(option);

//         const filterOption = option.cloneNode(true);
//         monthFilter.appendChild(filterOption);
//     });
// }

// // Load modules
// async function loadModules() {
//     try {
//         showLoading();
//         const response = await fetch(`${API_BASE_URL}/modules`, {
//             method: 'GET',
//             headers: getAuthHeaders()
//         });

//         if (handleAuthError(response)) {
//             displayEmptyState();
//             return;
//         }

//         if (response.ok) {
//             modules = await response.json();
//             displayModules(modules);
//         } else {
//             throw new Error(`Failed to load modules. Status: ${response.status}`);
//         }
//     } catch (error) {
//         console.error('Error loading modules:', error);
//         showErrorAlert('Failed to load modules. Please check your connection and try again.');
//         displayEmptyState();
//     }
// }

// // Display modules in table
// function displayModules(moduleList) {
//     const tableBody = document.getElementById('moduleList');

//     if (!moduleList || moduleList.length === 0) {
//         displayEmptyState();
//         return;
//     }

//     tableBody.innerHTML = '';

//     moduleList.forEach(module => {
//         const row = document.createElement('tr');

//         let videoDisplay = '';
//         if (module.videoFiles && module.videoFiles.length > 0) {
//             videoDisplay = `${module.videoFiles.length} file(s)`;
//         }

//         row.innerHTML = `
//             <td>${module.moduleId}</td>
//             <td>${escapeHtml(module.title)}</td>
//             <td>${videoDisplay}</td>
//             <td>${module.monthId}</td>
//             <td>
//                 <button class="action-btn view" onclick="viewModule(${module.moduleId})" title="View Videos">
//                     <i class="fas fa-eye"></i> View
//                 </button>
//                 <button class="action-btn update" onclick="editModule(${module.moduleId})" title="Edit Module">
//                     <i class="fas fa-edit"></i> Edit
//                 </button>
//                 <button class="action-btn delete" onclick="deleteModule(${module.moduleId})" title="Delete Module">
//                     <i class="fas fa-trash"></i> Delete
//                 </button>
//             </td>
//         `;
//         tableBody.appendChild(row);
//     });
// }

// // Escape HTML
// function escapeHtml(text) {
//     const div = document.createElement('div');
//     div.textContent = text;
//     return div.innerHTML;
// }

// // Empty state
// function displayEmptyState() {
//     const tableBody = document.getElementById('moduleList');
//     tableBody.innerHTML = `
//         <tr>
//             <td colspan="5" class="empty-state">
//                 <i class="fas fa-video"></i>
//                 <p>No video modules found. Add your first module to get started.</p>
//             </td>
//         </tr>
//     `;
// }

// // Loading state
// function showLoading() {
//     const tableBody = document.getElementById('moduleList');
//     tableBody.innerHTML = `
//         <tr>
//             <td colspan="5" class="empty-state">
//                 <i class="fas fa-spinner fa-spin"></i>
//                 <p>Loading modules...</p>
//             </td>
//         </tr>
//     `;
// }

// // Filter modules
// function filterModules() {
//     const selectedMonthId = monthFilter.value;

//     if (!selectedMonthId) {
//         displayModules(modules);
//         return;
//     }

//     const filteredModules = modules.filter(module =>
//         module.monthId.toString() === selectedMonthId
//     );
//     displayModules(filteredModules);
// }

// // View module videos
// function viewModule(moduleId) {
//     const module = modules.find(m => m.moduleId === moduleId);
//     if (!module || !module.videoFiles || module.videoFiles.length === 0) {
//         showErrorAlert('No video files found for this module.');
//         return;
//     }

//     let videoListHtml = '';
//     module.videoFiles.forEach(fileUrl => {
//         videoListHtml += `
//             <div style="margin-bottom: 15px;">
//                 <video controls width="100%">
//                     <source src="${fileUrl}" type="video/mp4">
//                     Your browser does not support the video tag.
//                 </video>
//             </div>
//         `;
//     });

//     Swal.fire({
//         title: `Videos for: ${module.title}`,
//         html: videoListHtml,
//         width: 600,
//         background: 'rgba(45, 55, 72, 0.95)',
//         color: '#fff'
//     });
// }

// // Modal functions
// function openAddModal() {
//     isEditing = false;
//     modalTitle.textContent = 'Add New Video Module';
//     submitBtn.innerHTML = '<i class="fas fa-save"></i> Save Module';
//     moduleForm.reset();
//     document.getElementById('moduleId').value = '';
//     videoFilesInput.value = "";
//     moduleModal.style.display = 'flex';
// }

// function openEditModal(module) {
//     isEditing = true;
//     modalTitle.textContent = 'Edit Video Module';
//     submitBtn.innerHTML = '<i class="fas fa-save"></i> Update Module';

//     document.getElementById('moduleId').value = module.moduleId;
//     document.getElementById('moduleTitle').value = module.title;
//     document.getElementById('monthSelect').value = module.monthId;
//     videoFilesInput.value = "";

//     moduleModal.style.display = 'flex';
// }

// function closeModal() {
//     moduleModal.style.display = 'none';
//     moduleForm.reset();
//     isEditing = false;
// }

// // Handle form submit
// async function handleFormSubmit(e) {
//     e.preventDefault();

//     const moduleId = document.getElementById('moduleId').value;
//     const title = document.getElementById('moduleTitle').value.trim();
//     const monthId = parseInt(document.getElementById('monthSelect').value);
//     const fileInput = document.getElementById('videoFiles'); // Can hold images/videos/docs

//     if (!title) {
//         Swal.fire("Error", "Please enter a module title", "error");
//         return; 
//     }
//     if (!monthId) {
//         Swal.fire("Error", "Please select a month ID", "error");
//         return;
//     }
//     if (fileInput.files.length === 0 && !moduleId) {
//         Swal.fire("Error", "Please upload at least one file", "error");
//         return;
//     }

//     const formData = new FormData();
//     formData.append("title", title);
//     formData.append("monthId", monthId);

//     for (let i = 0; i < fileInput.files.length; i++) {
//         formData.append("video", fileInput.files[i]); // match backend @RequestParam("video")
//     }

//     const originalText = submitBtn.innerHTML;
//     submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
//     submitBtn.disabled = true;

//     try {
//         let url, method;
//         if (moduleId) {
//             url = `${API_BASE_URL}/modules/update/${moduleId}`;
//             method = "PUT";
//         } else {
//             url = `${API_BASE_URL}/modules/create`;
//             method = "POST";
//         }

//         const response = await fetch(url, {
//             method,
//             headers: { 'Authorization': `Bearer ${token}` }, // DO NOT set Content-Type
//             body: formData
//         });

//         if (response.ok) {
//             Swal.fire("Success", moduleId ? "Module updated!" : "Module created!", "success");
//             closeModal();
//             await loadModules();
//         } else {
//             const err = await response.text();
//             throw new Error(err || "Failed to save module");
//         }
//     } catch (err) {
//         console.error(err);
//         Swal.fire("Error", err.message, "error");
//     } finally {
//         submitBtn.innerHTML = originalText;
//         submitBtn.disabled = false;
//     }
// }


// // Edit module
// async function editModule(moduleId) {
//     try {
//         showLoading();
//         let response = await fetch(`${API_BASE_URL}/modules/${moduleId}`, {
//             method: 'GET',
//             headers: getAuthHeaders()
//         });

//         if (handleAuthError(response)) return;

//         if (response.ok) {
//             const module = await response.json();
//             openEditModal(module);
//         } else {
//             throw new Error(`Failed to fetch module details. Status: ${response.status}`);
//         }
//     } catch (error) {
//         console.error('Error fetching module:', error);
//         showErrorAlert('Failed to load module details');
//     } finally {
//         if (modules.length > 0) {
//             displayModules(modules);
//         } else {
//             displayEmptyState();
//         }
//     }
// }

// // Delete module
// async function deleteModule(moduleId) {
//     const result = await Swal.fire({
//         title: 'Are you sure?',
//         text: 'This will permanently delete the video module.',
//         icon: 'warning',
//         showCancelButton: true,
//         confirmButtonColor: '#e53e3e',
//         cancelButtonColor: '#718096',
//         confirmButtonText: 'Yes, delete it!',
//         cancelButtonText: 'Cancel',
//         background: 'rgba(45, 55, 72, 0.95)',
//         color: '#fff'
//     });

//     if (result.isConfirmed) {
//         try {
//             let response = await fetch(`${API_BASE_URL}/modules/delete/${moduleId}`, {
//                 method: 'DELETE',
//                 headers: getAuthHeaders()
//             });

//             if (handleAuthError(response)) return;

//             if (response.ok) {
//                 showSuccessAlert('Module deleted successfully!');
//                 await loadModules();
//             } else {
//                 throw new Error(`Failed to delete module. Status: ${response.status}`);
//             }
//         } catch (error) {
//             console.error('Error deleting module:', error);
//             showErrorAlert('Failed to delete module');
//         }
//     }
// }

// // Alerts
// function showSuccessAlert(message) {
//     Swal.fire({
//         icon: 'success',
//         title: 'Success!',
//         text: message,
//         showConfirmButton: false,
//         timer: 2000,
//         background: 'rgba(45, 55, 72, 0.95)',
//         color: '#fff'
//     });
// }

// function showErrorAlert(message) {
//     Swal.fire({
//         icon: 'error',
//         title: 'Error!',
//         text: message,
//         confirmButtonColor: '#667eea',
//         background: 'rgba(45, 55, 72, 0.95)',
//         color: '#fff'
//     });
// }


const API_BASE_URL = 'http://localhost:8080/api';

        let modules = [];
        let months = [];
        let isEditing = false;

        // Get token from localStorage
        const token = localStorage.getItem('accessToken');

        // DOM Elements
        const moduleList = document.getElementById('moduleList');
        const moduleModal = document.getElementById('moduleModal');
        const moduleForm = document.getElementById('moduleForm');
        const openModalBtn = document.getElementById('openModal');
        const closeModalBtn = document.getElementById('closeModal');
        const monthFilter = document.getElementById('monthFilter');
        const monthSelect = document.getElementById('monthSelect');
        const modalTitle = document.getElementById('modalTitle');
        const submitBtn = document.getElementById('submitBtn');
        const menuToggle = document.getElementById('menuToggle');
        const sidebar = document.getElementById('sidebar');
        const urlList = document.getElementById('urlList');
        const addUrlBtn = document.getElementById('addUrlBtn');

        // Initialize
        document.addEventListener('DOMContentLoaded', function() {
            // Check if user is authenticated
            if (!token) {
                showErrorAlert('Access denied. Please log in first.');
                return;
            }
            
            loadMonths();
            loadModules();
            initializeEventListeners();
        });

        // Event Listeners
        function initializeEventListeners() {
            openModalBtn.addEventListener('click', openAddModal);
            closeModalBtn.addEventListener('click', closeModal);
            moduleForm.addEventListener('submit', handleFormSubmit);
            monthFilter.addEventListener('change', filterModules);
            addUrlBtn.addEventListener('click', addUrlInput);
            
            // Mobile menu toggle
            if (menuToggle) {
                menuToggle.addEventListener('click', function() {
                    sidebar.classList.toggle('active');
                });
            }

            // Close modal when clicking outside
            moduleModal.addEventListener('click', function(e) {
                if (e.target === moduleModal) {
                    closeModal();
                }
            });

            // Close sidebar when clicking outside on mobile
            document.addEventListener('click', function(e) {
                if (window.innerWidth <= 768 && !sidebar.contains(e.target) && !menuToggle.contains(e.target)) {
                    sidebar.classList.remove('active');
                }
            });

            // ESC key to close modal
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape' && moduleModal.style.display === 'flex') {
                    closeModal();
                }
            });
        }

        // Create headers with authentication
        function getAuthHeaders() {
            return {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            };
        }

        // Handle authentication errors
        function handleAuthError(response) {
            if (response.status === 403 || response.status === 401) {
                showErrorAlert('Access Denied: You do not have permission or your session has expired.');
                return true;
            }
            return false;
        }

        // Load months from API
        async function loadMonths() {
            try {
                const response = await fetch(`${API_BASE_URL}/batch-months`, {
                    method: 'GET',
                    headers: getAuthHeaders()
                });
                
                if (handleAuthError(response)) return;
                
                if (response.ok) {
                    months = await response.json();
                    populateMonthDropdowns();
                } else {
                    console.warn('Failed to load months, using fallback');
                    // Create fallback month IDs
                    months = [
                        { monthId: 1 },
                        { monthId: 2 },
                        { monthId: 3 },
                        { monthId: 4 },
                        { monthId: 5 },
                        { monthId: 6 },
                        { monthId: 7 },
                        { monthId: 8 },
                        { monthId: 9 },
                        { monthId: 10 },
                        { monthId: 11 },
                        { monthId: 12 }
                    ];
                    populateMonthDropdowns();
                }
            } catch (error) {
                console.error('Error loading months:', error);
                // Create fallback month IDs
                months = [
                    { monthId: 1 },
                    { monthId: 2 },
                    { monthId: 3 },
                    { monthId: 4 },
                    { monthId: 5 },
                    { monthId: 6 },
                    { monthId: 7 },
                    { monthId: 8 },
                    { monthId: 9 },
                    { monthId: 10 },
                    { monthId: 11 },
                    { monthId: 12 }
                ];
                populateMonthDropdowns();
                showErrorAlert('Failed to load months, using available data');
            }
        }

        // Populate month dropdowns with IDs only
        function populateMonthDropdowns() {
            // Clear existing options
            monthSelect.innerHTML = '<option value="">Select Month ID</option>';
            monthFilter.innerHTML = '<option value="">All Months</option>';

            months.forEach(month => {
                const option = document.createElement('option');
                option.value = month.monthId;
                option.textContent = `Month ${month.monthId}`;
                monthSelect.appendChild(option);

                const filterOption = option.cloneNode(true);
                monthFilter.appendChild(filterOption);
            });
        }

        // Load modules from API
        async function loadModules() {
            try {
                showLoading();
                
                // Try to get all modules first
                const response = await fetch(`${API_BASE_URL}/modules`, {
                    method: 'GET',
                    headers: getAuthHeaders()
                });
                
                if (handleAuthError(response)) {
                    displayEmptyState();
                    return;
                }
                
                if (response.ok) {
                    modules = await response.json();
                    displayModules(modules);
                } else {
                    // If no general endpoint, try to get by month (assuming monthId = 1)
                    const monthResponse = await fetch(`${API_BASE_URL}/modules/month/1`, {
                        method: 'GET',
                        headers: getAuthHeaders()
                    });
                    
                    if (handleAuthError(monthResponse)) {
                        displayEmptyState();
                        return;
                    }
                    
                    if (monthResponse.ok) {
                        modules = await monthResponse.json();
                        displayModules(modules);
                    } else {
                        throw new Error(`Failed to load modules. Status: ${response.status}`);
                    }
                }
            } catch (error) {
                console.error('Error loading modules:', error);
                showErrorAlert('Failed to load modules. Please check your connection and try again.');
                displayEmptyState();
            }
        }

        // Display modules in table
        function displayModules(moduleList) {
            const tableBody = document.getElementById('moduleList');
            
            if (!moduleList || moduleList.length === 0) {
                displayEmptyState();
                return;
            }

            tableBody.innerHTML = '';

            moduleList.forEach(module => {
                const row = document.createElement('tr');
                
                // Format video URLs for display
                let urlDisplay = '';
                if (module.videoUrls && module.videoUrls.length > 0) {
                    if (module.videoUrls.length === 1) {
                        const displayUrl = module.videoUrls[0].length > 50 ? 
                            module.videoUrls[0].substring(0, 50) + '...' : 
                            module.videoUrls[0];
                        urlDisplay = `<a href="${module.videoUrls[0]}" target="_blank" style="color: #4facfe; text-decoration: none;">${escapeHtml(displayUrl)}</a>`;
                    } else {
                        urlDisplay = `<span class="video-url" title="${module.videoUrls.join(', ')}">${module.videoUrls.length} videos</span>`;
                    }
                }
                
                row.innerHTML = `
                    <td>${module.moduleId}</td>
                    <td>${escapeHtml(module.title)}</td>
                    <td>${urlDisplay}</td>
                    <td>${module.monthId}</td>
                    <td>
                        <button class="action-btn view" onclick="viewModule(${module.moduleId})" title="View URLs">
                            <i class="fas fa-eye"></i> View
                        </button>
                        <button class="action-btn update" onclick="editModule(${module.moduleId})" title="Edit Module">
                            <i class="fas fa-edit"></i> Edit
                        </button>
                        <button class="action-btn delete" onclick="deleteModule(${module.moduleId})" title="Delete Module">
                            <i class="fas fa-trash"></i> Delete
                        </button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        }

        // Escape HTML to prevent XSS
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        // Display empty state
        function displayEmptyState() {
            const tableBody = document.getElementById('moduleList');
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="empty-state">
                        <i class="fas fa-video"></i>
                        <p>No video modules found. Add your first module to get started.</p>
                    </td>
                </tr>
            `;
        }

        // Show loading state
        function showLoading() {
            const tableBody = document.getElementById('moduleList');
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="empty-state">
                        <i class="fas fa-spinner fa-spin"></i>
                        <p>Loading modules...</p>
                    </td>
                </tr>
            `;
        }

        // Filter modules by month
        function filterModules() {
            const selectedMonthId = monthFilter.value;
            
            if (!selectedMonthId) {
                displayModules(modules);
                return;
            }

            const filteredModules = modules.filter(module => 
                module.monthId.toString() === selectedMonthId
            );
            displayModules(filteredModules);
        }

        // Add URL input field
        function addUrlInput(url = '') {
            const urlId = Date.now(); // Unique ID for this URL input
            const urlItem = document.createElement('div');
            urlItem.className = 'url-item';
            urlItem.id = `url-${urlId}`;
            
            urlItem.innerHTML = `
                <input type="url" class="url-input" value="${url}" placeholder="Video URL (e.g., https://youtube.com/watch?v=...)" style="flex: 1; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
                <div class="url-actions">
                    <button type="button" class="url-btn delete" onclick="removeUrl(${urlId})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            `;
            
            urlList.appendChild(urlItem);
        }

        // Remove URL input
        function removeUrl(urlId) {
            const urlItem = document.getElementById(`url-${urlId}`);
            if (urlItem) {
                urlList.removeChild(urlItem);
            }
        }

        // View module URLs
        function viewModule(moduleId) {
            const module = modules.find(m => m.moduleId === moduleId);
            if (!module || !module.videoUrls || module.videoUrls.length === 0) {
                showErrorAlert('No video URLs found for this module.');
                return;
            }
            
            let urlListHtml = '<ul style="text-align: left; margin: 10px 0; padding-left: 20px;">';
            module.videoUrls.forEach(url => {
                urlListHtml += `<li style="margin-bottom: 8px;"><a href="${url}" target="_blank" style="color: #4facfe;">${url}</a></li>`;
            });
            urlListHtml += '</ul>';
            
            Swal.fire({
                title: `Video URLs for: ${module.title}`,
                html: urlListHtml,
                icon: 'info',
                confirmButtonText: 'Close',
                background: 'rgba(45, 55, 72, 0.95)',
                color: '#fff',
                customClass: {
                    popup: 'swal-popup',
                    confirmButton: 'swal-confirm'
                }
            });
        }

        // Modal functions
        function openAddModal() {
            isEditing = false;
            modalTitle.textContent = 'Add New Video Module';
            submitBtn.innerHTML = '<i class="fas fa-save"></i> Save Module';
            moduleForm.reset();
            document.getElementById('moduleId').value = '';
            
            // Clear URL list and add one empty URL input
            urlList.innerHTML = '';
            addUrlInput();
            
            moduleModal.style.display = 'flex';
            
            // Focus on first input
            setTimeout(() => {
                document.getElementById('moduleTitle').focus();
            }, 100);
        }

        function openEditModal(module) {
            isEditing = true;
            modalTitle.textContent = 'Edit Video Module';
            submitBtn.innerHTML = '<i class="fas fa-save"></i> Update Module';
            
            document.getElementById('moduleId').value = module.moduleId;
            document.getElementById('moduleTitle').value = module.title;
            document.getElementById('monthSelect').value = module.monthId;
            
            // Clear URL list and add existing URLs
            urlList.innerHTML = '';
            if (module.videoUrls && module.videoUrls.length > 0) {
                module.videoUrls.forEach(url => addUrlInput(url));
            } else {
                addUrlInput(); // Add one empty URL input if no URLs exist
            }
            
            moduleModal.style.display = 'flex';
            
            // Focus on first input
            setTimeout(() => {
                document.getElementById('moduleTitle').focus();
            }, 100);
        }

        function closeModal() {
            moduleModal.style.display = 'none';
            moduleForm.reset();
            isEditing = false;
        }

        //save
async function handleFormSubmit(e) {
    e.preventDefault();

    const moduleId = document.getElementById('moduleId').value;
    const title = document.getElementById('moduleTitle').value.trim();
    const monthId = parseInt(document.getElementById('monthSelect').value);
    const fileInput = document.getElementById('videoFiles'); // Can hold images/videos/docs

    if (!title) {
        Swal.fire("Error", "Please enter a module title", "error");
        return; 
    }
    if (!monthId) {
        Swal.fire("Error", "Please select a month ID", "error");
        return;
    }
    if (fileInput.files.length === 0 && !moduleId) {
        Swal.fire("Error", "Please upload at least one file", "error");
        return;
    }

    const formData = new FormData();
    formData.append("title", title);
    formData.append("monthId", monthId);

    for (let i = 0; i < fileInput.files.length; i++) {
        formData.append("video", fileInput.files[i]); // match backend @RequestParam("video")
    }

    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
    submitBtn.disabled = true;

    try {
        let url, method;
        if (moduleId) {
            url = `${API_BASE_URL}/modules/update/${moduleId}`;
            method = "PUT";
        } else {
            url = `${API_BASE_URL}/modules/create`;
            method = "POST";
        }

        const response = await fetch(url, {
            method,
            headers: { 'Authorization': `Bearer ${token}` }, // DO NOT set Content-Type
            body: formData
        });

        if (response.ok) {
            Swal.fire("Success", moduleId ? "Module updated!" : "Module created!", "success");
            closeModal();
            await loadModules();
        } else {
            const err = await response.text();
            throw new Error(err || "Failed to save module");
        }
    } catch (err) {
        console.error(err);
        Swal.fire("Error", err.message, "error");
    } finally {
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    }
}


        // Edit module
        async function editModule(moduleId) {
            try {
                showLoading();
                // Try different endpoint structures
                let response = await fetch(`${API_BASE_URL}/modules/${moduleId}`, {
                    method: 'GET',
                    headers: getAuthHeaders()
                });
                
                if (response.status === 404) {
                    // Try alternative endpoint
                    response = await fetch(`${API_BASE_URL}/modules/get/${moduleId}`, {
                        method: 'GET',
                        headers: getAuthHeaders()
                    });
                }
                
                if (handleAuthError(response)) {
                    return;
                }
                
                if (response.ok) {
                    const module = await response.json();
                    openEditModal(module);
                } else {
                    throw new Error(`Failed to fetch module details. Status: ${response.status}`);
                }
            } catch (error) {
                console.error('Error fetching module:', error);
                showErrorAlert('Failed to load module details');
            } finally {
                if (modules.length > 0) {
                    displayModules(modules);
                } else {
                    displayEmptyState();
                }
            }
        }

        // Delete module
        async function deleteModule(moduleId) {
            const result = await Swal.fire({
                title: 'Are you sure?',
                text: 'This will permanently delete the video module.',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#e53e3e',
                cancelButtonColor: '#718096',
                confirmButtonText: 'Yes, delete it!',
                cancelButtonText: 'Cancel',
                background: 'rgba(45, 55, 72, 0.95)',
                color: '#fff',
                customClass: {
                    popup: 'swal-popup',
                    confirmButton: 'swal-confirm',
                    cancelButton: 'swal-cancel'
                }
            });

            if (result.isConfirmed) {
                try {
                    // Try different endpoint structures for deletion
                    let response = await fetch(`${API_BASE_URL}/modules/delete/${moduleId}`, {
                        method: 'DELETE',
                        headers: getAuthHeaders()
                    });
                    
                    if (response.status === 404) {
                        // Try alternative endpoint
                        response = await fetch(`${API_BASE_URL}/modules/${moduleId}`, {
                            method: 'DELETE',
                            headers: getAuthHeaders()
                        });
                    }

                    if (handleAuthError(response)) {
                        return;
                    }

                    if (response.ok) {
                        showSuccessAlert('Module deleted successfully!');
                        await loadModules(); // Reload the modules list
                    } else {
                        throw new Error(`Failed to delete module. Status: ${response.status}`);
                    }
                } catch (error) {
                    console.error('Error deleting module:', error);
                    showErrorAlert('Failed to delete module');
                }
            }
        }

        // Alert functions
        function showSuccessAlert(message) {
            Swal.fire({
                icon: 'success',
                title: 'Success!',
                text: message,
                showConfirmButton: false,
                timer: 2000,
                timerProgressBar: true,
                background: 'rgba(45, 55, 72, 0.95)',
                color: '#fff',
                customClass: {
                    popup: 'swal-popup'
                }
            });
        }

        function showErrorAlert(message) {
            Swal.fire({
                icon: 'error',
                title: 'Error!',
                text: message,
                confirmButtonColor: '#667eea',
                background: 'rgba(45, 55, 72, 0.95)',
                color: '#fff',
                customClass: {
                    popup: 'swal-popup',
                    confirmButton: 'swal-confirm'
                }
            });
        }

        // Add custom styles for SweetAlert2
        function addCustomStyles() {
            const style = document.createElement('style');
            style.textContent = `
                .swal-popup {
                    backdrop-filter: blur(10px) !important;
                    -webkit-backdrop-filter: blur(10px) !important;
                    border: 1px solid rgba(255, 255, 255, 0.2) !important;
                    border-radius: 20px !important;
                }
                
                .swal-confirm, .swal-cancel {
                    border-radius: 25px !important;
                    padding: 10px 20px !important;
                    font-weight: 600 !important;
                    transition: all 0.3s ease !important;
                }
                
                .swal-confirm:hover, .swal-cancel:hover {
                    transform: translateY(-2px) !important;
                    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3) !important;
                }
            `;
            document.head.appendChild(style);
        }

        // Initialize custom styles
        addCustomStyles();