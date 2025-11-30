package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Wrapper;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.jetbrains.annotations.NotNull;

public class SkullState implements IStateHandler<Skull> {

    @Override
    public void save(@NotNull Skull blockState, @NotNull ObjectNode node) {
        final int MINECRAFT_VERSION = Wrapper.getInstance().getVersion();
        if(MINECRAFT_VERSION >= 1903) {
            node.put("NoteBlockSound", blockState.getNoteBlockSound() == null ? "" : blockState.getNoteBlockSound().toString());
        }
        if(MINECRAFT_VERSION >= 1801) {
            if(blockState.getPlayerProfile() != null) {
                node.set("PlayerProfile", objectMapper.valueToTree(blockState.getPlayerProfile()));
            }
        }
        if(blockState.getOwningPlayer() != null) {
            node.set("OwningPlayer", objectMapper.valueToTree(blockState.getOwningPlayer()));
        }
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Skull blockState, @NotNull ObjectNode node) {
        final int MINECRAFT_VERSION = Wrapper.getInstance().getVersion();
        if(MINECRAFT_VERSION >= 1903 && node.get("NoteBlockSound") instanceof TextNode noteBlockSoundNode) {
            NamespacedKey key = NamespacedKey.fromString(noteBlockSoundNode.asText(""));
            if(key != null) {
                blockState.setNoteBlockSound(key);
            }
        }

        if(MINECRAFT_VERSION >= 1801) {
            if(node.has("PlayerProfile")) {
                blockState.setPlayerProfile(JsonHelper.treeToValue(node.get("PlayerProfile"), PlayerProfile.class));
            }
        }
        if(node.has("OwningPlayer")) {
            blockState.setOwningPlayer(JsonHelper.treeToValue(node.get("OwningPlayer"), OfflinePlayer.class));
        }
        loadToTileState(blockState, node);
        blockState.update(true, false);
    }
}