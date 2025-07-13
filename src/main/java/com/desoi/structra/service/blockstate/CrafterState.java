package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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

        ObjectNode slotDisabledNode = JsonNodeFactory.instance.objectNode();
        for (int i = 0; i < blockState.getInventory().getSize(); i++) { // düzeltim
            slotDisabledNode.put(String.valueOf(i), blockState.isSlotDisabled(i));
        }
        node.set("SlotDisabled", slotDisabledNode);

        node.put("Triggered", blockState.isTriggered());

        ObjectNode lootableNode = JsonNodeFactory.instance.objectNode();
        NonState.saveLootable(blockState, lootableNode);
        node.set("Lootable", lootableNode);

        ObjectNode containerNode = JsonNodeFactory.instance.objectNode();
        NonState.saveContainer(blockState, containerNode);
        node.set("Container", containerNode);
    }

    @Override
    public void loadTo(@NotNull Crafter blockState, ObjectNode node) {
        blockState.setCraftingTicks(node.has("CraftingTicks") ? node.get("CraftingTicks").asInt() : 0);

        ObjectNode slotDisabledNode = node.has("SlotDisabled") && node.get("SlotDisabled").isObject() ? (ObjectNode) node.get("SlotDisabled") : null;
        if (slotDisabledNode != null) {
            for (int i = 0; i < blockState.getInventory().getSize(); i++) {
                boolean disabled = slotDisabledNode.has(String.valueOf(i)) && slotDisabledNode.get(String.valueOf(i)).asBoolean();
                blockState.setSlotDisabled(i, disabled);
            }
        }

        blockState.setTriggered(node.has("Triggered") && node.get("Triggered").asBoolean());

        ObjectNode lootableNode = node.has("Lootable") && node.get("Lootable").isObject() ? (ObjectNode) node.get("Lootable") : null;
        if(lootableNode != null) {
            NonState.saveLootable(blockState, lootableNode);
        }

        ObjectNode containerNode = node.has("Container") &&  node.get("Container").isObject() ? (ObjectNode) node.get("Container") : null;
        if(containerNode != null) {
            NonState.saveContainer(blockState, containerNode);
        }

        blockState.update();
    }
}
