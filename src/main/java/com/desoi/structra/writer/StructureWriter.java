package com.desoi.structra.writer;

import com.desoi.structra.Structra;
import com.desoi.structra.model.BlockTraversalOrder;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Validate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

@Getter
public class StructureWriter {
    @Getter
    static private final ObjectMapper objectMapper = new ObjectMapper();

    private final @NotNull File file;
    private final @NotNull ObjectNode root;

    private final @NotNull String version;
    private final @NotNull BlockTraversalOrder blockTraversalOrder = BlockTraversalOrder.DEFAULT;

    private final @NotNull ObjectNode paletteNode;
    private final @NotNull ArrayNode blockDataNode;
    private final @NotNull ObjectNode tileEntitiesNode;

    private final @NotNull CommandSender executor;
    private final @NotNull Location originBlockLocation;
    private final @NotNull World originWorld;
    private final @NotNull Vector minVector;
    private final @NotNull Vector maxVector;
    private final int xSize;
    private final int ySize;
    private final int zSize;
    private final @NotNull Vector relative;

    private final int delayTicks;
    private final int periodTicks;
    private final int batchSize;

    private final @NotNull List<Vector> vectors;
    @Setter
    private long startNanoTime;

    public StructureWriter(File file, CommandSender executor, Vector vector1, Vector vector2, Location originLocation, int delayTicks, int periodTicks, int batchSize) {
        Validate.validate(file != null, "File cannot be null.");
        Validate.validate(file.exists(), "File doesn't exist.");
        Validate.validate(file.getName().endsWith(Structra.FILE_EXTENSION), String.format("File extension must be '%s'.", Structra.FILE_EXTENSION));
        Validate.validate(executor != null, "Executor cannot be null.");
        Validate.validate(vector1 != null && vector2 != null, "Vectors cannot be null.");
        Validate.validate(originLocation != null, "Origin location cannot be null.");
        Validate.validate(originLocation.getWorld() != null, "Origin world cannot be null.");
        Validate.validate(delayTicks >= 0, "Delay ticks cannot be negative.");
        Validate.validate(periodTicks >= 0, "Period ticks cannot be negative.");
        Validate.validate(batchSize > 0, "Batch size must be positive.");

        this.file = file;
        this.root = objectMapper.createObjectNode();
        this.version = Structra.getInstance().getDescription().getVersion();
        root.put("Version", version);
        this.paletteNode = JsonHelper.getOrCreate(root, "Palette");
        this.blockDataNode = JsonHelper.getOrCreateArray(root, "BlockData");
        this.tileEntitiesNode = JsonHelper.getOrCreate(root, "TileEntities");

        this.executor = executor;
        this.originBlockLocation = originLocation.getBlock().getLocation().clone();
        this.originWorld = originLocation.getWorld();
        this.minVector = Vector.getMinimum(vector1, vector2);
        this.maxVector = Vector.getMaximum(vector1, vector2);
        this.xSize = maxVector.getBlockX() - minVector.getBlockX();
        this.ySize = maxVector.getBlockY() - minVector.getBlockY();
        this.zSize = maxVector.getBlockZ() - minVector.getBlockZ();
        ObjectNode sizeNode = JsonHelper.getOrCreate(root, "Size");
        sizeNode.put("x", xSize);
        sizeNode.put("y", ySize);
        sizeNode.put("z", zSize);
        sizeNode.put("Total", getSize());
        this.relative = minVector.clone().subtract(originBlockLocation.toVector());

        this.delayTicks = delayTicks;
        this.periodTicks = periodTicks;
        this.batchSize = batchSize;
        this.vectors = blockTraversalOrder.getVectors(minVector, maxVector);
    }

    public StructureWriteTask execute() {
        StructureWriteTask writeTask = new StructureWriteTask(this);
        writeTask.execute();
        return writeTask;
    }

    public int getSize() {
        return xSize * ySize * zSize;
    }
}
