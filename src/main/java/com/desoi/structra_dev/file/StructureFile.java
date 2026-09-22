package com.desoi.structra_dev.file;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.jspecify.annotations.NullMarked;

import java.util.*;

/**
 * @see StructureFileReader
 */
@NullMarked
public class StructureFile { // immutable

    private final String version;
    private final Vector3i size;
    private final Vector3i relative;
    private final BiMap<String, Short> palette;
    private final short[] blockData;
    private final Map<String, ObjectNode> tileEntities;
    private final Map<String, List<ObjectNode>> entities;

    StructureFile(String version, Vector3i size, Vector3i relative, BiMap<String, Short> palette, ShortArrayList blockData, Map<String, ObjectNode> tileEntities, Map<String, List<ObjectNode>> entities) {
        this.version = Preconditions.checkNotNull(version, "version");
        this.size = new Vector3i(Preconditions.checkNotNull(size, "size"));
        this.relative = new Vector3i(Preconditions.checkNotNull(relative, "relative"));

        this.palette = ImmutableBiMap.copyOf(Preconditions.checkNotNull(palette, "palette"));
        this.blockData = blockData.toShortArray();
        this.tileEntities = ImmutableMap.copyOf(Preconditions.checkNotNull(tileEntities, "tileEntities"));


        Preconditions.checkNotNull(entities, "entities");
        ImmutableMap.Builder<String, List<ObjectNode>> builder = ImmutableMap.builder();

        for (Map.Entry<String, List<ObjectNode>> entry : entities.entrySet()) {
            builder.put(entry.getKey(), List.copyOf(entry.getValue()));
        }

        this.entities = builder.build();
    }

    public ObjectNode assemble() {
        return StructureFileAssembler.assemble(this);
    }

    public short getBlockData(int index) {
        return blockData[index];
    }

    public int getBlockDataLength() {
        return blockData.length;
    }

    public String getVersion() {
        return version;
    }

    public Vector3ic getSize() {
        return size;
    }

    public Vector3ic getRelative() {
        return relative;
    }

    public BiMap<String, Short> getPalette() {
        return palette;
    }

    public BiMap<Short, String> getPaletteInverse() {
        return palette.inverse();
    }

    public Map<String, ObjectNode> getTileEntities() {
        return tileEntities;
    }

    public Map<String, List<ObjectNode>> getEntities() {
        return entities;
    }
}
