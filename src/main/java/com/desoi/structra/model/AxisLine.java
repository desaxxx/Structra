package com.desoi.structra.model;

import com.google.common.base.Preconditions;
import org.bukkit.Axis;

@Deprecated(since = "2.0-beta1")
public enum AxisLine {
    POSITIVE_X(Axis.X),
    NEGATIVE_X(Axis.X),
    POSITIVE_Y(Axis.Y),
    NEGATIVE_Y(Axis.Y),
    POSITIVE_Z(Axis.Z),
    NEGATIVE_Z(Axis.Z),;

    private final Axis axis;
    AxisLine(Axis axis) {
        this.axis = axis;
    }

    public boolean isSameAxis(AxisLine other) {
        Preconditions.checkNotNull(other, "other");
        return axis == other.axis;
    }

    public AxisLine opposite() {
        for(AxisLine other : values()) {
            if(axis == other.axis && this != other) {
                return other;
            }
        }

        return null;
    }

    public boolean isOpposite(AxisLine other) {
        Preconditions.checkNotNull(other, "other");
        return axis == other.axis && this != other;
    }

    public boolean isPositive() {
        return this == POSITIVE_X || this == POSITIVE_Y || this == POSITIVE_Z;
    }

    public boolean isNegative() {
        return this == NEGATIVE_X || this == NEGATIVE_Y || this == NEGATIVE_Z;
    }

    public <T> T compareAxis(T xCase, T yCase, T zCase) {
        return axis == Axis.X
                ? xCase
                : (axis == Axis.Y ? yCase : zCase);
    }

    public Axis getAxis() {
        return axis;
    }
}