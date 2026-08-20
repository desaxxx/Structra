package com.desoi.structra.loader;

import com.desoi.structra.Structra;
import com.desoi.structra.history.HistoryFile;
import com.desoi.structra.model.BlockTraversalOrder;
import com.desoi.structra.model.IInform;
import com.desoi.structra.model.Position;
import com.desoi.structra.util.Validate;
import com.desoi.structra.writer.StructureWriteTask;
import com.desoi.structra.writer.StructureWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StructureLoader implements IInform {

    private final @NotNull StructureFile structureFile;

    private final @NotNull CommandSender executor;
    private final int delayTicks;
    private final int periodTicks;
    private final int batchSize;

    private final @NotNull Location originLocation;
    private final @NotNull World originWorld;
    private final @NotNull Position minPosition;
    private final @NotNull Position maxPosition;
    private final @NotNull BlockTraversalOrder blockTraversalOrder;
    private final @NotNull ArrayNode reorderedBlockDataNode;

    private final @NotNull List<Position> positions;

    public StructureLoader(StructureFile structureFile, CommandSender executor, int delayTicks, int periodTicks, int batchSize,
                           Location originLocation, BlockTraversalOrder blockTraversalOrder) {
        Validate.notNull(structureFile, "StructureFile cannot be null.");
        Validate.notNull(executor, "Executor cannot be null.");
        Validate.validate(delayTicks >= 0, "Delay ticks cannot be negative.");
        Validate.validate(periodTicks >= 0, "Period ticks cannot be negative.");
        Validate.validate(batchSize > 0, "Batch size must be positive.");
        Validate.notNull(originLocation, "Origin location cannot be null.");
        Validate.notNull(originLocation.getWorld(), "Origin world cannot be null.");
        Validate.notNull(blockTraversalOrder, "BlockTraversalOrder cannot be null.");

        this.structureFile = structureFile;
        this.executor = executor;
        this.delayTicks = delayTicks;
        this.periodTicks = periodTicks;
        this.batchSize = batchSize;

        this.originLocation = originLocation.clone();
        this.originWorld = originLocation.getWorld();
        this.minPosition = structureFile.getRelative().clone().add(Position.fromLocation(this.originLocation, false));
        this.maxPosition = minPosition.clone().add(new Position(structureFile.getXSize()-1, structureFile.getYSize()-1, structureFile.getZSize()-1));
        this.blockTraversalOrder = blockTraversalOrder;
        this.positions = blockTraversalOrder.getPositions(minPosition, maxPosition);
        this.reorderedBlockDataNode = blockTraversalOrder.reorderBlockData(structureFile.getBlockDataNode(), minPosition, maxPosition, BlockTraversalOrder.DEFAULT);
    }

    public StructureLoader(HistoryFile historyFile, CommandSender executor, int delayTicks, int periodTicks, int batchSize, BlockTraversalOrder blockTraversalOrder) {
        this(historyFile, executor, delayTicks, periodTicks, batchSize, historyFile.getOriginLocation(), blockTraversalOrder);
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
     * Validates file version.
     * @since 1.1
     */
    public void validateVersion() {
        String fileVersion = structureFile.getVersion();
        String pluginVersion = StructureWriter.WRITER_VERSION;
        Validate.validate(fileVersion.equals(pluginVersion),
                String.format("Structra file version mismatch! " +
                        "File version: %s, Plugin version: %s. " +
                        "This structure file cannot be loaded with the current plugin version. " +
                        "Please downgrade your plugin or regenerate the structra file."
                ,fileVersion, pluginVersion)
        );
    }

    /**
     * Create a {@link StructurePasteTask} for the loader.
     * @return Paste task object
     * @since 1.1
     */
    @NotNull
    public StructurePasteTask createPasteTask() {
        return new StructurePasteTask(this);
    }

    /**
     * Save current region as Structra to history folder.
     * @param completeTask Runnable to run after completing history save process
     * @since 1.0-SNAPSHOT
     */
    public void saveHistory(Runnable completeTask) {
        //Validate.validate(pasteTask == null || !pasteTask.isRunning(), "You cannot save history while loader is running.");
        File file = new File(Structra.getInstance().getHistoryFolder(), String.format("history_%s" + Structra.FILE_EXTENSION, new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())));
        StructureWriter writer = new StructureWriter(file, executor, minPosition, maxPosition, originLocation, delayTicks, periodTicks, batchSize, true);
        StructureWriteTask writerTask = writer.createWriteTask();
        writerTask.setSilent(true);
        writerTask.execute(completeTask);
    }

    public @NotNull StructureFile getStructureFile() {
        return structureFile;
    }

    public @NotNull CommandSender getExecutor() {
        return executor;
    }

    public int getDelayTicks() {
        return delayTicks;
    }

    public int getPeriodTicks() {
        return periodTicks;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public @NotNull Location getOriginLocation() {
        return originLocation;
    }

    public @NotNull World getOriginWorld() {
        return originWorld;
    }

    public @NotNull Position getMinPosition() {
        return minPosition;
    }

    public @NotNull Position getMaxPosition() {
        return maxPosition;
    }

    public @NotNull BlockTraversalOrder getBlockTraversalOrder() {
        return blockTraversalOrder;
    }

    public @NotNull ArrayNode getReorderedBlockDataNode() {
        return reorderedBlockDataNode;
    }

    public @NotNull List<Position> getPositions() {
        return positions;
    }
}
