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

import java.util.Map;

public class JsonHelper {

    static private final @NotNull ObjectMapper objectMapper = new ObjectMapper();

    @NotNull
    static public ObjectNode getOrCreate(@NotNull ObjectNode parent, @NotNull String key) {
        JsonNode existing  = parent.get(key);
        if(existing instanceof ObjectNode) {
            return (ObjectNode) existing;
        }else {
            return parent.putObject(key);
        }
    }

    @NotNull
    static public ArrayNode getOrCreateArray(@NotNull ObjectNode parent, @NotNull String key) {
        JsonNode existing  = parent.get(key);
        if(existing instanceof ArrayNode) {
            return (ArrayNode) existing;
        }else {
            return parent.putArray(key);
        }
    }

    @NotNull
    static public String findPath(@NotNull JsonNode root, @NotNull JsonNode target) {
        return findPath(root, target, "", 0);
    }

    @NotNull
    static private String findPath(@NotNull JsonNode root, @NotNull JsonNode target, @NotNull String currentPath, int attempt) {
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
        return currentPath + "/- (Reached to an element that is neither Object nor Array)";
    }

    @NotNull
    static public String getPropertyMatching(@NotNull JsonNode parentNode, @NotNull Object compare, @NotNull String def) {
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
    static public <T> T treeToValue(@NotNull TreeNode treeNode, @NotNull Class<T> valueType) {
        try {
            return objectMapper.treeToValue(treeNode, valueType);
        } catch (JsonProcessingException e) {
            throw new StructraException("Failed to parse tree to value: " + e);
        }
    }

    @NotNull
    static public Map<String, Object> nodeToMap(@NotNull JsonNode node) {
        return objectMapper.convertValue(node, new TypeReference<>() {});
    }


    // Deserialize Bukkit Objects

    @NotNull
    static public ItemStack deserializeItemStack(@NotNull JsonNode node) {
        return ItemStack.deserialize(nodeToMap(node));
    }

    @Nullable
    static public Location deserializeLocation(@NotNull JsonNode node) {
        try {
            return Location.deserialize(nodeToMap(node));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
