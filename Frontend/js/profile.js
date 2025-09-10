document.addEventListener("DOMContentLoaded", () => {
    const fullNameInput = document.getElementById("fullName");
    const emailInput = document.getElementById("email");
    const phoneInput = document.getElementById("phone");
    const passwordInput = document.getElementById("password");
    const profileForm = document.getElementById("profileForm");

    // Load profile from localStorage
    let profile = JSON.parse(localStorage.getItem("adminProfile")) || {
        fullName: "Admin User",
        email: "admin@example.com",
        phone: "0711234567",
        password: "admin123"
    };

    function loadProfile() {
        fullNameInput.value = profile.fullName;
        emailInput.value = profile.email;
        phoneInput.value = profile.phone;
        passwordInput.value = profile.password;
    }

    loadProfile();

    // Update profile
    profileForm.addEventListener("submit", (e) => {
        e.preventDefault();
        profile.fullName = fullNameInput.value;
        profile.email = emailInput.value;
        profile.phone = phoneInput.value;
        profile.password = passwordInput.value;

        localStorage.setItem("adminProfile", JSON.stringify(profile));
        alert("Profile updated successfully!");
    });
});
