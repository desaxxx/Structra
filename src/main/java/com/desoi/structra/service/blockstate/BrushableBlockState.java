package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.BrushableBlock;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BrushableBlockState implements IStateHandler<BrushableBlock> {

    @Override
    public int minSupportedVersion() {
        return 200;
    }

    @Override
    public void save(@NotNull BrushableBlock blockState, @NotNull ObjectNode node) {
        ItemStack item = blockState.getItem();
        ObjectNode itemNode = node.objectNode();
        Map<String, Object> serialized = item.serialize();
        serialized.forEach(itemNode::putPOJO);
        node.set("Item", itemNode);


        ObjectNode lootableNode = JsonNodeFactory.instance.objectNode();
        NonState.saveLootable(blockState, lootableNode);
        node.set("Lootable", lootableNode);
    }

    @Override
    public void loadTo(@NotNull BrushableBlock blockState, ObjectNode node) {
        JsonNode itemNode = node.get("Item");
        ItemStack item = null;
        if (itemNode != null && itemNode.isObject()) {
            Map<String,Object> map = new ObjectMapper().convertValue(node.get("Item"), new TypeReference<>() {});
            item = ItemStack.deserialize(map);
        }
        blockState.setItem(item);

        ObjectNode lootableNode = node.has("Lootable") ? (ObjectNode) node.get("Lootable") : null;
        NonState.loadToLootable(blockState, lootableNode);

        blockState.update();
    }
}
