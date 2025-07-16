package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.BrewingStand;
import org.jetbrains.annotations.NotNull;

public class BrewingStandState implements IStateHandler<BrewingStand> {

    @Override
    public void save(@NotNull BrewingStand blockState, @NotNull ObjectNode node) {
        node.put("BrewingTime", blockState.getBrewingTime());
        node.put("FuelLevel", blockState.getFuelLevel());

        NonState.saveNameable(blockState, node);
        NonState.saveInventory(blockState.getInventory(), JsonHelper.getOrCreate(node, "Inventory"));

        // TODO check out other BrewerInventory fields
    }

    @Override
    public void loadTo(@NotNull BrewingStand blockState, ObjectNode node) {
        blockState.setBrewingTime(node.has("BrewingTime") ? node.get("BrewingTime").asInt() : 0);
        blockState.setFuelLevel(node.has("FuelLevel") ? node.get("FuelLevel").asInt() : 0);

        NonState.loadToNameable(blockState, node);

        blockState.update();

        // Live object
        if(node.get("Inventory") instanceof ObjectNode inventoryNode) {
            NonState.loadToInventory(blockState.getInventory(), inventoryNode);
        }
    }
}
