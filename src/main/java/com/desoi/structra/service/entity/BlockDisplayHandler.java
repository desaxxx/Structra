package com.desoi.structra.service.entity;

import com.desoi.structra.service.entityhandler.NonEntity;
import com.desoi.structra.service.entityhandler.IEntityHandler;
import com.desoi.structra.util.Wrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BlockDisplayHandler implements IEntityHandler<BlockDisplay> {

    @Override
    public int minSupportedVersion() {
        return 11904;
    }

    @Override
    public void save(@NotNull BlockDisplay entity, @NotNull ObjectNode node) {
        node.put("Block", entity.getBlock().getAsString());

        Transformation t = entity.getTransformation();
        putVector3f(node.putObject("Translation"), t.getTranslation());
        putQuaternionf(node.putObject("LeftRotation"), t.getLeftRotation());
        putVector3f(node.putObject("Scale"), t.getScale());
        putQuaternionf(node.putObject("RightRotation"), t.getRightRotation());

        node.put("InterpolationDuration", entity.getInterpolationDuration());
        if(Wrapper.getInstance().getVersion() >= 12002) {
            node.put("TeleportDuration", entity.getTeleportDuration());
        }
        node.put("ViewRange", entity.getViewRange());
        node.put("ShadowRadius", entity.getShadowRadius());
        node.put("ShadowStrength", entity.getShadowStrength());
        node.put("DisplayWidth", entity.getDisplayWidth());
        node.put("DisplayHeight", entity.getDisplayHeight());
        node.put("InterpolationDelay", entity.getInterpolationDelay());
        node.put("Billboard", entity.getBillboard().name());

        Color glow = entity.getGlowColorOverride();
        if (glow != null) node.put("GlowColorOverride", glow.asARGB());

        Display.Brightness brightness = entity.getBrightness();
        if (brightness != null) {
            ObjectNode brightnessNode = node.putObject("Brightness");
            brightnessNode.put("BlockLight", brightness.getBlockLight());
            brightnessNode.put("SkyLight", brightness.getSkyLight());
        }

        NonEntity.save(entity, node);
    }

    @Override
    public void spawnAndLoad(@NotNull Location location, @NotNull ObjectNode node) {
        BlockDisplay display = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);

        if (node.has("Block")) {
            try {
                BlockData data = Bukkit.createBlockData(node.get("Block").asText());
                display.setBlock(data);
            } catch (IllegalArgumentException ignored) {
            }
        }

        Vector3f translation = readVector3f(node.get("Translation"), new Vector3f(0,0,0));
        Quaternionf leftRotation = readQuaternionf(node.get("LeftRotation"));
        Vector3f scale = readVector3f(node.get("Scale"), new Vector3f(1,1,1));
        Quaternionf rightRotation = readQuaternionf(node.get("RightRotation"));
        display.setTransformation(new Transformation(translation, leftRotation, scale, rightRotation));

        if (node.has("InterpolationDuration")) display.setInterpolationDuration(node.get("InterpolationDuration").asInt());
        if (Wrapper.getInstance().getVersion() >= 12002 && node.has("TeleportDuration")) {
            display.setTeleportDuration(node.get("TeleportDuration").asInt());
        }
        if (node.has("InterpolationDelay")) display.setInterpolationDelay(node.get("InterpolationDelay").asInt());
        if (node.has("ViewRange")) display.setViewRange((float) node.get("ViewRange").asDouble());
        if (node.has("ShadowRadius")) display.setShadowRadius((float) node.get("ShadowRadius").asDouble());
        if (node.has("ShadowStrength")) display.setShadowStrength((float) node.get("ShadowStrength").asDouble());
        if (node.has("DisplayWidth")) display.setDisplayWidth((float) node.get("DisplayWidth").asDouble());
        if (node.has("DisplayHeight")) display.setDisplayHeight((float) node.get("DisplayHeight").asDouble());
        if (node.has("Billboard")) {
            try {
                display.setBillboard(Display.Billboard.valueOf(node.get("Billboard").asText()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (node.has("GlowColorOverride")) display.setGlowColorOverride(Color.fromARGB(node.get("GlowColorOverride").asInt()));

        if (node.get("Brightness") instanceof ObjectNode brightnessNode) {
            display.setBrightness(new Display.Brightness(brightnessNode.get("BlockLight").asInt(), brightnessNode.get("SkyLight").asInt()));
        }

        NonEntity.load(display, node);
    }

    private void putVector3f(ObjectNode node, Vector3f v) {
        node.put("x", v.x);
        node.put("y", v.y);
        node.put("z", v.z);
    }

    private void putQuaternionf(ObjectNode node, Quaternionf q) {
        node.put("x", q.x());
        node.put("y", q.y());
        node.put("z", q.z());
        node.put("w", q.w());
    }

    private Vector3f readVector3f(JsonNode node, Vector3f def) {
        if (!(node instanceof ObjectNode objNode)) return def;
        return new Vector3f(
                (float) objNode.get("x").asDouble(),
                (float) objNode.get("y").asDouble(),
                (float) objNode.get("z").asDouble()
        );
    }

    private Quaternionf readQuaternionf(JsonNode node) {
        if (!(node instanceof ObjectNode objNode)) return new Quaternionf();
        return new Quaternionf(
                (float) objNode.get("x").asDouble(),
                (float) objNode.get("y").asDouble(),
                (float) objNode.get("z").asDouble(),
                (float) objNode.get("w").asDouble(1.0)
        );
    }
}
