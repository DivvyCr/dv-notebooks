package com.dvc.notes.definition.internal;

import com.dvc.notes.definition.DefinitionBlock;
import com.vladsch.flexmark.parser.block.*;
import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefinitionBlockParser extends AbstractBlockParser {
    final private static String DFN_FORMAT = "^(\\${2})\\s*$";
    final DefinitionBlock block = new DefinitionBlock();
    private boolean isBlockClosed = false;

    DefinitionBlockParser(DataHolder options) {
    }


    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public boolean canContain(ParserState state, BlockParser blockParser, final Block block) {
        return true;
    }

    @Override
    public BlockContinue tryContinue(ParserState state) {
        if (isBlockClosed) {
            return BlockContinue.none();
        }

        BasedSequence line = state.getLine();
        final BasedSequence tryLine = line.subSequence(state.getNextNonSpaceIndex(), line.length());
        Matcher matcher = Pattern.compile(DFN_FORMAT).matcher(tryLine);

        if (matcher.find()) {
            isBlockClosed = true;
            return BlockContinue.atIndex(state.getIndex() + 2); // Account for closing marker '$$'.
        }

        return BlockContinue.atIndex(state.getIndex());
    }

    @Override
    public void closeBlock(ParserState state) {
        block.setCharsFromContent();
    }

    public static class Factory implements CustomBlockParserFactory {

        @Override
        public @NotNull BlockParserFactory apply(@NotNull DataHolder options) {
            return new BlockFactory(options);
        }

        @Override
        public @Nullable Set<Class<?>> getAfterDependents() {
            return null;
        }

        @Override
        public @Nullable Set<Class<?>> getBeforeDependents() {
            return null;
        }

        @Override
        public boolean affectsGlobalScope() {
            return false;
        }
    }

    public static class BlockFactory extends AbstractBlockParserFactory {
        BlockFactory(DataHolder options) {
            super(options);
        }

        @Override
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
            if (state.getIndent() == 0 && !matchedBlockParser.getBlockParser().isParagraphParser()) {
                BasedSequence line = state.getLine();
                final BasedSequence tryLine = line.subSequence(state.getNextNonSpaceIndex(), line.length());

                Matcher matcher = Pattern.compile(DFN_FORMAT).matcher(tryLine);
                if (matcher.find()) {
                    DefinitionBlockParser definitionBlockParser = new DefinitionBlockParser(state.getProperties());
                    return BlockStart.of(definitionBlockParser).atIndex(state.getLineEndIndex()); // Account for opening marker '$$'.
                }
            }
            return BlockStart.none();
        }
    }
}
