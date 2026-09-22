package com.desoi.structra.model;

import com.desoi.structra.direction.Direction;
import com.desoi.structra.direction.Direction3D;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.base.Preconditions;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NullMarked
public class BlockTraversalOrder {
    // DO NOT CHANGE AS THIS IS THE ONLY ORDER USED IN StructraWriter.
    public static final BlockTraversalOrder DEFAULT = create(Direction.P_X, Direction.P_Y, Direction.P_Z);


    private final Direction3D direction3D;
    /**
     * @since 1.1
     */
    private BlockTraversalOrder(Direction3D direction3D) {
        this.direction3D = Preconditions.checkNotNull(direction3D, "direction3D");
    }

    public static BlockTraversalOrder create(Direction first, Direction second, Direction third) {
        return new BlockTraversalOrder(Direction3D.of(first, second, third));
    }
    public static BlockTraversalOrder create(Direction3D directions) {
        return new BlockTraversalOrder(directions);
    }

    /**
     * @return Direction3D
     * @since 1.1
     */
    public Direction3D getDirection3D() {
        return direction3D;
    }

    public List<Vector3i> getPositions(Vector3ic pos1, Vector3ic pos2) {
        Preconditions.checkNotNull(pos1, "pos1");
        Preconditions.checkNotNull(pos2, "pos2");

        Vector3i minPosition = pos1.min(pos2, new Vector3i());
        Vector3i maxPosition = pos1.max(pos2, new Vector3i());

        int xLen = 1 + maxPosition.x() - minPosition.x();
        int yLen = 1 + maxPosition.y() - minPosition.y();
        int zLen = 1 + maxPosition.z() - minPosition.z();

        Direction first = direction3D.first();
        Direction second = direction3D.second();
        Direction third = direction3D.third();

        int firstSize = getDistance(first, xLen, yLen, zLen);
        int secondSize = getDistance(second, xLen, yLen, zLen);
        int thirdSize = getDistance(third, xLen, yLen, zLen);

        List<Vector3i> positions = new ArrayList<>();

        for(int k = 0; k <= thirdSize; k++) {
            for(int j = 0; j <= secondSize; j++) {
                for(int i = 0; i <= firstSize; i++) {

                    Vector3i offset = new Vector3i()
                            .add(applyAxis(first, i, minPosition, maxPosition))
                            .add(applyAxis(second, j, minPosition, maxPosition))
                            .add(applyAxis(third, k, minPosition, maxPosition));

                    positions.addLast(offset);
                }
            }
        }

        return positions;
    }

    /**
     * Get Positions between specified positions unique to this order.
     *
     * @param pos1 the position 1
     * @param pos2 the position 2
     * @return the list of positions
     * @since 1.0-SNAPSHOT
     * @deprecated Use {@link #getPositions(Vector3ic, Vector3ic)} instead.
     */
    @Deprecated(since = "2.0-beta3")
    public List<Position> getPositions(Position pos1, Position pos2) {
        List<Vector3i> positions = getPositions(pos1.toVector3i(), pos2.toVector3i());

        List<Position> translated = new ArrayList<>(positions.size());

        for(Vector3i vector : positions) {
            translated.add(new Position(vector.x(), vector.y(), vector.z()));
        }

        return translated;
    }

    /**
     * Get distance of based on direction.
     *
     * @param direction Direction
     * @param width Width
     * @param height Height
     * @param length Length
     * @return if direction is on axis X, returns {@code width},
     *         if direction is on axis Y, returns {@code height},
     *         if direction is on axis Z, returns {@code length}.
     *         else {@code 0}.
     * @since 1.1
     */
    private int getDistance(Direction direction, int width, int height, int length) {
        if (direction.isX()) return width;
        if (direction.isY()) return height;
        if (direction.isZ()) return length;
        return 0;
    }

