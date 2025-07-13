package com.desoi.structra.service.blockstate;

import com.desoi.structra.Structra;
import com.desoi.structra.service.BlockStateHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.DyeColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.jetbrains.annotations.NotNull;

public class SignState implements BlockStateHandler<Sign> {

    @Override
    public void save(@NotNull Sign blockState, @NotNull ObjectNode node) {
        final int MINECRAFT_VERSION = Structra.getInstance().WRAPPER.getVersion();

        if (MINECRAFT_VERSION >= 200) {
            node.put("Waxed", blockState.isWaxed());
        }
        if (MINECRAFT_VERSION >= 194) {
            // TODO look up for deprecations
            for (Side side : Side.values()) {
                SignSide signSide = blockState.getSide(side);
                String sideName = side.toString();
                ObjectNode sideNode = node.objectNode();
                sideNode.put("Color", signSide.getColor() == null ? "" : signSide.getColor().toString());
                sideNode.put("Glowing", signSide.isGlowingText());
                ObjectNode linesNode = node.objectNode();
                for (int line = 0; line < blockState.getLines().length; line++) {
                    linesNode.put(String.valueOf(line), signSide.getLine(line));
                }
                sideNode.set("Lines", linesNode);
                node.set(sideName, sideNode);
            }
        } else {
            node.put("Color", blockState.getColor().toString());
            node.put("Editable", blockState.isEditable());
            ObjectNode linesNode = node.objectNode();
            for (int line = 0; line < blockState.getLines().length; line++) {
                linesNode.put(String.valueOf(line), blockState.getLine(line));
            }
            node.set("Lines", linesNode);
        }
    }

    @Override
    public void loadTo(@NotNull Sign blockState, ObjectNode node) {
        final int MINECRAFT_VERSION = Structra.getInstance().WRAPPER.getVersion();
        if (MINECRAFT_VERSION >= 200) {
            blockState.setWaxed(node.has("Waxed") && node.get("Waxed").asBoolean());
        }
        if (MINECRAFT_VERSION >= 194) {
            for (Side side: Side.values()) {
                String sideName = side.toString();
                if (!node.has(sideName) || !node.get(sideName).isObject()) continue;
                ObjectNode sideNode = node.objectNode();
                SignSide signSide = blockState.getSide(side);
                try {
                    signSide.setColor(DyeColor.valueOf(sideNode.has("Color") ? sideNode.get("Color").asText("") : ""));
                } catch (IllegalArgumentException ignored) {}
                signSide.setGlowingText(node.has("Glowing") && node.get("Glowing").asBoolean());
                if (sideNode.has("Lines") && sideNode.get("Lines").isObject()) {
                    ObjectNode linesNode = (ObjectNode) sideNode.get("Lines");
                    for (int i = 0; i < signSide.getLines().length; i++) {
                        signSide.setLine(i, linesNode.has(String.valueOf(i)) ? linesNode.get(String.valueOf(i)).asText(""): "");
                    }
                }
            }
        } else {
            try {
                blockState.setColor(DyeColor.valueOf(node.has("Color") ? node.get("Color").asText("") : ""));
            } catch (IllegalArgumentException ignored) {}
            blockState.setEditable(node.has("Editable") && node.get("Editable").asBoolean());
            if (node.has("Lines") && node.get("Lines").isObject()) {
                ObjectNode linesNode = (ObjectNode) node.get("Lines");
                for (int i = 0; i < blockState.getLines().length; i++) {
                    blockState.setLine(i, linesNode.has(String.valueOf(i)) ? linesNode.get(String.valueOf(i)).asText(""): "");
                }
            }
        }
        blockState.update();
    }
}
