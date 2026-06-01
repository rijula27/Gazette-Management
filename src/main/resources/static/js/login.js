document.addEventListener("DOMContentLoaded", function () {

    const captchaImage =
        document.getElementById("captchaImage");

    const refreshBtn =
        document.getElementById("refreshCaptchaBtn");

    function refreshCaptcha() {


        fetch("/refresh-captcha", {
            method: "GET",
            credentials: "same-origin"
        })
            .then(response => {

                return response.text();

            })
            .then(data => {


                captchaImage.src =
                    "/captcha-image?t=" +
                    new Date().getTime();

            })
            .catch(error => {

                console.error(error);

            });
    }

    refreshBtn.addEventListener(
        "click",
        refreshCaptcha
    );

    captchaImage.addEventListener(
        "click",
        refreshCaptcha
    );

});