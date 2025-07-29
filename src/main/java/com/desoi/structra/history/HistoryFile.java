package com.desoi.structra.history;

import com.desoi.structra.loader.StructureFile;
import com.desoi.structra.util.Validate;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@Getter
public class HistoryFile extends StructureFile {

    protected final @NotNull Location originLocation;
    protected final @NotNull World originWorld;

    public HistoryFile(File file) {
        super(file);
        Validate.validate(root.get("Origin") instanceof ObjectNode originNode &&
                originNode.get("x") instanceof IntNode &&
                originNode.get("y") instanceof IntNode &&
                originNode.get("z") instanceof IntNode &&
                originNode.get("world") instanceof TextNode, "Failed to read Origin node - expected (x:int, y:int, z:int, world:string).");

        ObjectNode originNode = (ObjectNode) root.get("Origin");
        this.originWorld = Validate.validateException(() -> Bukkit.getWorld(originNode.get("world").asText()), "World not found.");
        this.originLocation = new Location(originWorld, originNode.get("x").asInt(), originNode.get("y").asInt(), originNode.get("z").asInt());
    }

    @Override
    public boolean isHistoryFile() {
        return true;
    }
}
