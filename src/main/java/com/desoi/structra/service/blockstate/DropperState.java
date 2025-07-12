package com.desoi.structra.service.blockstate;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Dropper;
import org.jetbrains.annotations.NotNull;

public class DropperState implements BlockStateHandler<Dropper> {

    @Override
    public void save(@NotNull Dropper blockState, @NotNull ObjectNode node) {
        ObjectNode lootableNode = JsonNodeFactory.instance.objectNode();
        NonState.saveLootable(blockState, lootableNode);
        node.set("Lootable", lootableNode);

        ObjectNode containerNode = JsonNodeFactory.instance.objectNode();
        NonState.saveContainer(blockState, containerNode);
        node.set("Container", containerNode);
    }

    @Override
    public void loadTo(@NotNull Dropper blockState, @NotNull ObjectNode node) {
        ObjectNode lootableNode = node.has("Lootable") && node.get("Lootable").isObject() ? (ObjectNode) node.get("Lootable") : null;
        NonState.loadToLootable(blockState, lootableNode);

        ObjectNode containerNode = node.has("Container") && node.get("Container").isObject() ? (ObjectNode) node.get("Container") : null;
        NonState.loadToContainer(blockState, containerNode);

        blockState.update();
    }
}
