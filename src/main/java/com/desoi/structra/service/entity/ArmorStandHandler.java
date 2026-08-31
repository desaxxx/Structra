package com.desoi.structra.service.entity;

import com.desoi.structra.service.entityhandler.NonEntity;
import com.desoi.structra.service.entityhandler.IEntityHandler;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ArmorStandHandler implements IEntityHandler<ArmorStand> {

    @SuppressWarnings("deprecation")
    @Override
    public void save(@NotNull ArmorStand entity, @NotNull ObjectNode node) {
        putEulerAngle(node.putObject("BodyPose"),       entity.getBodyPose());
        putEulerAngle(node.putObject("LeftArmPose"),    entity.getLeftArmPose());
        putEulerAngle(node.putObject("RightArmPose"),   entity.getRightArmPose());
        putEulerAngle(node.putObject("LeftLegPose"),    entity.getLeftLegPose());
        putEulerAngle(node.putObject("RightLegPose"),   entity.getRightLegPose());
        putEulerAngle(node.putObject("HeadPose"),       entity.getHeadPose());

        node.put("BasePlate",   entity.hasBasePlate());
        node.put("Visible",     entity.isVisible());
        node.put("Arms",        entity.hasArms());
        node.put("Small",       entity.isSmall());
        node.put("Marker",      entity.isMarker());

        putEquipmentLockTypes(entity, node);

        node.put("CanMove", entity.canMove());

        putEquipment(entity, node);

        node.put("CanTick", entity.canTick());

        // LivingEntity
        node.put("Gliding", entity.isGliding());
        node.put("Riptiding", entity.isRiptiding());
        node.put("AI", entity.hasAI());
        node.put("Collidable", entity.isCollidable());

        NonEntity.save(entity, node);
    }

    @Override
    public void spawnAndLoad(@NotNull Location location, @NotNull ObjectNode node) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        if (node.get("BodyPose") instanceof ObjectNode bodyPoseNode)            armorStand.setBodyPose(readEulerAngle(bodyPoseNode));
        if (node.get("LeftArmPose") instanceof ObjectNode leftArmPoseNode)      armorStand.setLeftArmPose(readEulerAngle(leftArmPoseNode));
        if (node.get("RightArmPose") instanceof ObjectNode rightArmPoseNode)    armorStand.setRightArmPose(readEulerAngle(rightArmPoseNode));
        if (node.get("LeftLegPose") instanceof ObjectNode leftLegPoseNode)      armorStand.setLeftLegPose(readEulerAngle(leftLegPoseNode));
        if (node.get("RightLegPose") instanceof ObjectNode rightLegPoseNode)    armorStand.setRightLegPose(readEulerAngle(rightLegPoseNode));
        if (node.get("HeadPose") instanceof ObjectNode headPoseNode)            armorStand.setHeadPose(readEulerAngle(headPoseNode));

        if (node.has("BasePlate"))  armorStand.setBasePlate(node.get("BasePlate").asBoolean());
        if (node.has("Visible"))    armorStand.setVisible(node.get("Visible").asBoolean());
        if (node.has("Arms"))       armorStand.setArms(node.get("Arms").asBoolean());
        if (node.has("Small"))      armorStand.setSmall(node.get("Small").asBoolean());
        if (node.has("Marker"))     armorStand.setMarker(node.get("Marker").asBoolean());

        loadEquipmentLockTypes(armorStand, node);

        if (node.has("CanMove")) armorStand.setCanMove(node.get("CanMove").asBoolean());

        loadEquipment(armorStand, node);

        if (node.has("CanTick")) armorStand.setCanTick(node.get("CanTick").asBoolean());

        // LivingEntity
        if (node.has("Gliding")) armorStand.setGliding(node.get("Gliding").asBoolean());
        if (node.has("Riptiding")) armorStand.setRiptiding(node.get("Riptiding").asBoolean());
        if (node.has("AI")) armorStand.setAI(node.get("AI").asBoolean());
        if (node.has("Collidable")) armorStand.setCollidable(node.get("Collidable").asBoolean());

        NonEntity.load(armorStand, node);
    }


    // Helpers

    private void putEulerAngle(@NotNull ObjectNode node, @NotNull EulerAngle angle) {
        node.put("x", angle.getX());
        node.put("y", angle.getY());
        node.put("z", angle.getZ());
    }

    private @NotNull EulerAngle readEulerAngle(@NotNull ObjectNode node) {
        return new EulerAngle(node.get("x").asDouble(), node.get("y").asDouble(), node.get("z").asDouble());
    }

    private void putEquipmentLockTypes(@NotNull ArmorStand as, @NotNull ObjectNode node) {
        ObjectNode equipmentLock = JsonHelper.OBJECT_MAPPER.createObjectNode();

        for(EquipmentSlot slot : EquipmentSlot.values()) {
            ArrayNode lockTypes = JsonHelper.OBJECT_MAPPER.createArrayNode();

            for(ArmorStand.LockType lockType : ArmorStand.LockType.values()) {
                if(as.hasEquipmentLock(slot, lockType)) {
                    lockTypes.add(lockType.name());
                }
            }

            if(!lockTypes.isEmpty()) {
                equipmentLock.set(slot.name(), lockTypes);
            }
        }

        if(!equipmentLock.isEmpty()) {
            node.set("EquipmentLock", equipmentLock);
        }
    }

    private void loadEquipmentLockTypes(@NotNull ArmorStand as, @NotNull ObjectNode node) {
        ObjectNode equipmentLock = node.get("EquipmentLock") instanceof ObjectNode o ? o : null;
        if(equipmentLock == null) return;

        for(Map.Entry<String, JsonNode> entry: equipmentLock.properties()) {
            EquipmentSlot slot;
            try {
                slot = EquipmentSlot.valueOf(entry.getKey());
            } catch (IllegalArgumentException ignore) {
                continue;
            }

            ArrayNode lockTypes = entry.getValue() instanceof ArrayNode a ? a : null;
            if(lockTypes == null) continue;

            for(JsonNode lockTypeNode : lockTypes) {
                try {
                    ArmorStand.LockType lockType = ArmorStand.LockType.valueOf(lockTypeNode.asText());
                    as.addEquipmentLock(slot, lockType);
                } catch (IllegalArgumentException ignore) {
                }
            }
        }
    }

    private void putEquipment(@NotNull ArmorStand as, @NotNull ObjectNode node) {
        EntityEquipment equipment = as.getEquipment();

        ObjectNode equipmentNode = JsonHelper.OBJECT_MAPPER.createObjectNode();
        for(EquipmentSlot slot : EquipmentSlot.values()) {
            ObjectNode slotNode = JsonHelper.OBJECT_MAPPER.createObjectNode();

            ItemStack item = equipment.getItem(slot);
            // Item might be null on paper-api 1.17.1.
            if(item != null && !item.getType().isAir()) {
                slotNode.put("Item", JsonHelper.serializeItemStack(item));
            }

            // Cannot set drop change for non-mob entities
//            slotNode.put("DropChance", equipment.getDropChance(slot));

            if(!slotNode.isEmpty()) {
                equipmentNode.set(slot.name(), slotNode);
            }
        }

        if(!equipmentNode.isEmpty()) {
            node.set("Equipment", equipmentNode);
        }
    }

    private void loadEquipment(@NotNull ArmorStand as, @NotNull ObjectNode node) {
        ObjectNode equipmentNode = node.get("Equipment") instanceof ObjectNode o ? o : null;
        if(equipmentNode == null) return;

        EntityEquipment equipment = as.getEquipment();
        for(EquipmentSlot slot : EquipmentSlot.values()) {
            if (!(equipmentNode.get(slot.name()) instanceof ObjectNode slotNode)) continue;

            if (slotNode.get("Item") instanceof TextNode itemNode) {
                equipment.setItem(slot, JsonHelper.deserializeItemStack(itemNode));
            }

            // Cannot set drop change for non-mob entities
//            if (slotNode.get("DropChance") instanceof NumericNode dropChanceNode) {
//                equipment.setDropChance(slot, (float) dropChanceNode.asDouble());
//            }
        }
    }
}
