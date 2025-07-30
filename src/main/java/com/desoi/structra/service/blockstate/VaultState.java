package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Vault;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class VaultState implements IStateHandler<Vault> {

    @Override
    public int minSupportedVersion() {
        return 210;
    }

    @Override
    public void save(@NotNull Vault blockState, @NotNull ObjectNode node) {
        node.put("ActivationRange", blockState.getActivationRange());
        node.put("DeactivationRange", blockState.getDeactivationRange());

        NonState.saveLootTable(blockState.getLootTable(), JsonHelper.getOrCreate(node, "LootTable"));

        if (blockState.getDisplayedLootTable() != null) {
            NonState.saveLootTable(blockState.getDisplayedLootTable(), JsonHelper.getOrCreate(node, "DisplayedLootTable"));
        }

        if(!blockState.getKeyItem().isEmpty()) {
            node.put("KeyItem", JsonHelper.serializeItemStack(blockState.getKeyItem()));
        }
        if(!blockState.getDisplayedItem().isEmpty()) {
            node.put("DisplayedItem", JsonHelper.serializeItemStack(blockState.getDisplayedItem()));
        }

        node.put("NextStateUpdateTime", blockState.getNextStateUpdateTime());

        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Vault blockState, ObjectNode node) {
        if(node.has("ActivationRange")) {
            blockState.setActivationRange(node.get("ActivationRange").asDouble());
        }
        if (node.has("DeactivationRange")) {
            blockState.setDeactivationRange(node.get("DeactivationRange").asDouble());
        }

        if (node.get("LootTable") instanceof ObjectNode lootTableNode) {
            LootTable lootTable = NonState.getLootTable(lootTableNode);
            if(lootTable != null) blockState.setLootTable(lootTable);
        }

        if (node.get("DisplayedLootTable") instanceof ObjectNode displayedLootTableNode) {
            blockState.setDisplayedLootTable(NonState.getLootTable(displayedLootTableNode));
        }

        if (node.has("KeyItem")) {
            blockState.setKeyItem(JsonHelper.deserializeItemStack(node.get("KeyItem")));
        }

        if (node.has("DisplayedItem")) {
            blockState.setDisplayedItem(JsonHelper.deserializeItemStack(node.get("DisplayedItem")));
        }

        if (node.has("NextStateUpdateTime")) {
            blockState.setNextStateUpdateTime(node.get("NextStateUpdateTime").asLong());
        }

        loadToTileState(blockState, node);

        blockState.update();
    }
}
