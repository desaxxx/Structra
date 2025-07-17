package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.TrialSpawner;
import org.jetbrains.annotations.NotNull;

public class TrialSpawnerState implements IStateHandler<TrialSpawner> {

    @Override
    public void save(@NotNull TrialSpawner blockState, @NotNull ObjectNode node) {
        node.put("Ominous", blockState.isOminous());
        node.put("CooldownLength", blockState.getCooldownLength());
        node.put("RequiredPlayerRange", blockState.getRequiredPlayerRange());
        node.put("CooldownEnd", blockState.getCooldownEnd());
        node.put("NextSpawnAttempt", blockState.getNextSpawnAttempt());
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull TrialSpawner blockState, ObjectNode node) {
        blockState.setOminous(node.has("Ominous") && node.get("Ominous").asBoolean());

        if (node.get("CooldownLength") instanceof IntNode cooldownLengthNode) {
            blockState.setCooldownLength(cooldownLengthNode.asInt());
        }

        if (node.get("RequiredPlayerRange") instanceof IntNode requiredPlayerRangeNode) {
            blockState.setRequiredPlayerRange(requiredPlayerRangeNode.asInt());
        }

        if (node.get("CooldownEnd") instanceof LongNode cooldownEndNode) {
            blockState.setCooldownEnd(cooldownEndNode.asLong());
        }

        if (node.get("NextSpawnAttempt") instanceof LongNode nextSpawnAttemptNode) {
            blockState.setNextSpawnAttempt(nextSpawnAttemptNode.asLong());
        }

        loadToTileState(blockState, node);
        blockState.update();
    }
}
