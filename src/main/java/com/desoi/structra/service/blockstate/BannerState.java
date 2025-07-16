package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BannerState implements IStateHandler<Banner> {

    @Override
    public void save(@NotNull Banner blockState, @NotNull ObjectNode node) {
        node.put("BaseColor", blockState.getBaseColor().toString());
        node.set("Patterns", objectMapper.valueToTree(blockState.getPatterns()));
        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Banner blockState, @NotNull ObjectNode node) {
        if(node.get("BaseColor") instanceof TextNode baseColorNode) {
            try {
                blockState.setBaseColor(DyeColor.valueOf(baseColorNode.asText()));
            } catch (IllegalArgumentException ignored) {}
        }

        List<Pattern> patterns = new ArrayList<>();
        if (node.get("Patterns") instanceof ArrayNode patternsNode) {
            for (JsonNode patternNode : patternsNode) {
                Pattern pattern = objectMapper.convertValue(patternNode, Pattern.class);
                patterns.add(pattern);
            }
        }
        blockState.setPatterns(patterns);
        loadToTileState(blockState, node);

        blockState.update();
    }
}
