package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Crafter;
import org.jetbrains.annotations.NotNull;

public class CrafterState implements IStateHandler<Crafter> {

    @Override
    public int minSupportedVersion() {
        return 210;
    }

    @Override
    public void save(@NotNull Crafter blockState, @NotNull ObjectNode node) {
        node.put("CraftingTicks", blockState.getCraftingTicks());

        ObjectNode slotDisabledNode = JsonHelper.getOrCreate(node,"SlotDisabled");
        for (int i = 0; i < blockState.getInventory().getSize(); i++) {
            slotDisabledNode.put(String.valueOf(i), blockState.isSlotDisabled(i));
        }
        node.put("Triggered", blockState.isTriggered());

        NonState.saveLootable(blockState, JsonHelper.getOrCreate(node, "Lootable"));
        NonState.saveNameable(blockState, node);
        NonState.saveInventory(blockState.getInventory(), JsonHelper.getOrCreate(node, "Inventory"));
    }

    @Override
    public void loadTo(@NotNull Crafter blockState, ObjectNode node) {
        blockState.setCraftingTicks(node.get("CraftingTicks") instanceof ObjectNode craftingTicksNode ? craftingTicksNode.asInt() : 0);

        if(node.get("SlotDisabled") instanceof ObjectNode slotDisabledNode) {
            for (int i = 0; i < blockState.getInventory().getSize(); i++) {
                if(slotDisabledNode.get(String.valueOf(i)) instanceof BooleanNode isDisabledNode) {
                    blockState.setSlotDisabled(i, !isDisabledNode.asBoolean()); // it works the other way somehow.
                }
            }
        }

        if(node.get("Triggered") instanceof BooleanNode triggeredNode) {
            blockState.setTriggered(triggeredNode.asBoolean());
        }

        if(node.get("Lootable") instanceof ObjectNode LootableNode) {
            NonState.loadToLootable(blockState, LootableNode);
        }
        NonState.loadToNameable(blockState, node);

        blockState.update();

        // Live object
        if(node.get("Inventory") instanceof ObjectNode inventoryNode) {        // Doesn't load idk why.
            NonState.loadToInventory(blockState.getInventory(), inventoryNode);
        }
    }
}