    /**
     * Calculate a position with specified direction, step size, min and max positions.
     *
     * @param direction Direction
     * @param step Step size
     * @param min Minimum position
     * @param max Maximum position
     * @return new Position similar to a vector
     * @since 1.1
     */
    private Vector3ic applyAxis(Direction direction, int step, Vector3ic min, Vector3ic max) {
        if (direction.isX()) {
            int x = direction.isPositive() ? min.x() + step : max.x() - step;
            return new Vector3i(x, 0, 0);
        }
        if (direction.isY()) {
            int y = direction.isPositive() ? min.y() + step : max.y() - step;
            return new Vector3i(0, y, 0);
        }
        if (direction.isZ()) {
            int z = direction.isPositive() ? min.z() + step : max.z() - step;
            return new Vector3i(0, 0, z);
        }
        return new Vector3i();
    }

    public ArrayNode reorderBlockData(ArrayNode blockData,
                                      Vector3ic pos1,
                                      Vector3ic pos2,
                                      BlockTraversalOrder sourceOrder) {
        Preconditions.checkNotNull(blockData, "blockData");
        Preconditions.checkNotNull(pos1, "pos1");
        Preconditions.checkNotNull(pos2, "pos2");
        Preconditions.checkNotNull(sourceOrder, "sourceOrder");

        if (sourceOrder.equals(this)) {
            return blockData;
        }

        Vector3i minPosition = pos1.min(pos2, new Vector3i());
        Vector3i maxPosition = pos1.max(pos2, new Vector3i());

        int xLen = 1 + maxPosition.x() - minPosition.x();
        int yLen = 1 + maxPosition.y() - minPosition.y();
        int zLen = 1 + maxPosition.z() - minPosition.z();

        int totalBlocks = xLen * yLen * zLen;
        if(totalBlocks != blockData.size()) {
            throw new StructraException("Block data size doesn't match region size");
        }

        // Create index mapping array
        int[] indexMapping = createIndexMapping(
                xLen, yLen, zLen,
                sourceOrder.direction3D,
                this.direction3D,
                minPosition, maxPosition
        );

        // Build new block data using the index mapping
        ArrayNode reorderedData = JsonNodeFactory.instance.arrayNode();
        for (int targetIndex = 0; targetIndex < totalBlocks; targetIndex++) {
            int sourceIndex = indexMapping[targetIndex];
            reorderedData.add(blockData.get(sourceIndex).shortValue());
        }

        return reorderedData;
    }

    /**
     * Reorder block data from another traversal order.
     *
     * @param blockData Source block data
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @param sourceOrder Source traversal order to convert from
     * @return Reordered block data as ArrayNode
     * @since 1.1
     * @deprecated Use {@link #reorderBlockData(ArrayNode, Vector3ic, Vector3ic, BlockTraversalOrder)} instead.
     */
    @Deprecated(since = "2.0-beta3")
    public ArrayNode reorderBlockData(ArrayNode blockData,
                                             Position pos1,
                                             Position pos2,
                                             BlockTraversalOrder sourceOrder) {
        return reorderBlockData(blockData, pos1.toVector3i(), pos2.toVector3i(), sourceOrder);
    }

