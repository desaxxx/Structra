package com.desoi.structra.loader;

import com.desoi.structra.model.Position;
import com.desoi.structra.util.Validate;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
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
    private final @NotNull Position minPosition;
    private final @NotNull Position maxPosition;

    private final @NotNull List<Position> positions;
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
        this.minPosition = structureFile.getRelative().clone().add(Position.fromLocation(originBlockLocation));
        this.maxPosition = minPosition.clone().add(new Position(structureFile.getXSize(), structureFile.getYSize(), structureFile.getZSize()));
        this.positions = new ArrayList<>(structureFile.getBlockTraversalOrder().getPositions(minPosition, maxPosition));
    }

    @NotNull
    public StructurePasteTask execute() {
        StructurePasteTask pasteTask = new StructurePasteTask(this);
        pasteTask.execute();
        return pasteTask;
    }
}
