package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Campfire;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CampfireState implements IStateHandler<Campfire> {

    @Override
    public void save(@NotNull Campfire blockState, @NotNull ObjectNode node) {
        for (int i = 0; i < blockState.getSize(); i++) {
            ObjectNode slotNode = JsonHelper.getOrCreate(node, "Slot_" + i);

            slotNode.put("CookTime", blockState.getCookTime(i));
            slotNode.put("CookTimeTotal", blockState.getCookTimeTotal(i));
            ItemStack item = blockState.getItem(i);
            if(item != null) {
                slotNode.set("Item", objectMapper.valueToTree(item.serialize()));
            }
        }
    }

    @Override
    public void loadTo(@NotNull Campfire blockState, ObjectNode node) {
        for (int i = 0; i < blockState.getSize(); i++) {
            ObjectNode slotNode = JsonHelper.getOrCreate(node, "Slot_" + i);

            blockState.setCookTime(i, slotNode.has("CookTime") ? slotNode.get("CookTime").asInt() : 0);
            blockState.setCookTimeTotal(i, slotNode.has("CookTimeTotal") ? slotNode.get("CookTimeTotal").asInt() : 0);
            if (slotNode.get("Item") instanceof ObjectNode itemNode) {
                blockState.setItem(i, JsonHelper.deserializeItemStack(itemNode));
            }
        }
        blockState.update();
    }
}
