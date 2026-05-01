document.addEventListener("DOMContentLoaded", function () {

    /* ================= MENU TOGGLE ================= */
    const menuToggle = document.querySelector(".menu-toggle");
    const nav = document.querySelector("nav ul");

    if (menuToggle && nav) {
        menuToggle.addEventListener("click", () => {
            nav.classList.toggle("active");
        });
    }

    /* ================= GALLERY ================= */
    const slides = document.querySelectorAll(".slide-wrapper");
    const prevBtn = document.querySelector(".gallery-nav.prev");
    const nextBtn = document.querySelector(".gallery-nav.next");

    let currentSlide = 0;
    let slideInterval;

    function showSlide(index) {
        slides.forEach((slide, i) => {
            slide.classList.toggle("active", i === index);
        });
    }

    function nextSlide() {
        currentSlide = (currentSlide + 1) % slides.length;
        showSlide(currentSlide);
    }

    function startAutoSlide() {
        slideInterval = setInterval(nextSlide, 4000);
    }

    function stopAutoSlide() {
        clearInterval(slideInterval);
    }

    if (slides.length > 0) {
        showSlide(currentSlide);
        startAutoSlide();
    }

    if (prevBtn && nextBtn) {
        prevBtn.addEventListener("click", () => {
            stopAutoSlide();
            currentSlide = (currentSlide - 1 + slides.length) % slides.length;
            showSlide(currentSlide);
            startAutoSlide();
        });

        nextBtn.addEventListener("click", () => {
            stopAutoSlide();
            nextSlide();
            startAutoSlide();
        });
    }

    /* ================= THEME SWITCH ================= */
    const themeButtons = document.querySelectorAll(".theme-btn");

    themeButtons.forEach(btn => {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            const theme = this.dataset.theme;
            document.body.className = theme; // apply class
        });
    });

    /* ================= CHARACTER SPACING ================= */
    const spacingButtons = document.querySelectorAll(".char-spacing-btn");

    spacingButtons.forEach(btn => {
        btn.addEventListener("click", function () {
            const spacing = this.dataset.spacing;

            if (spacing === "on") {
                document.body.style.letterSpacing = "1px";
            } else {
                document.body.style.letterSpacing = "normal";
            }
        });
    });

});