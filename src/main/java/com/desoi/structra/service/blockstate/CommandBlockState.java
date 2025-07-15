package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.CommandBlock;
import org.jetbrains.annotations.NotNull;

public class CommandBlockState implements IStateHandler<CommandBlock> {

    @Override
    public void save(@NotNull CommandBlock blockState, @NotNull ObjectNode node) {
        //noinspection deprecation
        node.put("Name", blockState.getName());
        node.put("Command", blockState.getCommand());
    }

    @Override
    public void loadTo(@NotNull CommandBlock blockState, ObjectNode node) {
        //noinspection deprecation
        blockState.setName(node.has("Name") ? node.get("Name").asText() : null);
        blockState.setCommand(node.has("Command") ? node.get("Command").asText() : null);

        blockState.update();
    }
}
