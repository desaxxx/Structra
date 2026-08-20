package com.desoi.structra.service.entity;

import com.desoi.structra.service.entityhandler.IEntityHandler;
import com.desoi.structra.util.JsonHelper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class ArmorStandHandler implements IEntityHandler<ArmorStand> {

    @SuppressWarnings("deprecation")
    @Override
    public void save(@NonNull ArmorStand entity, @NotNull ObjectNode node) {
        node.put("Visible", entity.isVisible());
        node.put("Small", entity.isSmall());
        node.put("Arms", entity.hasArms());
        node.put("BasePlate", entity.hasBasePlate());
        node.put("Gravity", entity.hasGravity());
        node.put("Invulnerable", entity.isInvulnerable());
        node.put("Marker", entity.isMarker());
        node.put("CanMove", entity.canMove());
        node.put("CanTick", entity.canTick());

        if (entity.getCustomName() != null) {
            node.put("CustomName", entity.getCustomName());
        }

        EntityEquipment eq = entity.getEquipment();
        ItemStack helmet = eq.getHelmet();
        if (!helmet.isEmpty()) node.put("Helmet", JsonHelper.serializeItemStack(helmet));
        ItemStack chest = eq.getChestplate();
        if (!chest.isEmpty()) node.put("Chestplate", JsonHelper.serializeItemStack(chest));
        ItemStack legs = eq.getLeggings();
        if (!legs.isEmpty()) node.put("Leggings", JsonHelper.serializeItemStack(legs));
        ItemStack boots = eq.getBoots();
        if (!boots.isEmpty()) node.put("Boots", JsonHelper.serializeItemStack(boots));
        ItemStack mainHand = eq.getItemInMainHand();
        if (!mainHand.isEmpty()) node.put("MainHand", JsonHelper.serializeItemStack(mainHand));
        ItemStack offHand = eq.getItemInOffHand();
        if (!offHand.isEmpty()) node.put("OffHand", JsonHelper.serializeItemStack(offHand));

        ObjectNode headNode = JsonHelper.getOrCreate(node, "HeadPose");
        headNode.put("x", entity.getHeadPose().getX());
        headNode.put("y", entity.getHeadPose().getY());
        headNode.put("z", entity.getHeadPose().getZ());

        ObjectNode bodyNode = JsonHelper.getOrCreate(node, "BodyPose");
        bodyNode.put("x", entity.getBodyPose().getX());
        bodyNode.put("y", entity.getBodyPose().getY());
        bodyNode.put("z", entity.getBodyPose().getZ());

        ObjectNode leftArmNode = JsonHelper.getOrCreate(node, "LeftArmPose");
        leftArmNode.put("x", entity.getLeftArmPose().getX());
        leftArmNode.put("y", entity.getLeftArmPose().getY());
        leftArmNode.put("z", entity.getLeftArmPose().getZ());

        ObjectNode rightArmNode = JsonHelper.getOrCreate(node, "RightArmPose");
        rightArmNode.put("x", entity.getRightArmPose().getX());
        rightArmNode.put("y", entity.getRightArmPose().getY());
        rightArmNode.put("z", entity.getRightArmPose().getZ());

        ObjectNode leftLegNode = JsonHelper.getOrCreate(node, "LeftLegPose");
        leftLegNode.put("x", entity.getLeftLegPose().getX());
        leftLegNode.put("y", entity.getLeftLegPose().getY());
        leftLegNode.put("z", entity.getLeftLegPose().getZ());

        ObjectNode rightLegNode = JsonHelper.getOrCreate(node, "RightLegPose");
        rightLegNode.put("x", entity.getRightLegPose().getX());
        rightLegNode.put("y", entity.getRightLegPose().getY());
        rightLegNode.put("z", entity.getRightLegPose().getZ());
    }

    @Override
    public void spawnAndLoad(Location location, ObjectNode node) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        if (node.has("Visible")) armorStand.setVisible(node.get("Visible").asBoolean());
        if (node.has("Small")) armorStand.setSmall(node.get("Small").asBoolean());
        if (node.has("Arms")) armorStand.setArms(node.get("Arms").asBoolean());
        if (node.has("BasePlate")) armorStand.setBasePlate(node.get("BasePlate").asBoolean());
        if (node.has("Gravity")) armorStand.setGravity(node.get("Gravity").asBoolean());
        if (node.has("Invulnerable")) armorStand.setInvulnerable(node.get("Invulnerable").asBoolean());
        if (node.has("Marker")) armorStand.setMarker(node.get("Marker").asBoolean());
        if (node.has("CanMove")) armorStand.setCanMove(node.get("CanMove").asBoolean());
        if (node.has("CanTick")) armorStand.setCanTick(node.get("CanTick").asBoolean());

        if (node.has("CustomName")) armorStand.setCustomName(node.get("CustomName").asText());

        EntityEquipment eq = armorStand.getEquipment();
        if (node.has("Helmet")) eq.setHelmet(JsonHelper.deserializeItemStack(node.get("Helmet")));
        if (node.has("Chestplate")) eq.setChestplate(JsonHelper.deserializeItemStack(node.get("Chestplate")));
        if (node.has("Leggings")) eq.setLeggings(JsonHelper.deserializeItemStack(node.get("Leggings")));
        if (node.has("Boots")) eq.setBoots(JsonHelper.deserializeItemStack(node.get("Boots")));
        if (node.has("MainHand")) eq.setItemInMainHand(JsonHelper.deserializeItemStack(node.get("MainHand")));
        if (node.has("OffHand")) eq.setItemInOffHand(JsonHelper.deserializeItemStack(node.get("OffHand")));

        if (node.get("HeadPose") instanceof ObjectNode n) {
            armorStand.setHeadPose(new EulerAngle(n.get("x").asDouble(), n.get("y").asDouble(), n.get("z").asDouble()));
        }
        if (node.get("BodyPose") instanceof ObjectNode n) {
            armorStand.setBodyPose(new EulerAngle(n.get("x").asDouble(), n.get("y").asDouble(), n.get("z").asDouble()));
        }
        if (node.get("LeftArmPose") instanceof ObjectNode n) {
            armorStand.setLeftArmPose(new EulerAngle(n.get("x").asDouble(), n.get("y").asDouble(), n.get("z").asDouble()));
        }
        if (node.get("RightArmPose") instanceof ObjectNode n) {
            armorStand.setRightArmPose(new EulerAngle(n.get("x").asDouble(), n.get("y").asDouble(), n.get("z").asDouble()));
        }
        if (node.get("LeftLegPose") instanceof ObjectNode n) {
            armorStand.setLeftLegPose(new EulerAngle(n.get("x").asDouble(), n.get("y").asDouble(), n.get("z").asDouble()));
        }
        if (node.get("RightLegPose") instanceof ObjectNode n) {
            armorStand.setRightLegPose(new EulerAngle(n.get("x").asDouble(), n.get("y").asDouble(), n.get("z").asDouble()));
        }
    }
}
