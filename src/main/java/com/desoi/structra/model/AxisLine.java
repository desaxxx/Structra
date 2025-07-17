package com.desoi.structra.model;

import lombok.Getter;
import org.bukkit.Axis;

@SuppressWarnings("unused")
public enum AxisLine {
    X(Axis.X),
    NEGATIVE_X(Axis.X),
    Y(Axis.Y),
    NEGATIVE_Y(Axis.Y),
    Z(Axis.Z),
    NEGATIVE_Z(Axis.Z),;

    @Getter
    private final Axis axis;
    AxisLine(Axis axis) {
        this.axis = axis;
    }

    public boolean equals(AxisLine other) {
        return this == other;
    }

    public boolean isSameAxis(AxisLine other) {
        return other != null && this.axis == other.axis;
    }

    public AxisLine opposite() {
        for(AxisLine other : AxisLine.values()) {
            if(isSameAxis(other) && !equals(other)) {
                return other;
            }
        }
        return null;
    }

    public boolean isOpposite(AxisLine other) {
        AxisLine opposite = opposite();
        return opposite != null && opposite == other;
    }

    public boolean isPositive() {
        return this == X || this == Y || this == Z;
    }

    public boolean isNegative() {
        return this == NEGATIVE_X || this == NEGATIVE_Y || this == NEGATIVE_Z;
    }

    public <T> T compareAxis(T xCase, T yCase, T zCase) {
        return getAxis() == Axis.X ? xCase : (getAxis() == Axis.Y ? yCase : zCase);
    }
}