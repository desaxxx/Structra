package com.desoi.structra.direction;

import com.desoi.structra.util.Validate;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @see Direction
 * @since 1.1
 */
@Getter
public class Direction3D {

    /*
     * fff -> first direction encoded
     * sss -> second direction encoded
     * ttt -> third direction encoded
     * Direction3D encoded: 0b0000 0fff 0sss 0ttt
     */
    private final int encoded;

    private Direction3D(int encoded) {
        this.encoded = (byte) (encoded & 4095);
    }

    /**
     * @return First direction
     * @since 1.1
     */
    public Direction first() {
        return Direction.of((byte) ((encoded >> 8) & 7));
    }

    /**
     * @return Second direction
     * @since 1.1
     */
    public Direction second() {
        return Direction.of((byte) ((encoded >> 4) & 7));
    }

    /**
     * @return Third direction
     * @since 1.1
     */
    public Direction third() {
        return Direction.of((byte) (encoded & 7));
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Direction3D that = (Direction3D) o;
        return encoded == that.encoded;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(encoded);
    }

    @NotNull
    public static Direction3D of(@NotNull Direction first, @NotNull Direction second, @NotNull Direction third) {
        Validate.notNull(first, "First direction cannot be null.");
        Validate.notNull(second, "Second direction cannot be null.");
        Validate.notNull(third, "Third direction cannot be null.");
        Validate.validate(!first.isSameAxis(second) && !first.isSameAxis(third) && !second.isSameAxis(third), "Directions cannot be on same axis.");

        byte value = 0;
        value |= (byte) (first.getEncoded() << 8);
        value |= (byte) (second.getEncoded() << 4);
        value |= third.getEncoded();
        return new Direction3D(value);
    }
}
