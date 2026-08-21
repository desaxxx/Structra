package com.desoi.structra.service.entity;

import com.desoi.structra.service.entityhandler.IEntityHandler;
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
import org.jspecify.annotations.NonNull;

public class BlockDisplayHandler implements IEntityHandler<BlockDisplay> {

    @SuppressWarnings("deprecation")
    @Override
    public void save(@NonNull BlockDisplay entity, @NotNull ObjectNode node) {
        node.put("Block", entity.getBlock().getAsString());

        var t = entity.getTransformation();
        putVector3f(node.putObject("Translation"), t.getTranslation());
        putQuaternionf(node.putObject("LeftRotation"), t.getLeftRotation());
        putVector3f(node.putObject("Scale"), t.getScale());
        putQuaternionf(node.putObject("RightRotation"), t.getRightRotation());

        node.put("InterpolationDelay", entity.getInterpolationDelay());
        node.put("InterpolationDuration", entity.getInterpolationDuration());
        node.put("TeleportDuration", entity.getTeleportDuration());

        node.put("ViewRange", entity.getViewRange());
        node.put("ShadowRadius", entity.getShadowRadius());
        node.put("ShadowStrength", entity.getShadowStrength());
        node.put("DisplayWidth", entity.getDisplayWidth());
        node.put("DisplayHeight", entity.getDisplayHeight());

        node.put("Billboard", entity.getBillboard().name());

        Display.Brightness brightness = entity.getBrightness();
        if (brightness != null) {
            ObjectNode brightnessNode = node.putObject("Brightness");
            brightnessNode.put("Block", brightness.getBlockLight());
            brightnessNode.put("Sky", brightness.getSkyLight());
        }

        Color glow = entity.getGlowColorOverride();
        if (glow != null) node.put("GlowColorOverride", glow.asARGB());

        node.put("Glowing", entity.isGlowing());
        node.put("Invulnerable", entity.isInvulnerable());
        node.put("Invisible", entity.isInvisible());
        node.put("Gravity", entity.hasGravity());
        node.put("Silent", entity.isSilent());
        node.put("PersistenceRequired", entity.isPersistent());

        if (entity.getCustomName() != null) {
            node.put("CustomName", entity.getCustomName());
            node.put("CustomNameVisible", entity.isCustomNameVisible());
        }
    }

    @Override
    public void spawnAndLoad(Location location, ObjectNode node) {
        BlockDisplay display = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);

        if (node.has("Block")) {
            BlockData data = Bukkit.createBlockData(node.get("Block").asText());
            display.setBlock(data);
        }

        Vector3f translation = readVector3f(node.get("Translation"), new Vector3f(0,0,0));
        Quaternionf leftRotation = readQuaternionf(node.get("LeftRotation"));
        Vector3f scale = readVector3f(node.get("Scale"), new Vector3f(1,1,1));
        Quaternionf rightRotation = readQuaternionf(node.get("RightRotation"));
        display.setTransformation(new Transformation(translation, leftRotation, scale, rightRotation));

        if (node.has("InterpolationDelay")) display.setInterpolationDelay(node.get("InterpolationDelay").asInt());
        if (node.has("InterpolationDuration")) display.setInterpolationDuration(node.get("InterpolationDuration").asInt());
        if (node.has("TeleportDuration")) display.setTeleportDuration(node.get("TeleportDuration").asInt());

        if (node.has("ViewRange")) display.setViewRange((float) node.get("ViewRange").asDouble());
        if (node.has("ShadowRadius")) display.setShadowRadius((float) node.get("ShadowRadius").asDouble());
        if (node.has("ShadowStrength")) display.setShadowStrength((float) node.get("ShadowStrength").asDouble());
        if (node.has("DisplayWidth")) display.setDisplayWidth((float) node.get("DisplayWidth").asDouble());
        if (node.has("DisplayHeight")) display.setDisplayHeight((float) node.get("DisplayHeight").asDouble());

        if (node.has("Billboard")) display.setBillboard(Display.Billboard.valueOf(node.get("Billboard").asText()));

        if (node.get("Brightness") instanceof ObjectNode brightnessNode) {
            display.setBrightness(new Display.Brightness(brightnessNode.get("Block").asInt(), brightnessNode.get("Sky").asInt()));
        }

        if (node.has("GlowColorOverride")) display.setGlowColorOverride(Color.fromARGB(node.get("GlowColorOverride").asInt()));

        if (node.has("Glowing")) display.setGlowing(node.get("Glowing").asBoolean());
        if (node.has("Invulnerable")) display.setInvulnerable(node.get("Invulnerable").asBoolean());
        if (node.has("Invisible")) display.setInvisible(node.get("Invisible").asBoolean());
        if (node.has("Gravity")) display.setGravity(node.get("Gravity").asBoolean());
        if (node.has("Silent")) display.setSilent(node.get("Silent").asBoolean());
        if (node.has("PersistenceRequired")) display.setPersistent(node.get("PersistenceRequired").asBoolean());

        if (node.has("CustomName")) {
            display.setCustomName(node.get("CustomName").asText());
            if (node.has("CustomNameVisible")) display.setCustomNameVisible(node.get("CustomNameVisible").asBoolean());
        }
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
                (float) objNode.get("w").asDouble()
        );
    }
}
