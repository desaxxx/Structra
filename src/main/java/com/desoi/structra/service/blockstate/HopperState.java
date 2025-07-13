package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Hopper;
import org.jetbrains.annotations.NotNull;

public class HopperState implements IStateHandler<Hopper> {

    @Override
    public void save(@NotNull Hopper blockState, @NotNull ObjectNode node) {
        node.put("CustomName", miniMessage.serializeOr(blockState.customName(), ""));

        ObjectNode containerNode = JsonNodeFactory.instance.objectNode();
        NonState.saveContainer(blockState, containerNode);
        node.set("Container", containerNode);

        ObjectNode lootableNode = JsonNodeFactory.instance.objectNode();
        NonState.loadToLootable(blockState, containerNode);
        node.set("Lootable", lootableNode);
    }

    @Override
    public void loadTo(@NotNull Hopper blockState, ObjectNode node) {
        blockState.customName(miniMessage.deserialize(node.get("CustomName").asText("")));

        ObjectNode lootableNode = node.has("Lootable") && node.get("Lootable").isObject() ? (ObjectNode) node.get("Lootable") : null;
        NonState.loadToLootable(blockState, lootableNode);

        ObjectNode containerNode = node.has("Container") && node.get("Container").isObject() ? (ObjectNode) node.get("Container") : null;
        NonState.loadToContainer(blockState, containerNode);

        blockState.update();
    }
}
