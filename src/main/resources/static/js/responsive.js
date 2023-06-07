const html = document.querySelector("html");

function updateInnerHeight() {
    document.documentElement.style.setProperty('--vh', `${window.innerHeight/100}px`);
}

updateInnerHeight(); /* Initial invocation. */
window.addEventListener('resize', updateInnerHeight);
window.addEventListener('orientationchange', updateInnerHeight);

function sidebarToggle() {
    html.classList.toggle('chapters-hidden');
    html.classList.toggle('chapters-shown');
}

document.getElementById("toggle-chapter-navigation")
    .addEventListener('click', sidebarToggle);

const x = document.getElementById("chapter-navigation");
window.onload = () => { x.style.left = "calc((100vw - var(--content-width)) / 2 - " + x.offsetWidth + "px)"};
window.onresize = () => { x.style.left = "calc((100vw - var(--content-width)) / 2 - " + x.offsetWidth + "px)"};
