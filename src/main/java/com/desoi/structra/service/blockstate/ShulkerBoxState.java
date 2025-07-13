package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.ShulkerBox;
import org.jetbrains.annotations.NotNull;

public class ShulkerBoxState implements IStateHandler<ShulkerBox> {

    @Override
    public void save(@NotNull ShulkerBox blockState, @NotNull ObjectNode node) {
        NonState.saveLootable(blockState, JsonHelper.getOrCreate(node,"Lootable"));
        NonState.saveContainer(blockState, JsonHelper.getOrCreate(node,"Container"));
    }

    @Override
    public void loadTo(@NotNull ShulkerBox blockState, @NotNull ObjectNode node) {
        NonState.loadToLootable(blockState, JsonHelper.getOrCreate(node,"Lootable"));
        NonState.loadToContainer(blockState, JsonHelper.getOrCreate(node,"Container"));
        blockState.update();
    }
}
