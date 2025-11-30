package com.desoi.structra.direction;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @since 1.1
 */
@Getter
public class Direction {
    public static final Direction P_X = new Direction((byte) 0); // 000
    public static final Direction N_X = new Direction((byte) 4); // 100
    public static final Direction P_Y = new Direction((byte) 1); // 001
    public static final Direction N_Y = new Direction((byte) 5); // 101
    public static final Direction P_Z = new Direction((byte) 2); // 010
    public static final Direction N_Z = new Direction((byte) 6); // 110


    private final byte encoded;
    private Direction(byte encoded) {
        this.encoded = (byte) (encoded & 7);
    }

    public boolean isX() {
        return (encoded & 3) == 0;
    }

    public boolean isY() {
        return (encoded & 3) == 1;
    }

    public boolean isZ() {
        return (encoded & 3) == 2;
    }

    public boolean isPositive() {
        return (encoded & 4) == 0;
    }

    public boolean isNegative() {
        return (encoded & 4) != 0;
    }

    public Direction getOpposite() {
        return new Direction((byte) (encoded ^ 4));
    }

    public boolean isSameAxis(Direction other) {
        if(other == null) return false;
        return (encoded & 3) == (other.encoded & 3);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Direction direction = (Direction) o;
        return encoded == direction.encoded;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(encoded);
    }

    @Override
    public String toString() {
        return String.format("Direction[%d]", encoded);
    }

    @NotNull
    public static Direction of(byte encoded) {
        return new Direction(encoded);
    }
}
