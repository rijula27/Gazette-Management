const AES_SECRET_KEY_FOR_CLIENT = "Xy8QsR9n8S2sJ3vV0cP0sW8eH6fC8zJ9kM9uQ9tR5rS3aC8dF1";

function base64EncodeWordArray(wordArray) {
  return CryptoJS.enc.Base64.stringify(wordArray);
}

function base64DecodeToWordArray(b64) {
  return CryptoJS.enc.Base64.parse(b64);
}


document.addEventListener("DOMContentLoaded", function () {


    const captchaImage =
        document.getElementById("captchaImage");

    const refreshBtn =
        document.getElementById("refreshCaptchaBtn");


    const loginForm = document.getElementById("loginform");

    // ✅ FIXED submit handler
    loginForm.addEventListener("submit", function (e) {
        e.preventDefault();

        const passwordField = document.querySelector('input[name="password"]');
        const password = passwordField.value || "";


      const key = CryptoJS.SHA256(AES_SECRET_KEY_FOR_CLIENT);


        // generate random 16-byte IV
      const iv = CryptoJS.lib.WordArray.random(16);

      // encrypt with AES CBC + PKCS7 (CryptoJS default)
      const encrypted = CryptoJS.AES.encrypt(
        CryptoJS.enc.Utf8.parse(password),
        key,
        { iv: iv, mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7 }
      );

      // ciphertext as Base64 (raw cipher, not OpenSSL wrapper)
      const cipherBase64 = base64EncodeWordArray(encrypted.ciphertext);

      // iv as Base64
      const ivBase64 = base64EncodeWordArray(iv);

      // final payload: iv:ciphertext (both base64)
      const payload = ivBase64 + ":" + cipherBase64;

      // replace password field with payload and submit
      passwordField.value = payload;

      // optionally add a hidden field to indicate this is encrypted (not required)
      // let encFlag = document.getElementById('encFlag');
      // if (!encFlag) {
      //   encFlag = document.createElement('input');
      //   encFlag.type = 'hidden';
      //   encFlag.name = 'enc';
      //   encFlag.id = 'encFlag';
      //   encFlag.value = 'aes';
      //   loginForm.appendChild(encFlag);
      // }

      this.submit();
    });

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