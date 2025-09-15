 // Constants
        const API_BASE_URL = 'http://localhost:8080/api';

        // Get token from storage with validation
        function getAuthToken() {
            let token = localStorage.getItem('accessToken') || getCookie('authToken');
            
            // Validate token format
            if (token && isValidJwt(token)) {
                return token;
            } else {
                console.warn('Invalid or missing JWT token');
                // Clear invalid token
                localStorage.removeItem('accessToken');
                document.cookie = 'authToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
                return null;
            }
        }

        // Basic JWT validation
        function isValidJwt(token) {
            if (!token) return false;
            const parts = token.split('.');
            return parts.length === 3;
        }

        // Utility function to get cookie value
        function getCookie(name) {
            const value = `; ${document.cookie}`;
            const parts = value.split(`; ${name}=`);
            if (parts.length === 2) return parts.pop().split(';').shift();
            return null;
        }

        // DOM elements
        const userAvatar = document.getElementById('userAvatar');
        const profileAvatar = document.getElementById('profileAvatar');
        const profileDropdown = document.getElementById('profileDropdown');
        const profileUsername = document.getElementById('profileUsername');
        const profileRole = document.getElementById('profileRole');
        const profileEmail = document.getElementById('profileEmail');
        const profilePhone = document.getElementById('profilePhone');
        const profileAccountType = document.getElementById('profileAccountType');
        const overlay = document.getElementById('overlay');
        const editProfileModal = document.getElementById('editProfileModal');
        const profileForm = document.getElementById('profileForm');
        const closeModal = document.getElementById('closeModal');
        const cancelEdit = document.getElementById('cancelEdit');
        const editProfileBtn = document.getElementById('editProfileBtn');
        const mobileMenu = document.getElementById('mobileMenu');
        const navMenu = document.getElementById('navMenu');
        const backToTop = document.getElementById('backToTop');

        // Current user data
        let currentUser = null;

        // Initialize the page
        document.addEventListener('DOMContentLoaded', () => {
            initializeApp();
        });

        // Initialize the application
        function initializeApp() {
            // Load the current user from the API
            loadCurrentUser();
            setupEventListeners();
            
            // Set up other functionality
            setupGeneralFunctionality();
            
            // Set up mobile menu
            setupMobileMenu();
        }

        // Set up event listeners
        function setupEventListeners() {
            // Profile dropdown functionality
            if (userAvatar) {
                userAvatar.addEventListener('click', toggleProfileDropdown);
            }
            
            if (overlay) {
                overlay.addEventListener('click', closeDropdown);
            }
            
            document.addEventListener('click', closeDropdown);
            
            if (profileDropdown) {
                profileDropdown.addEventListener('click', (e) => {
                    e.stopPropagation();
                });
            }
            
            // Edit profile functionality
            if (editProfileBtn) {
                editProfileBtn.addEventListener('click', openEditModal);
            }
            
            if (closeModal) {
                closeModal.addEventListener('click', closeEditModal);
            }
            
            if (cancelEdit) {
                cancelEdit.addEventListener('click', closeEditModal);
            }
            
            if (profileForm) {
                profileForm.addEventListener('submit', handleProfileUpdate);
            }
            
            // Close modal when clicking outside
            window.addEventListener('click', (e) => {
                if (e.target === editProfileModal) {
                    closeEditModal();
                }
            });
        }

        // Set up mobile menu functionality
        function setupMobileMenu() {
            if (!mobileMenu || !navMenu) return;
            
            mobileMenu.addEventListener('click', () => {
                navMenu.classList.toggle('active');
                mobileMenu.classList.toggle('active');
                overlay.classList.toggle('active');
                
                // Toggle the menu icon between bars and times
                const menuIcon = mobileMenu.querySelector('i');
                if (menuIcon) {
                    if (navMenu.classList.contains('active')) {
                        menuIcon.classList.remove('fa-bars');
                        menuIcon.classList.add('fa-times');
                    } else {
                        menuIcon.classList.remove('fa-times');
                        menuIcon.classList.add('fa-bars');
                    }
                }
                
                // Prevent body scrolling when menu is open
                document.body.style.overflow = navMenu.classList.contains('active') ? 'hidden' : '';
            });
            
            // Close menu when clicking on links
            const navLinks = navMenu.querySelectorAll('a');
            navLinks.forEach(link => {
                link.addEventListener('click', () => {
                    navMenu.classList.remove('active');
                    mobileMenu.classList.remove('active');
                    overlay.classList.remove('active');
                    
                    // Reset icon
                    const menuIcon = mobileMenu.querySelector('i');
                    if (menuIcon) {
                        menuIcon.classList.remove('fa-times');
                        menuIcon.classList.add('fa-bars');
                    }
                    
                    document.body.style.overflow = '';
                });
            });
            
            // Close menu when clicking outside
            document.addEventListener('click', (e) => {
                if (navMenu.classList.contains('active') && 
                    !navMenu.contains(e.target) && 
                    e.target !== mobileMenu &&
                    !mobileMenu.contains(e.target)) {
                    navMenu.classList.remove('active');
                    mobileMenu.classList.remove('active');
                    overlay.classList.remove('active');
                    
                    // Reset icon
                    const menuIcon = mobileMenu.querySelector('i');
                    if (menuIcon) {
                        menuIcon.classList.remove('fa-times');
                        menuIcon.classList.add('fa-bars');
                    }
                    
                    document.body.style.overflow = '';
                }
            });
            
            // Close menu on escape key
            document.addEventListener('keydown', (e) => {
                if (e.key === 'Escape' && navMenu.classList.contains('active')) {
                    navMenu.classList.remove('active');
                    mobileMenu.classList.remove('active');
                    overlay.classList.remove('active');
                    
                    // Reset icon
                    const menuIcon = mobileMenu.querySelector('i');
                    if (menuIcon) {
                        menuIcon.classList.remove('fa-times');
                        menuIcon.classList.add('fa-bars');
                    }
                    
                    document.body.style.overflow = '';
                }
            });
        }

        // Set up general functionality
        function setupGeneralFunctionality() {
            // Loading screen
            window.addEventListener('load', () => {
                setTimeout(() => {
                    const loader = document.getElementById('loader');
                    if (loader) loader.classList.add('hidden');
                }, 1500);
            });

            // Scroll progress bar
            window.addEventListener('scroll', () => {
                const scrollProgress = document.getElementById('scrollProgress');
                if (scrollProgress) {
                    const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
                    const scrollHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
                    const progress = (scrollTop / scrollHeight) * 100;
                    scrollProgress.style.width = progress + '%';
                }
            });

            // Enhanced navbar scroll effect
            let lastScrollTop = 0;
            window.addEventListener('scroll', () => {
                const navbar = document.getElementById('navbar');
                if (navbar) {
                    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
                    
                    if (scrollTop > 100) {
                        navbar.classList.add('scrolled');
                    } else {
                        navbar.classList.remove('scrolled');
                    }

                    // Hide navbar on scroll down, show on scroll up
                    if (scrollTop > lastScrollTop && scrollTop > 200) {
                        navbar.style.transform = 'translateY(-100%)';
                    } else {
                        navbar.style.transform = 'translateY(0)';
                    }
                    lastScrollTop = scrollTop;
                }
            });

            // Back to top button
            if (backToTop) {
                window.addEventListener('scroll', () => {
                    if (window.pageYOffset > 300) {
                        backToTop.classList.add('visible');
                    } else {
                        backToTop.classList.remove('visible');
                    }
                });

                backToTop.addEventListener('click', () => {
                    window.scrollTo({
                        top: 0,
                        behavior: 'smooth'
                    });
                });
            }

            // Enhanced smooth scrolling
            document.querySelectorAll('a[href^="#"]').forEach(anchor => {
                anchor.addEventListener('click', function (e) {
                    e.preventDefault();
                    const target = document.querySelector(this.getAttribute('href'));
                    if (target) {
                        const headerOffset = 100;
                        const elementPosition = target.offsetTop;
                        const offsetPosition = elementPosition - headerOffset;

                        window.scrollTo({
                            top: offsetPosition,
                            behavior: 'smooth'
                        });
                    }
                });
            });

            // Counter animation for stats
            const animateCounters = () => {
                const counters = document.querySelectorAll('[data-target]');
                const observer = new IntersectionObserver((entries) => {
                    entries.forEach(entry => {
                        if (entry.isIntersecting) {
                            const counter = entry.target;
                            const target = parseInt(counter.getAttribute('data-target'));
                            let current = 0;
                            const increment = target / 100;
                            const suffix = counter.textContent.replace(/[\d,]/g, '') || '';

                            const updateCounter = () => {
                                current += increment;
                                if (current >= target) {
                                    counter.textContent = target.toLocaleString() + (suffix.includes('%') ? '%' : (target >= 1000 ? '+' : ''));
                                } else {
                                    counter.textContent = Math.floor(current).toLocaleString() + (target >= 1000 ? '+' : '');
                                    requestAnimationFrame(updateCounter);
                                }
                            };

                            updateCounter();
                            observer.unobserve(counter);
                        }
                    });
                }, { threshold: 0.7 });

                counters.forEach(counter => observer.observe(counter));
            };

            // Initialize counter animation
            animateCounters();

            // Intersection Observer for fade-in animations
            const observeElements = () => {
                const observer = new IntersectionObserver((entries) => {
                    entries.forEach(entry => {
                        if (entry.isIntersecting) {
                            entry.target.style.animationPlayState = 'running';
                        }
                    });
                }, { threshold: 0.1, rootMargin: '0px 0px -50px 0px' });

                document.querySelectorAll('.fade-in-up, .scale-in').forEach(el => {
                    el.style.animationPlayState = 'paused';
                    observer.observe(el);
                });
            };

            // Initialize animations
            observeElements();

            // Search functionality
            const searchBar = document.querySelector('.search-bar');
            const searchBtn = document.querySelector('.search-btn');
            
            if (searchBtn && searchBar) {
                searchBtn.addEventListener('click', () => {
                    if (searchBar.value.trim() !== '') {
                        alert(`Searching for: ${searchBar.value}`);
                        searchBar.value = '';
                    }
                });
                
                searchBar.addEventListener('keypress', (e) => {
                    if (e.key === 'Enter' && searchBar.value.trim() !== '') {
                        alert(`Searching for: ${searchBar.value}`);
                        searchBar.value = '';
                    }
                });
            }

            // Particle animation for hero section
            const createParticles = () => {
                const particlesContainer = document.querySelector('.particles');
                if (!particlesContainer) return;
                
                for (let i = 0; i < 20; i++) {
                    const particle = document.createElement('div');
                    particle.classList.add('particle');
                    particle.style.left = `${Math.random() * 100}%`;
                    particle.style.top = `${Math.random() * 100}%`;
                    particle.style.width = `${Math.random() * 10 + 2}px`;
                    particle.style.height = particle.style.width;
                    particle.style.animationDuration = `${Math.random() * 10 + 5}s`;
                    particlesContainer.appendChild(particle);
                }
            };

            createParticles();
            
            // Setup chatbot functionality
            setupChatbot();
        }

        // Setup chatbot functionality
        function setupChatbot() {
            const chatbot = document.getElementById('chatbotButton');
            if (!chatbot) return;
            
            let isDragging = false;
            let currentX;
            let currentY;
            let initialX;
            let initialY;
            let xOffset = 0;
            let yOffset = 0;
            
            // Reset position to default if it's causing issues
            localStorage.removeItem('chatbotPosition');
            
            // Load saved position if available
            const savedPosition = localStorage.getItem('chatbotPosition');
            if (savedPosition) {
                try {
                    const position = JSON.parse(savedPosition);
                    // Make sure the position is within viewport
                    if (position.x !== undefined && position.y !== undefined) {
                        chatbot.style.right = 'auto';
                        chatbot.style.bottom = 'auto';
                        chatbot.style.left = Math.min(position.x, window.innerWidth - 70) + 'px';
                        chatbot.style.top = Math.min(position.y, window.innerHeight - 70) + 'px';
                        xOffset = position.x;
                        yOffset = position.y;
                    }
                } catch (e) {
                    console.error('Error loading chatbot position:', e);
                    // Reset to default position if there's an error
                    resetChatbotPosition();
                }
            } else {
                // Set default position
                resetChatbotPosition();
            }
            
            function resetChatbotPosition() {
                chatbot.style.right = '30px';
                chatbot.style.bottom = '30px';
                chatbot.style.left = 'auto';
                chatbot.style.top = 'auto';
                xOffset = 0;
                yOffset = 0;
            }
            
            chatbot.addEventListener('mousedown', dragStart);
            chatbot.addEventListener('touchstart', dragStart);
            
            document.addEventListener('mouseup', dragEnd);
            document.addEventListener('touchend', dragEnd);
            
            document.addEventListener('mousemove', drag);
            document.addEventListener('touchmove', drag);
            
            function dragStart(e) {
                if (e.type === "touchstart") {
                    initialX = e.touches[0].clientX - xOffset;
                    initialY = e.touches[0].clientY - yOffset;
                } else {
                    initialX = e.clientX - xOffset;
                    initialY = e.clientY - yOffset;
                }
                
                if (e.target === chatbot || e.target.parentNode === chatbot) {
                    isDragging = true;
                    
                    // Remove pulse animation while dragging
                    chatbot.style.animation = 'none';
                    
                    // Switch to absolute positioning for dragging
                    const rect = chatbot.getBoundingClientRect();
                    chatbot.style.position = 'fixed';
                    chatbot.style.right = 'auto';
                    chatbot.style.bottom = 'auto';
                    chatbot.style.left = rect.left + 'px';
                    chatbot.style.top = rect.top + 'px';
                }
            }
            
            function dragEnd(e) {
                if (!isDragging) return;
                
                initialX = currentX;
                initialY = currentY;
                isDragging = false;
                
                // Save position to localStorage
                localStorage.setItem('chatbotPosition', JSON.stringify({
                    x: xOffset,
                    y: yOffset
                }));
                
                // Restore pulse animation
                setTimeout(() => {
                    chatbot.style.animation = 'pulse 2s infinite';
                }, 100);
            }
            
            function drag(e) {
                if (isDragging) {
                    e.preventDefault();
                    
                    if (e.type === "touchmove") {
                        currentX = e.touches[0].clientX - initialX;
                        currentY = e.touches[0].clientY - initialY;
                    } else {
                        currentX = e.clientX - initialX;
                        currentY = e.clientY - initialY;
                    }
                    
                    // Keep within viewport boundaries
                    currentX = Math.max(0, Math.min(currentX, window.innerWidth - chatbot.offsetWidth));
                    currentY = Math.max(0, Math.min(currentY, window.innerHeight - chatbot.offsetHeight));
                    
                    xOffset = currentX;
                    yOffset = currentY;
                    
                    setTranslate(currentX, currentY, chatbot);
                }
            }
            
            function setTranslate(xPos, yPos, el) {
                el.style.transform = "translate3d(" + xPos + "px, " + yPos + "px, 0)";
            }
            
            // Add click effect
            chatbot.addEventListener('click', function(e) {
                // Only navigate if it wasn't a drag operation
                if (!isDragging) {
                    e.preventDefault();
                    
                    // Create ripple effect
                    const ripple = document.createElement('div');
                    ripple.style.position = 'absolute';
                    ripple.style.borderRadius = '50%';
                    ripple.style.backgroundColor = 'rgba(255, 255, 255, 0.4)';
                    ripple.style.width = '0px';
                    ripple.style.height = '0px';
                    ripple.style.top = '50%';
                    ripple.style.left = '50%';
                    ripple.style.transform = 'translate(-50%, -50%)';
                    ripple.style.transition = 'all 0.5s ease';
                    
                    this.appendChild(ripple);
                    
                    // Animate ripple
                    setTimeout(() => {
                        ripple.style.width = '100px';
                        ripple.style.height = '100px';
                        ripple.style.opacity = '0';
                    }, 10);
                    
                    // Navigate after animation
                    setTimeout(() => {
                        window.location.href = 'chatAssistant.html';
                    }, 500);
                }
            });
            
            // Make notification badge count down from 3 to 0
            const badge = document.querySelector('.notification-badge');
            if (badge) {
                let count = 3;
                
                setInterval(() => {
                    if (count > 0) {
                        count--;
                        badge.textContent = count;
                        
                        if (count === 0) {
                            badge.style.display = 'none';
                        }
                    }
                }, 5000);
            }
        }

        // Load current user data from API
      async function loadCurrentUser() {
    const USER_TOKEN = getAuthToken();
    
    if (!USER_TOKEN) {
        console.log('No valid token found, using mock data');
        showPlaceholderUserData();
        showDemoModeNotification();
        return;
    }

    try {
        console.log('Attempting to fetch current user...');
        const response = await fetch(`${API_BASE_URL}/user/current`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${USER_TOKEN}`,
                'Content-Type': 'application/json'
            }
        });
        
        console.log('Response status:', response.status);
        
        if (response.status === 401) {
            // Token expired or invalid
            localStorage.removeItem('accessToken');
            document.cookie = 'authToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
            showPlaceholderUserData();
            showError('Session expired. Please login again.');
            return;
        }
        
        // Log the raw response text to see what's actually returned
        const responseText = await response.text();
        console.log('Raw response:', responseText);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}, response: ${responseText}`);
        }
        
        // Try to parse as JSON
        try {
            currentUser = JSON.parse(responseText);
            console.log('Parsed user data:', currentUser);
            populateUserData(currentUser);
        } catch (parseError) {
            console.error('Failed to parse JSON:', parseError);
            showPlaceholderUserData();
            showError('Invalid response format from server');
        }
        
    } catch (error) {
        console.error('Error fetching current user:', error);
        // ... rest of error handling
    }
}

        // Show placeholder data when API fails
        function showPlaceholderUserData() {
            const firstLetter = 'U';
            if (userAvatar) userAvatar.textContent = firstLetter;
            if (profileAvatar) profileAvatar.textContent = firstLetter;
            
            if (profileUsername) profileUsername.textContent = 'Guest User';
            if (profileRole) profileRole.textContent = 'USER Account';
            if (profileEmail) profileEmail.textContent = 'user@example.com';
            if (profilePhone) profilePhone.textContent = 'Not provided';
            if (profileAccountType) profileAccountType.textContent = 'USER';
        }

        // Show notification that demo data is being used
        function showDemoModeNotification() {
            // Create a subtle notification
            const notification = document.createElement('div');
            notification.style.position = 'fixed';
            notification.style.top = '20px';
            notification.style.right = '20px';
            notification.style.background = 'rgba(255, 193, 7, 0.9)';
            notification.style.color = '#000';
            notification.style.padding = '10px 15px';
            notification.style.borderRadius = '5px';
            notification.style.zIndex = '10000';
            notification.style.fontSize = '14px';
            notification.style.boxShadow = '0 2px 10px rgba(0,0,0,0.1)';
            notification.innerHTML = '⚠️ Using demo data - Backend not connected';
            
            document.body.appendChild(notification);
            
            // Auto-remove after 5 seconds
            setTimeout(() => {
                if (document.body.contains(notification)) {
                    document.body.removeChild(notification);
                }
            }, 5000);
        }

        // Populate user data in the UI
        // Populate user data in the UI
