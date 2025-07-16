package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.SculkSensor;
import org.jetbrains.annotations.NotNull;

public class SculkSensorState implements IStateHandler<SculkSensor> {

    @Override
    public int minSupportedVersion() {
        return 170;
    }

    @Override
    public void save(@NotNull SculkSensor blockState, @NotNull ObjectNode node) {
        node.put("LastVibrationFrequency", blockState.getLastVibrationFrequency());
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull SculkSensor blockState, ObjectNode node) {
        blockState.setLastVibrationFrequency(node.has("LastVibrationFrequency") ? node.get("LastVibrationFrequency").asInt() : 0);
        loadToTileState(blockState, node);
        blockState.update();
    }
}
