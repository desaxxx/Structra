package com.desoi.structra_dev.file;

import com.desoi.structra.model.StructraException;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import org.joml.Vector3i;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

@NullMarked
public class StructureFileReader {

    private StructureFileReader() {
    }

    public static StructureFile read(File file) {
        Preconditions.checkNotNull(file, "file");

        ObjectNode root;
        try {
            root = (ObjectNode) JsonHelper.OBJECT_MAPPER.readTree(file);
        } catch (IOException e) {
            throw new StructraException("Failed to read file: " + e);
        }

        return new StructureFile(getVersion(root),
                getSize(root),
                getRelative(root),
                getPalette(root),
                getBlockData(root),
                getTileEntities(root),
                getEntities(root)
        );
    }

    private static String getVersion(ObjectNode root) {
        return root.get("Version").asText();
    }

    private static Vector3i getSize(ObjectNode root) {
        return getVector3i(root.get("Size"), new Vector3i());
    }

    private static Vector3i getRelative(ObjectNode root) {
        return getVector3i(root.get("Relative"), new Vector3i());
    }

    private static Vector3i getVector3i(@Nullable JsonNode root, Vector3i def) {
        if (!(root instanceof ObjectNode)) {
            return def;
        }

        int x = root.get("x").asInt();
        int y = root.get("y").asInt();
        int z = root.get("z").asInt();
        return new Vector3i(x, y, z);
    }

    private static BiMap<String, Short> getPalette(ObjectNode root) {
        if (!(root.get("Palette") instanceof ObjectNode paletteNode)) {
            return ImmutableBiMap.of();
        }

        BiMap<String, Short> palette = HashBiMap.create(paletteNode.size());

        for(Map.Entry<String, JsonNode> entry : paletteNode.properties()) {
            if (!(entry.getValue() instanceof NumericNode numericNode)) {
                continue;
            }

            palette.put(entry.getKey(), numericNode.shortValue());
        }

        return palette;
    }

    private static ShortArrayList getBlockData(ObjectNode root) {
        if (!(root.get("BlockData") instanceof ArrayNode blockDataNode)) {
            return new ShortArrayList(0);
        }

        ShortArrayList blockData = new ShortArrayList(blockDataNode.size());
        for (JsonNode node : blockDataNode) {
            if (!(node instanceof NumericNode numericNode)) {
                continue;
            }

            blockData.add(numericNode.shortValue());
        }

        return blockData;
    }

    private static Map<String, ObjectNode> getTileEntities(ObjectNode root) {
        if (!(root.get("TileEntities") instanceof ObjectNode tileEntitiesNode)) {
            return Map.of();
        }

        Map<String, ObjectNode> tileEntities = new HashMap<>(tileEntitiesNode.size());

        for(Map.Entry<String, JsonNode> entry : tileEntitiesNode.properties()) {
            if (!(entry.getValue() instanceof ObjectNode tileEntityNode)) {
                continue;
            }

            tileEntities.put(entry.getKey(), tileEntityNode);
        }

        return tileEntities;
    }

    private static Map<String, List<ObjectNode>> getEntities(ObjectNode root) {
        if (!(root.get("Entities") instanceof ObjectNode entitiesNode)) {
            return Map.of();
        }

        Map<String, List<ObjectNode>> entities = new HashMap<>(entitiesNode.size());

        for(Map.Entry<String, JsonNode> entry : entitiesNode.properties()) {
            if (!(entry.getValue() instanceof ArrayNode nearbyNode)) {
                continue;
            }

            List<ObjectNode> nearbyList = new ArrayList<>(nearbyNode.size());

            for(JsonNode entityNode : nearbyNode) {
                if (!(entityNode instanceof ObjectNode entityObjectNode)) {
                    continue;
                }

                nearbyList.add(entityObjectNode);
            }

            if (!nearbyList.isEmpty()) {
                entities.put(entry.getKey(), nearbyList);
            }
        }

        return entities;
    }
}
