package com.desoi.structra.model;

import com.desoi.structra.util.Validate;

import java.util.LinkedHashSet;

public class BlockTraversalOrder {

    static public final BlockTraversalOrder DEFAULT = new BlockTraversalOrder();

    // TODO options.
    public BlockTraversalOrder() {
    }


    public LinkedHashSet<Position> getPositions(Position minPosition, Position maxPosition) {
        Validate.validate(minPosition != null && maxPosition != null, "Positions cannot be null!");
        return Position.getPositions(minPosition, maxPosition);
    }
}
