console.log("login.js loaded");

async function login() {
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const messageBox = document.getElementById('messageBox');

    if (!email || !password) {
        messageBox.textContent = "Please enter both email and password.";
        messageBox.className = "message error";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/users/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email, password })
        });

        const result = await response.text();

        if (response.ok) {
            messageBox.textContent = result;
            messageBox.className = "message success";

            // ✅ Store email in localStorage
            localStorage.setItem("userEmail", email);

            // ✅ Redirect to dashboard
            setTimeout(() => {
                window.location.href = "dashboard.html";
            }, 1500);
        } else {
            messageBox.textContent = result;
            messageBox.className = "message error";
        }
    } catch (error) {
        messageBox.textContent = "Server error. Try again later.";
        messageBox.className = "message error";
    }
}

// ✅ Prevent login page access if already logged in
window.onload = () => {
    const userEmail = localStorage.getItem("userEmail");
    if (userEmail) {
        window.location.href = "dashboard.html";
    }
};

// Ensure login() is accessible in HTML
window.login = login;
