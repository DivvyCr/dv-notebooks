package com.dvc.notes.admonition.internal;

import com.dvc.notes.admonition.AdmonitionBlock;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.*;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.html.Attribute;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import com.vladsch.flexmark.util.sequence.builder.tree.Segment;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.vladsch.flexmark.html.renderer.RenderingPhase.BODY_TOP;

public class AdmonitionNodeRenderer implements PhasedNodeRenderer {
    public static AttributablePart ADMONITION_SVG_OBJECT_PART = new AttributablePart("ADMONITION_SVG_OBJECT_PART");
    public static AttributablePart ADMONITION_HEADING_PART = new AttributablePart("ADMONITION_HEADING_PART");
    public static AttributablePart ADMONITION_ICON_PART = new AttributablePart("ADMONITION_ICON_PART");
    public static AttributablePart ADMONITION_TITLE_PART = new AttributablePart("ADMONITION_TITLE_PART");
    public static AttributablePart ADMONITION_BODY_PART = new AttributablePart("ADMONITION_BODY_PART");

    final private AdmonitionOptions options;

    public AdmonitionNodeRenderer(DataHolder options) {
        this.options = new AdmonitionOptions(options);
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        Set<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(AdmonitionBlock.class, this::render));
        return set;
    }

    @Override
    public Set<RenderingPhase> getRenderingPhases() {
        LinkedHashSet<RenderingPhase> phaseSet = new LinkedHashSet<>();
        phaseSet.add(BODY_TOP);
        return phaseSet;
    }

    @Override
    public void renderDocument(@NotNull NodeRendererContext context, @NotNull HtmlWriter html, @NotNull Document document, @NotNull RenderingPhase phase) {
        // Had content related to SVG icons, see:
        // https://github.com/vsch/flexmark-java/blob/7c187fb2bcf3bbdad4a75226a746aee752255675/flexmark-ext-admonition/src/main/java/com/vladsch/flexmark/ext/admonition/internal/AdmonitionNodeRenderer.java#L45
    }

    private void render(AdmonitionBlock node, NodeRendererContext context, HtmlWriter html) {
        String info = node.getInfo().toString().toLowerCase();
        String title = node.getTitle().toString();
        String type = this.options.qualifierTypeMap.get(info);
        if (type == null) {
            type = options.unresolvedQualifier;
        }

        BasedSequence openingMarker = node.getOpeningMarker(); // either !!!, ???, or ???+

        if (type.equals("code")) {
            /* RENDER THE FOLLOWING:
              <table class="code-snippet">
                <tbody>
                  <tr>
                    <td class="code-caption">
                      <span class="aside-icon code></span> TITLE
                    </td>
                  </tr>
                  <tr>
                    <td class="code-text">
                      CONTENT
                    </td>
                  </tr>
                </tbody>
              </table>
            */

            BasedSequence contents = node.getChars();
            html.srcPos(contents).withAttr()
                    .attr(Attribute.CLASS_ATTR, "code-snippet")
                    .tag("table", false).line();
            html.tag("tbody", false).line();

            html.tag("tr", false).line();
            html.withAttr()
                    .attr(Attribute.CLASS_ATTR, "code-caption")
                    .tag("td").line();
            html.withAttr()
                    .attr(Attribute.CLASS_ATTR, "aside-icon")
                    .attr(Attribute.CLASS_ATTR, "code")
                    .tag("span").closeTag("span");
            html.withAttr()
                    .attr(Attribute.CLASS_ATTR, "code-caption-text")
                    .tag("span");
            html.text(title).closeTag("span").line();
            html.closeTag("td").line();
            html.closeTag("tr").line();

            html.tag("tr").line();
            html.withAttr()
                    .attr(Attribute.CLASS_ATTR, "code-content")
                    .attr(Attribute.CLASS_ATTR, "line-numbers")
                    .tag("td").line();
            context.renderChildren(node);
            html.closeTag("td").line();
            html.closeTag("tr").line();

            html.closeTag("tbody").line();
            html.closeTag("table").line();
        } else {
            /* RENDER THE FOLLOWING:
              <table class="aside">
                <tbody>
                  <tr>
                    <td class="aside-icon TYPE"></td>
                    <td class="aside-content TYPE">CONTENT</td>
                  </tr>
                </tbody>
              </table>
            */

            BasedSequence contents = node.getChars();
            html.srcPos(contents).withAttr()
                    .attr(Attribute.CLASS_ATTR, "aside")
                    .tag("table", false).line();

            html.tag("tbody", false).line();
            html.tag("tr", false).line();

            html.withAttr()
                    .attr(Attribute.CLASS_ATTR, "aside-icon")
                    .attr(Attribute.CLASS_ATTR, type)
                    .tag("td").closeTag("td").line();
            html.withAttr()
                    .attr(Attribute.CLASS_ATTR, "aside-content")
                    .attr(Attribute.CLASS_ATTR, type)
                    .tag("td").line();
            context.renderChildren(node);
            html.closeTag("td").line();

            html.closeTag("tr").line();
            html.closeTag("tbody").line();

            html.closeTag("table").line();

            // html.attr(Attribute.CLASS_ATTR, "adm-body").withAttr(ADMONITION_BODY_PART).tag("div").indent().line();

            // context.renderChildren(node);

            // html.unIndent().closeTag("div").line();
        }
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        @Override
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new AdmonitionNodeRenderer(options);
        }
    }
}
