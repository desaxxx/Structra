package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Location;
import org.bukkit.block.EndGateway;
import org.jetbrains.annotations.NotNull;

public class EndGatewayState implements IStateHandler<EndGateway> {

    @Override
    public void save(@NotNull EndGateway blockState, @NotNull ObjectNode node) {
        node.put("Age", blockState.getAge());
        node.put("ExactTeleport", blockState.isExactTeleport());

        Location exitLocation = blockState.getExitLocation();
        node.set("ExitLocation", objectMapper.valueToTree(exitLocation == null ? null : exitLocation.serialize()));
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull EndGateway blockState, ObjectNode node) {
        blockState.setAge(node.has("Age") ? node.get("Age").asInt() : 0);
        blockState.setExactTeleport(node.has("ExactTeleport") && node.get("ExactTeleport").asBoolean());

        if (node.has("ExitLocation")) {
            blockState.setExitLocation(JsonHelper.deserializeLocation(node.get("ExitLocation")));
        }
        loadToTileState(blockState, node);

        blockState.update(true, false);
    }
}
