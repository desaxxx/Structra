package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

        ObjectMapper mapper = new ObjectMapper();
        node.set("Patterns", mapper.valueToTree(blockState.getPatterns()));
    }

    @Override
    public void loadTo(@NotNull Banner blockState, @NotNull ObjectNode node) {
        String baseColor = node.has("BaseColor") ? node.get("BaseColor").asText() : "WHITE";
        blockState.setBaseColor(DyeColor.valueOf(baseColor));

        List<Pattern> patterns = new ArrayList<>();
        JsonNode patternsNode = node.get("Patterns");
        if (patternsNode != null && patternsNode.isArray()) {
            ObjectMapper mapper = new ObjectMapper();
            for (JsonNode patternNode : patternsNode) {
                Pattern pattern = mapper.convertValue(patternNode, Pattern.class);
                patterns.add(pattern);
            }
        }
        blockState.setPatterns(patterns);

        blockState.update();
    }
}
