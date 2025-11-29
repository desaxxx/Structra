package com.desoi.structra.model;

import com.desoi.structra.util.Util;
import com.desoi.structra.util.Validate;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Objects;

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
        int x = Math.min(pos1.x, pos2.x);
        int y = Math.min(pos1.y, pos2.y);
        int z = Math.min(pos1.z, pos2.z);
        return new Position(x, y, z, pos1.worldName);
    }

    /**
     * Get the maximum Position between given positions.
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @since 1.0-SNAPSHOT
     */
    public static Position getMaximum(Position pos1, Position pos2) {
        int x = Math.max(pos1.x, pos2.x);
        int y = Math.max(pos1.y, pos2.y);
        int z = Math.max(pos1.z, pos2.z);
        return new Position(x, y, z, pos1.worldName);
    }

    /**
     * @param location Bukkit Location
     * @param includeWorld whether include world or not.
     * @since 1.0-SNAPSHOT
     */
    public static Position fromLocation(Location location, boolean includeWorld) {
        Validate.notNull(location, "Location cannot be null to parse Position.");
        Position pos = new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if(includeWorld) pos.setWorldName(location.getWorld().getName());
        return pos;
    }

    /**
     * Get positions between given positions.
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @return LinkedHashSet of Positions
     * @since 1.0-SNAPSHOT
     */
    public static LinkedHashSet<Position> getPositions(Position pos1, Position pos2) {
        Validate.notNull(pos1, pos2, "Positions cannot be null.");
        Position minPosition = getMinimum(pos1, pos2);
        Position maxPosition = getMaximum(pos1, pos2);

        int width = maxPosition.width(minPosition);
        int height = maxPosition.height(minPosition);
        int length = maxPosition.length(minPosition);

        LinkedHashSet<Position> positions = new LinkedHashSet<>();
        for(int z = 0; z < length + 1; z++) {
            for(int y = 0; y < height + 1; y++) {
                for(int x = 0; x < width + 1; x++) {
                    Position pos = minPosition.clone().add(new Position(x, y, z));
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    @NotNull
    public String stringify() {
        String wn = worldName == null ? "null" : worldName;
        return "[" + x + "," + y + "," + z + "," + wn + "]";
    }

    /**
     * Check if the Position equals to given Object.
     * @param o Object
     * @return whether equals or not
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y && z == position.z && Objects.equals(worldName, position.worldName);
    }

    /**
     * Hash code.
     * @return int
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, worldName);
    }

    /**
     * Check whether the Position is between given positions.
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @return whether between or not.
     * @since 1.0.0
     */
    public boolean isBetween(Position pos1, Position pos2) {
        Validate.notNull(pos1, pos2, "Positions cannot be null.");
        Position min = getMinimum(pos1, pos2);
        Position max = getMaximum(pos1, pos2);
        return isBetween(min.x, max.x, x) && isBetween(min.y, max.y, y) && isBetween(min.z, max.z, z);
    }

    /**
     * @since 1.0.0
     */
    private boolean isBetween(int min, int max, int i) {
        return i >= min && i <= max;
    }

    /**
     * Check whether {@link #worldName} matches with given Positions's {@link #worldName}.
     * @param other Position
     * @return whether matches or not
     * @since 1.0.0
     */
    public boolean worldsMatch(Position other) {
        Validate.notNull(other, "Position cannot be null.");
        return (worldName == null && other.worldName == null) || Objects.equals(worldName, other.worldName);
    }


    /**
     * Convert to {@link Location} with an external {@link World}.
     * @param world World
     * @return Location
     * @since 1.0-SNAPSHOT
     */
    public Location toLocation(World world) {
        return new Location(world, x + 0.5, y + 0.5, z + 0.5);
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
     * Convert to {@link Vector}.
     * @return Vector
     * @since 1.0.0
     */
    @NotNull
    public Vector toVector() {
        return new Vector(x,y,z);
    }


    /**
     * Calculate the x size between positions.
     * @param other Other Position
     * @return X size
     * @since 1.0-SNAPSHOT
     */
    public int sizeX(Position other) {
        Validate.notNull(other, "Position cannot be null.");
        return Math.abs(this.x - other.x);
    }

    /**
     * Calculate the width between positions.
     * @param other Other Position
     * @return Width
     * @since 1.0-SNAPSHOT
     */
    public int width(Position other) {
        return sizeX(other);
    }

    /**
     * Calculate the y size between positions.
     * @param other Other Position
     * @return Y size
     * @since 1.0-SNAPSHOT
     */
    public int sizeY(Position other) {
        Validate.notNull(other, "Position cannot be null.");
        return Math.abs(this.y - other.y);
    }

    /**
     * Calculate the height between positions.
     * @param other Other Position
     * @return Height
     * @since 1.0-SNAPSHOT
     */
    public int height(Position other) {
        return sizeY(other);
    }

    /**
     * Calculate the z size between positions.
     * @param other Other Position
     * @return Z size
     * @since 1.0-SNAPSHOT
     */
    public int sizeZ(Position other) {
        Validate.notNull(other, "Position cannot be null.");
        return Math.abs(this.z - other.z);
    }

    /**
     * Calculate the length between positions.
     * @param other Other Position
     * @return Length
     * @since 1.0-SNAPSHOT
     */
    public int length(Position other) {
        return sizeZ(other);
    }

    /**
     * Calculate the size between the positions.
     * @param other Other Position
     * @return Size
     * @since 1.0-SNAPSHOT
     */
    public int size(Position other) {
        return sizeX(other) * sizeY(other) * sizeZ(other);
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
        Validate.notNull(other, "Position is null in addition.");
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
        Validate.notNull(other, "Position is null in subtraction.");
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
        Validate.notNull(other, "Position is null in multiplication.");
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
        Validate.notNull(other, "Position is null in division.");
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
        Validate.notNull(other, "Position is null in copying.");
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
