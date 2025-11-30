package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.CopperGolemStatue;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.0.1
 */
public class CopperGolemStatueState implements IStateHandler<CopperGolemStatue> {

    @Override
    public int minSupportedVersion() {
        return 2109;
    }

    @Override
    public void save(@NotNull CopperGolemStatue blockState, @NotNull ObjectNode node) {
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull CopperGolemStatue blockState, ObjectNode node) {
        loadToTileState(blockState, node);
        blockState.update();
    }
}
