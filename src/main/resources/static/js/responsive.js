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

const navBut = document.getElementById("toggle-chapter-navigation");
if (navBut) navBut.addEventListener('click', sidebarToggle);

const navElt = document.getElementById("chapter-navigation");
if (navElt) {
    window.onload = () => { navElt.style.left = "calc((100vw - var(--content-width)) / 2 - " + navElt.offsetWidth + "px)"};
    window.onresize = () => { navElt.style.left = "calc((100vw - var(--content-width)) / 2 - " + navElt.offsetWidth + "px)"};
}

// Enable KaTeX:
document.addEventListener("DOMContentLoaded", function () {
    var mathElems = document.getElementsByClassName("katex");
    var elems = [];
    for (const i in mathElems) {
        if (mathElems.hasOwnProperty(i)) elems.push(mathElems[i]);
    }

    elems.forEach(elem => {
        katex.render(elem.textContent, elem, {throwOnError: false, displayMode: elem.nodeName !== 'SPAN',});
    });
});
