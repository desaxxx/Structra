package com.desoi.structra.model;

import com.desoi.structra.util.Validate;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BlockTraversalOrder {

    static public final BlockTraversalOrder DEFAULT = new BlockTraversalOrder();

    // TODO options.
    public BlockTraversalOrder() {
    }


    public List<Vector> getVectors(Vector minVector, Vector maxVector) {
        Validate.validate(minVector != null, "Minimum vector cannot be null.");
        Validate.validate(maxVector != null, "Maximum vector cannot be null.");

        int xSize = maxVector.getBlockX() - minVector.getBlockX();
        int ySize = maxVector.getBlockY() - minVector.getBlockY();
        int zSize = maxVector.getBlockZ() - minVector.getBlockZ();

        List<Vector> vectors = new ArrayList<>(xSize * ySize * zSize);
        for(int z = 0; z < zSize; z++) {
            for(int y = 0; y < ySize; y++) {
                for(int x = 0; x < xSize; x++) {
                    Vector vec = minVector.clone().add(new Vector(x, y, z));
                    vectors.add(vec);
                }
            }
        }
        return vectors;
    }
}
