package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.TestBlock;
import org.jetbrains.annotations.NotNull;

public class TestBlockState implements BlockStateHandler<TestBlock> {

    @Override
    public int minSupportedVersion() {
        return 215;
    }

    @Override
    public void save(@NotNull TestBlock blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull TestBlock blockState, ObjectNode node) {
    }
}
