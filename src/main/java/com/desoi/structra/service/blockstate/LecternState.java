package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.desoi.structra.service.NonState;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Lectern;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class LecternState implements BlockStateHandler<Lectern> {

    @Override
    public void save(@NotNull Lectern blockState, @NotNull ObjectNode node) {
        node.put("Page", blockState.getPage());

        Inventory snapshotInv = blockState.getSnapshotInventory();
        ObjectNode invNode = node.objectNode();
        NonState.saveInventory(snapshotInv, invNode);
        node.set("Inventory", invNode);
    }

    @Override
    public void loadTo(@NotNull Lectern blockState, ObjectNode node) {
        blockState.setPage(node.has("Page") ? node.get("Page").asInt() : 0);

        if (node.has("Inventory") && node.get("Inventory").isObject()) {
            ObjectNode invNode = (ObjectNode) node.get("Inventory");
            NonState.loadToInventory(blockState.getInventory(), invNode);
        }

        blockState.update();
    }
}
