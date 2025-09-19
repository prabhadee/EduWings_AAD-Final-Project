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

            // Enhanced notification bell with particle explosion
            const notificationBell = document.getElementById('notificationBell');
            if (notificationBell) {
                notificationBell.addEventListener('click', (e) => {
                    // Create explosion effect
                    createExplosion(e.target);
                    
                    // Enhanced notification with sound effect simulation
                    const notifications = [
                        'You have 3 new course enrollments! 🎓',
                        '2 instructors are waiting for approval ⏳',
                        'Monthly revenue increased by 15% 📈',
                        '5 new student reviews received ⭐',
                        'System backup completed successfully ✅'
                    ];
                    
                    const randomNotification = notifications[Math.floor(Math.random() * notifications.length)];
                    
                    // Create custom notification popup
                    showCustomNotification(randomNotification);
                });
            }

            // Custom notification system
            function showCustomNotification(message) {
                const notification = document.createElement('div');
                notification.style.cssText = `
                    position: fixed;
                    top: 30px;
                    right: 30px;
                    background: var(--neon-gradient);
                    color: white;
                    padding: 20px 30px;
                    border-radius: 15px;
                    font-weight: 600;
                    box-shadow: 0 20px 40px rgba(102, 126, 234, 0.4);
                    z-index: 1000;
                    transform: translateX(400px);
                    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
                    backdrop-filter: blur(15px);
                    border: 1px solid rgba(255, 255, 255, 0.2);
                    max-width: 350px;
                `;
                notification.textContent = message;
                document.body.appendChild(notification);
                
                // Animate in
                setTimeout(() => {
                    notification.style.transform = 'translateX(0)';
                }, 100);
                
                // Animate out and remove
                setTimeout(() => {
                    notification.style.transform = 'translateX(400px)';
                    setTimeout(() => {
                        document.body.removeChild(notification);
                    }, 400);
                }, 4000);
            }

            // Particle explosion effect
            function createExplosion(element) {
                const rect = element.getBoundingClientRect();
                const centerX = rect.left + rect.width / 2;
                const centerY = rect.top + rect.height / 2;
                
                for (let i = 0; i < 12; i++) {
                    const particle = document.createElement('div');
                    particle.style.cssText = `
                        position: fixed;
                        width: 6px;
                        height: 6px;
                        background: var(--cyber-gradient);
                        border-radius: 50%;
                        left: ${centerX}px;
                        top: ${centerY}px;
                        z-index: 1001;
                        pointer-events: none;
                    `;
                    
                    document.body.appendChild(particle);
                    
                    const angle = (i / 12) * Math.PI * 2;
                    const velocity = 150;
                    const vx = Math.cos(angle) * velocity;
                    const vy = Math.sin(angle) * velocity;
                    
                    let opacity = 1;
                    let x = 0;
                    let y = 0;
                    
                    const animate = () => {
                        x += vx * 0.016;
                        y += vy * 0.016;
                        opacity -= 0.02;
                        
                        particle.style.transform = `translate(${x}px, ${y}px)`;
                        particle.style.opacity = opacity;
                        
                        if (opacity > 0) {
                            requestAnimationFrame(animate);
                        } else {
                            document.body.removeChild(particle);
                        }
                    };
                    
                    requestAnimationFrame(animate);
                }
            }

            // Enhanced card interactions with 3D mouse tracking
            const cards = document.querySelectorAll('.card');
            cards.forEach(card => {
                card.addEventListener('mouseenter', (e) => {
                    card.style.transition = 'transform 0.1s ease';
                });
                
                card.addEventListener('mousemove', (e) => {
                    const rect = card.getBoundingClientRect();
                    const x = e.clientX - rect.left;
                    const y = e.clientY - rect.top;
                    
                    const centerX = rect.width / 2;
                    const centerY = rect.height / 2;
                    
                    const rotateX = (y - centerY) / centerY * -10;
                    const rotateY = (x - centerX) / centerX * 10;
                    
                    card.style.transform = `
                        translateY(-15px) 
                        scale(1.05) 
                        rotateX(${rotateX}deg) 
                        rotateY(${rotateY}deg)
                        perspective(1000px)
                    `;
                });
                
                card.addEventListener('mouseleave', () => {
                    card.style.transition = 'transform 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94)';
                    card.style.transform = '';
                });
                
                card.addEventListener('click', () => {
                    // Pulse effect on click
                    card.style.transform = 'scale(0.95)';
                    setTimeout(() => {
                        card.style.transform = '';
                    }, 150);
                });
            });

            // Enhanced stats items with counter animation
            const statsItems = document.querySelectorAll('.stats-item h3');
            const observer = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const target = entry.target;
                        const text = target.textContent;
                        
                        // Extract number from text
                        const number = text.match(/[\d.]+/);
                        if (number) {
                            const finalValue = parseFloat(number[0]);
                            const suffix = text.replace(number[0], '');
                            let currentValue = 0;
                            const increment = finalValue / 50;
                            
                            const counter = setInterval(() => {
                                currentValue += increment;
                                if (currentValue >= finalValue) {
                                    currentValue = finalValue;
                                    clearInterval(counter);
                                }
                                target.textContent = currentValue.toFixed(finalValue % 1 !== 0 ? 1 : 0) + suffix;
                            }, 30);
                        }
                        
                        observer.unobserve(target);
                    }
                });
            }, { threshold: 0.5 });
            
            statsItems.forEach(item => observer.observe(item));

            // Activity items hover effects
            const activityItems = document.querySelectorAll('.activity-item');
            activityItems.forEach(item => {
                item.addEventListener('mouseenter', () => {
                    item.style.background = 'rgba(102, 126, 234, 0.1)';
                    item.style.transform = 'translateX(10px) scale(1.02)';
                    item.style.boxShadow = '0 10px 25px rgba(102, 126, 234, 0.2)';
                });
                
                item.addEventListener('mouseleave', () => {
                    item.style.background = '';
                    item.style.transform = '';
                    item.style.boxShadow = '';
                });
            });

            // Sidebar link enhanced effects
            const sidebarLinks = document.querySelectorAll('.sidebar ul li a');
            sidebarLinks.forEach(link => {
                link.addEventListener('mouseenter', () => {
                    // Add ripple effect
                    const ripple = document.createElement('div');
                    ripple.style.cssText = `
                        position: absolute;
                        width: 0;
                        height: 0;
                        border-radius: 50%;
                        background: rgba(255, 255, 255, 0.1);
                        transform: translate(-50%, -50%);
                        animation: ripple 0.6s linear;
                        pointer-events: none;
                        top: 50%;
                        left: 50%;
                    `;
                    
                    link.appendChild(ripple);
                    
                    setTimeout(() => {
                        ripple.remove();
                    }, 600);
                });
            });

            // Add ripple animation to CSS
            const style = document.createElement('style');
            style.textContent = `
                @keyframes ripple {
                    to {
                        width: 200px;
                        height: 200px;
                        opacity: 0;
                    }
                }
            `;
            document.head.appendChild(style);

            // Dynamic background gradient animation
            let gradientAngle = 135;
            setInterval(() => {
                gradientAngle += 0.5;
                document.body.style.background = `linear-gradient(${gradientAngle}deg, #0f0f23 0%, #1a1a2e 100%)`;
            }, 100);

            // Add parallax effect to particles
            window.addEventListener('mousemove', (e) => {
                const particles = document.querySelectorAll('.particle');
                const mouseX = e.clientX / window.innerWidth;
                const mouseY = e.clientY / window.innerHeight;
                
                particles.forEach((particle, index) => {
                    const speed = (index % 3 + 1) * 0.5;
                    const x = (mouseX - 0.5) * speed * 20;
                    const y = (mouseY - 0.5) * speed * 20;
                    
                    particle.style.transform += ` translate(${x}px, ${y}px)`;
                });
            });

            console.log('🚀 Extraordinary Admin Dashboard Loaded Successfully!');
        });
  