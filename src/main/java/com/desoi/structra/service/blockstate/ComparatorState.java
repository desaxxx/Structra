package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Comparator;
import org.jetbrains.annotations.NotNull;

public class ComparatorState implements BlockStateHandler<Comparator> {

    @Override
    public void save(@NotNull Comparator blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull Comparator blockState, ObjectNode node) {
    }
}
