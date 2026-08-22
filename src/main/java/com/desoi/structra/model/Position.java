package com.desoi.structra.model;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a block position in a Minecraft world.
 *
 * @since 1.0-SNAPSHOT
 */
public class Position {

    private int x;
    private int y;
    private int z;
    private String worldName;

    public Position(double x, double y, double z) {
        this(NumberConversions.floor(x), NumberConversions.floor(y), NumberConversions.floor(z), null);
    }
    public Position(int x, int y, int z) {
        this(x, y, z, null);
    }
    public Position(double x, double y, double z, String worldName) {
        this(NumberConversions.floor(x), NumberConversions.floor(y), NumberConversions.floor(z), worldName);
    }
    public Position(int x, int y, int z, String worldName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    /**
     * Get the minimum Position between given positions.
     *
     * @param pos1 position 1
     * @param pos2 position 2
     * @return the position with minimum values
     * @since 1.0-SNAPSHOT
     */
    public static @NotNull Position getMinimum(Position pos1, Position pos2) {
        int x = Math.min(pos1.x, pos2.x);
        int y = Math.min(pos1.y, pos2.y);
        int z = Math.min(pos1.z, pos2.z);
        return new Position(x, y, z);
    }

    /**
     * Get the maximum Position between given positions.
     *
     * @param pos1 position 1
     * @param pos2 position 2
     * @return the position with maximum values
     * @since 1.0-SNAPSHOT
     */
    public static @NotNull Position getMaximum(Position pos1, Position pos2) {
        int x = Math.max(pos1.x, pos2.x);
        int y = Math.max(pos1.y, pos2.y);
        int z = Math.max(pos1.z, pos2.z);
        return new Position(x, y, z);
    }

    /**
     * Get a Position instance from the given location.
     *
     * @param location the location
     * @param includeWorld whether include the world
     * @return the position result
     * @since 1.0-SNAPSHOT
     */
    public static @NotNull Position fromLocation(Location location, boolean includeWorld) {
        Preconditions.checkNotNull(location, "location");
        Position pos = new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        if(includeWorld && location.getWorld() != null) {
            pos.setWorldName(location.getWorld().getName());
        }

        return pos;
    }

    /**
     * Get positions between two positions.
     *
     * @param pos1 position 1
     * @param pos2 position 2
     * @return the position list
     * @since 1.0-SNAPSHOT
     */
    public static @NotNull List<Position> getPositions(Position pos1, Position pos2) {
        Preconditions.checkNotNull(pos1, "pos1");
        Preconditions.checkNotNull(pos2, "pos2");
        Position minPosition = getMinimum(pos1, pos2);
        Position maxPosition = getMaximum(pos1, pos2);

        int width = maxPosition.width(minPosition);
        int height = maxPosition.height(minPosition);
        int length = maxPosition.length(minPosition);

        List<Position> positions = new ArrayList<>();
        for(int z = 0; z < length; z++) {
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    Position pos = minPosition.copy().add(new Position(x, y, z));
                    positions.add(pos);
                }
            }
        }

