package com.desoi.structra.service.blockstate;

import com.desoi.structra.Structra;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.Beacon;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class BeaconState implements BlockStateHandler<Beacon> {

    @Override
    public void save(@NotNull Beacon blockState, @NotNull ObjectNode node) {
        final int MINECRAFT_VERSION = Structra.getInstance().WRAPPER.getVersion();
        PotionEffect primaryEffect = blockState.getPrimaryEffect();
        if (primaryEffect != null) {
            ObjectNode primaryNode = JsonNodeFactory.instance.objectNode();
            NonState.savePotionEffectType(primaryEffect, primaryNode, MINECRAFT_VERSION);
            node.set("PrimaryEffect", primaryNode);
        }
        PotionEffect secondaryEffect = blockState.getSecondaryEffect();
        if (secondaryEffect != null) {
            ObjectNode secondaryNode = JsonNodeFactory.instance.objectNode();
            NonState.savePotionEffectType(secondaryEffect, secondaryNode, MINECRAFT_VERSION);
            node.set("SecondaryEffect", secondaryNode);
        }
        node.put("CustomName", blockState.getCustomName());
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
        blockState.setCustomName(node.has("CustomName") && !node.get("CustomName").isNull() ? node.get("CustomName").asText() : null);

        blockState.update();
    }
}
