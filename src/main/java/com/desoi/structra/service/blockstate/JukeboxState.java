package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.jetbrains.annotations.NotNull;

public class JukeboxState implements IStateHandler<Jukebox> {

    @Override
    public void save(@NotNull Jukebox blockState, @NotNull ObjectNode node) {
        node.put("Playing", blockState.getPlaying().toString());
        if(!blockState.getRecord().isEmpty()) {
            node.put("Record", JsonHelper.serializeItemStack(blockState.getRecord()));
        }
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Jukebox blockState, @NotNull ObjectNode node) {
        if(node.get("Playing") instanceof TextNode playingNode) {
            blockState.setPlaying(Material.getMaterial(playingNode.asText("")));
        }
        if (node.get("Record") instanceof ObjectNode recordNode) {
            blockState.setRecord(JsonHelper.deserializeItemStack(recordNode));
        }
        loadToTileState(blockState, node);
        blockState.update(true, false);
    }
}
