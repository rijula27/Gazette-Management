

    document.addEventListener('DOMContentLoaded', () => {
      // Menu toggle functionality
      const menuToggle = document.querySelector('.menu-toggle');
      const nav = document.querySelector('nav ul');

      if (menuToggle) {
        menuToggle.addEventListener('click', () => {
          nav.classList.toggle('active');
        });
      }


    });


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


set_style_from_cookie();


// Function to switch contrast mode
function switchStyle(style) {
    const body = document.body;

    if (style === "black") {
        body.classList.remove("white-contrast", "default-contrast");
        body.classList.add("black-contrast");
        localStorage.setItem("contrast", "black");
    } else if (style === "white") {
        body.classList.remove("black-contrast", "default-contrast");
        body.classList.add("white-contrast");
        localStorage.setItem("contrast", "white");
    } else {
        body.classList.remove("black-contrast", "white-contrast");
        body.classList.add("default-contrast");
        localStorage.setItem("contrast", "default");
    }
}

// Function to control character spacing
function setCharSpacing(on) {
    const spacing = on ? "1px" : "normal";
    document.body.style.letterSpacing = spacing;
    sessionStorage.setItem("letterSpacing", spacing);
}

// Load saved preferences on page load
window.addEventListener("DOMContentLoaded", function () {
    // Load contrast
    // const contrast = localStorage.getItem("contrast") || "default";
    // switchStyle(contrast);


    const allowedContrast = ["black", "white", "default"];

    const contrast = localStorage.getItem("contrast");

    switchStyle(
        allowedContrast.includes(contrast) ? contrast : "default"
    );

    
    // Load spacing
    const spacing = localStorage.getItem("letterSpacing");
    if (spacing) {
        document.body.style.letterSpacing = spacing;
    }
});


const root = document.documentElement;

// Font size handlers
document.getElementById('fontLarge').onclick = () => root.style.fontSize = '18px';
document.getElementById('fontDefault').onclick = () => root.style.fontSize = '16px';
document.getElementById('fontSmall').onclick = () => root.style.fontSize = '14px';

// Contrast mode
function switchStyle(mode) {
    if (mode === 'black') {
        document.body.style.background = '#000';
        document.body.style.color = '#ff0';
    } else if (mode === 'white') {
        document.body.style.background = '#fff';
        document.body.style.color = '#000';
    } else {
        document.body.style.background = '';
        document.body.style.color = '';
    }
}

// Character spacing
function setCharSpacing(on) {
    document.body.style.letterSpacing = on ? '2px' : 'normal';
}