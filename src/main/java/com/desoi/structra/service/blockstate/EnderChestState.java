package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.EnderChest;
import org.jetbrains.annotations.NotNull;

/*
 * 1.19 -> unimplemented Lidded.
 */
public class EnderChestState implements BlockStateHandler<EnderChest> {

    @Override
    public void save(@NotNull EnderChest blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull EnderChest blockState, ObjectNode node) {
    }
}
