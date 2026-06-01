document.addEventListener("DOMContentLoaded", function () {

    const canvas = document.getElementById("captchaCanvas");
    const refreshBtn = document.getElementById("refreshCaptchaBtn");

    function drawCaptcha() {

        const captchaValue =
            document.getElementById("captchaValue").value;

        const ctx = canvas.getContext("2d");

        ctx.clearRect(0, 0, canvas.width, canvas.height);

        ctx.fillStyle = "#f0f0f0";
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        // Draw random lines
        for (let i = 0; i < 5; i++) {

            ctx.strokeStyle =
                `rgba(${Math.floor(Math.random() * 255)},
                       ${Math.floor(Math.random() * 255)},
                       ${Math.floor(Math.random() * 255)},
                       0.7)`;

            ctx.beginPath();

            ctx.moveTo(
                Math.random() * canvas.width,
                Math.random() * canvas.height
            );

            ctx.lineTo(
                Math.random() * canvas.width,
                Math.random() * canvas.height
            );

            ctx.stroke();
        }

        // Draw random dots
        for (let i = 0; i < 30; i++) {

            ctx.fillStyle =
                `rgba(${Math.floor(Math.random() * 255)},
                       ${Math.floor(Math.random() * 255)},
                       ${Math.floor(Math.random() * 255)},
                       0.5)`;

            ctx.beginPath();

            ctx.arc(
                Math.random() * canvas.width,
                Math.random() * canvas.height,
                1.5,
                0,
                2 * Math.PI
            );

            ctx.fill();
        }

        ctx.font = "24px Arial";
        ctx.fillStyle = "#000";
        ctx.textBaseline = "middle";

        ctx.fillText(
            captchaValue,
            30,
            canvas.height / 2
        );
    }

    drawCaptcha();

    // Refresh CAPTCHA by reloading login page
    refreshBtn.addEventListener("click", function () {
        window.location.reload();
    });

    // Optional: clicking canvas also refreshes
    canvas.addEventListener("click", function () {
        window.location.reload();
    });

});