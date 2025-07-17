package com.desoi.structra.loader;

import com.desoi.structra.Structra;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.StateService;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Util;
import com.desoi.structra.util.Validate;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class StructurePasteTask {

    private final @NotNull StructureLoader structureLoader;

    public StructurePasteTask(StructureLoader structureLoader) {
        Validate.validate(structureLoader != null, "StructureLoader cannot be null");
        this.structureLoader = structureLoader;
    }

    public void execute() {
        structureLoader.setStartNanoTime(System.nanoTime());
        final int size = structureLoader.getVectors().size();
        new BukkitRunnable() {
            int looped = 0;
            int index = 0;
            float ratio = 0f;

            @Override
            public void run() {
                // information
                ratio = (float) looped / size;
                Util.tell(structureLoader.getExecutor(), String.format("&ePasting Structra to world '%s'... (%.1f%%)", structureLoader.getOriginWorld().getName(), ratio*100));

                //
                for(int i = 0; i < structureLoader.getBatchSize(); i++) {
                    index = i + looped;
                    if(index >= size) {
                        cancel();
                        ratio = 1.0f;
                        long elapsedMS = (System.nanoTime() - structureLoader.getStartNanoTime()) / 1_000_000;
                        Util.tell(structureLoader.getExecutor(), String.format("&ePasting Structra to world '%s'... (%.1f%%)", structureLoader.getOriginWorld().getName(), ratio*100));
                        Util.tell(structureLoader.getExecutor(), String.format("&aPasted '%d blocks' to world '%s' in %d ms", size, structureLoader.getOriginWorld().getName(), elapsedMS));
                        return;
                    }

                    Location blockLocation = structureLoader.getVectors().get(index).toLocation(structureLoader.getOriginWorld());
                    if(!blockLocation.getChunk().isLoaded()) {
                        blockLocation.getChunk().load(true);
                    }
                    Block block = blockLocation.getBlock();

                    short id = structureLoader.getStructureFile().getBlockDataNode().get(index) instanceof ShortNode idNode ? idNode.shortValue() : -1;
                    if(id == -1) {
                        Util.tell(structureLoader.getExecutor(), String.format("There was an error reading BlockData for index '%s'", index));
                        continue;
                    }

                    String data = JsonHelper.getPropertyMatching(structureLoader.getStructureFile().getPaletteNode(), id, "");
                    try {
                        BlockData bData = Bukkit.createBlockData(data);
                        block.setType(bData.getMaterial(), false); // false -> no physics
                        block.setBlockData(bData);
                    } catch (IllegalArgumentException e) {
                        Util.tell(structureLoader.getExecutor(), String.format("There was an error reading BlockData for index '%s'", index));
                        block.setType(Material.AIR, false);
                    }

                    if(structureLoader.getStructureFile().getTileEntitiesNode().get(String.valueOf(index)) instanceof ObjectNode tileEntity) {
                        BlockState blockState = block.getState();
                        IStateHandler<BlockState> handler = StateService.getHandler(blockState);
                        if(handler != null) {
                            handler.loadTo(blockState, tileEntity);
                        }
                    }
                }

                looped += structureLoader.getBatchSize();
            }
        }.runTaskTimer(Structra.getInstance(), structureLoader.getDelayTicks(), structureLoader.getPeriodTicks());
    }
}
