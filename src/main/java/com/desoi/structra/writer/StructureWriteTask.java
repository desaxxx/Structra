package com.desoi.structra.writer;

import com.desoi.structra.Structra;
import com.desoi.structra.model.StructraException;
import com.desoi.structra.model.IInform;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.StateService;
import com.desoi.structra.util.Util;
import com.desoi.structra.util.Validate;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class StructureWriteTask implements IInform {

    private final @NotNull StructureWriter structureWriter;

    public StructureWriteTask(StructureWriter structureWriter) {
        Validate.validate(structureWriter != null, "StructureWriter cannot be null");
        this.structureWriter = structureWriter;
    }

    @Getter
    private boolean running = false;

    @Override
    public @NotNull CommandSender informer() {
        return structureWriter.informer();
    }
    @Override
    public boolean isSilent() {
        return structureWriter.isSilent();
    }
    public void setSilent(boolean silent) {
        structureWriter.setSilent(silent);
    }

    /**
     * Calculate the estimated remaining time.
     * @return Seconds remained
     * @since 1.0.0
     */
    public int estimatedRemainingTime() {
        int size = structureWriter.getPositions().size();
        int batchSize = structureWriter.getBatchSize();
        int period = structureWriter.getPeriodTicks() / 20;
        return (int) Math.floor((double) (size-1) / batchSize * period);
    }


    /**
     * Execute with a complete task.
     * @param completeTask Task to run after completion.
     * @since 1.0-SNAPSHOT
     */
    public void execute(Runnable completeTask) {
        Validate.validate(completeTask != null, "Complete task cannot be null.");
        if(running) {
            Util.tell(structureWriter.getExecutor(), "&cStructure writer is already running!");
            return;
        }
        running = true;
        structureWriter.setStartNanoTime(System.nanoTime());
        final int size = structureWriter.getPositions().size();
        new BukkitRunnable() {
            int looped = 0;
            byte nextId = 0;
            int index = 0;
            float ratio = 0.0f;

            @Override
            public void run() {
                // information
                ratio = (float) looped / size;
                inform(String.format("&eCopying Structra to file... (%.1f%%)", ratio*100));

                //
                for(int i = 0; i < structureWriter.getBatchSize(); i++) {
                    index = i + looped;
                    if(index >= size) {
                        cancel();
                        saveToFile();
                        ratio = 1.0f;
                        long elapsedMS = (System.nanoTime() - structureWriter.getStartNanoTime()) / 1_000_000;
                        inform(String.format("&eCopying Structra to file... (%.1f%%)", ratio*100));
                        inform(String.format("&aSaved '%d blocks' to file '%s' in %d ms", size, structureWriter.getFile().getName(), elapsedMS));
                        completeTask.run();
                        return;
                    }

                    Location blockLocation = structureWriter.getPositions().get(index).toLocation(structureWriter.getOriginWorld());
                    Block block = blockLocation.getBlock();

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
                    IStateHandler<BlockState> handler = StateService.getHandler(state);
                    ObjectNode tileEntity = StructureWriter.getObjectMapper().createObjectNode();
                    if(handler != null) {
                        tileEntity.put("Type", handler.name());
                        handler.save(state, tileEntity);
                        structureWriter.getTileEntitiesNode().set(String.valueOf(index), tileEntity);
                    }
                }

                looped += structureWriter.getBatchSize();
            }
        }.runTaskTimer(Structra.getInstance(), structureWriter.getDelayTicks(), structureWriter.getPeriodTicks());
    }

    private void saveToFile() {
        try {
            StructureWriter.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(structureWriter.getFile(), structureWriter.getRoot());
        } catch (IOException e) {
            throw new StructraException(String.format("Couldn't save to file '%s'", structureWriter.getFile().getName()) + e);
        }
    }

    /**
     * Execute with no complete task.
     * @since 1.0-SNAPSHOT
     */
    public void execute() {
        execute(() -> {});
    }
}
