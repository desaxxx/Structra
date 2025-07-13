package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Bell;
import org.jetbrains.annotations.NotNull;

public class BellState implements BlockStateHandler<Bell> {

    @Override
    public void save(@NotNull Bell blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull Bell blockState, ObjectNode node) {
    }
}
