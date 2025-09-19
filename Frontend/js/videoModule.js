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
//         // Try different endpoints for months
//         let response = await fetch(`${API_BASE_URL}/months/all`, {
//             method: 'GET',
//             headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' }
//         });
        
//         if (response.status === 404) {
//             // Try alternative endpoint
//             response = await fetch(`${API_BASE_URL}/batch-months`, {
//                 method: 'GET',
//                 headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' }
//             });
//         }

//         if (handleAuthError(response)) return;

//         if (response.ok) {
//             months = await response.json();
//         } else {
//             // Create fallback month IDs if API fails
//             months = Array.from({ length: 12 }, (_, i) => ({ monthId: i + 1 }));
//         }
//     } catch {
//         // Create fallback month IDs if network error
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
//         if (module.videoUrls && module.videoUrls.length > 0) {
//             videoDisplay = `${module.videoUrls.length} file(s)`;
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
//     if (!module || !module.videoUrls || module.videoUrls.length === 0) {
//         showErrorAlert('No video files found for this module.');
//         return;
//     }

//     let videoListHtml = '';
//     module.videoUrls.forEach(fileUrl => {
//         // Check if it's a video file
//         if (fileUrl.match(/\.(mp4|webm|ogg)$/i)) {
//             videoListHtml += `
//                 <div style="margin-bottom: 15px;">
//                     <video controls width="100%">
//                         <source src="${fileUrl}" type="video/mp4">
//                         Your browser does not support the video tag.
//                     </video>
//                     <div style="margin-top: 5px;">
//                         <a href="${fileUrl}" target="_blank" style="color: #4facfe;">Download/View</a>
//                     </div>
//                 </div>
//             `;
//         } else {
//             // For non-video files, show a download link
//             videoListHtml += `
//                 <div style="margin-bottom: 15px; padding: 10px; background: rgba(255,255,255,0.1); border-radius: 5px;">
//                     <i class="fas fa-file" style="margin-right: 10px;"></i>
//                     <a href="${fileUrl}" target="_blank" style="color: #4facfe;">${fileUrl}</a>
//                 </div>
//             `;
//         }
//     });

//     Swal.fire({
//         title: `Files for: ${module.title}`,
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
// // async function handleFormSubmit(e) {
// //     e.preventDefault();

// //     const moduleId = document.getElementById('moduleId').value;
// //     const title = document.getElementById('moduleTitle').value.trim();
// //     const monthId = parseInt(document.getElementById('monthSelect').value);
// //     const fileInput = document.getElementById('videoFiles');

// //     if (!title) {
// //         Swal.fire("Error", "Please enter a module title", "error");
// //         return; 
// //     }
// //     if (!monthId) {
// //         Swal.fire("Error", "Please select a month ID", "error");
// //         return;
// //     }
// //     if (fileInput.files.length === 0 && !moduleId) {
// //         Swal.fire("Error", "Please upload at least one file", "error");
// //         return;
// //     }

// //     const formData = new FormData();
// //     formData.append("title", title);
// //     formData.append("monthId", monthId);

// //     for (let i = 0; i < fileInput.files.length; i++) {
// //         formData.append("video", fileInput.files[i]);
// //     }

// //     const originalText = submitBtn.innerHTML;
// //     submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
// //     submitBtn.disabled = true;

// //     try {
// //         let url, method;
// //         if (moduleId) {
// //             url = `${API_BASE_URL}/modules/update/${moduleId}`;
// //             method = "PUT";
// //         } else {
// //             url = `${API_BASE_URL}/modules/create`;
// //             method = "POST";
// //         }

// //         const response = await fetch(url, {
// //             method,
// //             headers: { 'Authorization': `Bearer ${token}` },
// //             body: formData
// //         });

// //         if (response.ok) {
// //             Swal.fire("Success", moduleId ? "Module updated!" : "Module created!", "success");
// //             closeModal();
// //             await loadModules();
// //         } else {
// //             const err = await response.text();
// //             throw new Error(err || "Failed to save module");
// //         }
// //     } catch (err) {
// //         console.error(err);
// //         Swal.fire("Error", err.message, "error");
// //     } finally {
// //         submitBtn.innerHTML = originalText;
// //         submitBtn.disabled = false;
// //     }
// // }
// // Handle form submit
// async function handleFormSubmit(e) {
//     e.preventDefault();

//     const moduleId = document.getElementById('moduleId').value;
//     const title = document.getElementById('moduleTitle').value.trim();
//     const monthId = parseInt(document.getElementById('monthSelect').value);
//     const fileInput = document.getElementById('videoFiles');

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
//         formData.append("video", fileInput.files[i]);
//     }

