package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Wrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.jetbrains.annotations.NotNull;

public class JukeboxState implements IStateHandler<Jukebox> {

    @Override
    public void save(@NotNull Jukebox blockState, @NotNull ObjectNode node) {
        node.put("Playing", blockState.getPlaying().toString());
        if(!blockState.getRecord().getType().isAir()) {
            node.put("Record", JsonHelper.serializeItemStack(blockState.getRecord()));
        }

        if (Wrapper.getInstance().getVersion() >= 11904) {
            NonState.saveInventory(blockState.getSnapshotInventory(), JsonHelper.getOrCreate(node, "Inventory"));
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

        if (Wrapper.getInstance().getVersion() >= 11904 && node.get("Inventory") instanceof ObjectNode inventoryNode) {
            NonState.loadToInventory(blockState.getInventory(), inventoryNode);
        }

        loadToTileState(blockState, node);
        blockState.update(true, false);
    }
}
