package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.SculkCatalyst;
import org.jetbrains.annotations.NotNull;

public class SculkCatalystState implements BlockStateHandler<SculkCatalyst> {

    @Override
    public int minSupportedVersion() {
        return 190;
    }

    @Override
    public void save(@NotNull SculkCatalyst blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull SculkCatalyst blockState, ObjectNode node) {
    }
}
