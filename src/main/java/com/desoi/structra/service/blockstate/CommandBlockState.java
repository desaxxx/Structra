package com.desoi.structra.service.blockstate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.CommandBlock;
import org.jetbrains.annotations.NotNull;

public class CommandBlockState implements BlockStateHandler<CommandBlock> {

    @Override
    public void save(@NotNull CommandBlock blockState, @NotNull ObjectNode node) {
        node.put("Name", miniMessage.serializeOr(blockState.name(), ""));
        node.put("Command", blockState.getCommand());
    }

    @Override
    public void loadTo(@NotNull CommandBlock blockState, ObjectNode node) {
        blockState.name(miniMessage.deserialize(node.has("Name") ? node.get("Name").asText() : ""));
        blockState.setCommand(node.has("Command") ? node.get("Command").asText() : "");

        blockState.update();
    }
}
