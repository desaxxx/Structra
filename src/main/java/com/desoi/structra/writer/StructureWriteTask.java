package com.desoi.structra.writer;

import com.desoi.structra.Structra;
import com.desoi.structra.model.Position;
import com.desoi.structra.model.IInform;
import com.desoi.structra.service.entityhandler.EntityService;
import com.desoi.structra.service.entityhandler.IEntityHandler;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.StateService;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Util;
import com.desoi.structra.util.Validate;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class StructureWriteTask implements IInform {

    private final @NotNull StructureWriter structureWriter;

    public StructureWriteTask(@NotNull StructureWriter structureWriter) {
        Validate.notNull(structureWriter, "StructureWriter cannot be null");
        this.structureWriter = structureWriter;
    }

    private boolean running = false;

    @Override
    public @NotNull CommandSender informer() {
        return structureWriter.informer();
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
        int size = structureWriter.getPositions().size();
        int batchSize = structureWriter.getBatchSize();
        double periodSeconds = structureWriter.getPeriodTicks() / 20.0;
        return (int) Math.floor((double) (size-1) / batchSize * periodSeconds);
    }


    /**
     * Execute with a complete task.
     * @param completeTask Task to run after completion.
     * @since 1.0-SNAPSHOT
     */
    public void execute(Runnable completeTask) {
        Validate.notNull(completeTask, "Complete task cannot be null.");
        if(running) {
            Util.tell(structureWriter.getExecutor(), "&cStructure writer is already running!");
            return;
        }
        running = true;
        structureWriter.setStartNanoTime(System.nanoTime());
        final List<Position> positions = new ArrayList<>(structureWriter.getPositions());
        final int size = positions.size();

        Map<String, Collection<Entity>> positionKeyToEntities = collectEntities();

        new BukkitRunnable() {
            int looped = 0;
            short nextId = 0;
            int index = 0;
            float ratio = 0.0f;
            int entityCount = 0;

            @Override
            public void run() {
                // information
                ratio = (float) looped / size;
                inform(String.format("&eCopying Structra to file... (%.1f%%)", ratio*100));

                for(int i = 0; i < structureWriter.getBatchSize(); i++) {
                    index = i + looped;
                    if(index >= size) {
                        cancel();
                        saveToFileAsync(size, entityCount, completeTask);
                        return;
                    }

                    Position blockPosition = positions.get(index);
                    Location blockLocation = blockPosition.toLocation(structureWriter.getOriginWorld());
                    Block block = blockLocation.getBlock();
                    String positionKey = blockPosition.copy().subtract(structureWriter.getMinPosition()).separatedByComma(); // "15,5,0"

                    String data = block.getBlockData().getAsString();
                    short id;
                    if(structureWriter.getPaletteNode().get(data) instanceof NumericNode idNode) {
                        id = idNode.shortValue();
                    }else {
                        id = nextId++;
                        structureWriter.getPaletteNode().put(data, id);
                    }
                    structureWriter.getBlockDataNode().add(id);

                    BlockState state = block.getState();
                    IStateHandler<BlockState> stateHandler = StateService.getHandler(state);
                    if(stateHandler != null) {
                        ObjectNode tileEntity = JsonHelper.OBJECT_MAPPER.createObjectNode();
                        tileEntity.put("Type", stateHandler.name());
                        stateHandler.save(state, tileEntity);
                        structureWriter.getTileEntitiesNode().set(positionKey, tileEntity);
                    }

                    Collection<Entity> entities = positionKeyToEntities.get(positionKey);
                    if(entities != null && !entities.isEmpty()) {
                        ArrayNode nearbyNode = JsonHelper.OBJECT_MAPPER.createArrayNode();

                        for (Entity entity : entities) {
                            IEntityHandler<Entity> entityHandler = EntityService.getHandler(entity.getType());
                            if (entityHandler == null) continue;
                            Location loc = entity.getLocation();

                            ObjectNode entityNode = nearbyNode.addObject();
                            entityNode.put("Type", entity.getType().name());
                            ObjectNode offsetNode = entityNode.putObject("Offset");
                            offsetNode.put("x", loc.getX() - blockLocation.getX());
                            offsetNode.put("y", loc.getY() - blockLocation.getY());
                            offsetNode.put("z", loc.getZ() - blockLocation.getZ());
                            entityNode.put("Yaw", loc.getYaw());
                            entityNode.put("Pitch", loc.getPitch());

                            entityHandler.save(entity, entityNode);
                            entityCount++;
                        }

                        if(!nearbyNode.isEmpty()) {
                            structureWriter.getEntitiesNode().set(positionKey, nearbyNode);
                        }
                    }
                }

                looped += structureWriter.getBatchSize();
            }
        }.runTaskTimer(Structra.getInstance(), structureWriter.getDelayTicks(), structureWriter.getPeriodTicks());
    }

    private void saveToFileAsync(int blockCount, int entityCount, Runnable completeTask) {
        Structra plugin = Structra.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            IOException error = null;
            try {
                JsonHelper.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(structureWriter.getFile(), structureWriter.getRoot());
            } catch (IOException e) {
                error = e;
            }

            final IOException finalError = error;
            if(!plugin.isEnabled()) return;
            Bukkit.getScheduler().runTask(plugin, () -> {
                running = false;
                if(finalError != null) {
                    plugin.getLogger().log(Level.SEVERE, String.format("Couldn't save to file '%s'", structureWriter.getFile().getName()), finalError);
                    informIgnoreSilent(String.format("&cCouldn't save to file '%s': %s", structureWriter.getFile().getName(), finalError.getMessage()));
                    return;
                }

                long elapsedMS = (System.nanoTime() - structureWriter.getStartNanoTime()) / 1_000_000;
                inform(String.format("&eCopying Structra to file... (%.1f%%)", 100.0f));
                inform(String.format("&aSaved '%d blocks and %d entities' to file '%s' in %d ms", blockCount, entityCount, structureWriter.getFile().getName(), elapsedMS));
                completeTask.run();
            });
        });
    }

    private @NotNull Map<String, Collection<Entity>> collectEntities() {
        Multimap<String, Entity> positionKeyToEntities = HashMultimap.create();

        Position minPosition = structureWriter.getMinPosition();

        World world = structureWriter.getOriginWorld();
        Location minLocation = minPosition.toLocation(world);
        Location maxLocation = structureWriter.getMaxPosition().copy().add(new Position(1,1,1)).toLocation(world);
        BoundingBox areaBox = BoundingBox.of(minLocation, maxLocation);
        Collection<Entity> entities = world.getNearbyEntities(areaBox);

        for(Entity entity : entities) {
            Position blockPosition = Position.fromLocation(entity.getLocation(), false);
            String positionKey = blockPosition.copy().subtract(minPosition).separatedByComma();

            positionKeyToEntities.put(positionKey, entity);
        }

        return positionKeyToEntities.asMap();
    }

    /**
     * Execute with no complete task.
     * @since 1.0-SNAPSHOT
     */
    public void execute() {
        execute(() -> {});
    }

    public boolean isRunning() {
        return running;
    }
}
