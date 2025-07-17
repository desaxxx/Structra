package com.desoi.structra.writer;

import com.desoi.structra.Structra;
import com.desoi.structra.model.StructraException;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.StateService;
import com.desoi.structra.util.Util;
import com.desoi.structra.util.Validate;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.desoi.structra.service.statehandler.IStateHandler.objectMapper;

public class StructureWriteTask {

    private final @NotNull StructureWriter structureWriter;

    public StructureWriteTask(StructureWriter structureWriter) {
        Validate.validate(structureWriter != null, "StructureWriter cannot be null");
        this.structureWriter = structureWriter;
    }

    public void execute() {
        structureWriter.setStartNanoTime(System.nanoTime());
        final int size = structureWriter.getVectors().size();
        new BukkitRunnable() {
            int looped = 0;
            byte nextId = 0;
            int index = 0;

            @Override
            public void run() {
                // information
                float ratio = (float) looped / size;
                Util.tell(structureWriter.getExecutor(), String.format("&eCopying Structra to file... (%.1f%%)", ratio*100));

                //
                for(int i = 0; i < structureWriter.getBatchSize(); i++) {
                    index = i + looped;
                    if(index >= size) {
                        cancel();
                        saveToFile();
                        ratio = 1.0f;
                        long elapsedMS = (System.nanoTime() - structureWriter.getStartNanoTime()) / 1_000_000;
                        Util.tell(structureWriter.getExecutor(), String.format("&eCopying Structra to file... (%.1f%%)", ratio*100));
                        Util.tell(structureWriter.getExecutor(), String.format("&aSaved '%d blocks' to file '%s' in %d ms", size, structureWriter.getFile().getName(), elapsedMS));
                        return;
                    }

                    Location blockLocation = structureWriter.getVectors().get(index).toLocation(structureWriter.getOriginWorld());
                    Block block = blockLocation.getBlock();

                    String data = block.getBlockData().getAsString();
                    short id;
                    if(structureWriter.getPaletteNode().get(data) instanceof ShortNode idNode) {
                        id = idNode.shortValue();
                    }else {
                        id = nextId++;
                        structureWriter.getPaletteNode().put(data, id);
                    }
                    structureWriter.getBlockDataNode().add(id);

                    BlockState state = block.getState();
                    IStateHandler<BlockState> handler = StateService.getHandler(state);
                    ObjectNode tileEntity = objectMapper.createObjectNode();
                    if(handler != null) {
                        tileEntity.put("Type", handler.name());
                        //tileEntity.set("Offset", getOffsetNode(blockLocation, tileEntity));
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
}
