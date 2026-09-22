package com.desoi.structra_dev.file;

import com.desoi.structra_dev.write.StructureWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import org.joml.Vector3i;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public class StructureFileBuilder {

    private Vector3i size = new Vector3i();
    private Vector3i relative = new Vector3i();
    private BiMap<String, Short> palette = HashBiMap.create();
    private ShortArrayList blockData = new ShortArrayList();
    private Map<String, ObjectNode> tileEntities = new HashMap<>();
    private Map<String, List<ObjectNode>> entities = new HashMap<>();

    private short nextPaletteId = 0;

    public StructureFileBuilder() {
    }

    public StructureFile build() {
        return new StructureFile(StructureWriter.VERSION, size, relative, palette, blockData, tileEntities, entities);
    }

    public StructureFileBuilder size(Vector3i size) {
        this.size = Preconditions.checkNotNull(size, "size");
        return this;
    }

    public StructureFileBuilder relative(Vector3i relative) {
        this.relative = Preconditions.checkNotNull(relative, "relative");
        return this;
    }

    public StructureFileBuilder putPalette(String data, short id) {
        Preconditions.checkNotNull(data, "data");
        palette.put(data, id);
        return this;
    }

    public StructureFileBuilder palette(BiMap<String, Short> palette) {
        this.palette = Preconditions.checkNotNull(palette, "palette");
        return this;
    }

    public boolean hasPalette(String data) {
        return palette.containsKey(data);
    }

    public boolean hasPalette(short index) {
        return palette.containsValue(index);
    }

    public short getOrCreatePalette(String data) {
        Short id = palette.get(data);

        if (id != null) {
            return id;
        }

        short newId = nextPaletteId++;
        palette.put(data, newId);
        return newId;
    }

    public StructureFileBuilder addBlockData(short index) {
        blockData.add(index);
        return this;
    }

    public StructureFileBuilder blockData(ShortArrayList blockData) {
        this.blockData = Preconditions.checkNotNull(blockData, "blockData");
        return this;
    }

    public StructureFileBuilder addTileEntity(String positionKey, ObjectNode tileEntity) {
        Preconditions.checkNotNull(positionKey, "positionKey");
        Preconditions.checkNotNull(tileEntity, "tileEntity");
        tileEntities.put(positionKey, tileEntity);
        return this;
    }

    public StructureFileBuilder tileEntities(Map<String, ObjectNode> tileEntities) {
        this.tileEntities = Preconditions.checkNotNull(tileEntities, "tileEntities");
        return this;
    }

    public StructureFileBuilder addEntity(String positionKey, List<ObjectNode> entityList) {
        Preconditions.checkNotNull(positionKey, "positionKey");
        Preconditions.checkNotNull(entityList, "entityList");
        entities.put(positionKey, entityList);
        return this;
    }

    public StructureFileBuilder entities(Map<String, List<ObjectNode>> entityList) {
        this.entities = Preconditions.checkNotNull(entityList, "entityList");
        return this;
    }
}
