import {EditorState, StateField} from "@codemirror/state"
import {drawSelection, EditorView, keymap} from "@codemirror/view"
import {history, indentWithTab} from "@codemirror/commands"
import {syntaxHighlighting} from "@codemirror/language"
import {styleTags, tagHighlighter, tags} from "@lezer/highlight"
import {markdown, markdownLanguage} from "@codemirror/lang-markdown"
import {vim} from "@replit/codemirror-vim"
import {parseMixed} from "@lezer/common"
import {parser as specialCharacterParser} from "../lezer/special-characters-lang.js"

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
    {tag: tags.operator, class: "md-special"},
    {tag: tags.controlOperator, class: "md-math-special"},
]);

const latexHighlightParser = specialCharacterParser.configure({
    props: [
        styleTags({
            Delimiter: tags.operator, // Parser Wrapper consumes delimiters from wrapped parser (ie. InlineLaTeX)?
            Special: tags.controlOperator,
            Any: tags.regexp
        })
    ]
});

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
            /* Latch onto OPENING delimiters: */
            if (next == 36 /* $ */ && cx.char(pos + 1) == 96 /* ` */) {
                return cx.addDelimiter(InlineLatexDelim, pos, pos + 2, true, false);
            }
            if (next == 96 /* ` */ && cx.char(pos + 1) == 36 /* $ */) {
                return cx.addDelimiter(InlineLatexDelim, pos, pos + 2, false, true);
            }
            return -1;
        },
        before: "InlineCode"
    }],
    wrap: parseMixed(node => {
        return node.name === "InlineLatex" ? {parser: latexHighlightParser} : null
    })
};

const BlockLaTeX = {
    defineNodes: [{
        name: "Latex",
        style: tags.regexp
    }, {
        name: "LatexMark",
        style: tags.operator
    }],
    parseBlock: [{
        name: "LatexParser",
        parse(cx, line) {
            if (line.text.trim() == "```math") {
                cx.addElement(cx.elt("LatexMark", cx.parsedPos, cx.parsedPos + line.text.length))
                cx.nextLine();
                const startPos = cx.parsedPos;

                while (line.text.trim() != "```") cx.nextLine();

                cx.addElement(cx.elt("Latex", startPos, cx.parsedPos - 1));
                cx.addElement(cx.elt("LatexMark", cx.parsedPos, cx.parsedPos + line.text.length))
                cx.nextLine(); // Must move past the block
                return true;
            }
            return false;
        },
        before: "FencedCode"
    }],
    wrap: parseMixed(node => {
        return node.name === "Latex" ? {parser: latexHighlightParser} : null
    })
}

const CustomDefinition = {
    defineNodes: [{
        name: "Definition",
        style: tags.operator
    }],
    parseInline: [{
        name: "CustomDefinitionParser",
        parse(cx, next, pos) {
            if (next == 38 /* & */ && cx.char(pos + 1) == 38 /* & */) {
                return cx.addElement(cx.elt("Definition", pos, pos + 2));
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
        markdown({base: markdownLanguage, extensions: [BlockLaTeX, InlineLaTeX, CustomDefinition]}),
        keymap.of([indentWithTab]), // By default, Tab is reserved for accessibile navigation.
    ]
});

let editorView = new EditorView({
    state: startState,
    parent: editorElement
});