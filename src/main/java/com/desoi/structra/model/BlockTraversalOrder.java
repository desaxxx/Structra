package com.desoi.structra.model;

import com.desoi.structra.util.Validate;

import java.util.ArrayList;
import java.util.List;

public class BlockTraversalOrder {

    static public final BlockTraversalOrder DEFAULT = new BlockTraversalOrder();

    // TODO options.
    public BlockTraversalOrder() {
    }


    public List<Position> getPositions(Position minPosition, Position maxPosition) {
        Validate.validate(minPosition != null, "Minimum position cannot be null.");
        Validate.validate(maxPosition != null, "Maximum position cannot be null.");

        int xSize = maxPosition.getX() - minPosition.getX();
        int ySize = maxPosition.getY() - minPosition.getY();
        int zSize = maxPosition.getZ() - minPosition.getZ();

        List<Position> positions = new ArrayList<>(xSize * ySize * zSize);
        for(int z = 0; z < zSize; z++) {
            for(int y = 0; y < ySize; y++) {
                for(int x = 0; x < xSize; x++) {
                    Position pos = minPosition.clone().add(new Position(x, y, z));
                    positions.add(pos);
                }
            }
        }
        return positions;
    }
}
