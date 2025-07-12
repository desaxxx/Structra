package com.desoi.structra.service.blockstate;

import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Vault;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class VaultState implements BlockStateHandler<Vault> {

    @Override
    public int minSupportedVersion() {
        return 210;
    }

    @Override
    public void save(@NotNull Vault blockState, @NotNull ObjectNode node) {
        node.put("ActivationRange", blockState.getActivationRange());
        node.put("DeactivationRange", blockState.getDeactivationRange());

        ObjectNode lootableNode = node.objectNode();
        NonState.saveLootTable(blockState.getLootTable(), lootableNode);
        node.set("LootTable", lootableNode);

        ItemStack keyItem = blockState.getKeyItem();
        node.putPOJO("KeyItem", keyItem.serialize());
    }

    @Override
    public void loadTo(@NotNull Vault blockState, ObjectNode node) {
        blockState.setActivationRange(node.has("ActivationRange") ? node.get("ActivationRange").asDouble() : 0.0);
        blockState.setDeactivationRange(node.has("DeactiverationRange") ? node.get("DeactivationRange").asDouble() : 0.0);

        LootTable lootTable = null;
        if (node.has("LootTable") && node.get("LootTable").isObject()) {
            lootTable = NonState.getLootTable((ObjectNode) node.get("LootTable"));
        }

        if (lootTable != null){
            blockState.setLootTable(lootTable);
        }

        ItemStack keyItem = null;

        if (node.has("KeyItem") && node.get("KeyItem").isObject()) {
            keyItem = ItemStack.deserialize(JsonHelper.exitNodeToMap((ObjectNode) node.get("KeyItem")));
        }

        if (keyItem != null) {
            blockState.setKeyItem(keyItem);
        }

        blockState.update();
    }
}
