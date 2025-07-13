package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Conduit;
import org.jetbrains.annotations.NotNull;

public class ConduitState implements BlockStateHandler<Conduit> {

    @Override
    public void save(@NotNull Conduit blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull Conduit blockState, ObjectNode node) {
    }
}
