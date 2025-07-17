package com.desoi.structra.model;

import com.desoi.structra.util.Util;
import com.desoi.structra.util.Validate;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public Position clone() {
        return Validate.validateException(() -> (Position) super.clone(), "Clone not supported for Position.");
    }


    // STATIC
    public static Position getMinimum(Position pos1, Position pos2) {
        Position position = pos1.clone();
        position.x = Math.min(pos1.x, pos2.x);
        position.y = Math.min(pos1.y, pos2.y);
        position.z = Math.min(pos1.z, pos2.z);
        return position;
    }
    public static Position getMaximum(Position pos1, Position pos2) {
        Position position = pos1.clone();
        position.x = Math.max(pos1.x, pos2.x);
        position.y = Math.max(pos1.y, pos2.y);
        position.z = Math.max(pos1.z, pos2.z);
        return position;
    }
    public static Position fromLocation(Location location, boolean includeWorld) {
        Validate.validate(location != null, "Location cannot be null to parse Position.");
        Position pos = new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if(includeWorld) pos.setWorldName(location.getWorld().getName());
        return pos;
    }
    public static Position fromLocation(Location location) {
        return fromLocation(location, false);
    }


    // TO
    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }
    @NotNull
    public Location toLocation() {
        return toLocation(getWorld());
    }



    @Nullable
    public World getWorld() {
        if(worldName == null) return null;
        return Bukkit.getWorld(worldName);
    }

    public Position add(Position other) {
        Validate.validate(other != null, "Position is null in addition.");
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Position subtract(Position other) {
        Validate.validate(other != null, "Position is null in subtraction.");
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public Position multiply(Position other) {
        Validate.validate(other != null, "Position is null in multiplication.");
        this.x *= other.x;
        this.y *= other.y;
        this.z *= other.z;
        return this;
    }

    public Position divide(Position other) {
        Validate.validate(other != null, "Position is null in division.");
        this.x /= other.x;
        this.y /= other.y;
        this.z /= other.z;
        return this;
    }

    public Position copy(Position other) {
        Validate.validate(other != null, "Position is null in copying.");
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.worldName = other.worldName;
        return this;
    }



    public Position setX(int i) {
        this.x = i;
        return this;
    }
    public Position setX(double d) {
        return setX((int) Math.floor(d));
    }
    public Position setX(float f) {
        return setX((int) Math.floor(f));
    }

    public Position setY(int i) {
        this.y = i;
        return this;
    }
    public Position setY(double d) {
        return setY((int) Math.floor(d));
    }
    public Position setY(float f) {
        return setY((int) Math.floor(f));
    }

    public Position setZ(int i) {
        this.z = i;
        return this;
    }
    public Position setZ(double d) {
        return setZ((int) Math.floor(d));
    }
    public Position setZ(float f) {
        return setZ((int) Math.floor(f));
    }

    public Position setWorldName(String str) {
        this.worldName = str;
        return this;
    }
}
