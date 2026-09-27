package com.desoi.structra.service.blockstate;

import com.desoi.structra.Structra;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.service.statehandler.NonState;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Wrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FurnaceState implements IStateHandler<Furnace> {

    @Override
    public void save(@NotNull Furnace blockState, @NotNull ObjectNode node) {
        node.put("BurnTime", blockState.getBurnTime());
        node.put("CookTime", blockState.getCookTime());
        node.put("CookTimeTotal", blockState.getCookTimeTotal());
        node.put("CookSpeedMultiplier", blockState.getCookSpeedMultiplier());

        if (Wrapper.getInstance().getVersion() >= 11801) {
            ObjectNode recipesUsedNode = JsonHelper.getOrCreate(node, "RecipesUsed");
            for (Map.Entry<CookingRecipe<?>, Integer> entry : blockState.getRecipesUsed().entrySet()) {
                String recipeKey = entry.getKey().getKey().toString();
                recipesUsedNode.put(recipeKey, entry.getValue());
            }
        }

        NonState.saveNameable(blockState, node);
        NonState.saveInventory(blockState.getInventory(), JsonHelper.getOrCreate(node, "Inventory"));
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Furnace blockState, @NotNull ObjectNode node) {
        blockState.setBurnTime(node.has("BurnTime") ? node.get("BurnTime").shortValue() : 0);
        blockState.setCookTime(node.has("CookTime") ? node.get("CookTime").shortValue() : 0);
        blockState.setCookTimeTotal(node.has("CookTimeTotal") ? node.get("CookTimeTotal").shortValue() : 0);
        blockState.setCookSpeedMultiplier(node.has("CookSpeedMultiplier") ? node.get("CookSpeedMultiplier").shortValue() : 0);

        if (Wrapper.getInstance().getVersion() >= 11801 && node.get("RecipesUsed") instanceof ObjectNode recipesUsedNode) {
            Map<CookingRecipe<?>, Integer> recipesUsed = new HashMap<>();

            Iterator<String> recipeKeys = recipesUsedNode.fieldNames();
            while (recipeKeys.hasNext()) {
                String recipeString = recipeKeys.next();
                int count = recipesUsedNode.get(recipeString).intValue();

                NamespacedKey recipeKey;
                try {
                    recipeKey = NamespacedKey.fromString(recipeString);
                } catch (IllegalArgumentException e) { continue; }
                if (recipeKey == null) continue;

                Recipe r = Bukkit.getRecipe(recipeKey);
                if (!(r instanceof CookingRecipe<?> recipe)) continue;

                recipesUsed.put(recipe, count);
            }

            blockState.setRecipesUsed(recipesUsed);
        }

        NonState.loadToNameable(blockState, node);
        loadToTileState(blockState, node);

        blockState.update(true, false);

        // Live object
        if(node.get("Inventory") instanceof ObjectNode inventoryNode) {
            NonState.loadToInventory(blockState.getInventory(), inventoryNode);
        }
    }
}