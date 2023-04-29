var mde = new EasyMDE({
    element: document.getElementById("edit-content"),
    shortcuts: {"togglePreview": "Ctrl-Alt-P"},
    placeholder: "Content...",
    maxHeight: "70vh",
    toolbar: false,
    status: false,
    previewClass: "custom-editor-preview",
    previewRender: function (plaintext, preview) {
        var req = new XMLHttpRequest();
        req.onreadystatechange = () => {
            if (req.readyState == 4) {
                preview.innerHTML = req.response;
            }
        }
        req.open('POST', '/edit/chapter/preview');
        req.setRequestHeader('Content-type', 'text/markdown');
        req.send(mde.value());
        return "Loading...";
    },
    renderingConfig: {markedOptions: {gfm: false}} // GitHub-flavoured markdown is off.
});

var isPreview = false;
var previewButton = document.getElementById("preview-button");
previewButton.addEventListener('click', function togglePreview() {

    mde.togglePreview();

    if (!isPreview) {
        isPreview = true;
        document.getElementById("preview-icon").setAttribute("style", "display: none");
        document.getElementById("no-preview-icon").removeAttribute("style");
    } else {
        isPreview = false;
        document.getElementById("preview-icon").removeAttribute("style");
        document.getElementById("no-preview-icon").setAttribute("style", "display: none");
    }

    MathJax.typeset();
});