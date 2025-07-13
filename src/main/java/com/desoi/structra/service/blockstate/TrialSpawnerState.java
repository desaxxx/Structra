package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.TrialSpawner;
import org.jetbrains.annotations.NotNull;

public class TrialSpawnerState implements BlockStateHandler<TrialSpawner> {

    @Override
    public void save(@NotNull TrialSpawner blockState, @NotNull ObjectNode node) {
        node.put("Ominous", blockState.isOminous());
        node.put("CooldownLength", blockState.getCooldownLength());
        node.put("RequiredPlayerRange", blockState.getRequiredPlayerRange());
    }

    @Override
    public void loadTo(@NotNull TrialSpawner blockState, ObjectNode node) {
        blockState.setOminous(node.has("Ominous") && node.get("Ominous").asBoolean());
        blockState.setCooldownLength(node.has("CooldownLength") ? node.get("CooldownLength").asInt() : 0);
        blockState.setRequiredPlayerRange(node.has("RequiredPlayerRange") ? node.get("RequiredPlayerRange").asInt() : 0);
        blockState.update();
    }
}
