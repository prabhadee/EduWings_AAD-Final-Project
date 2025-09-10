document.addEventListener("DOMContentLoaded", function () {
    const signupForm = document.getElementById("signupForm");

    signupForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const firstName = document.getElementById("firstNameInput").value.trim();
        const lastName = document.getElementById("lastNameInput").value.trim();
        const email = document.getElementById("emailInput").value.trim();
        const phone = document.getElementById("phoneInput").value.trim();
        const password = document.getElementById("passwordInput").value;
        const confirmPassword = document.getElementById("confirmPasswordInput").value;

        // ✅ Client-side Validation
        if (password !== confirmPassword) {
            showSweetAlert("Oops!", "Passwords do not match!", "error");
            return;
        }
        if (password.length < 6) {
            showSweetAlert("Oops!", "Password must be at least 6 characters long!", "error");
            return;
        }

        // ✅ Prepare Data for Backend
        const requestData = {
            username: `${firstName} ${lastName}`,
            email: email,
            number: phone,
            password: password,
            role: "USER"
        };

        try {
            const response = await fetch("http://localhost:8080/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(requestData)
            });

            const data = await response.json();

            if (response.ok) {
                // Creative SweetAlert
                Swal.fire({
                    title: `Hello, ${firstName}! 🎉`,
                    text: `${data.message || "Welcome to eWings! Your account has been created."}`,
                    icon: "success",
                    showCancelButton: false,
                    confirmButtonText: "Let's Learn!",
                    timer: 4000,
                    timerProgressBar: true,
                    backdrop: `
                        rgba(0,0,123,0.4)
                        left top
                        no-repeat
                    `
                }).then(() => {
                    window.location.href = "login.html";
                });
            } else {
                showSweetAlert("Oops!", data.message || "Registration failed!", "error");
            }
        } catch (error) {
            console.error("Error:", error);
            showSweetAlert("Oops!", "Something went wrong. Please try again!", "error");
        }
    });

    function showSweetAlert(title, message, type = "info") {
        Swal.fire({
            title: title,
            text: message,
            icon: type,
            confirmButtonText: "OK",
            timer: type === "success" ? 4000 : null,
            timerProgressBar: type === "success",
        });
    }
});
