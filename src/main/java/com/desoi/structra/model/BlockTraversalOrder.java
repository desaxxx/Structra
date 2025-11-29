package com.desoi.structra.model;

import com.desoi.structra.direction.Direction;
import com.desoi.structra.direction.Direction3D;
import com.desoi.structra.util.Validate;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;

public class BlockTraversalOrder {
    // DO NOT CHANGE AS THIS IS THE ONLY ORDER USED IN StructraWriter.
    public static final @NotNull BlockTraversalOrder DEFAULT = create(Direction.P_X, Direction.P_Y, Direction.P_Z);


    private final @NotNull Direction3D direction3D;
    /**
     * @since 1.0.1
     */
    private BlockTraversalOrder(@NotNull Direction3D direction3D) {
        Validate.notNull(direction3D, "Direction3D cannot be null.");

        this.direction3D = direction3D;
    }

    /**
     * @return Direction3D
     * @since 1.0.1
     */
    @NotNull
    public Direction3D getDirection3D() {
        return direction3D;
    }


    /**
     * Get Positions between specified positions unique to this order.
     *
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @return LinkedList of positions
     * @since 1.0-SNAPSHOT
     */
    public LinkedList<Position> getPositions(@NotNull Position pos1, @NotNull Position pos2) {
        Validate.notNull(pos1, pos2, "Positions cannot be null.");
        Position minPosition = Position.getMinimum(pos1, pos2);
        Position maxPosition = Position.getMaximum(pos1, pos2);

        int width = maxPosition.width(minPosition);
        int height = maxPosition.height(minPosition);
        int length = maxPosition.length(minPosition);

        Direction first = direction3D.first();
        Direction second = direction3D.second();
        Direction third = direction3D.third();

        int firstSize = getDistance(first, width, height, length);
        int secondSize = getDistance(second, width, height, length);
        int thirdSize = getDistance(third, width, height, length);

        LinkedList<Position> positions = new LinkedList<>();
        for(int k = 0; k <= thirdSize; k++) {
            for(int j = 0; j <= secondSize; j++) {
                for(int i = 0; i <= firstSize; i++) {

                    Position offset = new Position(0, 0 ,0);
                    offset = offset.add(applyAxis(first, i, minPosition, maxPosition));
                    offset = offset.add(applyAxis(second, j, minPosition, maxPosition));
                    offset = offset.add(applyAxis(third, k, minPosition, maxPosition));

                    positions.addLast(offset);
                }
            }
        }
        return positions;
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
     * @since 1.0.1
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
     * @since 1.0.1
     */
    private Position applyAxis(Direction direction, int step, Position min, Position max) {
        if (direction.isX()) {
            int x = direction.isPositive() ? min.getX() + step : max.getX() - step;
            return new Position(x, 0, 0);
        }
        if (direction.isY()) {
            int y = direction.isPositive() ? min.getY() + step : max.getY() - step;
            return new Position(0, y, 0);
        }
        if (direction.isZ()) {
            int z = direction.isPositive() ? min.getZ() + step : max.getZ() - step;
            return new Position(0, 0, z);
        }
        return new Position(0, 0, 0);
    }

    /**
     * Reorder block data from another traversal order.
     *
     * @param blockData Source block data
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @param sourceOrder Source traversal order to convert from
     * @return Reordered block data as ArrayNode
     * @since 1.0.1
     */
    @NotNull
    public ArrayNode reorderBlockData(@NotNull ArrayNode blockData,
                                             @NotNull Position pos1,
                                             @NotNull Position pos2,
                                             @NotNull BlockTraversalOrder sourceOrder) {
        Validate.notNull(blockData, "Block data cannot be null.");
        Validate.notNull(pos1, pos2, "Positions cannot be null.");
        Validate.notNull(sourceOrder, "Source traversal order cannot be null.");

        if(sourceOrder.equals(this)) {
            return blockData;
        }

        Position minPosition = Position.getMinimum(pos1, pos2);
        Position maxPosition = Position.getMaximum(pos1, pos2);

        int width = maxPosition.width(minPosition);
        int height = maxPosition.height(minPosition);
        int length = maxPosition.length(minPosition);

        int totalBlocks = (width + 1) * (height + 1) * (length + 1);
        Validate.validate(blockData.size() == totalBlocks,
                "Block data size doesn't match region size.");

        // Create index mapping array
        int[] indexMapping = createIndexMapping(
                width, height, length,
                sourceOrder.direction3D,
                this.direction3D,
                minPosition, maxPosition
        );

        // Build new block data using the index mapping
        ArrayNode reorderedData = JsonNodeFactory.instance.arrayNode();
        for (int targetIndex = 0; targetIndex < totalBlocks; targetIndex++) {
            int sourceIndex = indexMapping[targetIndex];
            reorderedData.add(blockData.get(sourceIndex).asInt());
        }

        return reorderedData;
    }

    /**
     * Creates an index mapping from target order to source order.
     * For each position in target order, finds its corresponding index in source order.
     * @since 1.0.1
     */
    private int[] createIndexMapping(int width, int height, int length,
                                            Direction3D sourceDir, Direction3D targetDir,
                                            Position min, Position max) {
        int totalBlocks = (width + 1) * (height + 1) * (length + 1);
        int[] mapping = new int[totalBlocks];

        Direction targetFirst = targetDir.first();
        Direction targetSecond = targetDir.second();
        Direction targetThird = targetDir.third();

        int targetFirstSize = getDistance(targetFirst, width, height, length);
        int targetSecondSize = getDistance(targetSecond, width, height, length);
        int targetThirdSize = getDistance(targetThird, width, height, length);

        int targetIndex = 0;
        for(int k = 0; k <= targetThirdSize; k++) {
            for(int j = 0; j <= targetSecondSize; j++) {
                for(int i = 0; i <= targetFirstSize; i++) {
                    // Calculate absolute X, Y, Z position for this target index
                    int x = getCoordinateForAxis(targetFirst, i, targetSecond, j, targetThird, k,
                            min.getX(), max.getX(), 'X');
                    int y = getCoordinateForAxis(targetFirst, i, targetSecond, j, targetThird, k,
                            min.getY(), max.getY(), 'Y');
                    int z = getCoordinateForAxis(targetFirst, i, targetSecond, j, targetThird, k,
                            min.getZ(), max.getZ(), 'Z');

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
     * @since 1.0.1
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
     * @since 1.0.1
     */
    private int calculateSourceIndex(int x, int y, int z,
                                            int width, int height, int length,
                                            Direction3D sourceDir, Position min, Position max) {
        Direction sourceFirst = sourceDir.first();
        Direction sourceSecond = sourceDir.second();
        Direction sourceThird = sourceDir.third();

        // Calculate step values in source order
        int stepFirst = calculateStep(sourceFirst, x, y, z, min, max);
        int stepSecond = calculateStep(sourceSecond, x, y, z, min, max);
        int stepThird = calculateStep(sourceThird, x, y, z, min, max);

        int firstSize = getDistance(sourceFirst, width, height, length);
        int secondSize = getDistance(sourceSecond, width, height, length);

        // Calculate linear index: k * (width+1) * (height+1) + j * (width+1) + i
        return stepThird * (firstSize + 1) * (secondSize + 1)
                + stepSecond * (firstSize + 1)
                + stepFirst;
    }

    /**
     * Calculate the step value for a direction given absolute coordinates.
     * @since 1.0.1
     */
    private int calculateStep(Direction dir, int x, int y, int z, Position min, Position max) {
        if (dir.isX()) {
            return dir.isPositive() ? (x - min.getX()) : (max.getX() - x);
        }
        if (dir.isY()) {
            return dir.isPositive() ? (y - min.getY()) : (max.getY() - y);
        }
        if (dir.isZ()) {
            return dir.isPositive() ? (z - min.getZ()) : (max.getZ() - z);
        }
        return 0;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BlockTraversalOrder that = (BlockTraversalOrder) o;
        return Objects.equals(direction3D, that.direction3D);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(direction3D);
    }

    @NotNull
    public static BlockTraversalOrder create(Direction first, Direction second, Direction third) {
        return new BlockTraversalOrder(Direction3D.of(first, second, third));
    }
    @NotNull
    public static BlockTraversalOrder create(Direction3D directions) {
        return new BlockTraversalOrder(directions);
    }
}
