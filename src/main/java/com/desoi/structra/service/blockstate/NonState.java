package com.desoi.structra.service.blockstate;

import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class NonState {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    static public <T extends Container> void saveContainer(@NotNull T container, @NotNull ObjectNode parentNode) {
        Component customName = container.customName();
        parentNode.put("CustomName", customName != null ? miniMessage.serialize(customName) : "");

        ObjectNode inventoryNode = JsonNodeFactory.instance.objectNode();
        saveInventory(container.getInventory(), inventoryNode);
        parentNode.set("Inventory", inventoryNode);
    }

    static public <T extends Container> void loadToContainer(@NotNull T container, ObjectNode parentNode) {
        if (parentNode == null) return;
        if (parentNode.has("CustomName")) {
            String customNameStr = parentNode.get("CustomName").asText();
            container.customName(customNameStr.isEmpty() ? null : miniMessage.deserialize(customNameStr));
        }
        ObjectNode inventoryNode = parentNode.has("Inventory") && parentNode.get("Inventory").isObject() ? (ObjectNode) parentNode.get("Inventory") : null;
        if (inventoryNode != null) loadToInventory(container.getInventory(), inventoryNode);
    }

    static public void saveInventory(@NotNull Inventory inventory, @NotNull ObjectNode parentNode) {
        ObjectNode itemsNode = JsonNodeFactory.instance.objectNode();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) continue;
            Map<String, Object> serialized = item.serialize();
            ObjectNode itemNode = JsonNodeFactory.instance.objectNode();
            serialized.forEach((key, value) -> {
                if (value != null) itemNode.putPOJO(key, value);
            });
            itemsNode.set(String.valueOf(i), itemNode);
        }
        parentNode.set("Item", itemsNode);
    }

    static public void loadToInventory(@NotNull Inventory inventory, ObjectNode parentNode) {
        if (parentNode == null) return;
        JsonNode itemsNode = parentNode.get("Item");
        if (itemsNode == null || !itemsNode.isObject()) return;
        ObjectNode objItemsNode = (ObjectNode) itemsNode;
        for (int i = 0; i < inventory.getSize(); i++) {
            JsonNode itemNode = objItemsNode.get(String.valueOf(i));
            if (itemNode != null && itemNode.isObject()) {
                ItemStack item = ItemStack.deserialize(JsonHelper.itemNodeToMap((ObjectNode) itemNode));
                inventory.setItem(i, item);
            } else {
                inventory.setItem(i, null);
            }
        }
    }

    static public <T extends Lootable> void saveLootable(@NotNull T lootable, @NotNull ObjectNode parentNode) {
        LootTable lootTable = lootable.getLootTable();
        if (lootTable != null) {
            lootTable.getKey();
            parentNode.put("LootTable", lootTable.getKey().toString());
        }
        parentNode.put("Seed", lootable.getSeed());
    }

    static public <T extends Lootable> void loadToLootable(@NotNull T lootable, ObjectNode parentNode) {
        if (parentNode == null) return;
        lootable.setLootTable(getLootTable(parentNode));
        if (parentNode.has("Seed")) lootable.setSeed(parentNode.get("Seed").asLong());
    }

    static public <T extends LootTable> void saveLootTable(@NotNull T lootTable, @NotNull ObjectNode parentNode) {
        lootTable.getKey();
        parentNode.put("LootTable", lootTable.getKey().toString());
    }

    @Nullable
    static public LootTable getLootTable(ObjectNode parentNode) {
        if (parentNode == null) return null;
        String keyString = parentNode.has("LootTable") ? parentNode.get("LootTable").asText() : null;
        if (keyString != null) {
            NamespacedKey key = NamespacedKey.fromString(keyString);
            if (key != null) {
                return Bukkit.getLootTable(key);
            }
        }
        return null;
    }

    static public void savePotionEffectType(@NotNull PotionEffect potionEffect, @NotNull ObjectNode parentNode, final int MINECRAFT_VERSION) {
        if (MINECRAFT_VERSION >= 205) {
            NamespacedKey key = potionEffect.getType().getKey();
            parentNode.put("PotionEffectType", key.toString());
        } else {
            String name = potionEffect.getType().getName();
            parentNode.put("PotionEffectType", name);
        }
    }

    @Nullable
    static public PotionEffectType getPotionEffectType(ObjectNode parentNode, final int MINECRAFT_VERSION) {
        if (parentNode == null) return null;
        if (MINECRAFT_VERSION >= 205) {
            String keyStr = parentNode.has("PotionEffectType") ? parentNode.get("PotionEffectType").asText() : "";
            NamespacedKey key = NamespacedKey.fromString(keyStr);
            if (key == null) return null;
            return Registry.EFFECT.get(key);
        } else {
            String name = parentNode.has("PotionEffectType") ? parentNode.get("PotionEffectType").asText() : "";
            return PotionEffectType.getByName(name);
        }
    }

}