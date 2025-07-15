package com.desoi.structra.model;

import com.desoi.structra.Structra;
import com.desoi.structra.Util;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.StateService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class StructureLoader {

    private final @NotNull CommandSender sender;
    private final @NotNull ObjectMapper objectMapper = new ObjectMapper();
    private final @NotNull File file;
    private final int delay;
    private final int period;
    private final int batchSize;

    public StructureLoader(@NotNull CommandSender sender, @NotNull File file, int delay, int period, int batchSize) {
        this.sender = sender;
        this.file = file;
        if(!file.getName().endsWith(Structra.FILE_EXTENSION)) {
            throw new StructraException("File name must end with '.structra'");
        }
        if(!file.exists()) {
            throw new StructraException("File does not exist");
        }
        this.delay = delay;
        this.period = period;
        this.batchSize = batchSize;
    }

    public void load(@NotNull Location originLocation) {
        new StructurePaste(originLocation).paste();
    }




    private class StructurePaste {

        private final @NotNull ObjectNode rootObject;
        private final @NotNull Location originLocation;
        private final @NotNull World world;

        public StructurePaste(@NotNull Location originLocation) {
            JsonNode root;
            try {
                root = objectMapper.readTree(file);
            } catch (IOException e) {
                throw new StructraException("There was a problem reading the file. " + e);
            }
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
        @Getter
        private int xSize, ySize, zSize;
        private Vector minVector;
        private Vector maxVector;

        public int getSize() {
            return xSize * ySize * zSize;
        }

        public void paste() {
            start = System.nanoTime();

            String version = rootObject.has("Version") ? rootObject.get("Version").asText() : null;
            if(version == null || !version.equals(Structra.getInstance().getDescription().getVersion())) {
                Util.tell(sender, "&cReminder: Version of the Structra doesn't match with used plugin.");
            }

            JsonNode sizeNode = rootObject.has("Size") ? rootObject.get("Size") : null;
            if(!(sizeNode instanceof ObjectNode size)) {
                throw new StructraException("There was a problem reading Size information.");
            }
            xSize = size.get("x").asInt();
            ySize = size.get("y").asInt();
            zSize = size.get("z").asInt();

            JsonNode relativeNode = rootObject.get("Relative");
            if(!(relativeNode instanceof ObjectNode relative)) {
                throw new StructraException("There was a problem reading Relative information.");
            }
            Vector relativeVector = new Vector(relative.get("x").asInt(), relative.get("y").asInt(), relative.get("z").asInt());
            minVector = relativeVector.clone().add(originLocation.toVector());
            maxVector = minVector.clone().add(new Vector(xSize, ySize, zSize));

            pasteBlocks();
        }


        private void pasteBlocks() {
            List<Vector> vectors = new ArrayList<>(getVectors());

            JsonNode palette = rootObject.has("Palette") ? rootObject.get("Palette") : null;
            if(palette == null || !palette.isObject()) {
                throw new StructraException("There was a problem reading Palette information.");
            }
            JsonNode blockData = rootObject.has("BlockData") ? rootObject.get("BlockData") : null;
            if(blockData == null || !blockData.isArray()) {
                throw new StructraException("There was a problem reading BlockData information.");
            }
            JsonNode tileEntities = rootObject.has("TileEntities") ? rootObject.get("TileEntities") : null;
            if(tileEntities == null || !tileEntities.isArray()) {
                throw new StructraException("There was a problem reading TileEntities information.");
            }

            startDataRunnable(vectors, (ObjectNode) palette, (ArrayNode) blockData, (ArrayNode) tileEntities);
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

        private void startDataRunnable(@NotNull List<Vector> vectors, @NotNull ObjectNode palette, @NotNull ArrayNode blockData, @NotNull ArrayNode tileEntities) {
            final int size = vectors.size();
            new BukkitRunnable() {
                int looped = 0;
                int index = 0;
                float ratio = 0f;

                @Override
                public void run() {
                    // information
                    ratio = (float) looped / (size + tileEntities.size());
                    Util.tell(sender, String.format("&ePasting Structra to world '%s'... (%.1f%%)", world.getName(), ratio*100));

                    //
                    for(int i = 0; i < batchSize; i++) {
                        index = i + looped;
                        if(index >= size) {
                            cancel();
                            startTileEntitiesRunnable(tileEntities);
                            return;
                        }

                        Location blockLocation = vectors.get(index).toLocation(world);
                        Block block = blockLocation.getBlock();

                        byte id = (byte) (blockData.has(index) ? blockData.get(index).shortValue() : -1);
                        if(id == -1) {
                            Util.tell(sender, String.format("There was an error reading BlockData for index '%s'", index));
                            continue;
                        }

                        String data = palette.has(String.valueOf(id)) ? palette.get(String.valueOf(id)).asText() : "";
                        try {
                            BlockData bData = Bukkit.createBlockData(data);
                            block.setType(bData.getMaterial(), false); // false -> no physics
                            block.setBlockData(bData);
                        } catch (IllegalArgumentException e) {
                            Util.tell(sender, String.format("There was an error reading BlockData for index '%s'", index));
                            block.setType(Material.AIR, false);
                        }
                    }

                    looped += batchSize;
                }
            }.runTaskTimer(Structra.getInstance(), delay, period);
        }


        private void startTileEntitiesRunnable(@NotNull ArrayNode tileEntities) {
            final int size = tileEntities.size();
            new BukkitRunnable() {
                int looped = 0;
                int index = 0;
                float ratio = 0f;

                @Override
                public void run() {
                    // information
                    ratio = (float) (looped + getSize()) / (getSize() + size);
                    Util.tell(sender, String.format("&ePasting Structra to world '%s'... (%.1f%%)", world.getName(), ratio*100));

                    //
                    for(int i = 0; i < batchSize; i++) {
                        index = i + looped;
                        if(index >= size) {
                            cancel();
                            ratio = 1.0f;
                            long elapsedMS = (System.nanoTime() - start) / 1_000_000;
                            Util.tell(sender, String.format("&ePasting Structra to world '%s'... (%.1f%%)", world.getName(), ratio*100));
                            Util.tell(sender, String.format("[Structra] &aPasted '%d blocks' to world '%s' in %d ms", getSize(), world.getName(), elapsedMS));
                            return;
                        }

                        JsonNode tileEntityNode = tileEntities.get(index);
                        if(tileEntityNode == null || !tileEntityNode.isObject()) {
                            Util.log(String.format("There was a problem reading TileEntity with index '%d'", index));
                            continue;
                        }
                        ObjectNode tileEntity = (ObjectNode) tileEntityNode;
                        Vector offSet = getOffset(tileEntity);
                        if(offSet == null) {
                            Util.log(String.format("There was a problem reading Offset of TileEntity with index '%d'", index));
                            continue;
                        }

                        Block block = minVector.clone().add(offSet).toLocation(world).getBlock();
                        BlockState blockState = block.getState();
                        IStateHandler<BlockState> handler = StateService.getHandler(blockState);
                        if(handler != null) {
                            handler.loadTo(blockState, tileEntity);
                        }
                    }

                    looped += batchSize;
                }
            }.runTaskTimer(Structra.getInstance(), delay, period);
        }



        @Nullable
        private Vector getOffset(@NotNull JsonNode parentNode) {
            JsonNode offsetNode = parentNode.get("Offset");
            if(offsetNode == null || !offsetNode.isObject()) return null;
            JsonNode xNode = offsetNode.get("x");
            JsonNode yNode = offsetNode.get("y");
            JsonNode zNode = offsetNode.get("z");
            if(xNode == null || yNode == null || zNode == null) return null;
            return new Vector(xNode.asInt(), yNode.asInt(), zNode.asInt());
        }
    }
}
