package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class CreatureSpawnerState implements IStateHandler<CreatureSpawner> {

    @Override
    public void save(@NotNull CreatureSpawner blockState, @NotNull ObjectNode node) {
        node.put("Delay", blockState.getDelay());
        node.put("MinSpawnDelay", blockState.getMinSpawnDelay());
        node.put("MaxSpawnDelay", blockState.getMaxSpawnDelay());
        node.put("SpawnCount", blockState.getSpawnCount());
        node.put("RequiredPlayerRange", blockState.getRequiredPlayerRange());
        node.put("SpawnedType", blockState.getSpawnedType()  == null ? "" : blockState.getSpawnedType().name()); // toString() -> name()
        node.put("SpawnRange", blockState.getSpawnRange());
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull CreatureSpawner blockState, ObjectNode node) {
        blockState.setDelay(node.has("Delay") ? node.get("Delay").asInt() : 0);
        blockState.setMinSpawnDelay(node.has("MinSpawnDelay") ? node.get("MinSpawnDelay").asInt() : 0);
        blockState.setMaxSpawnDelay(node.has("MaxSpawnDelay") ? node.get("MaxSpawnDelay").asInt() : 1); // must be greater than 0
        blockState.setSpawnCount(node.has("SpawnCount") ? node.get("SpawnCount").asInt() : 1);
        blockState.setRequiredPlayerRange(node.has("RequiredPlayerRange") ? node.get("RequiredPlayerRange").asInt() : 16);

        if(node.get("SpawnedType") instanceof TextNode spawnedTypeNode) {
            try {
                blockState.setSpawnedType(EntityType.valueOf(spawnedTypeNode.asText()));
            }catch (IllegalArgumentException ignored) {}
        }

        blockState.setSpawnRange(node.has("SpawnRange") ? node.get("SpawnRange").asInt() : 4); // IDK why 4 for necessary
        loadToTileState(blockState, node);

        blockState.update(true, false);
    }
}
