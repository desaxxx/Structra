package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Chest;
import org.jetbrains.annotations.NotNull;

public class ChestState implements IStateHandler<Chest> {

    @Override
    public void save(@NotNull Chest blockState, @NotNull ObjectNode node) {
        NonState.saveNameable(blockState, node);
        NonState.saveLootable(blockState, JsonHelper.getOrCreate(node, "Lootable"));
        NonState.saveInventory(blockState.getInventory(), JsonHelper.getOrCreate(node, "Inventory"));
        // check out #getBlockInventory(), plain inventory may not be live.
    }

    @Override
    public void loadTo(@NotNull Chest blockState, ObjectNode node) {
        NonState.saveNameable(blockState, node);

        if(node.get("Lootable") instanceof ObjectNode lootableNode) {
            NonState.loadToLootable(blockState, lootableNode);
        }

        blockState.update();

        // Live object
        if(node.get("Inventory") instanceof ObjectNode inventoryNode) {
            NonState.loadToInventory(blockState.getInventory(), inventoryNode);
        }
    }
}
