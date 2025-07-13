package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Barrel;
import org.jetbrains.annotations.NotNull;

public class BarrelState implements IStateHandler<Barrel> {

    @Override
    public void save(@NotNull Barrel blockState, @NotNull ObjectNode node) {
        ObjectNode containerNode = JsonNodeFactory.instance.objectNode();
        ObjectNode lootableNode = JsonNodeFactory.instance.objectNode();

        NonState.saveContainer(blockState, containerNode);
        NonState.saveLootable(blockState, lootableNode);

        node.set("Container", containerNode);
        node.set("Lootable", lootableNode);
    }

    @Override
    public void loadTo(@NotNull Barrel blockState, ObjectNode node) {
        ObjectNode containerNode = (ObjectNode) node.get("Container");
        ObjectNode lootableNode = (ObjectNode) node.get("Lootable");

        NonState.loadToContainer(blockState, containerNode);
        NonState.loadToLootable(blockState, lootableNode);

        blockState.update();
    }

}
