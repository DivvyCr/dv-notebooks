function updateInnerHeight() {
    document.documentElement.style.setProperty('--vh', `${window.innerHeight/100}px`);
}

window.addEventListener('resize', updateInnerHeight);
window.addEventListener('orientationchange', updateInnerHeight);

updateInnerHeight(); /* Initial invocation. */