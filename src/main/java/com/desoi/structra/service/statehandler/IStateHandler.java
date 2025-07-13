package com.desoi.structra.service.statehandler;

import com.desoi.structra.Structra;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IStateHandler<B extends BlockState> {

    default int minSupportedVersion() {
        return 165;
    }

    @Nullable
    default IStateHandler<B> getIfSupported() {
        return Structra.getInstance().WRAPPER.getVersion() >= minSupportedVersion() ? this : null;
    }

    //
    MiniMessage miniMessage = MiniMessage.miniMessage();
    ObjectMapper objectMapper = new ObjectMapper();

    @NotNull
    default String name() {
        return this.getClass().getSimpleName();
    }

    void save(@NotNull B blockState, @NotNull ObjectNode node);
    void loadTo(@NotNull B blockState, ObjectNode node);
}
