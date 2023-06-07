import { EditorState, StateField } from "@codemirror/state"
import { EditorView, drawSelection } from "@codemirror/view"
import { history } from "@codemirror/commands"
import { syntaxHighlighting, defaultHighlightStyle, HighlightStyle } from "@codemirror/language"
import {tags, classHighlighter, tagHighlighter} from "@lezer/highlight"
import { markdown } from "@codemirror/lang-markdown"
import { vim } from "@replit/codemirror-vim"

// COMPILE TO editor.bundle.js WITH THE FOLLOWING:
// .\node_modules\.bin\rollup .\src\main\resources\static\js\editor.mjs -f iife -o .\src\main\resources\static\js\editor.bundle.js -p @rollup/plugin-node-resolve

const editorElement = document.getElementById("editor");
const formTextElement = document.getElementById("edit-content");

const syncFormText = StateField.define({
    // we won't use the actual StateField value, null or undefined is fine
    create: () => null,
    update: (value, transaction) => {
        if (transaction.docChanged) {
            // access new content via the Transaction
            formTextElement.value = transaction.newDoc.toString();
        }
        return null;
    },
});

const mdHighlighter = tagHighlighter([
    {tag: tags.heading, class: "md-heading"},
    {tag: tags.strong, class: "md-bold"},
    {tag: tags.emphasis, class: "md-italic"},
    {tag: tags.meta, class: "md-meta"},
    {tag: tags.labelName, class: "md-meta-label"}
]);

let startState = EditorState.create({
    doc: formTextElement.value,
    extensions: [
        EditorView.lineWrapping,
        history(),
        drawSelection(),
        syntaxHighlighting(mdHighlighter),
        syncFormText,
        vim(),
        markdown(),
    ]
});

let editorView = new EditorView({
    state: startState,
    parent: editorElement
});