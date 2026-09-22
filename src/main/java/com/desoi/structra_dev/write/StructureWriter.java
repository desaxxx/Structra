package com.desoi.structra_dev.write;

import com.desoi.structra_dev.file.StructureFileBuilder;
import com.desoi.structra_dev.options.StructureOptions;
import com.desoi.structra_dev.options.StructureOptionsView;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.jspecify.annotations.NullMarked;

import java.io.File;

@NullMarked
public class StructureWriter {
    public static final String VERSION = "2.0";

    private final CommandSender executor;
    private final Vector3i position1;
    private final Vector3i position2;
    private final Location originLocation;
    private final World originWorld;
    private final StructureOptionsView options;

    public StructureWriter(CommandSender executor,
                           Vector3i position1,
                           Vector3i position2,
                           Location originLocation,
                           StructureOptions options) {
        this.executor = Preconditions.checkNotNull(executor, "executor");
        this.position1 = new Vector3i(Preconditions.checkNotNull(position1, "position1"));
        this.position2 = new Vector3i(Preconditions.checkNotNull(position2, "position2"));
        this.originLocation = Preconditions.checkNotNull(originLocation, "originLocation");
        this.originWorld = Preconditions.checkNotNull(
                originLocation.getWorld(),
                "originLocation cannot have null world"
        );
        this.options = new StructureOptions(Preconditions.checkNotNull(options, "options"));
    }

    public StructureWriteTask createTask(File file) {
        return new StructureWriteTask(this, Preconditions.checkNotNull(file, "file"));
    }

    public CommandSender getExecutor() {
        return executor;
    }

    public Vector3ic getPosition1() {
        return position1;
    }

    public Vector3ic getPosition2() {
        return position2;
    }

    public Location getOriginLocation() {
        return originLocation;
    }

    public World getOriginWorld() {
        return originWorld;
    }

    public StructureOptionsView getOptions() {
        return options;
    }
}
