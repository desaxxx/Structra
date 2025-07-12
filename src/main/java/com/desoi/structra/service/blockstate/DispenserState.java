package com.desoi.structra.service.blockstate;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Dispenser;
import org.jetbrains.annotations.NotNull;

public class DispenserState implements BlockStateHandler<Dispenser> {

    @Override
    public void save(@NotNull Dispenser blockState, @NotNull ObjectNode node) {
        node.put("CustomName", miniMessage.serializeOr(blockState.customName(),""));

        ObjectNode containerNode = JsonNodeFactory.instance.objectNode();
        NonState.saveContainer(blockState, containerNode);
        node.set("Container", containerNode);

        ObjectNode lootableNode = JsonNodeFactory.instance.objectNode();
        NonState.saveLootable(blockState, lootableNode);
        node.set("Lootable", lootableNode);

    }

    @Override
    public void loadTo(@NotNull Dispenser blockState, ObjectNode node) {
        blockState.customName(miniMessage.deserialize(node.get("CustomName").asText("")));

        ObjectNode lootableNode = node.has("Lootable") && node.get("Lootable").isObject() ? (ObjectNode) node.get("Lootable") : null;
        NonState.loadToLootable(blockState, lootableNode);

        ObjectNode containerNode = node.has("Container") && node.get("Container").isObject() ? (ObjectNode) node.get("Container") : null;
        NonState.loadToContainer(blockState, containerNode);

        blockState.update();
    }
}
