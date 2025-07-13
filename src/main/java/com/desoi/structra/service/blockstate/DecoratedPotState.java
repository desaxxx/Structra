package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.DecoratedPot;
import org.jetbrains.annotations.NotNull;


public class DecoratedPotState implements BlockStateHandler<DecoratedPot> {

    /**
     * later of 1.20 -> added Sherd methods and Side enum
     * 1.20.4 -> implemented BlockInventoryHolder
     */
    @Override
    public int minSupportedVersion() {
        return 200;
    }

    @Override
    public void save(@NotNull DecoratedPot blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull DecoratedPot blockState, ObjectNode node) {
    }
}
