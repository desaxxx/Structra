package com.desoi.structra.service.blockstate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.SculkSensor;
import org.jetbrains.annotations.NotNull;

public class SculkSensorState implements BlockStateHandler<SculkSensor> {

    @Override
    public int minSupportedVersion() {
        return 170;
    }

    @Override
    public void save(@NotNull SculkSensor blockState, @NotNull ObjectNode node) {
        node.put("LastVibrationFrequency", blockState.getLastVibrationFrequency());
    }

    @Override
    public void loadTo(@NotNull SculkSensor blockState, ObjectNode node) {
        blockState.setLastVibrationFrequency(node.has("LastVibrationFrequency") ? node.get("LastVibrationFrequency").asInt() : 0);
        blockState.update();
    }
}
