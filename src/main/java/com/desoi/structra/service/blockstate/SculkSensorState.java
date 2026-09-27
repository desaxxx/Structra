package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.util.Wrapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.SculkSensor;
import org.jetbrains.annotations.NotNull;

public class SculkSensorState implements IStateHandler<SculkSensor> {

    @Override
    public int minSupportedVersion() {
        return 11700;
    }

    @Override
    public void save(@NotNull SculkSensor blockState, @NotNull ObjectNode node) {
        node.put("LastVibrationFrequency", blockState.getLastVibrationFrequency());

        if (Wrapper.getInstance().getVersion() >= 11801) {
            node.put("ListenerRange", blockState.getListenerRange());
        }

        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull SculkSensor blockState, ObjectNode node) {
        blockState.setLastVibrationFrequency(node.has("LastVibrationFrequency") ? node.get("LastVibrationFrequency").asInt() : 0);

        if (Wrapper.getInstance().getVersion() >= 11801 && node.get("ListenerRange") instanceof IntNode listenerRangeNode) {
            blockState.setListenerRange(listenerRangeNode.asInt());
        }

        loadToTileState(blockState, node);
        blockState.update(true, false);
    }
}
