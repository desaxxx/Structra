package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.EnchantingTable;
import org.jetbrains.annotations.NotNull;

public class EnchantingTableState implements BlockStateHandler<EnchantingTable> {

    @Override
    public void save(@NotNull EnchantingTable blockState, @NotNull ObjectNode node) {
        node.put("CustomName", blockState.customName() != null ? miniMessage.serializeOrNull(blockState.customName()) : "");
    }

    @Override
    public void loadTo(@NotNull EnchantingTable blockState, ObjectNode node) {
        String customNameStr = node.has("CustomName") ? node.get("CustomName").asText() : "";
        blockState.customName(customNameStr.isEmpty() ? null : miniMessage.deserialize(customNameStr));

        blockState.update();
    }
}
