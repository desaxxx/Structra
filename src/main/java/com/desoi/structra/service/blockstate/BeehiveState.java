package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Beehive;
import org.jetbrains.annotations.NotNull;

public class BeehiveState implements IStateHandler<Beehive> {

    @Override
    public void save(@NotNull Beehive blockState, @NotNull ObjectNode node) {
        if(blockState.getFlower() != null) {
            node.set("Location", objectMapper.valueToTree(blockState.getFlower().serialize()));
        }
        NonState.saveEntityBlockStorage(blockState, JsonHelper.getOrCreate(node, "EntityBlockStorage"));
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Beehive blockState, ObjectNode node) {
        if(node.has("Location")) {
            blockState.setFlower(JsonHelper.deserializeLocation(node.get("Location")));
        }
        NonState.loadToEntityBlockStorage(blockState, JsonHelper.getOrCreate(node, "EntityBlockStorage"));
        loadToTileState(blockState, node);
        blockState.update();
    }
}
