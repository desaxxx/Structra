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
        NonState.saveNameable(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Beacon blockState, ObjectNode node) {
        if(node.get("PrimaryEffect") instanceof ObjectNode primaryNode) {
            PotionEffectType primaryEffect = NonState.getPotionEffectType(primaryNode);
            if(primaryEffect != null) blockState.setPrimaryEffect(primaryEffect);
        }
        if(node.get("SecondaryEffect") instanceof ObjectNode secondaryNode) {
            PotionEffectType secondaryEffect = NonState.getPotionEffectType(secondaryNode);
            if(secondaryEffect != null) blockState.setSecondaryEffect(secondaryEffect);
        }

        NonState.loadToNameable(blockState, node);

        blockState.update();
    }
}
