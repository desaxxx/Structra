package com.desoi.structra.writer;

import com.desoi.structra.Structra;
import com.desoi.structra.model.BlockTraversalOrder;
import com.desoi.structra.model.IInform;
import com.desoi.structra.model.Position;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Validate;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedList;

@Getter
public class StructureWriter implements IInform {
    public static final @NotNull String WRITER_VERSION = "2";
    private static final @NotNull BlockTraversalOrder BLOCK_TRAVERSAL_ORDER = BlockTraversalOrder.DEFAULT;

    private final @NotNull File file;
    private final @NotNull ObjectNode root;

    private final @NotNull String version;

    private final @NotNull CommandSender executor;
    private final @NotNull Location originLocation;
    private final @NotNull World originWorld;
    private final @NotNull Position minPosition;
    private final @NotNull Position maxPosition;

    private final int xSize;
    private final int ySize;
    private final int zSize;
    private final @NotNull Position relative;
    private @Nullable Position origin = null; // History-specific

    private final @NotNull ObjectNode paletteNode;
    private final @NotNull ArrayNode blockDataNode;
    private final @NotNull ObjectNode tileEntitiesNode;

    private final int delayTicks;
    private final int periodTicks;
    private final int batchSize;

    private final @NotNull LinkedList<Position> positions;
    @Setter
    private long startNanoTime;

    public StructureWriter(File file, CommandSender executor, Position position1, Position position2, Location originLocation,
                           int delayTicks, int periodTicks, int batchSize, boolean historyWriter) {
        Validate.notNull(file, "File cannot be null.");
        Validate.validate(file.getName().endsWith(Structra.FILE_EXTENSION), String.format("File extension must be '%s'.", Structra.FILE_EXTENSION));
        Validate.notNull(executor, "Executor cannot be null.");
        Validate.notNull(position1, position2, "Positions cannot be null.");
        Validate.notNull(originLocation, "Origin location cannot be null.");
        Validate.notNull(originLocation.getWorld(), "Origin world cannot be null.");
        Validate.validate(delayTicks >= 0, "Delay ticks cannot be negative.");
        Validate.validate(periodTicks >= 0, "Period ticks cannot be negative.");
        Validate.validate(batchSize > 0, "Batch size cannot be non-positive.");

        this.file = file;
        this.root = JsonHelper.OBJECT_MAPPER.createObjectNode();
        this.version = WRITER_VERSION;
        root.put("Version", version);

        this.executor = executor;
        this.originLocation = originLocation.clone();
        this.originWorld = originLocation.getWorld();
        Position min = Position.getMinimum(position1, position2);
        this.minPosition = new Position(min.getX(), min.getY(), min.getZ());
        Position max = Position.getMaximum(position1, position2);
        this.maxPosition = new Position(max.getX(), max.getY(), max.getZ());
        this.xSize = maxPosition.width(minPosition) + 1;
        this.ySize = maxPosition.height(minPosition) + 1;
        this.zSize = maxPosition.length(minPosition) + 1;
        ObjectNode sizeNode = JsonHelper.getOrCreate(root, "Size");
        sizeNode.put("x", xSize);
        sizeNode.put("y", ySize);
        sizeNode.put("z", zSize);
        sizeNode.put("Total", getSize());
        // Relative = Min - Origin -> Min = Relative + Origin
        this.relative = minPosition.clone().subtract(Position.fromLocation(this.originLocation, false));
        ObjectNode relativeNode = JsonHelper.getOrCreate(root, "Relative");
        relativeNode.put("x", relative.getX());
        relativeNode.put("y", relative.getY());
        relativeNode.put("z", relative.getZ());
        if(historyWriter) {
            this.origin = Position.fromLocation(this.originLocation, true);
            ObjectNode originNode = JsonHelper.getOrCreate(root, "Origin");
            originNode.put("x", origin.getX());
            originNode.put("y", origin.getY());
            originNode.put("z", origin.getZ());
            originNode.put("world", origin.getWorldName());
        }

        this.paletteNode = JsonHelper.getOrCreate(root, "Palette");
        this.blockDataNode = JsonHelper.getOrCreateArray(root, "BlockData");
        this.tileEntitiesNode = JsonHelper.getOrCreate(root, "TileEntities");

        this.delayTicks = delayTicks;
        this.periodTicks = periodTicks;
        this.batchSize = batchSize;
        this.positions = new LinkedList<>(BLOCK_TRAVERSAL_ORDER.getPositions(minPosition, maxPosition));
    }
    public StructureWriter(File file, CommandSender executor, Position position1, Position position2, Location originLocation,
                           int delayTicks, int periodTicks, int batchSize) {
        this(file, executor, position1, position2, originLocation, delayTicks, periodTicks, batchSize, false);
    }

    @Override
    public @NotNull CommandSender informer() {
        return executor;
    }

    private boolean silent = false;
    @Override
    public boolean isSilent() {
        return silent;
    }

    @Override
    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    /**
     * Create a {@link StructureWriteTask} for the writer.
     * @return Write task object
     * @since 1.1
     */
    @NotNull
    public StructureWriteTask createWriteTask() {
        return new StructureWriteTask(this);
    }

    public int getSize() {
        return xSize * ySize * zSize;
    }


    // ==================
    // Deprecated
    // ==================

    @Deprecated(since = "1.1")
    private StructureWriteTask task;

    /**
     * Get the single {@link StructureWriteTask} created for the writer.
     * @return Write task object
     * @since 1.0-SNAPSHOT
     * @deprecated Use {@link #createWriteTask()} since this method generates single task and reuses it.
     */
    @Deprecated(since = "1.1")
    @NotNull
    public StructureWriteTask getTask() {
        if(task == null) task = new StructureWriteTask(this);
        return task;
    }
}
