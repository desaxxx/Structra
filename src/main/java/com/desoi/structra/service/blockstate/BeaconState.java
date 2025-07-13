package com.desoi.structra.service.blockstate;

import com.desoi.structra.Structra;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Beacon;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class BeaconState implements IStateHandler<Beacon> {

    @Override
    public void save(@NotNull Beacon blockState, @NotNull ObjectNode node) {
        final int MINECRAFT_VERSION = Structra.getInstance().WRAPPER.getVersion();
        PotionEffect primaryEffect = blockState.getPrimaryEffect();
        if (primaryEffect != null) {
            NonState.savePotionEffectType(primaryEffect, JsonHelper.getOrCreate(node, "PrimaryEffect"), MINECRAFT_VERSION);
        }
        PotionEffect secondaryEffect = blockState.getSecondaryEffect();
        if (secondaryEffect != null) {
            NonState.savePotionEffectType(secondaryEffect, JsonHelper.getOrCreate(node, "SecondaryEffect"), MINECRAFT_VERSION);
        }
        node.put("CustomName", miniMessage.serializeOrNull(blockState.customName()));
    }

    @Override
    public void loadTo(@NotNull Beacon blockState, ObjectNode node) {
        final int MINECRAFT_VERSION = Structra.getInstance().WRAPPER.getVersion();
        ObjectNode primaryNode = node.has("PrimaryEffect") && node.get("PrimaryEffect").isObject() ? (ObjectNode) node.get("PrimaryEffect") : null;
        PotionEffectType primaryEffect = NonState.getPotionEffectType(primaryNode, MINECRAFT_VERSION);
        if (primaryEffect != null) {
            blockState.setPrimaryEffect(primaryEffect);
        }
        ObjectNode secondaryNode = node.has("SecondaryEffect") && node.get("SecondaryEFfect").isObject() ? (ObjectNode) node.get("SecondaryEffect") : null;
        PotionEffectType secondaryEffect = NonState.getPotionEffectType(secondaryNode, MINECRAFT_VERSION);
        if (secondaryEffect != null) {
            blockState.setSecondaryEffect(secondaryEffect);
        }
        blockState.customName(node.has("CustomName") ? miniMessage.deserialize(node.get("CustomName").asText("")) : null);

        blockState.update();
    }
}
