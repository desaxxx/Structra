package com.desoi.structra.model;

import com.desoi.structra.util.Util;
import com.desoi.structra.util.Validate;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 1.0-SNAPSHOT
 */
@Getter
public class Position implements Cloneable {

    private int x;
    private int y;
    private int z;
    private String worldName;

    public Position(int x, int y, int z, String worldName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    // WORLD CONSTRUCTORS
    public Position(double x, double y, double z, String worldName) {
        this((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z), worldName);
    }

    public Position(float x, float y, float z, String worldName) {
        this((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z), worldName);
    }

    public Position(String x, String y, String z, String worldName) {
        this(Util.parseInt(x, 0), Util.parseInt(y,0), Util.parseInt(z, 0), worldName);
    }


    // NO WORLD CONSTRUCTORS
    public Position(int x, int y, int z) {
        this(x,y,z,null);
    }

    public Position(double x, double y, double z) {
        this((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    public Position(float x, float y, float z) {
        this((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    public Position(String x, String y, String z) {
        this(Util.parseInt(x, 0), Util.parseInt(y,0), Util.parseInt(z, 0));
    }


    // CLONE
    /**
     * Get the clone of the Position.
     * @return new Position
     * @since 1.0-SNAPSHOT
     */
    public Position clone() {
        return Validate.validateException(() -> (Position) super.clone(), "Clone not supported for Position.");
    }


    // STATIC

    /**
     * Get the minimum Position between given positions.
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @return Minimum Position
     * @since 1.0-SNAPSHOT
     */
    public static Position getMinimum(Position pos1, Position pos2) {
        Position position = pos1.clone();
        position.x = Math.min(pos1.x, pos2.x);
        position.y = Math.min(pos1.y, pos2.y);
        position.z = Math.min(pos1.z, pos2.z);
        return position;
    }

    /**
     * Get the maximum Position between given positions.
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @since 1.0-SNAPSHOT
     */
    public static Position getMaximum(Position pos1, Position pos2) {
        Position position = pos1.clone();
        position.x = Math.max(pos1.x, pos2.x);
        position.y = Math.max(pos1.y, pos2.y);
        position.z = Math.max(pos1.z, pos2.z);
        return position;
    }

    /**
     * @param location Bukkit Location
     * @param includeWorld whether include world or not.
     * @since 1.0-SNAPSHOT
     */
    public static Position fromLocation(Location location, boolean includeWorld) {
        Validate.validate(location != null, "Location cannot be null to parse Position.");
        Position pos = new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if(includeWorld) pos.setWorldName(location.getWorld().getName());
        return pos;
    }


    /**
     * Convert to {@link Location} with an external {@link World}.
     * @param world World
     * @return Location
     * @since 1.0-SNAPSHOT
     */
    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    /**
     * Convert to {@link Location} with internal {@link #worldName}.
     * @return Location
     * @since 1.0-SNAPSHOT
     */
    @NotNull
    public Location toLocation() {
        return toLocation(getWorld());
    }


    /**
     * Get the Position world.<br>
     * <b>NOTE:</b> Position world may be null if not specified.
     * @return {@link World} if found, else {@code null}.
     * @since 1.0-SNAPSHOT
     */
    @Nullable
    public World getWorld() {
        if(worldName == null) return null;
        return Bukkit.getWorld(worldName);
    }

    /**
     * Add another Position values to the Position.
     * @param other Other Position
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position add(Position other) {
        Validate.validate(other != null, "Position is null in addition.");
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    /**
     * Remove another Position values to the Position.
     * @param other Other Position
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position subtract(Position other) {
        Validate.validate(other != null, "Position is null in subtraction.");
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    /**
     * Multiply another Position values with the Position.
     * @param other Other Position
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position multiply(Position other) {
        Validate.validate(other != null, "Position is null in multiplication.");
        this.x *= other.x;
        this.y *= other.y;
        this.z *= other.z;
        return this;
    }

    /**
     * Divide another Position values by the Position.
     * @param other Other Position
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position divide(Position other) {
        Validate.validate(other != null, "Position is null in division.");
        this.x /= other.x;
        this.y /= other.y;
        this.z /= other.z;
        return this;
    }

    /**
     * Copy another Position values to the Position.
     * @param other Other Position
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position copy(Position other) {
        Validate.validate(other != null, "Position is null in copying.");
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.worldName = other.worldName;
        return this;
    }


    /**
     * Set {@link #x} of the Position.
     * @param i value
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position setX(int i) {
        this.x = i;
        return this;
    }

    /**
     * Set {@link #x} of the Position.
     * @param d value
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position setX(double d) {
        return setX((int) Math.floor(d));
    }

    /**
     * Set {@link #x} of the Position.
     * @param f value
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position setX(float f) {
        return setX((int) Math.floor(f));
    }

    /**
     * Set {@link #y} of the Position.
     * @param i value
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position setY(int i) {
        this.y = i;
        return this;
    }

    /**
     * Set {@link #y} of the Position.
     * @param d value
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position setY(double d) {
        return setY((int) Math.floor(d));
    }

    /**
     * Set {@link #y} of the Position.
     * @param f value
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position setY(float f) {
        return setY((int) Math.floor(f));
    }


    /**
     * Set {@link #z} of the Position.
     * @param i value
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position setZ(int i) {
        this.z = i;
        return this;
    }

    /**
     * Set {@link #z} of the Position.
     * @param d value
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position setZ(double d) {
        return setZ((int) Math.floor(d));
    }

    /**
     * Set {@link #z} of the Position.
     * @param f value
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position setZ(float f) {
        return setZ((int) Math.floor(f));
    }

    /**
     * Set {@link #worldName} of the Position.
     * @param worldName World name
     * @return Current Position
     * @since 1.0-SNAPSHOT
     */
    public Position setWorldName(String worldName) {
        this.worldName = worldName;
        return this;
    }
}
