package com.desoi.structra.service.blockstate;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.ChiseledBookshelf;
import org.jetbrains.annotations.NotNull;

public class ChiseledBookshelfState implements BlockStateHandler<ChiseledBookshelf> {

    @Override
    public void save(@NotNull ChiseledBookshelf blockState, @NotNull ObjectNode node) {
        node.put("LastInteractedSlot", blockState.getLastInteractedSlot());

        ObjectNode inventoryNode = JsonNodeFactory.instance.objectNode();
        NonState.saveInventory(blockState.getSnapshotInventory(), inventoryNode);
        node.set("Inventory", inventoryNode);
    }

    @Override
    public void loadTo(@NotNull ChiseledBookshelf blockState, ObjectNode node) {
        blockState.setLastInteractedSlot(node.has("LastInteractedSlot") ? node.get("LastInteractedSlot").asInt() : 0);

        ObjectNode inventoryNode = node.has("Inventory") && node.get("Inventory").isObject() ? (ObjectNode) node.get("Inventory") : null;
        NonState.loadToInventory(blockState.getSnapshotInventory(), inventoryNode);

        blockState.update();
    }
}
