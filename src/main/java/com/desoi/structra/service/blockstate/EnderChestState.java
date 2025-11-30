package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.EnderChest;
import org.jetbrains.annotations.NotNull;

/*
 * 1.19 -> unimplemented Lidded.
 */
public class EnderChestState implements IStateHandler<EnderChest> {

    @Override
    public void save(@NotNull EnderChest blockState, @NotNull ObjectNode node) {
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull EnderChest blockState, ObjectNode node) {
        loadToTileState(blockState, node);
        blockState.update(true, false);
    }
}
