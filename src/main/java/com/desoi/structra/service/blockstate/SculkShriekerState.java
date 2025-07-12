package com.desoi.structra.service.blockstate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.SculkShrieker;
import org.jetbrains.annotations.NotNull;

public class SculkShriekerState implements BlockStateHandler<SculkShrieker> {

    @Override
    public int minSupportedVersion() {
        return 190;
    }

    @Override
    public void save(@NotNull SculkShrieker blockState, @NotNull ObjectNode node) {
        node.put("WarningLevel", blockState.getWarningLevel());
    }

    @Override
    public void loadTo(@NotNull SculkShrieker blockState, @NotNull ObjectNode node) {
        blockState.setWarningLevel(node.has("WarningLevel") ? node.get("WarningLevel").asInt() : 0);
        blockState.update();
    }
}
