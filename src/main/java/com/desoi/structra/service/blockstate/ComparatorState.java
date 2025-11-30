package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Comparator;
import org.jetbrains.annotations.NotNull;

public class ComparatorState implements IStateHandler<Comparator> {

    @Override
    public void save(@NotNull Comparator blockState, @NotNull ObjectNode node) {
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Comparator blockState, ObjectNode node) {
        loadToTileState(blockState, node);
        blockState.update(true, false);
    }
}
