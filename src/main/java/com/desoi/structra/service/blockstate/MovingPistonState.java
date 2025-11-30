package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.papermc.paper.block.MovingPiston;
import org.jetbrains.annotations.NotNull;

public class MovingPistonState implements IStateHandler<MovingPiston> {

    @Override
    public int minSupportedVersion() {
        return 1800;
    }

    @Override
    public void save(@NotNull MovingPiston blockState, @NotNull ObjectNode node) {
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull MovingPiston blockState, ObjectNode node) {
        loadToTileState(blockState, node);
        blockState.update(true, false);
    }
}
