const html = document.querySelector("html");
const menuButton = document.getElementById("toggle-chapter-select");
const mobileMenuButton = document.getElementById("toggle-alt-chapter-select");

function sidebarToggle() {
    html.classList.toggle('chapters-hidden');
    html.classList.toggle('chapters-shown');
};

if (window.matchMedia("(max-width: 600px)").matches) { sidebarToggle(); }
menuButton.addEventListener('click', function() { sidebarToggle(); });
mobileMenuButton.addEventListener('click', function() { sidebarToggle(); });

