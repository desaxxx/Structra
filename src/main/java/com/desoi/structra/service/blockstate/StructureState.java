package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Structure;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public class StructureState implements IStateHandler<Structure> {

    @Override
    public void save(@NotNull Structure blockState, @NotNull ObjectNode node) {
        node.put("Author", blockState.getAuthor());
        node.put("Integrity", blockState.getIntegrity());
        node.put("Metadata", blockState.getMetadata());
        node.put("Mirror", blockState.getMirror().name());

        node.set("RelativePosition", objectMapper.valueToTree(blockState.getRelativePosition().serialize()));

        node.put("Rotation", blockState.getRotation().name());
        node.put("Seed", blockState.getSeed());
        node.put("StructureName", blockState.getStructureName());

        node.set("StructureSize", objectMapper.valueToTree(blockState.getStructureSize().serialize()));

        node.put("UsageMode", blockState.getUsageMode().name());
        node.put("BoundingBoxVisible", blockState.isBoundingBoxVisible());
        node.put("IgnoreEntities", blockState.isIgnoreEntities());
        node.put("ShowAir", blockState.isShowAir());
    }

    @Override
    public void loadTo(@NotNull Structure blockState, ObjectNode node) {
        if(node.has("Author")) {
            blockState.setAuthor(node.get("Author").asText());
        }
        if(node.has("Integrity") && node.get("Integrity") instanceof NumericNode numericNode) {
            blockState.setIntegrity(numericNode.asInt());
        }
        if(node.has("Metadata") && node.get("Metadata") instanceof ObjectNode objectNode) {
            blockState.setMetadata(objectNode.asText());
        }
        if(node.has("Mirror")) {
            Mirror mirror = Mirror.NONE;
            try {
                mirror = Mirror.valueOf(node.get("Mirror").asText(""));
            } catch (IllegalArgumentException ignored) {}
            blockState.setMirror(mirror);
        }

        if(node.has("RelativePosition")) {
            blockState.setRelativePosition(BlockVector.deserialize(JsonHelper.nodeToMap(node.get("RelativePosition"))));
        }

        if(node.has("Rotation")) {
            StructureRotation structureRotation = StructureRotation.NONE;
            try {
                structureRotation = StructureRotation.valueOf(node.get("Rotation").asText(""));
            } catch (IllegalArgumentException ignored) {}
            blockState.setRotation(structureRotation);
        }
        if(node.get("Seed") instanceof NumericNode numericNode) {
            blockState.setSeed(numericNode.asInt());
        }
        if(node.get("StructureName") instanceof ObjectNode objectNode) {
            blockState.setStructureName(objectNode.asText());
        }

        if (node.has("StructureSize")) {
            blockState.setStructureSize(BlockVector.deserialize(JsonHelper.nodeToMap(node.get("StructureSize"))));
        }
        if(node.has("UsageMode")) {
            try {
                blockState.setUsageMode(UsageMode.valueOf(node.get("UsageMode").asText("")));
            } catch (IllegalArgumentException ignored) {}
        }
        if(node.get("BoundingBoxVisible") instanceof BooleanNode booleanNode) {
            blockState.setBoundingBoxVisible(booleanNode.asBoolean());
        }
        if(node.get("IgnoreEntities") instanceof BooleanNode booleanNode) {
            blockState.setIgnoreEntities(booleanNode.asBoolean());
        }
        if(node.get("ShowAir") instanceof BooleanNode booleanNode) {
            blockState.setShowAir(booleanNode.asBoolean());
        }

        blockState.update();
    }
}
