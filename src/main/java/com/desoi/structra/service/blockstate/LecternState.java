package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Lectern;
import org.jetbrains.annotations.NotNull;

public class LecternState implements IStateHandler<Lectern> {

    @Override
    public void save(@NotNull Lectern blockState, @NotNull ObjectNode node) {
        node.put("Page", blockState.getPage());

        NonState.saveInventory(blockState.getInventory(), JsonHelper.getOrCreate(node, "Inventory"));
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Lectern blockState, ObjectNode node) {
        blockState.setPage(node.has("Page") ? node.get("Page").asInt() : 0);
        loadToTileState(blockState, node);

        blockState.update(true, false);

        // Live object
        if (node.get("Inventory") instanceof ObjectNode inventoryNode) {
            NonState.loadToInventory(blockState.getInventory(), inventoryNode);
        }
    }
}
