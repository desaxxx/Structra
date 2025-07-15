package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.BrushableBlock;
import org.jetbrains.annotations.NotNull;

public class BrushableBlockState implements IStateHandler<BrushableBlock> {

    @Override
    public int minSupportedVersion() {
        return 200;
    }

    @Override
    public void save(@NotNull BrushableBlock blockState, @NotNull ObjectNode node) {
        node.set("Item", objectMapper.valueToTree(blockState.getItem().serialize()));


        ObjectNode lootableNode = JsonNodeFactory.instance.objectNode();
        NonState.saveLootable(blockState, lootableNode);
        node.set("Lootable", lootableNode);
    }

    @Override
    public void loadTo(@NotNull BrushableBlock blockState, ObjectNode node) {
        if (node.has("Item")) {
            blockState.setItem(JsonHelper.deserializeItemStack(node.get("Item")));
        }

        ObjectNode lootableNode = node.has("Lootable") ? (ObjectNode) node.get("Lootable") : null;
        NonState.loadToLootable(blockState, lootableNode);

        blockState.update();
    }
}
