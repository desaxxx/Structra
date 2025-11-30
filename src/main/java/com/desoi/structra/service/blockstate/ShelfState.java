package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Shelf;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.1
 */
public class ShelfState implements IStateHandler<Shelf> {

    @Override
    public int minSupportedVersion() {
        return 2109;
    }

    @Override
    public void save(@NotNull Shelf blockState, @NotNull ObjectNode node) {
        NonState.saveInventory(blockState.getInventory(), node);
    }

    @Override
    public void loadTo(@NotNull Shelf blockState, ObjectNode node) {
        NonState.loadToInventory(blockState.getInventory(), node);
    }
}
