package com.desoi.structra.loader;

import com.desoi.structra.Structra;
import com.desoi.structra.model.IInform;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.StateService;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Util;
import com.desoi.structra.util.Validate;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.0-SNAPSHOT
 */
public class StructurePasteTask implements IInform {

    private final @NotNull StructureLoader structureLoader;

    public StructurePasteTask(StructureLoader structureLoader) {
        Validate.validate(structureLoader != null, "StructureLoader cannot be null");
        this.structureLoader = structureLoader;
    }

    @Override
    public @NotNull CommandSender informer() {
        return structureLoader.informer();
    }
    @Override
    public boolean isSilent() {
        return structureLoader.isSilent();
    }
    public void setSilent(boolean silent) {
        structureLoader.setSilent(silent);
    }

    @Getter
    private boolean running = false;

    /**
     * Execute with a complete task.
     * @param completeTask Task to run after completion.
     * @since 1.0-SNAPSHOT
     */
    public void execute(Runnable completeTask) {
        Validate.validate(completeTask != null, "Complete task cannot be null.");
        if(running) {
            Util.tell(structureLoader.getExecutor(), "&cStructure loader is already running!");
            return;
        }
        running = true;
        structureLoader.setStartNanoTime(System.nanoTime());
        final int size = structureLoader.getPositions().size();
        new BukkitRunnable() {
            int looped = 0;
            int index = 0;
            float ratio = 0f;

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
                        long elapsedMS = (System.nanoTime() - structureLoader.getStartNanoTime()) / 1_000_000;
                        inform(String.format("&ePasting Structra to world '%s'... (%.1f%%)", structureLoader.getOriginWorld().getName(), ratio*100));
                        inform(String.format("&aPasted '%d blocks' to world '%s' in %d ms", size, structureLoader.getOriginWorld().getName(), elapsedMS));
                        completeTask.run();
                        return;
                    }

                    Location blockLocation = structureLoader.getPositions().get(index).toLocation(structureLoader.getOriginWorld());
                    if(!blockLocation.getChunk().isLoaded()) {
                        blockLocation.getChunk().load(true);
                    }
                    Block block = blockLocation.getBlock();

                    short id = structureLoader.getStructureFile().getBlockDataNode().get(index) instanceof NumericNode idNode ? idNode.shortValue() : -1;
                    if(id == -1) {
                        Util.tell(structureLoader.getExecutor(), String.format("There was an error reading BlockData id for index '%s'", index));
                        continue;
                    }

                    String data = JsonHelper.getPropertyMatching(structureLoader.getStructureFile().getPaletteNode(), (int) id, "");
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

    /**
     * Execute with no complete task.
     * @since 1.0-SNAPSHOT
     */
    public void execute() {
        execute(() -> {});
    }
}
