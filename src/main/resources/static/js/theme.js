// ✅ localStorage key names
var style_storage_name = "style";

// ✅ Delete old cookies on page load
document.cookie = "style=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
document.cookie = "creaseFont=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";

// ✅ switch_style — localStorage instead of cookie
function switch_style(css_title) {
    var i, link_tag;
    for (i = 0, link_tag = document.getElementsByTagName("link");
         i < link_tag.length; i++) {
        if ((link_tag[i].rel.indexOf("stylesheet") != -1) && link_tag[i].title) {
            link_tag[i].disabled = true;
            if (link_tag[i].title == css_title) {
                link_tag[i].disabled = false;
            }
        }
        // ✅ localStorage instead of cookie
        localStorage.setItem(style_storage_name, css_title);
    }
}

// ✅ Reads from localStorage instead of cookie
function set_style_from_storage() {
    var css_title = localStorage.getItem(style_storage_name);
    if (css_title && css_title.length) {
        switch_style(css_title);
    }
}

// ✅ Keep old name — existing calls in top_nav.js still work
function set_style_from_cookie() {
    set_style_from_storage();
}

// ✅ Function to switch contrast mode
function switchStyle(style) {
    const body = document.body;
    const allowedContrast = ["black", "white", "default"];
    const safeStyle = allowedContrast.includes(style) ? style : "default";

    if (safeStyle === "black") {
        body.classList.remove("white-contrast", "default-contrast");
        body.classList.add("black-contrast");
        localStorage.setItem("contrast", "black");
    } else if (safeStyle === "white") {
        body.classList.remove("black-contrast", "default-contrast");
        body.classList.add("white-contrast");
        localStorage.setItem("contrast", "white");
    } else {
        body.classList.remove("black-contrast", "white-contrast");
        body.classList.add("default-contrast");
        localStorage.setItem("contrast", "default");
    }
}

// ✅ Function to control character spacing
function setCharSpacing(on) {
    const spacing = on ? "1px" : "normal";
    document.body.style.letterSpacing = spacing;
    // ✅ localStorage instead of sessionStorage
    localStorage.setItem("letterSpacing", spacing);
}

// ✅ Load saved preferences on page load
window.addEventListener("DOMContentLoaded", function () {
    // Load style
    set_style_from_storage();

    // Load contrast
    const allowedContrast = ["black", "white", "default"];
    const contrast = localStorage.getItem("contrast");
    switchStyle(allowedContrast.includes(contrast) ? contrast : "default");

    // Load spacing
    const spacing = localStorage.getItem("letterSpacing");
    if (spacing && ["1px", "normal"].includes(spacing)) {
        document.body.style.letterSpacing = spacing;
    }
});