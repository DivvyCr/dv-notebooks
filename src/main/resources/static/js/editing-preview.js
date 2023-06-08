let isPreview = false;
const previewButton = document.getElementById("preview-button");
const previewElement = document.getElementById("editor-preview");
const formTextElement = document.getElementById("edit-content");

const updateKaTeX = () => {
    let mathElems = document.getElementsByClassName("katex");
    let elems = [];
    for (const i in mathElems) {
        if (mathElems.hasOwnProperty(i)) elems.push(mathElems[i]);
    }

    elems.forEach(elem => {
        // katex is initially loaded by the page
        katex.render(elem.textContent, elem, {throwOnError: false, displayMode: elem.nodeName !== 'SPAN',});
    });
}

const generatePreview = () => {
    let req = new XMLHttpRequest();
    req.onreadystatechange = () => {
        if (req.readyState === 4) {
            previewElement.innerHTML = req.response;
            Prism.highlightAll();
            updateKaTeX();
        }
    }
    req.open('POST', '/edit/chapter/preview');
    req.setRequestHeader('Content-type', 'text/markdown');
    req.send(formTextElement.value);
}

previewButton.addEventListener('click', function togglePreview() {
    // mde.togglePreview();
    generatePreview();

    if (!isPreview) {
        isPreview = true;

        document.getElementById("editor-preview").setAttribute("style", "visibility: visible");
        document.getElementById("editor").setAttribute("style", "visibility: hidden");

        document.getElementById("preview-icon").setAttribute("style", "display: none");
        document.getElementById("no-preview-icon").removeAttribute("style");
    } else {
        isPreview = false;

        document.getElementById("editor-preview").setAttribute("style", "visibility: hidden");
        document.getElementById("editor").setAttribute("style", "visibility: visible");

        document.getElementById("preview-icon").removeAttribute("style");
        document.getElementById("no-preview-icon").setAttribute("style", "display: none");
    }
});