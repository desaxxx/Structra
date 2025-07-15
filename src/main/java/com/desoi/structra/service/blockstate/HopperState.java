package com.desoi.structra.service.blockstate;

import com.desoi.structra.Util;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Hopper;
import org.jetbrains.annotations.NotNull;

public class HopperState implements IStateHandler<Hopper> {

    @Override
    public void save(@NotNull Hopper blockState, @NotNull ObjectNode node) {
        ObjectNode containerNode = JsonNodeFactory.instance.objectNode();
        NonState.saveContainer(blockState, containerNode);
        node.set("Container", containerNode);

        ObjectNode lootableNode = JsonNodeFactory.instance.objectNode();
        NonState.loadToLootable(blockState, containerNode);
        node.set("Lootable", lootableNode);
    }

    @Override
    public void loadTo(@NotNull Hopper blockState, ObjectNode node) {
        if(node.has("Container") && node.get("Container") instanceof ObjectNode containerNode) {
            NonState.loadToContainer(blockState, containerNode);
        }

        if(node.has("Lootable") && node.get("Lootable") instanceof ObjectNode lootableNode) {
            NonState.loadToLootable(blockState, lootableNode);
        }

        //Util.log("Debug, inventory: " + blockState.getInventory());

        Util.log("Debug, oldu mu olmadı mı bacım: " + blockState.update(true));
    }
}
