package com.dvc.notes.definition;

import com.dvc.notes.definition.internal.DefinitionBlockParser;
import com.dvc.notes.definition.internal.DefinitionNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import org.jetbrains.annotations.NotNull;

public class DefinitionExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
    public static DefinitionExtension create() {
        return new DefinitionExtension();
    }

    @Override
    public void rendererOptions(@NotNull MutableDataHolder mutableDataHolder) {
    }

    @Override
    public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String s) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
            htmlRendererBuilder.nodeRendererFactory(new DefinitionNodeRenderer.Factory());
        }
    }

    @Override
    public void parserOptions(MutableDataHolder mutableDataHolder) {
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customBlockParserFactory(new DefinitionBlockParser.Factory());
    }
}
