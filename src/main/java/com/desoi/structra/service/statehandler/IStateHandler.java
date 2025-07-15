package com.desoi.structra.service.statehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public interface IStateHandler<B extends BlockState> {

    default int minSupportedVersion() {
        return 165;
    }

    //
    ObjectMapper objectMapper = new ObjectMapper();

    @NotNull
    default String name() {
        return this.getClass().getSimpleName();
    }

    void save(@NotNull B blockState, @NotNull ObjectNode node);
    void loadTo(@NotNull B blockState, ObjectNode node);
}
