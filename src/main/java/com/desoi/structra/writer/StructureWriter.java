package com.desoi.structra.writer;

import com.desoi.structra.Structra;
import com.desoi.structra.model.BlockTraversalOrder;
import com.desoi.structra.model.Position;
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

    private final @NotNull CommandSender executor;
    private final @NotNull Location originBlockLocation;
    private final @NotNull World originWorld;
    private final @NotNull Position minPosition;
    private final @NotNull Position maxPosition;
    private final int xSize;
    private final int ySize;
    private final int zSize;
    private final @NotNull Position relative;

    private final @NotNull ObjectNode paletteNode;
    private final @NotNull ArrayNode blockDataNode;
    private final @NotNull ObjectNode tileEntitiesNode;

    private final int delayTicks;
    private final int periodTicks;
    private final int batchSize;

    private final @NotNull List<Position> Positions;
    @Setter
    private long startNanoTime;

    public StructureWriter(File file, CommandSender executor, Position position1, Position position2, Location originLocation, int delayTicks, int periodTicks, int batchSize) {
        Validate.validate(file != null, "File cannot be null.");
        Validate.validate(file.getName().endsWith(Structra.FILE_EXTENSION), String.format("File extension must be '%s'.", Structra.FILE_EXTENSION));
        Validate.validate(executor != null, "Executor cannot be null.");
        Validate.validate(position1 != null && position2 != null, "Positions cannot be null.");
        Validate.validate(originLocation != null, "Origin location cannot be null.");
        Validate.validate(originLocation.getWorld() != null, "Origin world cannot be null.");
        Validate.validate(delayTicks >= 0, "Delay ticks cannot be negative.");
        Validate.validate(periodTicks >= 0, "Period ticks cannot be negative.");
        Validate.validate(batchSize > 0, "Batch size must be positive.");

        this.file = file;
        this.root = objectMapper.createObjectNode();
        this.version = Structra.getInstance().getDescription().getVersion();
        root.put("Version", version);

        this.executor = executor;
        this.originBlockLocation = originLocation.getBlock().getLocation().clone();
        this.originWorld = originLocation.getWorld();
        Position min = Position.getMinimum(position1, position2);
        this.minPosition = new Position(min.getX(), min.getY(), min.getZ());
        Position max = Position.getMaximum(position1, position2);
        this.maxPosition = new Position(max.getX(), max.getY(), max.getZ());
        this.xSize = maxPosition.getX() - minPosition.getX();
        this.ySize = maxPosition.getY() - minPosition.getY();
        this.zSize = maxPosition.getZ() - minPosition.getZ();
        ObjectNode sizeNode = JsonHelper.getOrCreate(root, "Size");
        sizeNode.put("x", xSize);
        sizeNode.put("y", ySize);
        sizeNode.put("z", zSize);
        sizeNode.put("Total", getSize());
        this.relative = minPosition.clone().subtract(Position.fromLocation(originBlockLocation));
        ObjectNode relativeNode = JsonHelper.getOrCreate(root, "Relative");
        relativeNode.put("x", relative.getX());
        relativeNode.put("y", relative.getY());
        relativeNode.put("z", relative.getZ());

        this.paletteNode = JsonHelper.getOrCreate(root, "Palette");
        this.blockDataNode = JsonHelper.getOrCreateArray(root, "BlockData");
        this.tileEntitiesNode = JsonHelper.getOrCreate(root, "TileEntities");

        this.delayTicks = delayTicks;
        this.periodTicks = periodTicks;
        this.batchSize = batchSize;
        this.Positions = blockTraversalOrder.getPositions(minPosition, maxPosition);
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
