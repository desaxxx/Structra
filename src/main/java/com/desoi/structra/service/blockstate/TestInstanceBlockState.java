package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.TestInstanceBlock;
import org.jetbrains.annotations.NotNull;

public class TestInstanceBlockState implements IStateHandler<TestInstanceBlock> {

    @Override
    public int minSupportedVersion() {
        return 2105;
    }

    @Override
    public void save(@NotNull TestInstanceBlock blockState, @NotNull ObjectNode node) {
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull TestInstanceBlock blockState, ObjectNode node) {
        loadToTileState(blockState, node);
        blockState.update();
    }
}
