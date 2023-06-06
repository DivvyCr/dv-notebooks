package com.dvc.notes.definition;

import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;

public class DefinitionBlock extends Block {
    private BasedSequence type = BasedSequence.NULL;

    @Override
    public @NotNull BasedSequence[] getSegments() {
        return new BasedSequence[0];
    }

    public BasedSequence getType() {
        return type;
    }

    public void setType(BasedSequence type) {
        this.type = type;
    }
}
