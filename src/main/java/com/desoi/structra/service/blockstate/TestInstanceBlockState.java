package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.TestInstanceBlock;
import org.jetbrains.annotations.NotNull;

public class TestInstanceBlockState implements BlockStateHandler<TestInstanceBlock> {

    @Override
    public int minSupportedVersion() {
        return 215;
    }

    @Override
    public void save(@NotNull TestInstanceBlock blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull TestInstanceBlock blockState, ObjectNode node) {
    }
}
