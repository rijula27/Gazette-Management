// Generate canvas CAPTCHA
        function generateCaptcha() {
            const canvas = document.getElementById("captchaCanvas");
            const ctx = canvas.getContext("2d");
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            ctx.fillStyle = "#f0f0f0";
            ctx.fillRect(0, 0, canvas.width, canvas.height);

            // Draw random lines
            for (let i = 0; i < 5; i++) {
                ctx.strokeStyle = `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(
                    Math.random() * 255
                )}, ${Math.floor(Math.random() * 255)}, 0.7)`;
                ctx.beginPath();
                ctx.moveTo(Math.random() * canvas.width, Math.random() * canvas.height);
                ctx.lineTo(Math.random() * canvas.width, Math.random() * canvas.height);
                ctx.stroke();
            }

            // Generate CAPTCHA text
            const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            let captcha = "";
            for (let i = 0; i < 6; i++) {
                captcha += characters.charAt(Math.floor(Math.random() * characters.length));
            }

            ctx.font = "24px Arial";
            ctx.fillStyle = "#000";
            ctx.textBaseline = "middle";
            ctx.fillText(captcha, 40, canvas.height / 2);
            canvas.setAttribute("data-captcha", captcha);
        }

        // Validate CAPTCHA before submission
        document.getElementById("loginform").addEventListener("submit", function (event) {
            const userCaptcha = document.getElementById("captchaInput").value.trim();
            const generatedCaptcha = document.getElementById("captchaCanvas").getAttribute("data-captcha");
            const errorDiv = document.getElementById("errorMessage");

            // Clear previous errors
            errorDiv.textContent = "";

            if (userCaptcha !== generatedCaptcha) {
                errorDiv.textContent = "CAPTCHA does not match. Please try again.";
                errorDiv.style.opacity = "1";
                generateCaptcha();
                event.preventDefault(); // prevent form submission
            }
        });

        // Generate CAPTCHA on page load
        window.onload = generateCaptcha;