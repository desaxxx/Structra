package com.desoi.structra.loader;

import com.desoi.structra.Structra;
import com.desoi.structra.model.IInform;
import com.desoi.structra.model.Position;
import com.desoi.structra.model.Rotation;
import com.desoi.structra.service.entityhandler.EntityService;
import com.desoi.structra.service.entityhandler.IEntityHandler;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.StateService;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Util;
import com.desoi.structra.util.Validate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 1.0-SNAPSHOT
 */
public class StructurePasteTask implements IInform {

    private final @NotNull StructureLoader structureLoader;

    private long startNanoTime;

    public StructurePasteTask(@NotNull StructureLoader structureLoader) {
        Validate.notNull(structureLoader, "StructureLoader cannot be null");
        this.structureLoader = structureLoader;

        structureLoader.validateVersion();
    }

    @Override
    public @NotNull CommandSender informer() {
        return structureLoader.informer();
    }

    private boolean silent = false;
    @Override
    public boolean isSilent() {
        return silent;
    }

    @Override
    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    /**
     * Calculate the estimated remaining time.
     * @return Seconds remained
     * @since 1.0.0
     */
    public int estimatedRemainingTime() {
        int size = structureLoader.getPositions().size();
        int batchSize = structureLoader.getBatchSize();
        int period = structureLoader.getPeriodTicks() / 20;
        return (int) Math.floor((double) (size-1) / batchSize * period);
    }

    private boolean running = false;

    /**
     * Execute with a complete task.
     * @param completeTask Task to run after completion.
     * @since 1.0-SNAPSHOT
     */
    public void execute(Runnable completeTask) {
        Validate.notNull(completeTask, "Complete task cannot be null.");
        if(running) {
            Util.tell(structureLoader.getExecutor(), "&cStructure loader is already running!");
            return;
        }
        running = true;
        startNanoTime = System.nanoTime();
        final int size = structureLoader.getPositions().size();
        new BukkitRunnable() {
            int looped = 0;
            int index = 0;
            float ratio = 0f;
            int entityCount = 0;

            @Override
            public void run() {
                // information
                ratio = (float) looped / size;
                inform(String.format("&ePasting Structra to world '%s'... (%.1f%%)", structureLoader.getOriginWorld().getName(), ratio*100));

                //
                for(int i = 0; i < structureLoader.getBatchSize(); i++) {
                    index = i + looped;
                    if(index >= size) {
                        cancel();

                        ratio = 1.0f;
                        long elapsedMS = (System.nanoTime() - startNanoTime) / 1_000_000;
                        inform(String.format("&ePasting Structra to world '%s'... (%.1f%%)", structureLoader.getOriginWorld().getName(), ratio*100));
                        inform(String.format("&aPasted '%d blocks and %d entities' to world '%s' in %d ms", size, entityCount, structureLoader.getOriginWorld().getName(), elapsedMS));
                        completeTask.run();
                        return;
                    }

                    Position blockPosition = structureLoader.getPositions().get(index);
                    Location blockLocation = blockPosition.toLocation(structureLoader.getOriginWorld());
                    if(!blockLocation.getChunk().isLoaded()) {
                        blockLocation.getChunk().load(true);
                    }
                    Block block = blockLocation.getBlock();

                    Position relative = blockPosition.copy().subtract(structureLoader.getMinPosition());
                    Position fileCoord = structureLoader.getBlockTraversalOrder().inverseRotate(relative, structureLoader.getRotation(), structureLoader.getStructureFile().getXSize(), structureLoader.getStructureFile().getZSize());
                    String positionKey = fileCoord.separatedByComma();

                    short id = structureLoader.getReorderedBlockDataNode().get(index) instanceof NumericNode idNode ? idNode.shortValue() : -1;
                    if(id == -1) {
                        Util.tell(structureLoader.getExecutor(), String.format("There was an error reading BlockData id for index '%s'", index));
                        continue;
                    }

                    String data = JsonHelper.getPropertyMatching(structureLoader.getStructureFile().getPaletteNode(), (int) id, "");
                    try {
                        BlockData bData = Bukkit.createBlockData(data);
                        rotateBlockData(bData, structureLoader.getRotation());
                        block.setType(bData.getMaterial(), false); // false -> no physics
                        block.setBlockData(bData);
                    } catch (IllegalArgumentException e) {
                        Util.tell(structureLoader.getExecutor(), String.format("There was an error reading BlockData for index '%s'", index));
                        block.setType(Material.AIR, false);
                    }

                    // Block pos - Min Pos
                    if(structureLoader.getStructureFile().getTileEntitiesNode().get(positionKey) instanceof ObjectNode tileEntity) {
                        BlockState blockState = block.getState();
                        IStateHandler<BlockState> handler = StateService.getHandler(blockState);
                        if(handler != null) {
                            handler.loadTo(blockState, tileEntity);
                        }
                    }

                    if(structureLoader.getStructureFile().getEntitiesNode().get(positionKey) instanceof ArrayNode nearbyNode) {
                        for(JsonNode entityJNode : nearbyNode) {
                            if(!(entityJNode instanceof ObjectNode entityNode)) continue;

                            String type = entityNode.get("Type").asText();
                            IEntityHandler<Entity> entityHandler = EntityService.getHandler(type);
                            if(entityHandler == null) {
                                continue;
                            }

                            Location location = blockLocation.clone();
                            if (entityNode.get("Offset") instanceof ObjectNode offsetNode) {
                                double ox = offsetNode.get("x").asDouble();
                                double oy = offsetNode.get("y").asDouble();
                                double oz = offsetNode.get("z").asDouble();

                                double rotatedX = ox;
                                double rotatedZ = oz;
                                switch (structureLoader.getRotation()) {
                                    case CW_90  -> { rotatedX = 1.0 - oz; rotatedZ = ox; }
                                    case CW_180 -> { rotatedX = 1.0 - ox; rotatedZ = 1.0 - oz; }
                                    case CW_270 -> { rotatedX = oz; rotatedZ = 1.0 - ox; }
                                }
                                location.add(rotatedX, oy, rotatedZ);
                            }
                            if (entityNode.get("Yaw") instanceof NumericNode yawNode) {
                                float yaw = (float) yawNode.asDouble();
                                float addAngle = switch (structureLoader.getRotation()) {
                                    case CW_90  -> 90f;
                                    case CW_180 -> 180f;
                                    case CW_270 -> 270f;
                                    default     -> 0f;
                                };
                                location.setYaw(Location.normalizeYaw(yaw + addAngle));
                            }
                            if (entityNode.get("Pitch") instanceof NumericNode pitchNode) location.setPitch((float) pitchNode.asDouble());

                            entityHandler.spawnAndLoad(location, entityNode);
                            entityCount++;
                        }
                    }
                }

                looped += structureLoader.getBatchSize();
            }
        }.runTaskTimer(Structra.getInstance(), structureLoader.getDelayTicks(), structureLoader.getPeriodTicks());
    }

