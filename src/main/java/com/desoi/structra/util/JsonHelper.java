package com.desoi.structra.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
    static public String getChildMatches(@NotNull ObjectNode node, @NotNull Object obj, @NotNull String def) {
        return node.properties().stream()
                .filter(entry -> {
                    JsonNode value = entry.getValue();
                    if(obj instanceof String) {
                        return value.isTextual() && value.asText().equals(obj);
                    }
                    else if (obj instanceof Integer) {
                        return value.isInt() && value.asInt() == (int) obj;
                    }
                    else if(obj instanceof Boolean) {
                        return value.isBoolean() && value.asBoolean() == (boolean) obj;
                    }
                    return false;
                })
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(def);
    }

    @NotNull
    static public <T> T treeToValue(TreeNode treeNode, Class<T> valueType) {
        try {
            return objectMapper.treeToValue(treeNode, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[Structra] Failed to parse tree to value: " + e);
        }
    }




    @NotNull
    static public Map<String, Object> exitNodeToMap(@NotNull ObjectNode node) {
        return objectMapper.convertValue(node, new TypeReference<Map<String, Object>>() {});
    }

    public static Map<String, Object> itemNodeToMap(ObjectNode node) {
        return objectMapper.convertValue(node, new TypeReference<>() {});
    }

    @NotNull
    static public ItemStack deserializeItemStack(@NotNull ObjectNode node) {
        return ItemStack.deserialize(itemNodeToMap(node));
    }
}
