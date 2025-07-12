package com.desoi.structra.service.blockstate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class CreatureSpawnerState implements BlockStateHandler<CreatureSpawner> {

    @Override
    public void save(@NotNull CreatureSpawner blockState, @NotNull ObjectNode node) {
        node.put("Delay", blockState.getDelay());
        node.put("MinSpawnDelay", blockState.getMinSpawnDelay());
        node.put("MaxSpawnDelay", blockState.getMaxSpawnDelay());
        node.put("SpawnCount", blockState.getSpawnCount());
        node.put("RequiredPlayerRange", blockState.getRequiredPlayerRange());
        node.put("SpawnedType", blockState.getSpawnedType()  == null ? "" : blockState.getSpawnedType().toString());
        node.put("SpawnRange", blockState.getSpawnRange());
    }

    @Override
    public void loadTo(@NotNull CreatureSpawner blockState, ObjectNode node) {
        blockState.setDelay(node.has("Delay") ? node.get("Delay").asInt() : 0);
        blockState.setMinSpawnDelay(node.has("MinSpawnDelay") ? node.get("MinSpawnDelay").asInt() : 0);
        blockState.setMaxSpawnDelay(node.has("MaxSpawnDelay") ? node.get("MaxSpawnDelay").asInt() : 0);
        blockState.setSpawnCount(node.has("SpawnCount") ? node.get("SpawnCount").asInt() : 1);
        blockState.setRequiredPlayerRange(node.has("RequiredPlayerRange") ? node.get("RequiredPlayerRange").asInt() : 16);
        String spawnedType = node.has("SpawnedType") ? node.get("SpawnedType").asText() : "";
        if (!spawnedType.isEmpty()) {
            try {
                blockState.setSpawnedType(EntityType.valueOf(spawnedType.toUpperCase()));
            } catch (Exception ignored) {}
        }

        blockState.setSpawnRange(node.has("SpawnRange") ? node.get("SpawnRange").asInt() : 4);

        blockState.update();
    }
}
