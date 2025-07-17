package com.desoi.structra.loader;

import com.desoi.structra.util.Validate;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StructureLoader {

    private final @NotNull StructureFile structureFile;

    private final @NotNull CommandSender executor;
    private final int delayTicks;
    private final int periodTicks;
    private final int batchSize;

    private final @NotNull Location originBlockLocation;
    private final @NotNull World originWorld;
    private final @NotNull Vector minVector;
    private final @NotNull Vector maxVector;

    private final @NotNull List<Vector> vectors;
    @Setter
    private long startNanoTime;

    public StructureLoader(StructureFile structureFile, CommandSender executor, int delayTicks, int periodTicks, int batchSize, Location originLocation) {
        Validate.validate(structureFile != null, "StructureFile cannot be null");
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

        this.originBlockLocation = originLocation.getBlock().getLocation().clone();
        this.originWorld = originLocation.getWorld();
        this.minVector = structureFile.getRelative().clone().add(originBlockLocation.toVector());
        this.maxVector = minVector.clone().add(new Vector(structureFile.getXSize(), structureFile.getYSize(), structureFile.getZSize()));
        this.vectors = new ArrayList<>(structureFile.getBlockTraversalOrder().getVectors(minVector, maxVector));
    }

    @NotNull
    public StructurePasteTask execute() {
        StructurePasteTask pasteTask = new StructurePasteTask(this);
        pasteTask.execute();
        return pasteTask;
    }
}
