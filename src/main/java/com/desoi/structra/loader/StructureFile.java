package com.desoi.structra.loader;

import com.desoi.structra.Structra;
import com.desoi.structra.model.Position;
import com.desoi.structra.util.Validate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class StructureFile {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected final @NotNull File file;
    protected final @NotNull ObjectNode root;

    protected final @NotNull String version;
    protected final int xSize;
    protected final int ySize;
    protected final int zSize;
    protected final @NotNull Position relative;

    protected final @NotNull ObjectNode paletteNode;
    protected final @NotNull ArrayNode blockDataNode;
    protected final @NotNull ObjectNode tileEntitiesNode;
    protected final @NotNull ObjectNode entitiesNode;

    public StructureFile(File file) {
        Validate.notNull(file, "File cannot be null.");
        Validate.validate(file.exists(), "File doesn't exist.");
        Validate.validate(file.getName().endsWith(Structra.FILE_EXTENSION), String.format("File extension must be '%s'.", Structra.FILE_EXTENSION));
        this.root = Validate.validateException(() -> (ObjectNode) objectMapper.readTree(file), "Failed to read root node of the file.", true);
        Validate.validate(root.get("Version") instanceof TextNode, "Failed to read Version node - expected a text.");
        Validate.validate(root.get("Size") instanceof ObjectNode sizeNode &&
                sizeNode.get("x") instanceof IntNode &&
                sizeNode.get("y") instanceof IntNode &&
                sizeNode.get("z") instanceof IntNode, "Failed to read Size node - expected (x:int, y:int, z:int).");
        Validate.validate(root.get("Relative") instanceof ObjectNode relativeNode &&
                relativeNode.get("x") instanceof IntNode &&
                relativeNode.get("y") instanceof IntNode &&
                relativeNode.get("z") instanceof IntNode, "Failed to read Relative node - expected (x:int, y:int, z:int).");
        Validate.validate(root.get("Palette") instanceof ObjectNode, "Failed to read Palette node - expected an object.");
        Validate.validate(root.get("BlockData") instanceof ArrayNode, "Failed to read BlockData node - expected an array.");
        Validate.validate(root.get("TileEntities") instanceof ObjectNode, "Failed to read TileEntities node - expected an object.");

        this.file = file;
        this.version = root.get("Version").asText();
        ObjectNode sizeNode = (ObjectNode) root.get("Size");
        this.xSize = sizeNode.get("x").asInt();
        this.ySize = sizeNode.get("y").asInt();
        this.zSize = sizeNode.get("z").asInt();
        ObjectNode relativeNode = (ObjectNode) root.get("Relative");
        this.relative = new Position(relativeNode.get("x").asInt(), relativeNode.get("y").asInt(), relativeNode.get("z").asInt());
        this.paletteNode = (ObjectNode) root.get("Palette");
        this.blockDataNode = (ArrayNode) root.get("BlockData");
        this.tileEntitiesNode = (ObjectNode) root.get("TileEntities");
        JsonNode entitiesRaw = root.get("Entities");
        this.entitiesNode = entitiesRaw instanceof ObjectNode ? (ObjectNode) entitiesRaw : JsonNodeFactory.instance.objectNode();
    }

    public boolean isHistoryFile() {
        return false;
    }

    public @NotNull File getFile() {
        return file;
    }

    public @NotNull ObjectNode getRoot() {
        return root;
    }

    public @NotNull String getVersion() {
        return version;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public int getZSize() {
        return zSize;
    }

    public @NotNull Position getRelative() {
        return relative;
    }

    public @NotNull ObjectNode getPaletteNode() {
        return paletteNode;
    }

    public @NotNull ArrayNode getBlockDataNode() {
        return blockDataNode;
    }

    public @NotNull ObjectNode getTileEntitiesNode() {
        return tileEntitiesNode;
    }

    public @NotNull ObjectNode getEntitiesNode() {
        return entitiesNode;
    }
}
