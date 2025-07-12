package com.desoi.structra.service.blockstate;

import com.desoi.structra.Structra;
import com.desoi.structra.util.JsonHelper;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.jetbrains.annotations.NotNull;

public class SkullState implements BlockStateHandler<Skull> {

    @Override
    public void save(@NotNull Skull blockState, @NotNull ObjectNode node) {
        final int MINECRAFT_VERSION = Structra.getInstance().WRAPPER.getVersion();
        if(MINECRAFT_VERSION >= 193) {
            node.put("NoteBlockSound", blockState.getNoteBlockSound() == null ? null : blockState.getNoteBlockSound().toString());
        }
        if(MINECRAFT_VERSION >= 181) {
            if(blockState.getPlayerProfile() != null) {
                node.set("PlayerProfile", objectMapper.valueToTree(blockState.getPlayerProfile()));
            }
        }
        if(blockState.getOwningPlayer() != null) {
            node.set("OwningPlayer", objectMapper.valueToTree(blockState.getOwningPlayer()));
        }
    }

    @Override
    public void loadTo(@NotNull Skull blockState, @NotNull ObjectNode node) {
        final int MINECRAFT_VERSION = Structra.getInstance().WRAPPER.getVersion();
        if(MINECRAFT_VERSION >= 193) {
            NamespacedKey key = node.has("NoteNlockSound") ? NamespacedKey.fromString(node.get("NoteBlockSound").asText("")) : null;
            if(key != null) {
                blockState.setNoteBlockSound(key);
            }
        }
        if(MINECRAFT_VERSION >= 181) {
            if(node.has("PlayerProfile")) {
                blockState.setPlayerProfile(JsonHelper.treeToValue(node.get("PlayerProfile"), PlayerProfile.class));
            }
        }
        if(node.has("OwningPlayer")) {
            blockState.setOwningPlayer(JsonHelper.treeToValue(node.get("OwningPlayer"), OfflinePlayer.class));
        }
        blockState.update();
    }
}