package com.desoi.structra.loader;

import com.desoi.structra.Structra;
import com.desoi.structra.history.HistoryFile;
import com.desoi.structra.model.IInform;
import com.desoi.structra.model.Position;
import com.desoi.structra.util.Validate;
import com.desoi.structra.writer.StructureWriteTask;
import com.desoi.structra.writer.StructureWriter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Getter
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

    private final @NotNull List<Position> positions;
    @Setter
    private long startNanoTime;

    public StructureLoader(StructureFile structureFile, CommandSender executor, int delayTicks, int periodTicks, int batchSize, Location originLocation) {
        Validate.validate(structureFile != null, "StructureFile cannot be null.");
        Validate.validate(executor != null, "Executor cannot be null.");
        Validate.validate(delayTicks >= 0, "Delay ticks cannot be negative.");
        Validate.validate(periodTicks >= 0, "Period ticks cannot be negative.");
        Validate.validate(batchSize > 0, "Batch size must be positive.");
        Validate.validate(originLocation != null, "Origin location cannot be null.");
        Validate.validate(originLocation.getWorld() != null, "Origin world cannot be null.");

        this.structureFile = structureFile;
        this.executor = executor;
        this.delayTicks = delayTicks;
        this.periodTicks = periodTicks;
        this.batchSize = batchSize;

        this.originLocation = originLocation.clone();
        this.originWorld = originLocation.getWorld();
        this.minPosition = structureFile.getRelative().clone().add(Position.fromLocation(this.originLocation, false));
        this.maxPosition = minPosition.clone().add(new Position(structureFile.getXSize()-1, structureFile.getYSize()-1, structureFile.getZSize()-1));
        this.positions = new LinkedList<>(structureFile.getBlockTraversalOrder().getPositions(minPosition, maxPosition));
    }

    public StructureLoader(HistoryFile historyFile, CommandSender executor, int delayTicks, int periodTicks, int batchSize) {
        this(historyFile, executor, delayTicks, periodTicks, batchSize, historyFile.getOriginLocation());
    }

    @Override
    public @NotNull CommandSender informer() {
        return executor;
    }

    @Setter
    private boolean silent = false;
    @Override
    public boolean isSilent() {
        return silent;
    }



    private StructurePasteTask pasteTask;

    @NotNull
    public StructurePasteTask getTask() {
        if (pasteTask == null) pasteTask = new StructurePasteTask(this);
        return pasteTask;
    }

    /**
     * Save current region as Structra to history folder.<br>
     * <b>NOTE:</b> You cannot use this method if paste task is running.
     * @param completeTask Runnable to run after completing history save process.
     * @since 1.0-SNAPSHOT
     */
    public void saveHistory(Runnable completeTask) {
        Validate.validate(pasteTask == null || !pasteTask.isRunning(), "You cannot save history while loader is running.");
        File file = new File(Structra.getHistoryFolder(), String.format("history_%s" + Structra.FILE_EXTENSION, new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())));
        StructureWriter writer = new StructureWriter(file, executor, minPosition, maxPosition, originLocation, delayTicks, periodTicks, batchSize, true);
        StructureWriteTask writerTask = writer.getTask();
        writerTask.setSilent(true);
        writerTask.execute(completeTask);
    }
}