// Populate user data in the UI
function populateUserData(user) {
    if (!user) return;
    
    console.log('User object received:', user); // Debug the actual structure
    
    // Set avatar initials
    const firstLetter = user.username ? user.username.charAt(0).toUpperCase() : 'U';
    if (userAvatar) userAvatar.textContent = firstLetter;
    if (profileAvatar) profileAvatar.textContent = firstLetter;
    
    // Set user details - use the exact property names from your User entity
    if (profileUsername) profileUsername.textContent = user.username || 'N/A';
    if (profileRole) profileRole.textContent = `${user.role || 'USER'} Account`;
    if (profileEmail) profileEmail.textContent = user.email || 'N/A';
    if (profilePhone) profilePhone.textContent = user.number || 'Not provided';
    if (profileAccountType) profileAccountType.textContent = user.role || 'USER';
    
    // Store the user ID for update operations
    currentUser = user;
}

        // Toggle profile dropdown
        function toggleProfileDropdown() {
            if (!profileDropdown || !overlay) return;
            
            profileDropdown.classList.toggle('active');
            overlay.classList.toggle('active');
            
            // Prevent body scrolling when dropdown is open
            document.body.style.overflow = profileDropdown.classList.contains('active') ? 'hidden' : '';
        }

        // Close dropdown when clicking outside
        function closeDropdown(e) {
            if (!profileDropdown || !userAvatar || !overlay) return;
            
            if (!profileDropdown.contains(e.target) && e.target !== userAvatar) {
                profileDropdown.classList.remove('active');
                overlay.classList.remove('active');
                document.body.style.overflow = '';
            }
        }

        // Open edit modal with user data
        // Open edit modal with user data