//     const originalText = submitBtn.innerHTML;
//     submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
//     submitBtn.disabled = true;

//     try {
//         let url, method, headers;
        
//         if (moduleId) {
//             // Use the new endpoint for updates with files
//             url = `${API_BASE_URL}/modules/update-with-files/${moduleId}`;
//             method = "PUT";
//             headers = { 'Authorization': `Bearer ${token}` };
//             // Don't set Content-Type for FormData, browser will set it automatically
//         } else {
//             url = `${API_BASE_URL}/modules/create`;
//             method = "POST";
//             headers = { 'Authorization': `Bearer ${token}` };
//             // Don't set Content-Type for FormData
//         }

//         const response = await fetch(url, {
//             method,
//             headers,
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
// // View module videos with delete option
// function viewModule(moduleId) {
//     const module = modules.find(m => m.moduleId === moduleId);
//     if (!module || !module.videoUrls || module.videoUrls.length === 0) {
//         showErrorAlert('No video files found for this module.');
//         return;
//     }

//     let videoListHtml = '';
//     module.videoUrls.forEach((fileUrl, index) => {
//         // Check if it's a video file
//         if (fileUrl.match(/\.(mp4|webm|ogg)$/i)) {
//             videoListHtml += `
//                 <div style="margin-bottom: 15px; position: relative;">
//                     <video controls width="100%">
//                         <source src="${fileUrl}" type="video/mp4">
//                         Your browser does not support the video tag.
//                     </video>
//                     <div style="margin-top: 5px; display: flex; justify-content: space-between; align-items: center;">
//                         <a href="${fileUrl}" target="_blank" style="color: #4facfe;">Download/View</a>
//                         <button onclick="deleteVideo(${moduleId}, ${index})" style="background: #e53e3e; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">
//                             <i class="fas fa-trash"></i> Delete
//                         </button>
//                     </div>
//                 </div>
//             `;
//         } else {
//             // For non-video files, show a download link
//             videoListHtml += `
//                 <div style="margin-bottom: 15px; padding: 10px; background: rgba(255,255,255,0.1); border-radius: 5px; position: relative;">
//                     <i class="fas fa-file" style="margin-right: 10px;"></i>
//                     <a href="${fileUrl}" target="_blank" style="color: #4facfe;">${fileUrl}</a>
//                     <button onclick="deleteVideo(${moduleId}, ${index})" style="position: absolute; right: 10px; top: 10px; background: #e53e3e; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">
//                         <i class="fas fa-trash"></i>
//                     </button>
//                 </div>
//             `;
//         }
//     });

//     Swal.fire({
//         title: `Files for: ${module.title}`,
//         html: videoListHtml,
//         width: 600,
//         background: 'rgba(45, 55, 72, 0.95)',
//         color: '#fff'
//     });
// }

// // Delete individual video
// async function deleteVideo(moduleId, index) {
//     try {
//         const response = await fetch(`${API_BASE_URL}/modules/${moduleId}/videos/${index}`, {
//             method: 'DELETE',
//             headers: {
//                 'Authorization': `Bearer ${token}`,
//                 'Content-Type': 'application/json'
//             }
//         });
        
//         if (response.ok) {
//             showSuccessAlert('Video deleted successfully!');
//             await loadModules();
//             // Close and reopen the view modal to refresh the list
//             Swal.close();
//             viewModule(moduleId);
//         } else {
//             throw new Error('Failed to delete video');
//         }
//     } catch (error) {
//         console.error('Error deleting video:', error);
//         showErrorAlert('Failed to delete video');
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
const videoFilesInput = document.getElementById('videoFiles');

// Initialize
document.addEventListener('DOMContentLoaded', function () {
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

    if (menuToggle) {
        menuToggle.addEventListener('click', function () {
            sidebar.classList.toggle('active');
        });
    }

    moduleModal.addEventListener('click', function (e) {
        if (e.target === moduleModal) {
            closeModal();
        }
    });

    document.addEventListener('click', function (e) {
        if (window.innerWidth <= 768 && !sidebar.contains(e.target) && !menuToggle.contains(e.target)) {
            sidebar.classList.remove('active');
        }
    });

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && moduleModal.style.display === 'flex') {
            closeModal();
        }
    });
}

// Headers with token
function getAuthHeaders() {
    return {
        'Authorization': `Bearer ${token}`
    };
}

