package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
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
     * 1.20.1 -> added Sherd methods and Side enum
     * 1.20.4 -> implements BlockInventoryHolder and Lootable additionally
     */
    @Override
    public int minSupportedVersion() {
        return 2000;
    }

    @Override
    public void save(@NotNull DecoratedPot blockState, @NotNull ObjectNode node) {
        if(Wrapper.getInstance().getVersion() >= 12001) {
            ObjectNode sherdsNode = JsonHelper.getOrCreate(node, "Sherds");
            for(Map.Entry<DecoratedPot.Side, Material> entry : blockState.getSherds().entrySet()) {
                sherdsNode.put(entry.getKey().name(), entry.getValue().name());
            }
        }
        if (Wrapper.getInstance().getVersion() >= 12004) {
            NonState.saveInventory(blockState.getSnapshotInventory(), JsonHelper.getOrCreate(node, "inventory"));
            NonState.saveLootable(blockState, JsonHelper.getOrCreate(node, "Lootable"));
        }

        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull DecoratedPot blockState, ObjectNode node) {
        if(Wrapper.getInstance().getVersion() >= 12001 && node.get("Sherds") instanceof ObjectNode sherdsNode) {
            Iterator<String> sideKeys = sherdsNode.fieldNames();
            while(sideKeys.hasNext()) {
                String sideString = sideKeys.next();
                String materialString = sherdsNode.get(sideString).asText();

                DecoratedPot.Side side = null;
                try {
                    side = DecoratedPot.Side.valueOf(sideString);
                } catch (IllegalArgumentException ignored) {}
                Material material = null;
                try {
                    material = Material.valueOf(materialString);
                } catch (IllegalArgumentException ignored) {}

                if(side != null && material != null) {
                    blockState.setSherd(side, material);
                }
            }
        }
        if (Wrapper.getInstance().getVersion() >= 12004) {
            NonState.loadToInventory(blockState.getInventory(), JsonHelper.getOrCreate(node, "inventory"));
            NonState.loadToLootable(blockState, JsonHelper.getOrCreate(node, "Lootable"));
        }

        loadToTileState(blockState, node);

        blockState.update(true, false);
    }
}
