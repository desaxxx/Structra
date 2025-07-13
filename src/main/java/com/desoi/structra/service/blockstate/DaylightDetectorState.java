package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.DaylightDetector;
import org.jetbrains.annotations.NotNull;

public class DaylightDetectorState implements BlockStateHandler<DaylightDetector> {

    @Override
    public void save(@NotNull DaylightDetector blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull DaylightDetector blockState, ObjectNode node) {
    }
}
