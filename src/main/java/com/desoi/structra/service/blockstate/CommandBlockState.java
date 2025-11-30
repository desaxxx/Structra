package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.block.CommandBlock;
import org.jetbrains.annotations.NotNull;

public class CommandBlockState implements IStateHandler<CommandBlock> {

    @Override
    public void save(@NotNull CommandBlock blockState, @NotNull ObjectNode node) {
        //noinspection deprecation
        node.put("Name", blockState.getName());
        node.put("Command", blockState.getCommand());
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull CommandBlock blockState, ObjectNode node) {
        if(node.get("Name") instanceof TextNode nameNode) {
            //noinspection deprecation
            blockState.setName(nameNode.asText());
        }
        if(node.get("Command") instanceof TextNode commandNode) {
            blockState.setCommand(commandNode.asText());
        }
        loadToTileState(blockState, node);

        blockState.update(true, false);
    }
}
