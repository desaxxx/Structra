package com.desoi.structra.service.blockstate;

import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Structure;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class StructureState implements BlockStateHandler<Structure> {

    @Override
    public void save(@NotNull Structure blockState, @NotNull ObjectNode node) {
        node.put("Author", blockState.getAuthor());
        node.put("Integrity", blockState.getIntegrity());
        node.put("Metadata", blockState.getMetadata());
        node.put("Mirror", blockState.getMirror().name());

        BlockVector relativePosition = blockState.getRelativePosition();
        if (relativePosition != null) {
            ObjectNode relNode = node.objectNode();
            Map<String, Object> relMap = relativePosition.serialize();
            relMap.forEach((k,v) -> relNode.putPOJO(k,v));
            node.set("RelativePosition", relNode);
        } else {
            node.putNull("RelativePosition");
        }

        node.put("Rotation", blockState.getRotation().toString());
        node.put("Seed", blockState.getSeed());
        node.put("StructureName", blockState.getStructureName());

        BlockVector structureSize = blockState.getStructureSize();
        if (structureSize != null) {
            ObjectNode sizeNode = node.objectNode();
            Map<String, Object> sizeMap = structureSize.serialize();
            sizeMap.forEach((k,v) -> sizeNode.putPOJO(k,v));
            node.set("StructureSize", sizeNode);
        }

        node.put("UsageMode", blockState.getUsageMode().toString());
        node.put("BoundingBoxVisible", blockState.isBoundingBoxVisible());
        node.put("IgnoreEntities", blockState.isIgnoreEntities());
        node.put("ShowAir", blockState.isShowAir());
    }

    @Override
    public void loadTo(@NotNull Structure blockState, ObjectNode node) {
        blockState.setAuthor(node.has("Author") ? node.get("Author").asText("none") : "none");
        blockState.setIntegrity(node.has("Integrity") ? node.get("Integrity").asInt(0) : 0);
        blockState.setMetadata(node.has("Metadata") ? node.get("Metadata").asText("") : "");

        try {
            blockState.setMirror(node.has("Mirror") ? Mirror.valueOf(node.get("Mirror").asText("")) : Mirror.NONE);
        } catch (IllegalArgumentException ignored) {}

        blockState.setSeed(node.has("Seed") ? node.get("Seed").asInt(0) : 0);
        blockState.setStructureName(node.has("StructureName") ? node.get("StructureName").asText("") : "");

        if (node.has("StructureSize") && node.get("StructureSize").isObject()) {
            BlockVector size = BlockVector.deserialize(JsonHelper.exitNodeToMap((ObjectNode) node.get("StructureSize")));
            blockState.setStructureSize(size);
        }

        try {
            blockState.setUsageMode(node.has("UsageMode") ? UsageMode.valueOf(node.get("UsageMode").asText("")) : UsageMode.DATA);
        } catch (IllegalArgumentException ignored) {}

        blockState.setBoundingBoxVisible(node.has("BoundingBoxVisible") && node.get("BoundingBoxVisible").isBoolean());
        blockState.setIgnoreEntities(node.has("IgnoreEntities") && node.get("IgnoreEntities").isBoolean());
        blockState.setShowAir(node.has("ShowAir") && node.get("ShowAir").isBoolean());

        blockState.update();
    }
}
