package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
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
        if(!blockState.getItem().isEmpty()) {
            node.put("Item", JsonHelper.serializeItemStack(blockState.getItem()));
        }

        NonState.saveLootable(blockState, JsonHelper.getOrCreate(node, "Lootable"));
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull BrushableBlock blockState, ObjectNode node) {
        if (node.get("Item") instanceof ObjectNode itemNode) {
            blockState.setItem(JsonHelper.deserializeItemStack(itemNode));
        }

        if(node.get("Lootable") instanceof ObjectNode lootableNode) {
            NonState.loadToLootable(blockState, lootableNode);
        }
        loadToTileState(blockState, node);

        blockState.update();
    }
}
