package com.desoi.structra.service.blockstate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Campfire;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CampfireState implements BlockStateHandler<Campfire> {

    @Override
    public void save(@NotNull Campfire blockState, @NotNull ObjectNode node) {
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < blockState.getSize(); i++) {
            node.put(i + ".CookTime", blockState.getCookTime(i));
            node.put(i + ".CookTimeTotal", blockState.getCookTimeTotal(i));
            ItemStack item = blockState.getItem(i);
            if (item != null) {
                JsonNode itemNode =  mapper.valueToTree(item.serialize());
                node.set(i + ".Item", itemNode);
            } else {
                node.set(i + ".Item", JsonNodeFactory.instance.nullNode());
            }
        }
    }

    @Override
    public void loadTo(@NotNull Campfire blockState, ObjectNode node) {
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < blockState.getSize(); i++) {
            blockState.setCookTime(i, node.has(i + ".CookTime") ? node.get(i + ".CookTime").asInt() : 0);
            blockState.setCookTimeTotal(i, node.has(i + ".CookTimeTotal") ? node.get(i + ".CookTimeTotal").asInt() : 0);
            JsonNode itemNode = node.get(i + ".Item");
            ItemStack item = null;
            if (itemNode != null && itemNode.isObject()) {
                item = ItemStack.deserialize(mapper.convertValue(itemNode, Map.class));
            }
            blockState.setItem(i, item);
        }
        blockState.update();
    }
}
