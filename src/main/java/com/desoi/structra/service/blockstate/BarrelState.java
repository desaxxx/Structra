package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Barrel;
import org.jetbrains.annotations.NotNull;

public class BarrelState implements IStateHandler<Barrel> {

    @Override
    public void save(@NotNull Barrel blockState, @NotNull ObjectNode node) {
        NonState.saveNameable(blockState, node);
        NonState.saveLootable(blockState, JsonHelper.getOrCreate(node, "Lootable"));
        NonState.saveInventory(blockState.getInventory(), JsonHelper.getOrCreate(node, "Inventory"));
    }

    @Override
    public void loadTo(@NotNull Barrel blockState, ObjectNode node) {
        if(node.get("Lootable") instanceof ObjectNode lootableNode) {
            NonState.loadToLootable(blockState, lootableNode);
        }
        NonState.loadToNameable(blockState, node);

        blockState.update();

        // [*] Live objects like inventory should be modified after blockState#update();
        // [*] Live object example: blockState.getX().setY(), NOT directly like blockState.setY()
        if(node.get("Inventory") instanceof ObjectNode inventoryNode) {
            NonState.loadToInventory(blockState.getInventory(), inventoryNode);
        }
    }

}
