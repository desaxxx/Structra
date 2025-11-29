package com.desoi.structra.service.statehandler;

import com.desoi.structra.model.StructraException;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Wrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.Bukkit;
import org.bukkit.Nameable;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.EntityBlockStorage;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Base64;

public class NonState {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void saveInventory(@NotNull Inventory inventory, @NotNull ObjectNode parentNode) {
        ObjectNode itemsNode = JsonNodeFactory.instance.objectNode();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && !item.isEmpty()) {
                itemsNode.put(String.valueOf(i), JsonHelper.serializeItemStack(item));
            }
        }
        parentNode.set("Items", itemsNode);
    }

    public static void loadToInventory(@NotNull Inventory inventory, ObjectNode parentNode) {
        if (parentNode == null) return;
        if (!(parentNode.get("Items") instanceof ObjectNode itemsNode)) return;

        for (int i = 0; i < inventory.getSize(); i++) {
            JsonNode itemNode = itemsNode.get(String.valueOf(i));
            if (itemNode != null) {
                ItemStack item = JsonHelper.deserializeItemStack(itemNode);
                inventory.setItem(i, item);
            }
        }
    }

    public static <T extends Lootable> void saveLootable(@NotNull T lootable, @NotNull ObjectNode parentNode) {
        LootTable lootTable = lootable.getLootTable();
        if (lootTable != null) {
            parentNode.put("LootTable", lootTable.getKey().toString());
        }
        parentNode.put("Seed", lootable.getSeed());
    }

    public static <T extends Lootable> void loadToLootable(@NotNull T lootable, ObjectNode parentNode) {
        if (parentNode == null) return;
        lootable.setLootTable(getLootTable(parentNode));
        if (parentNode.has("Seed")) lootable.setSeed(parentNode.get("Seed").asLong());
    }

    public static <T extends LootTable> void saveLootTable(@NotNull T lootTable, @NotNull ObjectNode parentNode) {
        parentNode.put("LootTable", lootTable.getKey().toString());
    }

    @Nullable
    public static LootTable getLootTable(ObjectNode parentNode) {
        if (parentNode == null) return null;
        if(parentNode.get("LootTable") instanceof TextNode lootTableNode) {
            NamespacedKey key = NamespacedKey.fromString(lootTableNode.asText());
            if(key != null) {
                return Bukkit.getLootTable(key);
            }
        }
        return null;
    }

    public static <T extends Entity> void saveEntityBlockStorage(@NotNull EntityBlockStorage<T> entityBlockStorage, @NotNull ObjectNode parentNode) {
        parentNode.put("MaxEntities", entityBlockStorage.getMaxEntities());
    }

    public static <T extends Entity> void loadToEntityBlockStorage(@NotNull EntityBlockStorage<T> entityBlockStorage, ObjectNode parentNode) {
        if(parentNode.has("MaxEntities") && parentNode.get("MaxEntities").isInt()) {
            entityBlockStorage.setMaxEntities(parentNode.get("MaxEntities").asInt());
        }
    }

    // NAMEABLE
    @SuppressWarnings("deprecation")
    public static <N extends Nameable> void saveNameable(@NotNull N nameable, @NotNull ObjectNode parentNode) {
        if(nameable.getCustomName() == null) return;
        parentNode.put("CustomName", nameable.getCustomName());
    }

    @SuppressWarnings("deprecation")
    public static <N extends Nameable> void loadToNameable(@NotNull N nameable, ObjectNode parentNode) {
        if(parentNode.get("CustomName") instanceof TextNode customNameNode) {
            nameable.setCustomName(customNameNode.asText());
        }
    }

    public static <T extends TileState> void saveTileState(@NotNull T tileState, @NotNull ObjectNode parentNode) {
        /*
         * Serialization is added on Paper 1.19.2
         * I don't know how to store data without serialization since primitive or complex type can be of any object.
         */
        if(Wrapper.getInstance().getVersion() >= 192) {
            try {
                byte[] bytes = tileState.getPersistentDataContainer().serializeToBytes();
                parentNode.put("PersistentDataContainer", Base64.getEncoder().encodeToString(bytes));
            } catch (IOException e) {
                throw new StructraException("There was a problem saving PersistentDataContainer. " + e);
            }
        }
    }

    public static <T extends TileState> void loadToTileState(@NotNull T tileState, ObjectNode parentNode) {
        /*
         * Deserialization is added on Paper 1.19.2
         * I don't know how to store data without deserialization since primitive or complex type can be of any object.
         */
        if(Wrapper.getInstance().getVersion() >= 192 && parentNode.get("PersistentDataContainer") instanceof TextNode pdcNode) {
            try {
                tileState.getPersistentDataContainer().readFromBytes(Base64.getDecoder().decode(pdcNode.asText()));
            } catch (IOException e) {
                throw new StructraException("There was a problem reading PersistentDataContainer. " + e);
            }
        }
    }




    @SuppressWarnings("deprecation")
    public static void savePotionEffectType(@NotNull PotionEffect potionEffect, @NotNull ObjectNode parentNode, final int MINECRAFT_VERSION) {
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
    public static PotionEffectType getPotionEffectType(ObjectNode parentNode) {
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