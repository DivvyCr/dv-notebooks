import {EditorState, StateField} from "@codemirror/state"
import {drawSelection, EditorView} from "@codemirror/view"
import {history} from "@codemirror/commands"
import {syntaxHighlighting} from "@codemirror/language"
import {tagHighlighter, tags} from "@lezer/highlight"
import {markdown, markdownLanguage} from "@codemirror/lang-markdown"
import {vim} from "@replit/codemirror-vim"

// COMPILE TO editor.bundle.js WITH THE FOLLOWING:
// .\node_modules\.bin\rollup .\src\main\resources\static\js\codemirror-config.mjs -f iife -o .\src\main\resources\static\js\codemirror-editor.bundle.js -p @rollup/plugin-node-resolve

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
    {tag: tags.punctuation, class: "md-html-tag"},
    {tag: tags.typeName, class: "md-html-tag-name"},
    {tag: tags.meta, class: "md-meta"},
    {tag: tags.labelName, class: "md-meta-label"},
    {tag: tags.regexp, class: "md-math"},
    {tag: tags.operator, class: "md-math-delimiter"}
]);

const InlineLatexDelim = {resolve: "InlineLatex", mark: "InlineLatexMark"};
const InlineLaTeX /* MarkdownConfig */ = {
    defineNodes: [{
        name: "InlineLatex",
        style: tags.regexp
    }, {
        name: "InlineLatexMark",
        style: tags.operator
    }],
    parseInline: [{
        name: "InlineLatexParser",
        parse(cx, next, pos) {
            /* Handle OPENING delimiters: */
            if (next == 36 /* $ */ && cx.char(pos + 1) == 96 /* ` */) {
                let startDelim = pos;
                let endDelim = pos + 2;
                /* Eagerly capture characters until either EOL is reached or an ending delimiter is found: */
                while (endDelim < cx.end && (cx.char(endDelim) != 96 || cx.char(endDelim+1) != 36)) endDelim++;
                if (endDelim >= cx.end) return -1; /* Ending delimiter not found. */

                cx.addDelimiter(InlineLatexDelim, startDelim, startDelim + 2, true, false);
                return cx.addDelimiter(InlineLatexDelim, endDelim, endDelim + 2, false, true);
            }

            return -1;
        }
    }]
};

let startState = EditorState.create({
    doc: formTextElement.value,
    extensions: [
        EditorView.lineWrapping,
        history(),
        drawSelection(),
        syntaxHighlighting(mdHighlighter),
        syncFormText,
        vim(),
        markdown({base: markdownLanguage, extensions: [InlineLaTeX]}),
    ]
});

let editorView = new EditorView({
    state: startState,
    parent: editorElement
});