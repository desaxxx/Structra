package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.BrewingStand;
import org.jetbrains.annotations.NotNull;

public class BrewingStandState implements IStateHandler<BrewingStand> {

    @Override
    public void save(@NotNull BrewingStand blockState, @NotNull ObjectNode node) {
        node.put("BrewingTime", blockState.getBrewingTime());
        node.put("FuelLevel", blockState.getFuelLevel());

        // TODO BrewerInventory

        ObjectNode containerNode = JsonNodeFactory.instance.objectNode();
        NonState.saveContainer(blockState, containerNode);
        node.set("Container", containerNode);
    }

    @Override
    public void loadTo(@NotNull BrewingStand blockState, ObjectNode node) {
        blockState.setBrewingTime(node.has("BrewingTime") ? node.get("BrewingTime").asInt() : 0);
        blockState.setFuelLevel(node.has("FuelLevel") ? node.get("FuelLevel").asInt() : 0);
        ObjectNode containerNode = node.has("Container") && node.get("Container").isObject() ? (ObjectNode) node.get("Container") : null;
        NonState.loadToContainer(blockState, containerNode);

        blockState.update();
    }
}
