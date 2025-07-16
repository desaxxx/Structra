package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Wrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Material;
import org.bukkit.block.DecoratedPot;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;


public class DecoratedPotState implements IStateHandler<DecoratedPot> {

    /**
     * later of 1.20 -> added Sherd methods and Side enum
     * 1.20.4 -> implemented BlockInventoryHolder
     */
    @Override
    public int minSupportedVersion() {
        return 200;
    }

    @Override
    public void save(@NotNull DecoratedPot blockState, @NotNull ObjectNode node) {
        /*
         * DecoratedPot had only getShards() method, rest of the methods was added on Paper 1.20.1
         */
        if(Wrapper.getInstance().getVersion() >= 201) {
            ObjectNode sherdsNode = JsonHelper.getOrCreate(node, "Sherds");
            for(Map.Entry<DecoratedPot.Side, Material> entry : blockState.getSherds().entrySet()) {
                sherdsNode.put(entry.getKey().name(), entry.getValue().name());
            }
        }
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull DecoratedPot blockState, ObjectNode node) {
        if(Wrapper.getInstance().getVersion() >= 201 && node.get("Sherds") instanceof ObjectNode sherdsNode) {
            Iterator<String> sideKeys = sherdsNode.fieldNames();
            while(sideKeys.hasNext()) {
                String sideString = sideKeys.next();

                DecoratedPot.Side side = null;
                try {
                    side = DecoratedPot.Side.valueOf(sideString);
                } catch (IllegalArgumentException ignored) {}
                Material material = null;
                try {
                    material = Material.valueOf(sideString);
                } catch (IllegalArgumentException ignored) {}

                if(side != null && material != null) {
                    blockState.setSherd(side, material);
                }
            }
        }
        loadToTileState(blockState, node);

        blockState.update();
    }
}
