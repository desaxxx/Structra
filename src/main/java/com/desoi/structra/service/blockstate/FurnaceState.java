package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Furnace;
import org.jetbrains.annotations.NotNull;

public class FurnaceState implements IStateHandler<Furnace> {

    @Override
    public void save(@NotNull Furnace blockState, @NotNull ObjectNode node) {
        node.put("BurnTime", blockState.getBurnTime());
        node.put("CookTime", blockState.getCookTime());
        node.put("CookTimeTotal", blockState.getCookTimeTotal());
        node.put("CookSpeedMultiplier", blockState.getCookSpeedMultiplier());

        NonState.saveNameable(blockState, node);
        NonState.saveInventory(blockState.getInventory(), JsonHelper.getOrCreate(node, "Inventory"));
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Furnace blockState, @NotNull ObjectNode node) {
        blockState.setBurnTime(node.has("BurnTime") ? node.get("BurnTime").shortValue() : 0);
        blockState.setCookTime(node.has("CookTime") ? node.get("CookTime").shortValue() : 0);
        blockState.setCookTimeTotal(node.has("CookTimeTotal") ? node.get("CookTimeTotal").shortValue() : 0);
        blockState.setCookSpeedMultiplier(node.has("CookSpeedMultiplier") ? node.get("CookSpeedMultiplier").shortValue() : 0);

        NonState.loadToNameable(blockState, node);
        loadToTileState(blockState, node);

        blockState.update(true, false);

        // Live object
        if(node.get("Inventory") instanceof ObjectNode inventoryNode) {
            NonState.loadToInventory(blockState.getInventory(), inventoryNode);
        }
    }
}