// Handle auth errors
function handleAuthError(response) {
    if (response.status === 403 || response.status === 401) {
        showErrorAlert('Access Denied: You do not have permission or your session has expired.');
        return true;
    }
    return false;
}

// Load months
async function loadMonths() {
    try {
        let response = await fetch(`${API_BASE_URL}/months/all`, {
            method: 'GET',
            headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' }
        });
        
        if (response.status === 404) {
            response = await fetch(`${API_BASE_URL}/batch-months`, {
                method: 'GET',
                headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' }
            });
        }

        if (handleAuthError(response)) return;

        if (response.ok) {
            months = await response.json();
        } else {
            months = Array.from({ length: 12 }, (_, i) => ({ monthId: i + 1 }));
        }
    } catch {
        months = Array.from({ length: 12 }, (_, i) => ({ monthId: i + 1 }));
        showErrorAlert('Failed to load months, using available data');
    }
    populateMonthDropdowns();
}

// Populate month dropdowns
function populateMonthDropdowns() {
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

// Load modules
async function loadModules() {
    try {
        showLoading();
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
            throw new Error(`Failed to load modules. Status: ${response.status}`);
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

        let videoDisplay = '';
        if (module.videoUrls && module.videoUrls.length > 0) {
            videoDisplay = `${module.videoUrls.length} file(s)`;
        }

        row.innerHTML = `
            <td>${module.moduleId}</td>
            <td>${escapeHtml(module.title)}</td>
            <td>${videoDisplay}</td>
            <td>${module.monthId}</td>
            <td>
                <button class="action-btn view" onclick="viewModule(${module.moduleId})" title="View Videos">
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

// Escape HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Empty state
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

// Loading state
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

// Filter modules
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

// View module videos
function viewModule(moduleId) {
    const module = modules.find(m => m.moduleId === moduleId);
    if (!module || !module.videoUrls || module.videoUrls.length === 0) {
        showErrorAlert('No video files found for this module.');
        return;
    }

    let videoListHtml = '';
    module.videoUrls.forEach((fileUrl, index) => {
        if (fileUrl.match(/\.(mp4|webm|ogg)$/i)) {
            videoListHtml += `
                <div style="margin-bottom: 15px; position: relative;">
                    <video controls width="100%">
                        <source src="${fileUrl}" type="video/mp4">
                        Your browser does not support the video tag.
                    </video>
                    <div style="margin-top: 5px; display: flex; justify-content: space-between; align-items: center;">
                        <a href="${fileUrl}" target="_blank" style="color: #4facfe;">Download/View</a>
                        <button onclick="deleteVideo(${moduleId}, ${index})" style="background: #e53e3e; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">
                            <i class="fas fa-trash"></i> Delete
                        </button>
                    </div>
                </div>
            `;
        } else {
            videoListHtml += `
                <div style="margin-bottom: 15px; padding: 10px; background: rgba(255,255,255,0.1); border-radius: 5px; position: relative;">
                    <i class="fas fa-file" style="margin-right: 10px;"></i>
                    <a href="${fileUrl}" target="_blank" style="color: #4facfe;">${fileUrl}</a>
                    <button onclick="deleteVideo(${moduleId}, ${index})" style="position: absolute; right: 10px; top: 10px; background: #e53e3e; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            `;
        }
    });

    Swal.fire({
        title: `Files for: ${module.title}`,
        html: videoListHtml,
        width: 600,
        background: 'rgba(45, 55, 72, 0.95)',
        color: '#fff'
    });
}

// Modal functions
function openAddModal() {
    isEditing = false;
    modalTitle.textContent = 'Add New Video Module';
    submitBtn.innerHTML = '<i class="fas fa-save"></i> Save Module';
    moduleForm.reset();
    document.getElementById('moduleId').value = '';
    videoFilesInput.value = "";
    moduleModal.style.display = 'flex';
}

function openEditModal(module) {
    isEditing = true;
    modalTitle.textContent = 'Edit Video Module';
    submitBtn.innerHTML = '<i class="fas fa-save"></i> Update Module';

    document.getElementById('moduleId').value = module.moduleId;
    document.getElementById('moduleTitle').value = module.title;
    document.getElementById('monthSelect').value = module.monthId;
    videoFilesInput.value = "";

    moduleModal.style.display = 'flex';
}

function closeModal() {
    moduleModal.style.display = 'none';
    moduleForm.reset();
    isEditing = false;
}

