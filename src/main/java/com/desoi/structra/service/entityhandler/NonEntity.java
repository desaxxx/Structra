package com.desoi.structra.service.entityhandler;

import com.desoi.structra.util.Wrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class NonEntity {

    @SuppressWarnings("deprecation")
    public static void save(@NotNull Entity entity, @NotNull ObjectNode node) {
        node.put("Gravity", entity.hasGravity());
        node.put("Invulnerable", entity.isInvulnerable());

        String customName = entity.getCustomName();
        if (customName != null) node.put("CustomName", customName);

        node.put("CustomNameVisible", entity.isCustomNameVisible());
        node.put("Glowing", entity.isGlowing());
        node.put("Silent", entity.isSilent());
        node.put("Persistent", entity.isPersistent());

        if(Wrapper.getInstance().getVersion() >= 12004) {
            node.put("Invisible", entity.isInvisible());
        }
    }

    @SuppressWarnings("deprecation")
    public static void load(@NotNull Entity entity, @NotNull ObjectNode node) {
        if (node.has("Gravity")) entity.setGravity(node.get("Gravity").asBoolean());
        if (node.has("Invulnerable")) entity.setInvulnerable(node.get("Invulnerable").asBoolean());

        if (node.get("CustomName") instanceof TextNode customNameNode) {
            entity.setCustomName(customNameNode.asText());
        }

        if (node.has("CustomNameVisible")) entity.setCustomNameVisible(node.get("CustomNameVisible").asBoolean());
        if (node.has("Glowing")) entity.setGlowing(node.get("Glowing").asBoolean());
        if (node.has("Silent")) entity.setSilent(node.get("Silent").asBoolean());
        if (node.has("Persistent")) entity.setPersistent(node.get("Persistent").asBoolean());

        if (Wrapper.getInstance().getVersion() >= 12004 && node.has("Invisible")) {
            entity.setInvisible(node.get("Invisible").asBoolean());
        }
    }
}
