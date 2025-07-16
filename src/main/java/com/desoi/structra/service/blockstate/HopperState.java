package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Hopper;
import org.jetbrains.annotations.NotNull;

public class HopperState implements IStateHandler<Hopper> {

    @Override
    public void save(@NotNull Hopper blockState, @NotNull ObjectNode node) {
        NonState.saveLootable(blockState, JsonHelper.getOrCreate(node, "Lootable"));
        NonState.saveNameable(blockState, node);
        NonState.saveInventory(blockState.getInventory(), JsonHelper.getOrCreate(node, "Inventory"));
    }

    @Override
    public void loadTo(@NotNull Hopper blockState, ObjectNode node) {
        if(node.get("Lootable") instanceof ObjectNode lootableNode) {
            NonState.loadToLootable(blockState, lootableNode);
        }
        NonState.loadToNameable(blockState, node);

        blockState.update();

        // Live object
        if(node.get("Inventory") instanceof ObjectNode inventoryNode) {
            NonState.loadToInventory(blockState.getInventory(), inventoryNode);
        }
    }
}
