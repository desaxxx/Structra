package com.desoi.structra_dev.util;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Vector3ic;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class VectorUtil {

    public static Location toBukkitLocation(Vector3ic vector, @Nullable World world) {
        Preconditions.checkNotNull(vector, "vector");
        return new Location(world, vector.x(), vector.y(), vector.z());
    }

    public static String toPositionKey(Vector3ic vector) {
        Preconditions.checkNotNull(vector, "vector");
        return vector.x() + "," + vector.y() + "," + vector.z();
    }
}
