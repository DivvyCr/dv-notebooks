package com.dvc.notes.definition.internal;

import com.dvc.notes.definition.DefinitionBlock;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.html.Attribute;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class DefinitionNodeRenderer implements NodeRenderer {
    public DefinitionNodeRenderer(DataHolder options) {
    }

    @Override
    public @Nullable Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        Set<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(DefinitionBlock.class, this::render));
        return set;
    }

    private void render(DefinitionBlock node, NodeRendererContext context, HtmlWriter html) {
        BasedSequence contents = node.getChars();

        html.srcPos(contents).withAttr().attr(Attribute.CLASS_ATTR, "definition").tag("section", false).line();
        context.renderChildren(node);
        html.closeTag("section").line();
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        @Override
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new DefinitionNodeRenderer(options);
        }
    }
}