// Handle form submit - FIXED VERSION
async function handleFormSubmit(e) {
    e.preventDefault();

    const moduleId = document.getElementById('moduleId').value;
    const title = document.getElementById('moduleTitle').value.trim();
    const monthId = parseInt(document.getElementById('monthSelect').value);
    const fileInput = document.getElementById('videoFiles');

    // Validation
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

    // Create FormData
    const formData = new FormData();
    formData.append("title", title);
    formData.append("monthId", monthId);

    // CRITICAL FIX: Use correct parameter name that matches your controller
    if (fileInput.files.length > 0) {
        for (let i = 0; i < fileInput.files.length; i++) {
            formData.append("files", fileInput.files[i]); // Changed from "video" to "files"
        }
    }

    // Debug: Log FormData contents
    console.log("FormData contents:");
    for (let [key, value] of formData.entries()) {
        console.log(`${key}:`, value);
    }

    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
    submitBtn.disabled = true;

    try {
        let url, method;
        
        if (moduleId && fileInput.files.length > 0) {
            // Update with new files
            url = `${API_BASE_URL}/modules/update-with-files/${moduleId}`;
            method = "PUT";
        } else if (moduleId) {
            // Update without files (use JSON endpoint)
            url = `${API_BASE_URL}/modules/update/${moduleId}`;
            method = "PUT";
            
            // For JSON update, send as JSON instead of FormData
            const response = await fetch(url, {
                method,
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    title: title,
                    monthId: monthId,
                    videoUrls: [] // Keep existing URLs, or get them first
                })
            });

            if (response.ok) {
                Swal.fire("Success", "Module updated!", "success");
                closeModal();
                await loadModules();
                return;
            } else {
                const err = await response.text();
                throw new Error(err || "Failed to update module");
            }
        } else {
            // Create new module
            url = `${API_BASE_URL}/modules/create`;
            method = "POST";
        }

        // For create or update with files, use FormData
        const response = await fetch(url, {
            method,
            headers: {
                'Authorization': `Bearer ${token}`
                // DON'T set Content-Type for FormData - browser will set it automatically with boundary
            },
            body: formData
        });

        if (response.ok) {
            const result = await response.json();
            console.log("Server response:", result);
            Swal.fire("Success", moduleId ? "Module updated!" : "Module created!", "success");
            closeModal();
            await loadModules();
        } else {
            const errorText = await response.text();
            console.error("Server error:", errorText);
            throw new Error(errorText || `Server error: ${response.status}`);
        }
    } catch (err) {
        console.error("Upload error:", err);
        Swal.fire("Error", `Failed to save module: ${err.message}`, "error");
    } finally {
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    }
}

// Edit module
async function editModule(moduleId) {
    try {
        const response = await fetch(`${API_BASE_URL}/modules/${moduleId}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });

        if (handleAuthError(response)) return;

        if (response.ok) {
            const module = await response.json();
            openEditModal(module);
        } else {
            throw new Error(`Failed to fetch module details. Status: ${response.status}`);
        }
    } catch (error) {
        console.error('Error fetching module:', error);
        showErrorAlert('Failed to load module details');
    }
}

// Delete individual video
async function deleteVideo(moduleId, index) {
    try {
        const response = await fetch(`${API_BASE_URL}/modules/${moduleId}/videos/${index}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            showSuccessAlert('Video deleted successfully!');
            await loadModules();
            Swal.close();
            viewModule(moduleId);
        } else {
            throw new Error('Failed to delete video');
        }
    } catch (error) {
        console.error('Error deleting video:', error);
        showErrorAlert('Failed to delete video');
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
        color: '#fff'
    });

    if (result.isConfirmed) {
        try {
            const response = await fetch(`${API_BASE_URL}/modules/delete/${moduleId}`, {
                method: 'DELETE',
                headers: getAuthHeaders()
            });

            if (handleAuthError(response)) return;

            if (response.ok) {
                showSuccessAlert('Module deleted successfully!');
                await loadModules();
            } else {
                throw new Error(`Failed to delete module. Status: ${response.status}`);
            }
        } catch (error) {
            console.error('Error deleting module:', error);
            showErrorAlert('Failed to delete module');
        }
    }
}

// Alerts
function showSuccessAlert(message) {
    Swal.fire({
        icon: 'success',
        title: 'Success!',
        text: message,
        showConfirmButton: false,
        timer: 2000,
        background: 'rgba(45, 55, 72, 0.95)',
        color: '#fff'
    });
}

function showErrorAlert(message) {
    Swal.fire({
        icon: 'error',
        title: 'Error!',
        text: message,
        confirmButtonColor: '#667eea',
        background: 'rgba(45, 55, 72, 0.95)',
        color: '#fff'
    });
}