    /**
     * Execute with no complete task.
     * @since 1.0-SNAPSHOT
     */
    public void execute() {
        execute(() -> {});
    }

    private void rotateBlockData(BlockData data, Rotation rotation) {
        if (rotation == Rotation.NONE) return;
        if (data instanceof Directional d) {
            d.setFacing(rotateFacing(d.getFacing(), rotation));
        } else if (data instanceof Rotatable r) {
            r.setRotation(rotateFacing(r.getRotation(), rotation));
        } else if (data instanceof Orientable o) {
            if (rotation == Rotation.CW_90 || rotation == Rotation.CW_270) {
                if (o.getAxis() == Axis.X) o.setAxis(Axis.Z);
                else if (o.getAxis() == Axis.Z) o.setAxis(Axis.X);
            }
        } else if (data instanceof MultipleFacing f) {
            Set<BlockFace> activeFaces = Set.copyOf(f.getFaces());
            f.getAllowedFaces().forEach(face -> f.setFace(face, false));

            for (BlockFace face : activeFaces) {
                BlockFace rotated = rotateFacing(face, rotation);
                if (f.getAllowedFaces().contains(rotated)) {
                    f.setFace(rotated, true);
                }
            }
        }
    }

    private static BlockFace rotateFacing(BlockFace face, Rotation rotation) {
        return switch (rotation) {
            case NONE   -> face;
            case CW_90  -> rotateCW(face);
            case CW_180 -> rotateCW(rotateCW(face));
            case CW_270 -> rotateCW(rotateCW(rotateCW(face)));
        };
    }
    private static BlockFace rotateCW(BlockFace face) {
        return switch (face) {
            case NORTH            -> BlockFace.EAST;
            case EAST             -> BlockFace.SOUTH;
            case SOUTH            -> BlockFace.WEST;
            case WEST             -> BlockFace.NORTH;
            case NORTH_EAST       -> BlockFace.SOUTH_EAST;
            case SOUTH_EAST       -> BlockFace.SOUTH_WEST;
            case SOUTH_WEST       -> BlockFace.NORTH_WEST;
            case NORTH_WEST       -> BlockFace.NORTH_EAST;
            case NORTH_NORTH_EAST -> BlockFace.EAST_SOUTH_EAST;
            case EAST_SOUTH_EAST  -> BlockFace.SOUTH_SOUTH_WEST;
            case SOUTH_SOUTH_WEST -> BlockFace.WEST_NORTH_WEST;
            case WEST_NORTH_WEST  -> BlockFace.NORTH_NORTH_EAST;
            case NORTH_NORTH_WEST -> BlockFace.EAST_NORTH_EAST;
            case EAST_NORTH_EAST  -> BlockFace.SOUTH_SOUTH_EAST;
            case SOUTH_SOUTH_EAST -> BlockFace.WEST_SOUTH_WEST;
            case WEST_SOUTH_WEST  -> BlockFace.NORTH_NORTH_WEST;
            default -> face;
        };
    }

    public boolean isRunning() {
        return running;
    }
}
