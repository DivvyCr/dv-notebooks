package com.dvc.notes.admonition;

import com.dvc.notes.admonition.internal.AdmonitionBlockParser;
import com.dvc.notes.admonition.internal.AdmonitionNodeFormatter;
import com.dvc.notes.admonition.internal.AdmonitionNodeRenderer;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Extension for admonitions
 * <p>
 * Create it with {@link #create()} and then configure it on the builders
 * <p>
 * The parsed admonition text is turned into {@link AdmonitionBlock} nodes.
 */
public class AdmonitionExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension, Formatter.FormatterExtension
        // , Parser.ReferenceHoldingExtension
{
    final public static DataKey<Integer> CONTENT_INDENT = new DataKey<>("ADMONITION.CONTENT_INDENT", 4);
    final public static DataKey<Boolean> ALLOW_LEADING_SPACE = new DataKey<>("ADMONITION.ALLOW_LEADING_SPACE", true);
    final public static DataKey<Boolean> INTERRUPTS_PARAGRAPH = new DataKey<>("ADMONITION.INTERRUPTS_PARAGRAPH", true);
    final public static DataKey<Boolean> INTERRUPTS_ITEM_PARAGRAPH = new DataKey<>("ADMONITION.INTERRUPTS_ITEM_PARAGRAPH", true);
    final public static DataKey<Boolean> WITH_SPACES_INTERRUPTS_ITEM_PARAGRAPH = new DataKey<>("ADMONITION.WITH_SPACES_INTERRUPTS_ITEM_PARAGRAPH", true);
    final public static DataKey<Boolean> ALLOW_LAZY_CONTINUATION = new DataKey<>("ADMONITION.ALLOW_LAZY_CONTINUATION", true);
    final public static DataKey<String> UNRESOLVED_QUALIFIER = new DataKey<>("ADMONITION.UNRESOLVED_QUALIFIER", "note");
    final public static DataKey<Map<String, String>> QUALIFIER_TYPE_MAP = new DataKey<>("ADMONITION.QUALIFIER_TYPE_MAP", AdmonitionExtension::getQualifierTypeMap);

    public static Map<String, String> getQualifierTypeMap() {
        HashMap<String, String> qualifierTypeMap = new HashMap<>();
        qualifierTypeMap.put("info", "info");
        qualifierTypeMap.put("note", "note");
        qualifierTypeMap.put("tip", "tip");
        qualifierTypeMap.put("code", "code");
        return qualifierTypeMap;
    }

    private AdmonitionExtension() {}

    public static AdmonitionExtension create() {
        return new AdmonitionExtension();
    }

    @Override
    public void extend(Formatter.Builder formatterBuilder) {
        formatterBuilder.nodeFormatterFactory(new AdmonitionNodeFormatter.Factory());
    }

    @Override
    public void rendererOptions(@NotNull MutableDataHolder options) {}

    @Override
    public void parserOptions(MutableDataHolder options) {}

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customBlockParserFactory(new AdmonitionBlockParser.Factory());
    }

    @Override
    public void extend(@NotNull HtmlRenderer.Builder htmlRendererBuilder, @NotNull String rendererType) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
            htmlRendererBuilder.nodeRendererFactory(new AdmonitionNodeRenderer.Factory());
        } else if (htmlRendererBuilder.isRendererType("JIRA")) {

        }
    }
}
