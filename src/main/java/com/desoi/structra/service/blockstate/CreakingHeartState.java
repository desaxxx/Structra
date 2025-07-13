package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.CreakingHeart;
import org.jetbrains.annotations.NotNull;

public class CreakingHeartState implements IStateHandler<CreakingHeart> {

    @Override
    public int minSupportedVersion() {
        return 214;
    }

    @Override
    public void save(@NotNull CreakingHeart blockState, @NotNull ObjectNode node) {
    }

    @Override
    public void loadTo(@NotNull CreakingHeart blockState, ObjectNode node) {
    }
}
