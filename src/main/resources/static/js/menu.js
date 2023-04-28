const html = document.querySelector("html");
const menuButton = document.getElementById("toggle-menu");
const mobileMenuButton = document.getElementById("toggle-mobile-menu");

if (window.matchMedia("(max-width: 600px)").matches) {
    html.classList.toggle('menu-hidden');
    html.classList.toggle('menu-shown');
}

menuButton.addEventListener('click', function sidebarToggle() {
    html.classList.toggle('menu-hidden');
    html.classList.toggle('menu-shown');
});

mobileMenuButton.addEventListener('click', function sidebarToggle() {
    html.classList.toggle('menu-hidden');
    html.classList.toggle('menu-shown');
});

