package com.desoi.structra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public interface BlockStateHandler<T extends BlockState> { // somebody watching uuuuu yeeee

    MiniMessage miniMessage = MiniMessage.miniMessage();
    ObjectMapper objectMapper = new ObjectMapper();

    default int minSupportedVersion() {
        return 165;
    }

    @NotNull
    default String name() {
        return this.getClass().getSimpleName();
    }

    void save(@NotNull T blockState, @NotNull ObjectNode node);
    void loadTo(@NotNull T blockState, ObjectNode node);
}