        return positions;
    }

    /**
     * Get the Position world.
     *
     * @return the world if set and found, otherwise null
     * @since 1.0-SNAPSHOT
     */
    public @Nullable World getWorld() {
        if(worldName == null) {
            return null;
        }

        return Bukkit.getWorld(worldName);
    }


    /**
     * Get the clone of the Position.
     * @return new Position
     * @since 1.0-SNAPSHOT
     */
    public @NotNull Position copy() {
        return new Position(x, y, z, worldName);
    }


    /**
     * Return the string representation of the position, including the world name.
     *
     * @return the stringified position
     * @since 1.0-SNAPSHOT
     */
    public @NotNull String stringify() {
        String wn = worldName == null ? "null" : worldName;
        return "[" + x + "," + y + "," + z + "," + wn + "]";
    }

    /**
     * Return position string separated by comma.
     * @since 1.1
     */
    public @NotNull String separatedByComma() {
        return x + "," + y + "," + z;
    }

    /**
     * Check if the Position is between the given Positions.
     *
     * @param pos1 position 1
     * @param pos2 position 2
     * @return whether it is between the given positions or not
     * @since 1.0.0
     */
    public boolean isBetween(Position pos1, Position pos2) {
        Preconditions.checkNotNull(pos1, "pos1");
        Preconditions.checkNotNull(pos2, "pos2");

        Position min = getMinimum(pos1, pos2);
        Position max = getMaximum(pos1, pos2);

        return (x >= min.x && x <= max.x) &&
                (y >= min.y && y <= max.y) &&
                (z >= min.z && z <= max.z);
    }

    /**
     * Check if the worlds match with the given Position.
     *
     * @param other the other position
     * @return whether the worlds match or not
     * @since 1.0.0
     */
    public boolean worldsMatch(Position other) {
        Preconditions.checkNotNull(other, "other");

        return Objects.equals(worldName, other.worldName);
    }

    /**
     * Convert to a {@link Location}.
     *
     * @return the result location
     * @since 1.0-SNAPSHOT
     */
    public @NotNull Location toLocation() {
        return toLocation(getWorld());
    }

    /**
     * Convert to a {@link Location} with an external {@link World}.
     *
     * @param world the external world
     * @return the result location
     * @since 1.0-SNAPSHOT
     */
    public @NotNull Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    /**
     * Convert to a {@link Vector}.
     *
     * @return the result vector
     * @since 1.0.0
     */
    public @NotNull Vector toVector() {
        return new Vector(x, y, z);
    }


    /**
     * Calculate the x size (block count) between positions.
     *
     * @param other the other position
     * @return the x size
     * @since 1.0-SNAPSHOT
     */
    public int sizeX(Position other) {
        Preconditions.checkNotNull(other, "other");
        return Math.abs(this.x - other.x) + 1;
    }

    /**
     * Calculate the width (block count in x-axis) between positions.
     *
     * @param other the other position
     * @return the width
     * @since 1.0-SNAPSHOT
     * @see #sizeX(Position)
     */
    public int width(Position other) {
        return sizeX(other);
    }

    /**
     * Calculate the y size (block count) between positions.
     *
     * @param other the other position
     * @return the y size
     * @since 1.0-SNAPSHOT
     */
    public int sizeY(Position other) {
        Preconditions.checkNotNull(other, "other");
        return Math.abs(this.y - other.y) + 1;
    }

    /**
     * Calculate the height (block count in y-axis) between positions.
     *
     * @param other the other position
     * @return the height
     * @since 1.0-SNAPSHOT
     * @see #sizeY(Position)
     */
    public int height(Position other) {
        return sizeY(other);
    }

    /**
     * Calculate the z size (block count) between positions.
     *
     * @param other the other position
     * @return the z size
     * @since 1.0-SNAPSHOT
     */
    public int sizeZ(Position other) {
        Preconditions.checkNotNull(other, "other");
        return Math.abs(this.z - other.z) + 1;
    }

    /**
     * Calculate the length (block count in z axis) between positions.
     *
     * @param other the other position
     * @return the length
     * @since 1.0-SNAPSHOT
     * @see #sizeZ(Position)
     */
    public int length(Position other) {
        return sizeZ(other);
    }

    /**
     * Calculate the total block count (volume) between the positions.
     *
     * @param other the other position
     * @return the volume size
     * @since 1.0-SNAPSHOT
     */
    public int size(Position other) {
        return sizeX(other) * sizeY(other) * sizeZ(other);
    }


    /**
     * Add other Position values to the Position.
     *
     * @param other the other position
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position add(Position other) {
        Preconditions.checkNotNull(other, "other");
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    /**
     * Remove other Position values from the Position.
     *
     * @param other the other position
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position subtract(Position other) {
        Preconditions.checkNotNull(other, "other");
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    /**
     * Multiply with other Position values.
     * @param other the other position
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position multiply(Position other) {
        Preconditions.checkNotNull(other, "other");
        this.x *= other.x;
        this.y *= other.y;
        this.z *= other.z;
        return this;
    }

    /**
     * Divide with Position values.
     * @param other the other position
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position divide(Position other) {
        Preconditions.checkNotNull(other, "other");
        Preconditions.checkArgument(other.x != 0 && other.y != 0 && other.z != 0, "Cannot divide by zero");
        this.x /= other.x;
        this.y /= other.y;
        this.z /= other.z;
        return this;
    }

    /**
     * Copy other Position values to the Position.
     *
     * @param other the other position
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position copy(Position other) {
        Preconditions.checkNotNull(other, "other");
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.worldName = other.worldName;
        return this;
    }


    /**
     * Set x of the Position.
     *
     * @param x the x value
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position setX(int x) {
        this.x = x;
        return this;
    }

    /**
     * Set x of the Position from a double, flooring it.
     *
     * @param x the x value
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position setX(double x) {
        return setX(NumberConversions.floor(x));
    }

    /**
     * Set y of the Position.
     *
     * @param y the y value
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position setY(int y) {
        this.y = y;
        return this;
    }

    /**
     * Set y of the Position from a double, flooring it.
     *
     * @param y the y value
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position setY(double y) {
        return setY(NumberConversions.floor(y));
    }

    /**
     * Set z of the Position.
     *
     * @param z the z value
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position setZ(int z) {
        this.z = z;
        return this;
    }

    /**
     * Set z of the Position from a double, flooring it.
     *
     * @param z the z value
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position setZ(double z) {
        return setZ(NumberConversions.floor(z));
    }

    /**
     * Set the world name of the Position.
     *
     * @param worldName the world name
     * @return the current position
     * @since 1.0-SNAPSHOT
     */
    public Position setWorldName(String worldName) {
        this.worldName = worldName;
        return this;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getWorldName() {
        return worldName;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if (!(o instanceof Position position)) {
            return false;
        }

        return x == position.x &&
                y == position.y &&
                z == position.z &&
                Objects.equals(worldName, position.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, worldName);
    }
}
