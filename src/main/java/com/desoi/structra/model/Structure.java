package com.desoi.structra.model;

import com.desoi.structra.Structra;
import com.desoi.structra.Util;
import com.desoi.structra.service.BlockStateService;
import com.desoi.structra.service.blockstate.BlockStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Structure {

    private final @NotNull CommandSender sender;
    private final @NotNull ObjectMapper objectMaper = new ObjectMapper();
    private final @NotNull Vector minVector;
    private final @NotNull Vector maxVector;
    private final int delay;
    private final int period;
    private final int batchSize;

    public Structure(@NotNull CommandSender sender, @NotNull Vector vector1, @NotNull Vector vector2, int delay, int period, int batchSize) {
        this.sender = sender;
        this.minVector = Vector.getMinimum(vector1, vector2);
        this.maxVector = Vector.getMaximum(vector1, vector2);
        this.delay = Math.max(0, delay);
        this.period = Math.max(0, period);
        this.batchSize = Math.max(1, batchSize);
    }

    public int getXSize() {
        return maxVector.getBlockX() - minVector.getBlockX() + 1;
    }

    public int getYSize() {
        return maxVector.getBlockY() - minVector.getBlockY() + 1;
    }

    public int getZSize() {
        return maxVector.getBlockZ() - minVector.getBlockZ() + 1;
    }


    private class StructureSave {

        private final @NotNull JsonNode root;
        private final @NotNull ObjectNode rootObject;
        private final @NotNull File file;
        private final @NotNull Location originLocation;
        private final @NotNull World world;
        public StructureSave(@NotNull File file, @NotNull Location originLocation) {
            this.file = file;
            if(!file.getName().endsWith(".structra")) {
                throw new IllegalArgumentException("File name must end with '.structra'");
            }
            try {
                this.root = objectMaper.readTree(file);
            } catch (IOException e) {
                throw new RuntimeException("[Structra] Couldn't load JsonNode from given file. " + e);
            }
            this.rootObject = (ObjectNode) root;
            this.originLocation = originLocation.clone();
            this.world = originLocation.getWorld();
            if(world == null) {
                throw new RuntimeException("[Structra] World is null.");
            }
        }

        private long start;
        private long end;


        public void save() {
            start = System.currentTimeMillis();

            rootObject.put("Version", 1.0f);
            ObjectNode size = JsonHelper.getOrCreate(rootObject, "Size");
            size.put("x", getXSize());
            size.put("y", getYSize());
            size.put("z", getZSize());

            Location startLocation = minVector.toLocation(world);
            ObjectNode relative = JsonHelper.getOrCreate(rootObject, "Relative");
            relative.put("x", startLocation.getBlockX() - originLocation.getBlockX());
            relative.put("y", startLocation.getBlockY() - originLocation.getBlockY());
            relative.put("z", startLocation.getBlockZ() - originLocation.getBlockZ());

            writeBlocks(rootObject, originLocation, world);

        }


        private void writeBlocks(ObjectNode rootObject, @NotNull Location originLocation, @NotNull World world) {
            Location startLocation = minVector.toLocation(world);

            List<Vector> vectors = new ArrayList<>(getVectors());

            ObjectNode palette = JsonHelper.getOrCreate(rootObject, "Palette");
            ObjectNode blockData = JsonHelper.getOrCreate(rootObject, "BlockData");

            startRunnable(vectors, palette, blockData);
        }


        private Set<Vector> getVectors() {
            Set<Vector> vectors = new HashSet<>();
            for(int i = 0; i < getXSize(); i++) {
                for(int j = 0; j < getYSize(); j++) {
                    for(int k = 0; k < getZSize(); k++) {
                        Vector vec = new Vector(i, j, k);
                        vectors.add(minVector.clone().add(vec));
                    }
                }
            }
            return vectors;
        }


        private void startRunnable(@NotNull List<Vector> vectors, @NotNull ObjectNode palette, @NotNull ObjectNode blockData) {
            final List<Byte> blocksData = new ArrayList<>();
            final int size = vectors.size();
            new BukkitRunnable() {
                int looped = 0;
                byte nextId = 0;

                @Override
                public void run() {
                    // information
                    float ratio = (float) looped / size;
                    Util.log(String.format("Copying Structra to file... (%.1f)", ratio));

                    //
                    for(int i = 0; i < batchSize; i++) {
                        int index = i + looped;
                        if(index >= size) {
                            cancel();
                            saveToFile();
                            rootObject.set("BlockData", objectMaper.valueToTree(blocksData));
                            end = System.currentTimeMillis();
                            sender.sendMessage(String.format("[Structra] Saved '%d' to file '%s' in %d seconds", size, file.getName(), (end-start)));
                            return;
                        }

                        Location blockLocation = vectors.get(index).toLocation(world);
                        Block block = blockLocation.getBlock();

                        String data = block.getBlockData().getAsString();
                        byte id = parseByte(JsonHelper.getChildMatches(palette, data, "?"), (byte) -1);
                        if(id == -1) {
                            id = nextId++;
                            palette.put(String.valueOf(id), data);
                        }
                        blocksData.add(id);

                        BlockState state = block.getState();
                        getStateHandler(state).save(state, JsonHelper.getOrCreate(rootObject, "TileEntities"));
                    }

                    looped += batchSize;
                }
            }.runTaskTimer(Structra.getInstance(), delay, period);
        }

        private void saveToFile() {
            try {
                objectMaper.writerWithDefaultPrettyPrinter().writeValue(file, root);
            } catch (IOException e) {
                throw new RuntimeException(String.format("[Structra] Couldn't save to file '%s'", file.getName()), e);
            }
        }

        @NotNull
        private BlockStateHandler<BlockState> getStateHandler(@NotNull BlockState blockState) {
            BlockStateHandler<BlockState> stateHandler = BlockStateService.getHandler(blockState);
            if(stateHandler == null) {
                throw new RuntimeException(String.format("[Structra] Couldn't find BlockStateHandler for BlockState '%s'", blockState));
            }
            return stateHandler;
        }
    }


    private byte parseByte(@NotNull String str, byte def) {
        try {
            return Byte.parseByte(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
