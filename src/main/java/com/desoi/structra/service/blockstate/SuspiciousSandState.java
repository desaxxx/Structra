package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.SuspiciousSand;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("removal")
public class SuspiciousSandState implements IStateHandler<SuspiciousSand> {

    @Override
    public int minSupportedVersion() {
        return 194;
    }

    @Override
    public void save(@NotNull SuspiciousSand blockState, @NotNull ObjectNode node) {
        node.set("Item", objectMapper.valueToTree(blockState.getItem().serialize()));

        NonState.saveLootable(blockState, JsonHelper.getOrCreate(node, "Lootable"));
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull SuspiciousSand blockState, ObjectNode node) {
        if(node.has("Item")) {
            blockState.setItem(JsonHelper.deserializeItemStack(node.get("Item")));
        }
        if(node.get("Lootable") instanceof ObjectNode lootableNode) {
            NonState.loadToLootable(blockState, lootableNode);
        }
        loadToTileState(blockState, node);

        blockState.update();
    }
}
