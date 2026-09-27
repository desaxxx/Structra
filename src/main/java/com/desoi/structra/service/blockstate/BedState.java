package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Bed;
import org.jetbrains.annotations.NotNull;

public class BedState implements IStateHandler<Bed> {

    @Override
    public void save(@NotNull Bed blockState, @NotNull ObjectNode node) {
        // setColor is an unsupported operation.
    }

    @Override
    public void loadTo(@NotNull Bed blockState, ObjectNode node) {
        // setColor is an unsupported operation.
    }
}
