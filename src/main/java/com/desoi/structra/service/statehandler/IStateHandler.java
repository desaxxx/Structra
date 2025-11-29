package com.desoi.structra.service.statehandler;

import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Wrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.jetbrains.annotations.NotNull;

public interface IStateHandler<B extends BlockState> {

    default int minSupportedVersion() {
        return 1605;
    }

    default boolean isSupported() {
        return Wrapper.getInstance().getVersion() >= minSupportedVersion();
    }

    //
    ObjectMapper objectMapper = new ObjectMapper();

    @NotNull
    default String name() {
        return this.getClass().getSimpleName();
    }

    void save(@NotNull B blockState, @NotNull ObjectNode node);
    void loadTo(@NotNull B blockState, ObjectNode node);


    default <T extends TileState> void saveTileState(@NotNull T tileState, @NotNull ObjectNode node) {
        NonState.saveTileState(tileState, JsonHelper.getOrCreate(node, "TileState"));
    }
    default <T extends TileState> void loadToTileState(@NotNull T tileState, ObjectNode node) {
        if(node.get("TileState") instanceof ObjectNode tileStateNode) {
            NonState.saveTileState(tileState, tileStateNode);
        }
    }
}
