package com.desoi.structra.util;

import com.desoi.structra.model.StructraException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.Map;

public class JsonHelper {

    private static final @NotNull ObjectMapper objectMapper = new ObjectMapper();

    @NotNull
    public static ObjectNode getOrCreate(@NotNull ObjectNode parent, @NotNull String key) {
        JsonNode existing  = parent.get(key);
        if(existing instanceof ObjectNode) {
            return (ObjectNode) existing;
        }else {
            return parent.putObject(key);
        }
    }

    @NotNull
    public static ArrayNode getOrCreateArray(@NotNull ObjectNode parent, @NotNull String key) {
        JsonNode existing  = parent.get(key);
        if(existing instanceof ArrayNode) {
            return (ArrayNode) existing;
        }else {
            return parent.putArray(key);
        }
    }

    @Nullable
    public static String findPath(@NotNull JsonNode root, @NotNull JsonNode target) {
        return findPath(root, target, "", 0);
    }

    @Nullable
    private static String findPath(@NotNull JsonNode root, @NotNull JsonNode target, @NotNull String currentPath, int attempt) {
        if (root.equals(target)) {
            return currentPath;
        }
        if(attempt >= 100) return currentPath + "/... (Keeps on)";

        if (root instanceof ObjectNode obj) {
            for(Map.Entry<String, JsonNode> entry : obj.properties()) {
                String result = findPath(entry.getValue(), target, currentPath + "/" + entry.getKey(), attempt+1);
                if(result != null) return result;
            }
        } else if (root instanceof ArrayNode array) {
            for (int i = 0; i < array.size(); i++) {
                String result = findPath(array.get(i), target, currentPath + "[" + i + "]", attempt+1);
                if(result != null) return result;
            }
        }
        return null;
    }

    @NotNull
    public static String getPropertyMatching(@NotNull JsonNode parentNode, @NotNull Object compare, @NotNull String def) {
        return parentNode.properties().stream()
                .filter(entry -> {
                    JsonNode value = entry.getValue();
                    if(compare instanceof String str) {
                        return value.isTextual() && value.asText().equals(str);
                    }
                    else if (compare instanceof Integer i) {
                        return value.isInt() && value.asInt() == i;
                    }
                    else if(compare instanceof Boolean b) {
                        return value.isBoolean() && value.asBoolean() == b;
                    }
                    return false;
                })
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(def);
    }

    @NotNull
    public static <T> T treeToValue(@NotNull TreeNode treeNode, @NotNull Class<T> valueType) {
        try {
            return objectMapper.treeToValue(treeNode, valueType);
        } catch (JsonProcessingException e) {
            throw new StructraException("Failed to parse tree to value: " + e);
        }
    }

    @NotNull
    public static Map<String, Object> nodeToMap(@NotNull JsonNode node) {
        return objectMapper.convertValue(node, new TypeReference<>() {});
    }

    // Serialize Bukkit Objects

    @NotNull
    public static String serializeItemStack(@NotNull ItemStack itemStack) {
        return Base64.getEncoder().encodeToString(itemStack.serializeAsBytes());
    }

    // Deserialize Bukkit Objects

    @NotNull
    public static ItemStack deserializeItemStack(@NotNull JsonNode node) {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(node.asText()));
    }

    @Nullable
    public static Location deserializeLocation(@NotNull JsonNode node) {
        try {
            return Location.deserialize(nodeToMap(node));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
