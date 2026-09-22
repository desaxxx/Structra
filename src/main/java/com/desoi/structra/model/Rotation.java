package com.desoi.structra.model;

public enum Rotation {
    NONE, CW_90, CW_180, CW_270;

    // Rotated 90° around y-axis
    public Rotation rotated() {
        return values()[(ordinal() + 1) % 4];
    }
}
