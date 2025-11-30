package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.SculkShrieker;
import org.jetbrains.annotations.NotNull;

public class SculkShriekerState implements IStateHandler<SculkShrieker> {

    @Override
    public int minSupportedVersion() {
        return 1900;
    }

    @Override
    public void save(@NotNull SculkShrieker blockState, @NotNull ObjectNode node) {
        node.put("WarningLevel", blockState.getWarningLevel());
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull SculkShrieker blockState, @NotNull ObjectNode node) {
        blockState.setWarningLevel(node.has("WarningLevel") ? node.get("WarningLevel").asInt() : 0);
        loadToTileState(blockState, node);
        blockState.update(true, false);
    }
}
