package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.jetbrains.annotations.NotNull;

public class JukeboxState implements BlockStateHandler<Jukebox> {

    @Override
    public void save(@NotNull Jukebox blockState, @NotNull ObjectNode node) {
        node.put("Playing", blockState.getPlaying().toString());
        node.set("Record", objectMapper.valueToTree(blockState.getRecord().serialize()));
    }

    @Override
    public void loadTo(@NotNull Jukebox blockState, @NotNull ObjectNode node) {
        if(node.has("Playing")) {
            blockState.setPlaying(Material.getMaterial(node.get("Playing").asText("")));
        }
        if (node.has("Record") && node.get("Record") instanceof ObjectNode recordNode) {
            blockState.setRecord(JsonHelper.deserializeItemStack(recordNode));
        }
        blockState.update();
    }
}
