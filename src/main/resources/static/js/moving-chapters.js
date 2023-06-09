const parentIdTextElement = document.getElementById("parent-id");
const precedingIdTextElement = document.getElementById("preceding-id");

if (document.getElementById("active-chapter") == null || typeof (document.getElementById("active-chapter")) == "undefined") {
    // Creating a new chapter; need placeholder element to enable ordering:
    var placeholderContents = document.createElement("a");
    placeholderContents.innerHTML = "New Chapter";
    placeholderContents.id = "active-chapter";
    placeholderContents.className = document.getElementById(precedingIdTextElement.value).firstElementChild.className;

    var placeholder = document.createElement("li");
    placeholder.appendChild(placeholderContents);

    document.getElementById(precedingIdTextElement.value).insertAdjacentElement('afterend', placeholder);
}

let movableElement = document.getElementById("active-chapter").parentElement;
let movableElementText = document.getElementById("active-chapter");

window.addEventListener("load", (event) => {
    // init parent-id
    if (movableElement.parentElement.parentElement.tagName.toLowerCase() === "li") {
        parentIdTextElement.setAttribute("value", movableElement.parentElement.parentElement.id);
    } else {
        parentIdTextElement.removeAttribute("value");
    }

    // init preceding-id
    if (movableElement.previousElementSibling != null) {
        precedingIdTextElement.setAttribute("value", movableElement.previousElementSibling.id);
    } else {
        precedingIdTextElement.removeAttribute("value");
    }

    if (titleTextElement.value) {
        movableElementText.innerHTML = titleTextElement.value;
        headerTextElement.innerHTML = titleTextElement.value;
    } else {
        movableElementText.innerHTML = "New Chapter";
        headerTextElement.innerHTML = "New Chapter";
    }
});

window.addEventListener("unload", (event) => {
    document.getElementById("delete-button").click();
});

var titleTextElement = document.getElementById("edit-title");
var headerTextElement = document.getElementsByClassName("header-left")[0];
titleTextElement.addEventListener("keyup", (event) => {
    if (titleTextElement.value) {
        movableElementText.innerHTML = titleTextElement.value;
        headerTextElement.innerHTML = titleTextElement.value;
    } else {
        movableElementText.innerHTML = "New Chapter";
        headerTextElement.innerHTML = "New Chapter";
    }
});

var upButton = document.getElementById("move-up");
var inButton = document.getElementById("move-in");
var outButton = document.getElementById("move-out");
var downButton = document.getElementById("move-down");

function updateTextElements() {
    if (movableElement.parentElement.parentElement.tagName.toLowerCase() === "li") {
        parentIdTextElement.setAttribute("value", movableElement.parentElement.parentElement.id);
    } else {
        parentIdTextElement.removeAttribute("value");
    }
    if (!movableElement.previousElementSibling) {
        precedingIdTextElement.removeAttribute("value");
    } else {
        precedingIdTextElement.setAttribute("value", movableElement.previousElementSibling.id);
    }
}

upButton.addEventListener('click', function up() {
    movableElement.parentElement.insertBefore(movableElement, movableElement.previousElementSibling);
    updateTextElements();
});

inButton.addEventListener('click', function moveIn() {
    if (!movableElement.previousElementSibling) return;

    if (movableElement.previousElementSibling.lastElementChild.tagName.toLowerCase() !== "ul") {
        // Need to create a sub-list:
        var subList = document.createElement("ul");
        subList.className = "chapter-list";
        movableElement.previousElementSibling.appendChild(subList);
        precedingIdTextElement.removeAttribute("value");
    }
    movableElement.previousElementSibling.lastElementChild.appendChild(movableElement);

    // Adjust class:
    switch (document.getElementById("active-chapter").className) {
        case "chapter-lvl-0":
            document.getElementById("active-chapter").className = "chapter-lvl-1";
            break;
        case "chapter-lvl-1":
            document.getElementById("active-chapter").className = "chapter-deep";
            break;
    }

    updateTextElements();
});

outButton.addEventListener('click', function moveOut() {
    if (movableElement.parentElement.parentElement.tagName.toLowerCase() === "li") {
        movableElement.parentElement.parentElement.parentElement.insertBefore(movableElement, movableElement.parentElement.parentElement.nextElementSibling);
        updateTextElements();
    }

    // Adjust class:
    switch (document.getElementById("active-chapter").className) {
        case "chapter-lvl-1":
            document.getElementById("active-chapter").className = "chapter-lvl-0";
            break;
        case "chapter-deep":
            document.getElementById("active-chapter").className = "chapter-lvl-1";
            break;
    }
});

downButton.addEventListener('click', function down() {
    movableElement.parentElement.insertBefore(movableElement, movableElement.nextElementSibling.nextElementSibling);
    updateTextElements();
});