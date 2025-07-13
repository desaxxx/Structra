package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.BlockStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Location;
import org.bukkit.block.EndGateway;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EndGatewayState implements BlockStateHandler<EndGateway> {

    @Override
    public void save(@NotNull EndGateway blockState, @NotNull ObjectNode node) {
        node.put("Age", blockState.getAge());
        node.put("ExactTeleport", blockState.isExactTeleport());

        Location exitLocation = blockState.getExitLocation();
        if (exitLocation != null) {
            ObjectNode exitNode = node.objectNode();
            for (Map.Entry<String, Object> entry : exitLocation.serialize().entrySet()) {
                exitNode.putPOJO(entry.getKey(), entry.getValue());
            }
            node.set("ExitLocation", exitNode);
        } else {
            node.putNull("ExitLocation");
        }
    }

    @Override
    public void loadTo(@NotNull EndGateway blockState, ObjectNode node) {
        blockState.setAge(node.has("Age") ? node.get("Age").asInt() : 0);
        blockState.setExactTeleport(node.has("ExactTeleport") && node.get("ExactTeleport").asBoolean());

        Location exitLocation = null;
        if (node.has("ExitLocation") && node.get("ExitLocation").isObject()) {
            ObjectNode exitNode = (ObjectNode) node.get("ExitLocation");
            exitLocation = Location.deserialize(JsonHelper.exitNodeToMap(exitNode));
        }
        blockState.setExitLocation(exitLocation);

        blockState.update();
    }
}
