package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.ChiseledBookshelf;
import org.jetbrains.annotations.NotNull;

public class ChiseledBookshelfState implements IStateHandler<ChiseledBookshelf> {

    @Override
    public void save(@NotNull ChiseledBookshelf blockState, @NotNull ObjectNode node) {
        node.put("LastInteractedSlot", blockState.getLastInteractedSlot());

        NonState.saveInventory(blockState.getInventory(), JsonHelper.getOrCreate(node, "Inventory"));
    }

    @Override
    public void loadTo(@NotNull ChiseledBookshelf blockState, ObjectNode node) {
        if(node.get("LastInteractedSlot") instanceof IntNode lastInteractedSlotNode) {
            blockState.setLastInteractedSlot(lastInteractedSlotNode.asInt());
        }

        blockState.update();

        // Live object
        if(node.get("Inventory") instanceof ObjectNode inventoryNode) {
            NonState.loadToInventory(blockState.getInventory(), inventoryNode);
        }
    }
}
