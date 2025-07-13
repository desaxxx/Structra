package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Jigsaw;
import org.jetbrains.annotations.NotNull;

public class JigsawState implements IStateHandler<Jigsaw> {

    @Override
    public void save(@NotNull Jigsaw blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull Jigsaw blockState, ObjectNode node) {
    }
}
