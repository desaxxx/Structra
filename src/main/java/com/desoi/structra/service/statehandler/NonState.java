package com.desoi.structra.service.statehandler;

import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Wrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Nameable;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.EntityBlockStorage;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NonState {

    static private final ObjectMapper objectMapper = new ObjectMapper();

    static public void saveInventory(@NotNull Inventory inventory, @NotNull ObjectNode parentNode) {
        ObjectNode itemsNode = JsonNodeFactory.instance.objectNode();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                itemsNode.set(String.valueOf(i), objectMapper.valueToTree(item.serialize()));
            }
        }
        parentNode.set("Items", itemsNode);
    }

    static public void loadToInventory(@NotNull Inventory inventory, ObjectNode parentNode) {
        if (parentNode == null) return;
        if (!(parentNode.get("Items") instanceof ObjectNode itemsNode)) return;

        for (int i = 0; i < inventory.getSize(); i++) {
            JsonNode itemNode = itemsNode.get(String.valueOf(i));
            if (itemNode != null) {
                ItemStack item = ItemStack.deserialize(JsonHelper.nodeToMap(itemNode));
                inventory.setItem(i, item);
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

    static public <T extends Entity> void saveEntityBlockStorage(@NotNull EntityBlockStorage<T> entityBlockStorage, @NotNull ObjectNode parentNode) {
        parentNode.put("MaxEntities", entityBlockStorage.getMaxEntities());
    }

    static public <T extends Entity> void loadToEntityBlockStorage(@NotNull EntityBlockStorage<T> entityBlockStorage, ObjectNode parentNode) {
        if(parentNode.has("MaxEntities") && parentNode.get("MaxEntities").isInt()) {
            entityBlockStorage.setMaxEntities(parentNode.get("MaxEntities").asInt());
        }
    }

    // MINI MESSAGE & NAMEABLE
    @SuppressWarnings("deprecation")
    static public <N extends Nameable> void saveNameable(@NotNull N nameable, @NotNull ObjectNode parentNode) {
        final int MINECRAFT_VERSION = Wrapper.getInstance().getVersion();
        /*
         * Paper API doesn't bundle adventure minimessage before 1.19.
         */
        if(MINECRAFT_VERSION >= 190) {
            if(nameable.customName() == null) return;
            parentNode.put("CustomName", MiniMessage.miniMessage().serializeOrNull(nameable.customName()));
        }else {
            // Deprecated
            if(nameable.getCustomName() == null) return;
            parentNode.put("CustomName", nameable.getCustomName());
        }
    }

    static public <N extends Nameable> void loadToNameable(@NotNull N nameable, ObjectNode parentNode) {
        final int MINECRAFT_VERSION = Wrapper.getInstance().getVersion();
        /*
         * Paper API doesn't have a bundle for adventure minimessage before 1.19.
         */
        if(parentNode.has("CustomName")) {
            if(MINECRAFT_VERSION >= 190) {
                nameable.customName(MiniMessage.miniMessage().deserializeOrNull(parentNode.get("CustomName").asText()));
            }else {
                //noinspection deprecation
                nameable.setCustomName(parentNode.get("CustomName").asText());
            }
        }
    }


    // TODO TileState methods, LockableTileState, TileStateInventoryHolder




    @SuppressWarnings("deprecation")
    static public void savePotionEffectType(@NotNull PotionEffect potionEffect, @NotNull ObjectNode parentNode, final int MINECRAFT_VERSION) {
        if (MINECRAFT_VERSION >= 205) {
            NamespacedKey key = potionEffect.getType().getKey();
            parentNode.put("PotionEffectType", key.toString());
        } else {
            // PotionEffectType doesn't have a Registry or any key before 1.20.5
            String name = potionEffect.getType().getName();
            parentNode.put("PotionEffectType", name);
        }
    }

    @SuppressWarnings("deprecation")
    @Nullable
    static public PotionEffectType getPotionEffectType(ObjectNode parentNode) {
        if (parentNode == null) return null;
        final int MINECRAFT_VERSION = Wrapper.getInstance().getVersion();
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