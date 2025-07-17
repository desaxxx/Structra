package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Bukkit;
import org.bukkit.block.Conduit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ConduitState implements IStateHandler<Conduit> {

    @Override
    public void save(@NotNull Conduit blockState, @NotNull ObjectNode node) {
        LivingEntity target = blockState.getTarget();
        if (target != null) {
            node.put("TargetUUID", target.getUniqueId().toString());
        }

        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Conduit blockState, ObjectNode node) {

        if(node.has("TargetUUID")) {
            String uuidStr = node.get("TargetUUID").asText();
            try {
                UUID uuid = UUID.fromString(uuidStr);
                Entity entity = null;

                if (blockState.getLocation().getWorld() != null) {
                    entity = blockState.getLocation().getWorld().getEntity(uuid);
                }

                if (entity == null) {
                    entity = Bukkit.getEntity(uuid);
                }

                if (entity instanceof LivingEntity living) {
                    blockState.setTarget(living);
                } else {
                    blockState.setTarget(null);
                }
            } catch (IllegalArgumentException e) {
                blockState.setTarget(null);
            }

        }

        loadToTileState(blockState, node);
        blockState.update();
    }
}
