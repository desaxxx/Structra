package com.desoi.structra_dev.file;

import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import org.joml.Vector3ic;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

@NullMarked
public class StructureFileAssembler {

    private StructureFileAssembler() {
    }

    public static ObjectNode assemble(StructureFile structureFile) {
        Preconditions.checkNotNull(structureFile, "structureFile");

        ObjectNode root = JsonHelper.OBJECT_MAPPER.createObjectNode();

        root.put("Version", structureFile.getVersion());
        root.set("Size", assembleVector3i(structureFile.getSize()));
        root.set("Relative", assembleVector3i(structureFile.getRelative()));
        root.set("Palette", assemblePalette(structureFile.getPalette()));
        root.set("BlockData", assembleBlockData(structureFile));
        root.set("TileEntities", assembleTileEntity(structureFile.getTileEntities()));
        root.set("Entities", assembleEntities(structureFile.getEntities()));

        return root;
    }

    private static ObjectNode assembleVector3i(Vector3ic vector) {
        ObjectNode node = JsonHelper.OBJECT_MAPPER.createObjectNode();
        node.put("x", vector.x());
        node.put("y", vector.y());
        node.put("z", vector.z());
        return node;
    }

    private static ObjectNode assemblePalette(BiMap<String, Short> palette) {
        ObjectNode node = JsonHelper.OBJECT_MAPPER.createObjectNode();
        if (palette.isEmpty()) {
            return node;
        }

        for (Map.Entry<String, Short> entry : palette.entrySet()) {
            node.put(entry.getKey(), entry.getValue());
        }

        return node;
    }

    private static ArrayNode assembleBlockData(StructureFile structureFile) {
        ArrayNode node = JsonHelper.OBJECT_MAPPER.createArrayNode();

        for (int i = 0; i < structureFile.getBlockDataLength(); i++) {
            node.add(structureFile.getBlockData(i));
        }

        return node;
    }

    private static ObjectNode assembleTileEntity(Map<String, ObjectNode> tileEntities) {
        ObjectNode node = JsonHelper.OBJECT_MAPPER.createObjectNode();
        if (tileEntities.isEmpty()) {
            return node;
        }

        for (Map.Entry<String, ObjectNode> entry : tileEntities.entrySet()) {
            node.set(entry.getKey(), entry.getValue());
        }

        return node;
    }

    private static ObjectNode assembleEntities(Map<String, List<ObjectNode>> entities) {
        ObjectNode node = JsonHelper.OBJECT_MAPPER.createObjectNode();
        if (entities.isEmpty()) {
            return node;
        }

        for (Map.Entry<String, List<ObjectNode>> entry : entities.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }

            ArrayNode nearbyNode = node.putArray(entry.getKey());

            for (ObjectNode entityNode : entry.getValue()) {
                nearbyNode.add(entityNode);
            }
        }

        return node;
    }
}
