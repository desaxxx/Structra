package com.desoi.structra.service.blockstate;

import com.desoi.structra.Structra;
import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.DyeColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.jetbrains.annotations.NotNull;

public class SignState implements IStateHandler<Sign> {

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
                String sideName = side.name();
                ObjectNode sideNode = JsonHelper.getOrCreate(node, sideName);

                sideNode.put("Color", signSide.getColor() == null ? "" : signSide.getColor().toString());
                sideNode.put("Glowing", signSide.isGlowingText());
                ObjectNode linesNode = JsonHelper.getOrCreate(sideNode, "Lines");
                for (int line = 0; line < blockState.getLines().length; line++) {
                    linesNode.put(String.valueOf(line), blockState.getLine(line));
                }
            }
        } else {
            node.put("Color", blockState.getColor().toString());
            node.put("Editable", blockState.isEditable());
            ObjectNode linesNode = JsonHelper.getOrCreate(node, "Lines");
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
                if (!(node.get(side.name()) instanceof ObjectNode sideNode)) continue;

                SignSide signSide = blockState.getSide(side);

                if(sideNode.get("Color") instanceof ObjectNode colorNode) {
                    try {
                        signSide.setColor(DyeColor.valueOf(colorNode.asText()));
                    } catch (IllegalArgumentException ignored) {}
                }

                if(sideNode.get("Glowing") instanceof BooleanNode glowingNode) {
                    signSide.setGlowingText(glowingNode.asBoolean());
                }
                if (sideNode.get("Lines") instanceof ObjectNode linesNode) {
                    for (int i = 0; i < signSide.getLines().length; i++) {
                        if(linesNode.get(String.valueOf(i)) instanceof ObjectNode lineNode) {
                            signSide.setLine(i, lineNode.asText());
                        }
                    }
                }
            }
        } else {
            if(node.get("Color") instanceof ObjectNode colorNode) {
                try {
                    blockState.setColor(DyeColor.valueOf(colorNode.asText()));
                }catch (IllegalArgumentException ignored) {}
            }

            if(node.get("Editable") instanceof BooleanNode editableNode) {
                blockState.setEditable(editableNode.asBoolean());
            }

            if (node.get("Lines") instanceof ObjectNode linesNode) {
                for (int i = 0; i < blockState.getLines().length; i++) {
                    if(linesNode.get(String.valueOf(i)) instanceof ObjectNode lineNode) {
                        blockState.setLine(i, lineNode.asText());
                    }
                }
            }
        }

        blockState.update();
    }
}
