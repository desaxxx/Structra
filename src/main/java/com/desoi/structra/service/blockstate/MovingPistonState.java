package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.papermc.paper.block.MovingPiston;
import org.jetbrains.annotations.NotNull;

public class MovingPistonState implements BlockStateHandler<MovingPiston> {

    @Override
    public int minSupportedVersion() {
        return 180;
    }

    @Override
    public void save(@NotNull MovingPiston blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull MovingPiston blockState, ObjectNode node) {
    }
}
