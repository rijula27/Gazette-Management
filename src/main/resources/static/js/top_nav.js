document.addEventListener('DOMContentLoaded', () => {
    const menuToggle = document.querySelector('.menu-toggle');
    const nav = document.querySelector('nav ul');

    if (menuToggle) {
        menuToggle.addEventListener('click', () => {
            nav.classList.toggle('active');
        });
    }
});

// ✅ Override document.cookie for creaseFont — redirect to localStorage
(function() {
    const FONT_COOKIE = "creaseFont";

    const originalCookieDescriptor =
        Object.getOwnPropertyDescriptor(Document.prototype, 'cookie') ||
        Object.getOwnPropertyDescriptor(HTMLDocument.prototype, 'cookie');

    Object.defineProperty(document, 'cookie', {
        get: function() {
            let realCookies = originalCookieDescriptor.get.call(document);
            const fontValue = localStorage.getItem(FONT_COOKIE);
            if (fontValue) {
                realCookies = realCookies
                    .replace(new RegExp('(^|;\\s*)creaseFont=[^;]*', 'g'), '')
                    .replace(/^;\s*/, '');
                realCookies = FONT_COOKIE + '=' + fontValue +
                              (realCookies ? '; ' + realCookies : '');
            }
            return realCookies;
        },
        set: function(cookieString) {
            if (cookieString.indexOf(FONT_COOKIE + '=') === 0 ||
                cookieString.indexOf(encodeURIComponent(FONT_COOKIE) + '=') === 0) {
                const match = cookieString.match(/creaseFont=([^;]*)/);
                if (match) {
                    const value = decodeURIComponent(match[1]);
                    if (value) {
                        localStorage.setItem(FONT_COOKIE, value);
                    } else {
                        localStorage.removeItem(FONT_COOKIE);
                    }
                }
                return;
            }
            originalCookieDescriptor.set.call(document, cookieString);
        },
        configurable: true
    });
})();

// ✅ Delete existing creaseFont cookie
document.cookie = "creaseFont=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";

// Initialize creaseFont plugin
jQuery(document).ready(function () {
    $.creaseFont();
});

$(window).keydown(function (e) {
    switch (e.keyCode) {
        case 107:
            $('#fontLarge').click();
            return false;
        case 109:
            $('#fontSmall').click();
            return false;
    }
    return;
});