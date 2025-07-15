package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Vault;
import org.bukkit.inventory.ItemStack;
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

        ObjectNode lootableNode = node.objectNode();
        NonState.saveLootTable(blockState.getLootTable(), lootableNode);
        node.set("LootTable", lootableNode);

        ItemStack keyItem = blockState.getKeyItem();
        node.set("KeyItem", objectMapper.valueToTree(keyItem.serialize()));
    }

    @Override
    public void loadTo(@NotNull Vault blockState, ObjectNode node) {
        if(node.has("ActivationRange")) {
            blockState.setActivationRange(node.get("ActivationRange").asDouble());
        }
        if (node.has("DeactivationRange")) {
            blockState.setDeactivationRange(node.get("DeactivationRange").asDouble());
        }

        LootTable lootTable = null;
        if (node.has("LootTable") && node.get("LootTable").isObject()) {
            lootTable = NonState.getLootTable((ObjectNode) node.get("LootTable"));
        }

        if (lootTable != null){
            blockState.setLootTable(lootTable);
        }

        ItemStack keyItem = null;

        if (node.has("KeyItem")) {
            keyItem = ItemStack.deserialize(JsonHelper.nodeToMap(node.get("KeyItem")));
        }

        if (keyItem != null) {
            blockState.setKeyItem(keyItem);
        }

        blockState.update();
    }
}
