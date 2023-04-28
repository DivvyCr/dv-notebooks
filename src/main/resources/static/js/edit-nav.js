var parentIdTextElement = document.getElementById("parent-id");
var precedingIdTextElement = document.getElementById("preceding-id");

if (document.getElementById("menu-active") == null || typeof (document.getElementById("menu-active")) == "undefined") {
    // Creating a new chapter; need placeholder element to enable ordering:
    var placeholderContents = document.createElement("a");
    placeholderContents.innerHTML = "New Chapter";
    placeholderContents.id = "menu-active";
    placeholderContents.className = document.getElementById(precedingIdTextElement.value).firstElementChild.className;

    var placeholder = document.createElement("li");
    placeholder.appendChild(placeholderContents);

    var placeholderParent = document.getElementById(precedingIdTextElement.value).parentElement;
    placeholderParent.appendChild(placeholder);
}

var movableElement = document.getElementById("menu-active").parentElement;
var movableElementText = document.getElementById("menu-active");

window.addEventListener("load", (event) => {
    // init parent-id
    parentIdTextElement.setAttribute("value", movableElement.parentElement.parentElement.id);

    // init preceding-id
    if (movableElement.previousElementSibling != null) {
        precedingIdTextElement.setAttribute("value", movableElement.previousElementSibling.id);
    } else {
        precedingIdTextElement.removeAttribute("value");
    }
});

window.addEventListener("unload", (event) => {
    document.getElementById("delete-button").click();
});

var titleTextElement = document.getElementById("edit-title");
titleTextElement.addEventListener("keyup", (event) => {
    if (titleTextElement.value) {
        movableElementText.innerHTML = titleTextElement.value;
    } else {
        movableElementText.innerHTML = "New Chapter";
    }
});

var upButton = document.getElementById("move-up");
var inButton = document.getElementById("move-in");
var outButton = document.getElementById("move-out");
var downButton = document.getElementById("move-down");

upButton.addEventListener('click', function up() {
    movableElement.parentElement.insertBefore(movableElement, movableElement.previousElementSibling);
    parentIdTextElement.setAttribute("value", movableElement.parentElement.parentElement.id);
    if (movableElement.previousElementSibling != null) {
        precedingIdTextElement.setAttribute("value", movableElement.previousElementSibling.id);
    } else {
        precedingIdTextElement.removeAttribute("value");
    }
});

inButton.addEventListener('click', function moveIn() {
    if (movableElement.previousElementSibling == null) return;

    if (movableElement.previousElementSibling.lastElementChild.tagName.toLowerCase() !== "ul") {
        // Need to create a sub-list:
        var subList = document.createElement("ul");
        subList.className = "side-list";
        movableElement.previousElementSibling.appendChild(subList);
        precedingIdTextElement.removeAttribute("value");
    }
    movableElement.previousElementSibling.lastElementChild.appendChild(movableElement);

    // Adjust class:
    switch (document.getElementById("menu-active").className) {
        case "menu-lvl-0":
            document.getElementById("menu-active").className = "menu-lvl-1";
            break;
        case "menu-lvl-1":
            document.getElementById("menu-active").className = "menu-lvl-deep";
            break;
    }

    // Adjust inputs:
    parentIdTextElement.setAttribute("value", movableElement.parentElement.parentElement.id);
    precedingIdTextElement.setAttribute("value", movableElement.previousElementSibling.id);
});

outButton.addEventListener('click', function moveOut() {
    if (movableElement.parentElement.parentElement.tagName.toLowerCase() == "li") {
        movableElement.parentElement.parentElement.parentElement.insertBefore(movableElement, movableElement.parentElement.parentElement.nextElementSibling);
        parentIdTextElement.setAttribute("value", movableElement.parentElement.parentElement.id);
        precedingIdTextElement.setAttribute("value", movableElement.previousElementSibling.id);
    }

    // Adjust class:
    switch (document.getElementById("menu-active").className) {
        case "menu-lvl-1":
            document.getElementById("menu-active").className = "menu-lvl-0";
            break;
        case "menu-lvl-deep":
            document.getElementById("menu-active").className = "menu-lvl-1";
            break;
    }
});

downButton.addEventListener('click', function down() {
    movableElement.parentElement.insertBefore(movableElement, movableElement.nextElementSibling.nextElementSibling);
    parentIdTextElement.setAttribute("value", movableElement.parentElement.parentElement.id);
    precedingIdTextElement.setAttribute("value", movableElement.previousElementSibling.id);
});