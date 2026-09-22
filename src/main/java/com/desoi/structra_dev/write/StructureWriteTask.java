package com.desoi.structra_dev.write;

import com.desoi.structra.Structra;
import com.desoi.structra.model.BlockTraversalOrder;
import com.desoi.structra.model.StructraException;
import com.desoi.structra.service.entityhandler.EntityService;
import com.desoi.structra.service.entityhandler.IEntityHandler;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.StateService;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Util;
import com.desoi.structra_dev.file.StructureFileBuilder;
import com.desoi.structra_dev.util.VectorUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.joml.Vector3i;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

@NullMarked
public class StructureWriteTask {

    private final StructureWriter writer;
    private final File writeTo;
    private final StructureFileBuilder fileBuilder;

    private final Vector3i minPosition;
    private final Vector3i maxPosition;
    private final List<Vector3i> positions;

    private @Nullable BukkitTask task;
    private long startNanoTime;

    public StructureWriteTask(StructureWriter writer, File writeTo) {
        this.writer = Preconditions.checkNotNull(writer, "writer");
        this.writeTo = Preconditions.checkNotNull(writeTo, "writeTo");
        this.fileBuilder = new StructureFileBuilder();

        this.minPosition = writer.getPosition1().min(writer.getPosition2(), new Vector3i());
        this.maxPosition = writer.getPosition1().max(writer.getPosition2(), new Vector3i());
        this.positions = new ArrayList<>(BlockTraversalOrder.DEFAULT.getPositions(minPosition, maxPosition));
    }

    public void log(String... messages) {
        if (writer.getOptions().silent()) {
            return;
        }

        Util.tell(writer.getExecutor(), messages);
    }

    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }

    /**
     * @throws IllegalStateException if the write task is not being executed
     */
    public void terminate() {
        Preconditions.checkState(isRunning(), "write task is not being executed");
        task.cancel();
        task = null;
    }

    /**
     * @throws IllegalStateException if the write task is already being executed
     */
    public void execute(@Nullable Runnable onComplete) {
        Preconditions.checkState(!isRunning(), "write task is already being executed");

        startNanoTime = System.nanoTime();
        final int size = positions.size();
        final Map<String, Collection<Entity>> positionKeyToEntities = collectEntities();
        final String fileName = writeTo.getName();

        log("&eWriting process started for '%s'.".formatted(fileName));

        this.task = new BukkitRunnable() {
            final int batchSize = writer.getOptions().batchSize();
            int looped = 0;
            int entityCount = 0;

            @Override
            public void run() {
                float ratio = (float) looped / size;

                // Writing process for 'test': 10 blocks, 5 entities. (15% done)
                log("&eWriting process for '%s': %,d blocks, %,d entities. (%.1f%% done)".formatted(fileName, looped, entityCount, ratio*100));

                for (int i = 0; i < batchSize; i++) {
                    int batchIndex = i + looped;
                    if (batchIndex >= size) {
                        cancel();

                        long elapsedMillis = (System.nanoTime() - startNanoTime) / 1_000_000;
                        // Writing process over: 605 blocks, 10 entities. (100% done, took 7,600 ms)
                        log("&eWriting process over for '%s': %,d blocks, %,d entities. (100%% done, took %,d ms)".formatted(fileName, size, entityCount, elapsedMillis));

                        onTaskOver(onComplete);
                        return;
                    }

                    Vector3i blockPosition = positions.get(batchIndex);
                    Location blockLocation = VectorUtil.toBukkitLocation(blockPosition, writer.getOriginWorld());
                    Block block = blockLocation.getBlock();
                    String positionKey = VectorUtil.toPositionKey(blockPosition.sub(minPosition, new Vector3i()));

                    String data = block.getBlockData().getAsString();
                    short blockDataIndex = fileBuilder.getOrCreatePalette(data);
                    fileBuilder.addBlockData(blockDataIndex);

                    BlockState state = block.getState();
                    IStateHandler<BlockState> stateHandler = StateService.getHandler(state);
                    if(stateHandler != null) {
                        ObjectNode tileEntity = JsonHelper.OBJECT_MAPPER.createObjectNode();
                        tileEntity.put("Type", stateHandler.name());
                        stateHandler.save(state, tileEntity);
                        fileBuilder.addTileEntity(positionKey, tileEntity);
                    }

                    Collection<Entity> entities = positionKeyToEntities.get(positionKey);
                    if(entities != null && !entities.isEmpty()) {
                        List<ObjectNode> entityList = new ArrayList<>();

                        for (Entity entity : entities) {
                            IEntityHandler<Entity> entityHandler = EntityService.getHandler(entity.getType());
                            if (entityHandler == null) continue;
                            Location loc = entity.getLocation();

                            ObjectNode entityNode = JsonHelper.OBJECT_MAPPER.createObjectNode();
                            entityNode.put("Type", entity.getType().name());
                            ObjectNode offsetNode = entityNode.putObject("Offset");
                            offsetNode.put("x", loc.getX() - blockLocation.getX());
                            offsetNode.put("y", loc.getY() - blockLocation.getY());
                            offsetNode.put("z", loc.getZ() - blockLocation.getZ());
                            entityNode.put("Yaw", loc.getYaw());
                            entityNode.put("Pitch", loc.getPitch());

                            entityHandler.save(entity, entityNode);
                            entityList.add(entityNode);
                            entityCount++;
                        }

                        if(!entityList.isEmpty()) {
                            fileBuilder.addEntity(positionKey, entityList);
                        }
                    }
                }

                looped += batchSize;
            }
        }.runTaskTimer(Structra.getInstance(), writer.getOptions().delayTicks(), writer.getOptions().periodTicks());
    }

    private void onTaskOver(@Nullable Runnable onComplete) {
        saveToFile();
        if(onComplete != null) onComplete.run();
    }

    private Map<String, Collection<Entity>> collectEntities() {
        Multimap<String, Entity> positionKeyToEntities = HashMultimap.create();

        World world = writer.getOriginWorld();
        Location minLocation = VectorUtil.toBukkitLocation(minPosition, world);
        Location maxLocation = VectorUtil.toBukkitLocation(maxPosition.add(1,1,1, new Vector3i()), world);
        BoundingBox areaBox = BoundingBox.of(minLocation, maxLocation);
        Collection<Entity> entities = world.getNearbyEntities(areaBox);

        for(Entity entity : entities) {
            Location loc = entity.getLocation();
            Vector3i blockPosition = new Vector3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            String positionKey = VectorUtil.toPositionKey(blockPosition.sub(minPosition, new Vector3i()));

            positionKeyToEntities.put(positionKey, entity);
        }

        return positionKeyToEntities.asMap();
    }

    private void saveToFile() {
        ObjectNode root = fileBuilder.build().assemble();

        try {
            JsonHelper.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(writeTo, root);
        } catch (IOException e) {
            throw new StructraException("Failed to save structure data to file: %s".formatted(writeTo.getPath()), e);
        }
    }
}
