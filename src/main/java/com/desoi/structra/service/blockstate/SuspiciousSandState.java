package com.desoi.structra.service.blockstate;

import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.SuspiciousSand;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("removal")
public class SuspiciousSandState implements BlockStateHandler<SuspiciousSand> {

    @Override
    public int minSupportedVersion() {
        return 194;
    }

    @Override
    public void save(@NotNull SuspiciousSand blockState, @NotNull ObjectNode node) {
        ItemStack item = blockState.getItem();
        if (item != null) {
            ObjectNode itemNode = node.objectNode();
            Map<String,Object> serialized = item.serialize();
            serialized.forEach(itemNode::putPOJO);
            node.set("Item", itemNode);
        } else {
            node.putNull("Item");
        }

        ObjectNode lootNode = node.objectNode();
        NonState.saveLootable(blockState, lootNode);
        node.set("Lootable", lootNode);
    }

    @Override
    public void loadTo(@NotNull SuspiciousSand blockState, ObjectNode node) {
        ItemStack item = null;
        if(node.has("Item") && node.get("Item").isObject()) {
            item = ItemStack.deserialize(JsonHelper.itemNodeToMap((ObjectNode) node.get("Item")));
        }
        blockState.setItem(item);

        ObjectNode lootNode = node.has("Lootable") && node.get("Lootable").isObject() ? (ObjectNode) node.get("Lootable") : null;
        NonState.loadToLootable(blockState, lootNode);

        blockState.update();
    }
}