// Open edit modal with user data
function openEditModal() {
    if (!currentUser || !editProfileModal) return;
    
    document.getElementById('editUsername').value = currentUser.username || '';
    document.getElementById('editEmail').value = currentUser.email || '';
    document.getElementById('editPhone').value = currentUser.number || '';
    
    editProfileModal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

// Handle profile form submission
async function handleProfileUpdate(e) {
    e.preventDefault();
    
    if (!currentUser) return;
    
    const USER_TOKEN = getAuthToken();
    const updatedUser = {
        username: document.getElementById('editUsername').value,
        email: document.getElementById('editEmail').value,
        number: document.getElementById('editPhone').value
    };
    
    // Log what we're sending
    console.log('Sending update:', updatedUser);
    
    // ... rest of the function
}

        // Close edit modal
        function closeEditModal() {
            if (!editProfileModal) return;
            
            editProfileModal.classList.remove('active');
            document.body.style.overflow = '';
        }

        // Handle profile form submission
        async function handleProfileUpdate(e) {
            e.preventDefault();
            
            if (!currentUser) return;
            
            const USER_TOKEN = getAuthToken();
            const updatedUser = {
                username: document.getElementById('editUsername').value,
                email: document.getElementById('editEmail').value,
                number: document.getElementById('editPhone').value
            };
            
            try {
                // If no valid token, just update locally (demo mode)
                if (!USER_TOKEN) {
                    currentUser = { ...currentUser, ...updatedUser };
                    populateUserData(currentUser);
                    closeEditModal();
                    showSuccess('Profile updated successfully! (Demo mode)');
                    return;
                }
                
                // Log the data being sent for debugging
                console.log('Updating user with data:', updatedUser);
                console.log('User ID:', currentUser.id);
                
                const response = await fetch(`${API_BASE_URL}/user/${currentUser.id}`, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${USER_TOKEN}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(updatedUser)
                });
                
                console.log('Update response status:', response.status);
                
                if (response.status === 401) {
                    localStorage.removeItem('accessToken');
                    document.cookie = 'authToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
                    throw new Error('Session expired. Please login again.');
                }
                
                if (response.status === 403) {
                    // Try an alternative endpoint or method
                    console.log('403 Forbidden - trying alternative approach');
                    await tryAlternativeUpdate(USER_TOKEN, updatedUser);
                    return;
                }
                
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error('Server error response:', errorText);
                    throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
                }
                
                const savedUser = await response.json();
                currentUser = savedUser;
                populateUserData(currentUser);
                
                closeEditModal();
                showSuccess('Profile updated successfully!');
            } catch (error) {
                console.error('Error updating profile:', error);
                showError(error.message || 'Failed to update profile. Please try again.');
            }
        }

        // Alternative update method for when the main endpoint fails
        async function tryAlternativeUpdate(token, updatedUser) {
            try {
                // Try updating without the ID in the URL (some APIs use different patterns)
                const response = await fetch(`${API_BASE_URL}/user/update`, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(updatedUser)
                });
                
                if (response.ok) {
                    const savedUser = await response.json();
                    currentUser = savedUser;
                    populateUserData(currentUser);
                    closeEditModal();
                    showSuccess('Profile updated successfully!');
                    return;
                }
                
                // If that also fails, try a POST request instead
                const postResponse = await fetch(`${API_BASE_URL}/user/profile`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(updatedUser)
                });
                
                if (postResponse.ok) {
                    const savedUser = await postResponse.json();
                    currentUser = savedUser;
                    populateUserData(currentUser);
                    closeEditModal();
                    showSuccess('Profile updated successfully!');
                    return;
                }
                
                // If all API attempts fail, update locally
                console.log('All API update attempts failed, updating locally');
                currentUser = { ...currentUser, ...updatedUser };
                populateUserData(currentUser);
                closeEditModal();
                showSuccess('Profile updated locally! (Server update failed)');
                
            } catch (error) {
                console.error('Alternative update failed:', error);
                // Fallback to local update
                currentUser = { ...currentUser, ...updatedUser };
                populateUserData(currentUser);
                closeEditModal();
                showSuccess('Profile updated locally! (Server connection failed)');
            }
        }

        // Utility functions for notifications
        function showSuccess(message) {
            // Create a toast notification
            const toast = document.createElement('div');
            toast.style.position = 'fixed';
            toast.style.bottom = '20px';
            toast.style.right = '20px';
            toast.style.background = 'rgba(76, 175, 80, 0.9)';
            toast.style.color = 'white';
            toast.style.padding = '12px 20px';
            toast.style.borderRadius = '6px';
            toast.style.zIndex = '10000';
            toast.style.fontSize = '14px';
            toast.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)';
            toast.textContent = message;
            
            document.body.appendChild(toast);
            
            // Auto-remove after 3 seconds
            setTimeout(() => {
                if (document.body.contains(toast)) {
                    document.body.removeChild(toast);
                }
            }, 3000);
        }

        function showError(message) {
            // Create a toast notification
            const toast = document.createElement('div');
            toast.style.position = 'fixed';
            toast.style.bottom = '20px';
            toast.style.right = '20px';
            toast.style.background = 'rgba(244, 67, 54, 0.9)';
            toast.style.color = 'white';
            toast.style.padding = '12px 20px';
            toast.style.borderRadius = '6px';
            toast.style.zIndex = '10000';
            toast.style.fontSize = '14px';
            toast.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)';
            toast.textContent = message;
            
            document.body.appendChild(toast);
            
            // Auto-remove after 5 seconds
            setTimeout(() => {
                if (document.body.contains(toast)) {
                    document.body.removeChild(toast);
                }
            }, 5000);
        }
 