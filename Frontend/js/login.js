document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");
    const message = document.getElementById("message");
    const loginBtn = document.getElementById("loginBtn");

    loginForm.addEventListener("submit", function (e) {
        e.preventDefault();

        const email = document.getElementById("emailInput").value.trim();
        const password = document.getElementById("passwordInput").value;

        if (!email || !password) {
            showMessage("Please fill in all fields.", "error");
            return;
        }
        // Temporary hardcoded ADMIN check (for demo)
if (email === "admin@ewings.lk" && password === "admin123") {
    localStorage.setItem("accessToken", "dummy-token");
    localStorage.setItem("userRole", "ADMIN");

    showMessage("🎉 Login successful! Redirecting...", "success");

    setTimeout(() => {
        window.location.href = "/pages/Admin/adminDashboard.html";
    }, 2000);

    return; // Stop execution before calling AJAX
}

        // Loading state
        loginBtn.innerHTML = '<span class="spinner"></span> Signing In...';
        loginBtn.disabled = true;

        $.ajax({
            url: "http://localhost:8080/auth/login", // ✅ Replace with your actual API endpoint
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                email: email,
                password: password
            }),
            success: function (response) {
                console.log("Login Response:", response);

                if (response.code === 200 && response.status === "OK") {
                    const accessToken = response.data.accessToken;
                    const role = response.data.role;

                    // ✅ Store token and role in localStorage
                    localStorage.setItem("accessToken", accessToken);
                    localStorage.setItem("userRole", role);

                    showMessage("🎉 Login successful! Redirecting...", "success");

                    // ✅ Role-based redirection
                    setTimeout(() => {
                        if (role === "ADMIN") {
                            window.location.href = "/pages/Admin/adminDashboard.html";
                        } else if(role === "USER"){
                            window.location.href = "index.html"
                        }else {
                            window.location.href = "login.html";
                        }
                    }, 2000);
                } else {
                    showMessage(response.message || "Invalid email or password.", "error");
                    shakeForm();
                }
            },
            error: function (xhr, status, error) {
                console.error("Error:", error);
                showMessage("Something went wrong. Please try again later.", "error");
            },
            complete: function () {
                // Reset button state
                loginBtn.innerHTML = "Sign In";
                loginBtn.disabled = false;
            }
        });
    });

    function showMessage(msg, type = "info") {
        message.className = `message ${type}`;
        message.textContent = msg;
        message.classList.add("show");

        if (type !== "success") {
            setTimeout(() => hideMessage(), 5000);
        }
    }

    function hideMessage() {
        message.classList.remove("show");
    }

    function shakeForm() {
        loginForm.style.animation = "shake 0.5s ease-in-out";
        setTimeout(() => {
            loginForm.style.animation = "";
        }, 500);
    }
});
