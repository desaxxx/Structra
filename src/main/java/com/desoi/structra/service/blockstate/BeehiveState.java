package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.desoi.structra.service.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Location;
import org.bukkit.block.Beehive;
import org.jetbrains.annotations.NotNull;

public class BeehiveState implements BlockStateHandler<Beehive> {

    @Override
    public void save(@NotNull Beehive blockState, @NotNull ObjectNode node) {
        if(blockState.getFlower() != null) {
            node.set("Location", objectMapper.valueToTree(blockState.getFlower().serialize()));
        }
        NonState.saveEntityBlockStorage(blockState, JsonHelper.getOrCreate(node, "EntityBlockStorage"));
    }

    @Override
    public void loadTo(@NotNull Beehive blockState, ObjectNode node) {
        if(node.has("Location")) {
            blockState.setFlower(JsonHelper.treeToValue(node, Location.class));
        }
        NonState.loadToEntityBlockStorage(blockState, JsonHelper.getOrCreate(node, "EntityBlockStorage"));
        blockState.update();
    }
}
