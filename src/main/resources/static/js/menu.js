const html = document.querySelector("html");
const menuButton = document.getElementById("toggle-menu");
const mobileMenuButton = document.getElementById("toggle-mobile-menu");

menuButton.addEventListener('click', function sidebarToggle() {
    html.classList.toggle('menu-hidden');
});

mobileMenuButton.addEventListener('click', function sidebarToggle() {
    html.classList.toggle('menu-hidden');
});
