package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.block.Structure;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructureState implements IStateHandler<Structure> {

    @Override
    public void save(@NotNull Structure blockState, @NotNull ObjectNode node) {
        node.put("Author", blockState.getAuthor());
        node.put("Integrity", blockState.getIntegrity());
        node.put("Metadata", blockState.getMetadata());
        node.put("Mirror", blockState.getMirror().name());

        putBlockVector(node.putObject("RelativePosition"), blockState.getRelativePosition());

        node.put("Rotation", blockState.getRotation().name());
        node.put("Seed", blockState.getSeed());
        node.put("StructureName", blockState.getStructureName());

        putBlockVector(node.putObject("StructureSize"), blockState.getStructureSize());

        node.put("UsageMode", blockState.getUsageMode().name());
        node.put("BoundingBoxVisible", blockState.isBoundingBoxVisible());
        node.put("IgnoreEntities", blockState.isIgnoreEntities());
        node.put("ShowAir", blockState.isShowAir());
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Structure blockState, ObjectNode node) {
        if(node.get("Author") instanceof TextNode authorNode) {
            blockState.setAuthor(authorNode.asText());
        }
        if(node.has("Integrity") && node.get("Integrity") instanceof NumericNode numericNode) {
            blockState.setIntegrity(numericNode.asInt());
        }
        if(node.get("Metadata") instanceof TextNode metadataNode) {
            blockState.setMetadata(metadataNode.asText());
        }
        if(node.get("Mirror") instanceof TextNode mirrorNode) {
            try {
                blockState.setMirror(Mirror.valueOf(mirrorNode.asText()));
            } catch (IllegalArgumentException ignored) {}
        }

        if(node.has("RelativePosition")) {
            BlockVector relative = readBlockVector(node.get("RelativePosition"));
            if(relative != null) blockState.setRelativePosition(relative);
        }

        if(node.get("Rotation") instanceof TextNode rotationNode) {
            try {
                blockState.setRotation(StructureRotation.valueOf(rotationNode.asText()));
            } catch (IllegalArgumentException ignored) {}
        }
        if(node.get("Seed") instanceof NumericNode numericNode) {
            blockState.setSeed(numericNode.asInt());
        }
        if(node.get("StructureName") instanceof TextNode structureNameNode) {
            blockState.setStructureName(structureNameNode.asText());
        }

        if (node.has("StructureSize")) {
            BlockVector size = readBlockVector(node.get("StructureSize"));
            if(size != null) blockState.setStructureSize(size);
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

        loadToTileState(blockState, node);
        blockState.update(true, false);
    }


    private void putBlockVector(@NotNull ObjectNode node, @NotNull BlockVector blockVector) {
        node.put("x", blockVector.getBlockX());
        node.put("y", blockVector.getBlockY());
        node.put("z", blockVector.getBlockZ());
    }

    private @Nullable BlockVector readBlockVector(@NotNull JsonNode node) {
        if(node instanceof ObjectNode objNode) {
            return new BlockVector(objNode.get("x").asInt(), objNode.get("y").asInt(), objNode.get("z").asInt());
        }

        return null;
    }
}
