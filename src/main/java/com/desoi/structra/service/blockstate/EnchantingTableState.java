package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.EnchantingTable;
import org.jetbrains.annotations.NotNull;

public class EnchantingTableState implements IStateHandler<EnchantingTable> {

    @Override
    public void save(@NotNull EnchantingTable blockState, @NotNull ObjectNode node) {
        NonState.saveNameable(blockState, node);
    }

    @Override
    public void loadTo(@NotNull EnchantingTable blockState, ObjectNode node) {
        NonState.loadToNameable(blockState, node);

        blockState.update();
    }
}
