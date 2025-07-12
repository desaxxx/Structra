package com.desoi.structra.service.blockstate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.BrushableBlock;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BrushableBlockState implements BlockStateHandler<BrushableBlock> {

    @Override
    public int minSupportedVersion() {
        return 200;
    }

    @Override
    public void save(@NotNull BrushableBlock blockState, @NotNull ObjectNode node) {
        ItemStack item = blockState.getItem();
        if (item != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode itemNode = mapper.valueToTree(item);
            node.set("Item", itemNode);
        }

        ObjectNode lootableNode = JsonNodeFactory.instance.objectNode();
        NonState.saveLootable(blockState, lootableNode);
        node.set("Lootable", lootableNode);
    }

    @Override
    public void loadTo(@NotNull BrushableBlock blockState, ObjectNode node) {
        JsonNode itemNode = node.get("Item");
        ItemStack item = null;
        if (itemNode != null && itemNode.isObject()) {
            ObjectMapper mapper = new ObjectMapper();
            item = ItemStack.deserialize(mapper.convertValue(itemNode, Map.class));
        }
        blockState.setItem(item);

        ObjectNode lootableNode = node.has("Lootable") ? (ObjectNode) node.get("Lootable") : null;
        NonState.loadToLootable(blockState, lootableNode);

        blockState.update();
    }
}