    /**
     * Creates an index mapping from target order to source order.
     * For each position in target order, finds its corresponding index in source order.
     * @since 1.1
     */
    private int[] createIndexMapping(int width, int height, int length,
                                            Direction3D sourceDir, Direction3D targetDir,
                                            Vector3ic min, Vector3ic max) {
        int totalBlocks = width * height * length;
        int[] mapping = new int[totalBlocks];

        Direction targetFirst = targetDir.first();
        Direction targetSecond = targetDir.second();
        Direction targetThird = targetDir.third();

        int targetFirstSize = getDistance(targetFirst, width, height, length);
        int targetSecondSize = getDistance(targetSecond, width, height, length);
        int targetThirdSize = getDistance(targetThird, width, height, length);

        int targetIndex = 0;
        for(int k = 0; k < targetThirdSize; k++) {
            for(int j = 0; j < targetSecondSize; j++) {
                for(int i = 0; i < targetFirstSize; i++) {
                    // Calculate absolute X, Y, Z position for this target index
                    int x = getCoordinateForAxis(targetFirst, i, targetSecond, j, targetThird, k,
                            min.x(), max.x(), 'X');
                    int y = getCoordinateForAxis(targetFirst, i, targetSecond, j, targetThird, k,
                            min.y(), max.y(), 'Y');
                    int z = getCoordinateForAxis(targetFirst, i, targetSecond, j, targetThird, k,
                            min.z(), max.z(), 'Z');

                    // Calculate what index this position would be in source order
                    int sourceIndex = calculateSourceIndex(x, y, z, width, height, length,
                            sourceDir, min, max);

                    mapping[targetIndex] = sourceIndex;
                    targetIndex++;
                }
            }
        }

        return mapping;
    }

    /**
     * Get the coordinate value for a specific axis (X/Y/Z).
     * Checks which of the three directions controls this axis and returns the appropriate coordinate.
     * @since 1.1
     */
    private int getCoordinateForAxis(Direction first, int stepFirst,
                                            Direction second, int stepSecond,
                                            Direction third, int stepThird,
                                            int minCoord, int maxCoord,
                                            char axis) {
        Direction controllingDir = null;
        int step = 0;

        // Find which direction controls this axis
        if ((axis == 'X' && first.isX()) || (axis == 'Y' && first.isY()) || (axis == 'Z' && first.isZ())) {
            controllingDir = first;
            step = stepFirst;
        } else if ((axis == 'X' && second.isX()) || (axis == 'Y' && second.isY()) || (axis == 'Z' && second.isZ())) {
            controllingDir = second;
            step = stepSecond;
        } else if ((axis == 'X' && third.isX()) || (axis == 'Y' && third.isY()) || (axis == 'Z' && third.isZ())) {
            controllingDir = third;
            step = stepThird;
        }

        if (controllingDir != null) {
            return controllingDir.isPositive() ? minCoord + step : maxCoord - step;
        }

        return 0;
    }

    /**
     * Calculate the index in source order for a given absolute position.
     * @since 1.1
     */
    private int calculateSourceIndex(int x, int y, int z,
                                            int width, int height, int length,
                                            Direction3D sourceDir, Vector3ic min, Vector3ic max) {
        Direction sourceFirst = sourceDir.first();
        Direction sourceSecond = sourceDir.second();
        Direction sourceThird = sourceDir.third();

        // Calculate step values in source order
        int stepFirst = calculateStep(sourceFirst, x, y, z, min, max);
        int stepSecond = calculateStep(sourceSecond, x, y, z, min, max);
        int stepThird = calculateStep(sourceThird, x, y, z, min, max);

        int firstSize = getDistance(sourceFirst, width, height, length);
        int secondSize = getDistance(sourceSecond, width, height, length);

        // Calculate linear index: k * width * height + j * width + i
        return stepThird * firstSize * secondSize
                + stepSecond * firstSize
                + stepFirst;
    }

    /**
     * Calculate the step value for a direction given absolute coordinates.
     * @since 1.1
     */
    private int calculateStep(Direction dir, int x, int y, int z, Vector3ic min, Vector3ic max) {
        if (dir.isX()) {
            return dir.isPositive() ? (x - min.x()) : (max.x() - x);
        }
        if (dir.isY()) {
            return dir.isPositive() ? (y - min.y()) : (max.y() - y);
        }
        if (dir.isZ()) {
            return dir.isPositive() ? (z - min.z()) : (max.z() - z);
        }
        return 0;
    }



    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this) return true;
        if (o == null) return false;
        if (!(o instanceof BlockTraversalOrder that)) return false;

        return Objects.equals(direction3D, that.direction3D);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(direction3D);
    }
}
