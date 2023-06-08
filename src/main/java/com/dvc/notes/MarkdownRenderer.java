package com.dvc.notes;

import com.dvc.notes.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.gitlab.GitLabExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.List;

public class MarkdownRenderer {

    private static final MutableDataSet flexmarkOptions = new MutableDataSet()
            .set(Parser.EXTENSIONS, List.of(
                    AttributesExtension.create(),
                    DefinitionExtension.create(),
                    GitLabExtension.create(),
                    TablesExtension.create()));
    private static final Parser markdownParser = Parser.builder(flexmarkOptions).build();
    private static final HtmlRenderer htmlRenderer = HtmlRenderer.builder(flexmarkOptions).build();

    public static String renderMarkdown(String markdown) {
        return htmlRenderer.render(markdownParser.parse(markdown));
    }
}
