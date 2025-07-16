package com.desoi.structra.model;

import com.desoi.structra.Structra;
import com.desoi.structra.util.Util;
import com.desoi.structra.service.statehandler.StateService;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import java.util.List;

@Getter
public class StructureWriter {

    private final @NotNull CommandSender sender;
    private final @NotNull ObjectMapper objectMapper = new ObjectMapper();
    private final @NotNull Vector minVector;
    private final @NotNull Vector maxVector;
    private final int delay;
    private final int period;
    private final int batchSize;

    public StructureWriter(@NotNull CommandSender sender, @NotNull Vector vector1, @NotNull Vector vector2, int delay, int period, int batchSize) {
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

    public int getSize() {
        return getXSize() * getYSize() * getZSize();
    }

    public void save(@NotNull File file, @NotNull Location originLocation) {
        if(file.getParentFile() != null && !file.getParentFile().exists()) //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
        new StructureSave(file, originLocation).save();
    }


    private class StructureSave {

        private final @NotNull JsonNode root;
        private final @NotNull ObjectNode rootObject;
        private final @NotNull File file;
        private final @NotNull Location originLocation;
        private final @NotNull World world;
        public StructureSave(@NotNull File file, @NotNull Location originLocation) {
            this.file = file;
            if(!file.getName().endsWith(Structra.FILE_EXTENSION)) {
                throw new StructraException("File name must end with '.structra'");
            }
            this.root = objectMapper.createObjectNode();
            if(!root.isObject()) {
                throw new StructraException("Root object must be an object");
            }
            this.rootObject = (ObjectNode) root;
            this.originLocation = originLocation.getBlock().getLocation().clone();
            this.world = originLocation.getWorld();
            if(world == null) {
                throw new StructraException("Origin world is null.");
            }
        }

        private long start;


        public void save() {
            start = System.nanoTime();

            rootObject.put("Version", Structra.getInstance().getDescription().getVersion());
            ObjectNode size = JsonHelper.getOrCreate(rootObject, "Size");
            size.put("x", getXSize());
            size.put("y", getYSize());
            size.put("z", getZSize());
            size.put("Total", getSize());

            setRelativeNode();

            writeBlocks();

        }

        // Relative = minLocation - originLocation
        private void setRelativeNode() {
            Location startLocation = minVector.toLocation(world);
            ObjectNode relative = JsonHelper.getOrCreate(rootObject, "Relative");
            relative.put("x", startLocation.getBlockX() - originLocation.getBlockX());
            relative.put("y", startLocation.getBlockY() - originLocation.getBlockY());
            relative.put("z", startLocation.getBlockZ() - originLocation.getBlockZ());
        }


        private void writeBlocks() {
            List<Vector> vectors = new ArrayList<>(getVectors());

            ObjectNode palette = JsonHelper.getOrCreate(rootObject, "Palette");
            ArrayNode blockData = JsonHelper.getOrCreateArray(rootObject, "BlockData");
            ObjectNode tileEntities = JsonHelper.getOrCreate(rootObject, "TileEntities");

            startRunnable(vectors, palette, blockData, tileEntities);
        }


        private List<Vector> getVectors() {
            List<Vector> vectors = new ArrayList<>();
            for(int y = 0; y < getYSize(); y++) {
                for(int z = 0; z < getZSize(); z++) {
                    for(int x = 0; x < getXSize(); x++) {
                        Vector vec = new Vector(x, y, z);
                        vectors.add(minVector.clone().add(vec));
                    }
                }
            }
            return vectors;
        }


        private void startRunnable(@NotNull List<Vector> vectors, @NotNull ObjectNode palette, @NotNull ArrayNode blockData, @NotNull ObjectNode tileEntities) {
            final int size = vectors.size();
            new BukkitRunnable() {
                int looped = 0;
                byte nextId = 0;
                int index = 0;

                @Override
                public void run() {
                    // information
                    float ratio = (float) looped / size;
                    Util.tell(sender, String.format("&eCopying Structra to file... (%.1f%%)", ratio*100));

                    //
                    for(int i = 0; i < batchSize; i++) {
                        index = i + looped;
                        if(index >= size) {
                            cancel();
                            saveToFile();
                            ratio = 1.0f;
                            long elapsedMS = (System.nanoTime() - start) / 1_000_000;
                            Util.tell(sender, String.format("&eCopying Structra to file... (%.1f%%)", ratio*100));
                            Util.tell(sender, String.format("&aSaved '%d blocks' to file '%s' in %d ms", size, file.getName(), elapsedMS));
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
                        blockData.add(id);

                        BlockState state = block.getState();
                        IStateHandler<BlockState> handler = StateService.getHandler(state);
                        ObjectNode tileEntity = objectMapper.createObjectNode();
                        if(handler != null) {
                            tileEntity.put("Type", handler.name());
                            //tileEntity.set("Offset", getOffsetNode(blockLocation, tileEntity));
                            handler.save(state, tileEntity);
                            tileEntities.set(String.valueOf(index), tileEntity);
                        }
                    }

                    looped += batchSize;
                }
            }.runTaskTimer(Structra.getInstance(), delay, period);
        }

        // Offset = blockLocation - minLocation
//        private ObjectNode getOffsetNode(@NotNull Location blockLocation, @NotNull ObjectNode tileEntity) {
//            ObjectNode offset = JsonHelper.getOrCreate(tileEntity, "Offset");
//            Location startLocation = minVector.toLocation(world);
//            offset.put("x", blockLocation.getBlockX() - startLocation.getBlockX());
//            offset.put("y", blockLocation.getBlockY() - startLocation.getBlockY());
//            offset.put("z", blockLocation.getBlockZ() - startLocation.getBlockZ());
//            return offset;
//        }

        private void saveToFile() {
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
            } catch (IOException e) {
                throw new StructraException(String.format("Couldn't save to file '%s'", file.getName()) + e);
            }
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
