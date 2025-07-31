package com.desoi.structra.model;

import org.jetbrains.annotations.NotNull;

public interface Positioned {

    @NotNull Position minPosition();
    @NotNull Position maxPosition();
}
