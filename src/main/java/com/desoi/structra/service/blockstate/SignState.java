package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Wrapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.DyeColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.jetbrains.annotations.NotNull;

public class SignState implements IStateHandler<Sign> {

    @Override
    public void save(@NotNull Sign blockState, @NotNull ObjectNode node) {
        final int MINECRAFT_VERSION = Wrapper.getInstance().getVersion();

        if (MINECRAFT_VERSION >= 200) {
            node.put("Waxed", blockState.isWaxed());
        }
        if (MINECRAFT_VERSION >= 194) {
            for (Side side : Side.values()) {
                SignSide signSide = blockState.getSide(side);
                ObjectNode sideNode = JsonHelper.getOrCreate(node, side.name());

                sideNode.put("Color", signSide.getColor() == null ? "" : signSide.getColor().toString());
                sideNode.put("Glowing", signSide.isGlowingText());
                ObjectNode linesNode = JsonHelper.getOrCreate(sideNode, "Lines");
                for (int line = 0; line < signSide.getLines().length; line++) {
                    linesNode.put(String.valueOf(line), signSide.getLine(line));
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

//        if (blockState.getAllowedEditorUniqueId() != null) {
//            node.put("AllowedEditorUniqueId", blockState.getAllowedEditorUniqueId().toString());
//        }

        saveTileState(blockState, node);
    }

    @Override
    public void loadTo(@NotNull Sign blockState, ObjectNode node) {
        final int MINECRAFT_VERSION = Wrapper.getInstance().getVersion();
        if (MINECRAFT_VERSION >= 200 && node.get("Waxed") instanceof BooleanNode waxedNode) {
            blockState.setWaxed(waxedNode.asBoolean());
        }

        if (MINECRAFT_VERSION >= 194) {
            for (Side side: Side.values()) {
                if (!(node.get(side.name()) instanceof ObjectNode sideNode)) continue;
                SignSide signSide = blockState.getSide(side);

                if(sideNode.get("Color") instanceof TextNode colorNode) {
                    try {
                        signSide.setColor(DyeColor.valueOf(colorNode.asText()));
                    } catch (IllegalArgumentException ignored) {}
                }

                if(sideNode.get("Glowing") instanceof BooleanNode glowingNode) {
                    signSide.setGlowingText(glowingNode.asBoolean());
                }
                if (sideNode.get("Lines") instanceof ObjectNode linesNode) {
                    for (int i = 0; i < signSide.getLines().length; i++) {
                        if(linesNode.get(String.valueOf(i)) instanceof TextNode lineNode) {
                            signSide.setLine(i, lineNode.asText());
                        }
                    }
                }
            }
        }
        else {
            if(node.get("Color") instanceof TextNode colorNode) {
                try {
                    blockState.setColor(DyeColor.valueOf(colorNode.asText()));
                }catch (IllegalArgumentException ignored) {}
            }

            if(node.get("Editable") instanceof BooleanNode editableNode) {
                blockState.setEditable(editableNode.asBoolean());
            }

            if (node.get("Lines") instanceof ObjectNode linesNode) {
                for (int i = 0; i < blockState.getLines().length; i++) {
                    if(linesNode.get(String.valueOf(i)) instanceof TextNode lineNode) {
                        blockState.setLine(i, lineNode.asText());
                    }
                }
            }
        }

//        if (node.get("AllowedEditorUniqueId") instanceof TextNode allowedEditorUniqueIdNode) {
//            blockState.setAllowedEditorUniqueId(UUID.fromString(allowedEditorUniqueIdNode.asText()));
//        }

        loadToTileState(blockState, node);

        blockState.update();
    }
}
