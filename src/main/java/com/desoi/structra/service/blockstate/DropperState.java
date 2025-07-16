package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Dropper;
import org.jetbrains.annotations.NotNull;

public class DropperState implements IStateHandler<Dropper> {

    @Override
    public void save(@NotNull Dropper blockState, @NotNull ObjectNode node) {
        NonState.saveLootable(blockState, JsonHelper.getOrCreate(node, "Lootable"));
        NonState.saveNameable(blockState, node);
        NonState.saveInventory(blockState.getInventory(), JsonHelper.getOrCreate(node, "Inventory"));
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Dropper blockState, @NotNull ObjectNode node) {
        if(node.get("Lootable") instanceof ObjectNode lootableNode) {
            NonState.loadToLootable(blockState, lootableNode);
        }
        NonState.loadToNameable(blockState, node);
        loadToTileState(blockState, node);

        blockState.update();

        // Live object
        if(node.get("Inventory") instanceof ObjectNode inventoryNode) {
            NonState.loadToInventory(blockState.getInventory(), inventoryNode);
        }
    }
}
