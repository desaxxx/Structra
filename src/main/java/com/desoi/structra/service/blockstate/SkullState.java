package com.desoi.structra.service.blockstate;

import com.desoi.structra.service.statehandler.IStateHandler;
import com.desoi.structra.util.JsonHelper;
import com.desoi.structra.util.Wrapper;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SkullState implements IStateHandler<Skull> {

    @SuppressWarnings("deprecation")
    @Override
    public void save(@NotNull Skull blockState, @NotNull ObjectNode node) {
        int minecraft = Wrapper.getInstance().getVersion();

        if(minecraft >= 11903) {
            node.put("NoteBlockSound", blockState.getNoteBlockSound() != null ? blockState.getNoteBlockSound().toString() : null);
        }

        boolean hasProfile = false;

        if(minecraft >= 11801) {
            if(blockState.getPlayerProfile() != null) {
                hasProfile = true;
                node.set("PlayerProfile", playerProfileToJson(blockState.getPlayerProfile()));
            }
        }

        if(blockState.getOwningPlayer() != null && !hasProfile) {
            node.set("OwningPlayer", owningPlayerToJson(blockState.getOwningPlayer()));
        }
        saveTileState(blockState, node);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void loadTo(@NotNull Skull blockState, @NotNull ObjectNode node) {
        int minecraft = Wrapper.getInstance().getVersion();

        if(minecraft >= 11903 && node.get("NoteBlockSound") instanceof TextNode noteBlockSoundNode) {
            try {
                blockState.setNoteBlockSound(NamespacedKey.fromString(noteBlockSoundNode.asText()));
            } catch (Exception ignored) {
            }
        }

        boolean hasProfile = false;

        if(minecraft >= 11801) {
            if(node.has("PlayerProfile")) {
                hasProfile = true;
                blockState.setPlayerProfile(jsonToPlayerProfile(node.get("PlayerProfile")));
            }
        }

        if(node.has("OwningPlayer") && !hasProfile) {
            OfflinePlayer owningPlayer = jsonToOwningPlayer(node.get("OwningPlayer"));
            if(owningPlayer != null) {
                blockState.setOwningPlayer(owningPlayer);
            }
        }
        loadToTileState(blockState, node);
        blockState.update(true, false);
    }


    private JsonNode playerProfileToJson(PlayerProfile profile) {
        ObjectNode node = objectMapper.createObjectNode();

        if(profile.getName() != null) {
            node.put("Name", profile.getName());
        }

        if(profile.getId() != null) {
            node.put("Id", profile.getId().toString());
        }

        PlayerTextures textures = profile.getTextures();
        if(!textures.isEmpty()) {
            ObjectNode texturesNode = JsonHelper.getOrCreate(node, "Textures");

            texturesNode.put("SkinModel", textures.getSkinModel().name());

            if(textures.getSkin() != null) {
                texturesNode.put("Skin", textures.getSkin().toString());
            }

            if(textures.getCape() != null) {
                texturesNode.put("Cape", textures.getCape().toString());
            }
        }

        ArrayNode propertiesNode = objectMapper.createArrayNode();
        for(ProfileProperty property : profile.getProperties()) {
            ObjectNode propertyNode = objectMapper.createObjectNode();
            propertyNode.put("Name", property.getName());
            propertyNode.put("Value", property.getValue());
            propertyNode.put("Signature", property.getSignature());
            propertiesNode.add(propertyNode);
        }
        node.set("Properties", propertiesNode);

        return node;
    }

    // TODO: this is not going to work (return type is invalid for lower MC 1.18.1)
    private PlayerProfile jsonToPlayerProfile(JsonNode node) {
        String name = node.get("Name") instanceof TextNode nameNode ? nameNode.asText() : null;

        UUID id = null;
        if(node.get("Id") instanceof TextNode idNode) {
            try {
                id = UUID.fromString(idNode.asText());
            } catch (Exception ignored) {
            }
        }

        PlayerProfile profile = Bukkit.createProfile(id, name);

        if(node.get("Textures") instanceof ObjectNode texturesNode) {
            PlayerTextures textures = profile.getTextures();

            if(texturesNode.get("Skin") instanceof TextNode skinNode) {
                PlayerTextures.SkinModel skinModel = PlayerTextures.SkinModel.CLASSIC;

                if (texturesNode.get("SkinModel") instanceof TextNode skinModelNode) {
                    try {
                        skinModel = PlayerTextures.SkinModel.valueOf(skinModelNode.asText());
                    } catch (Exception ignored) {
                    }
                }

                try {
                    textures.setSkin(
                            new URI(skinNode.asText()).toURL(),
                            skinModel
                    );
                } catch (Exception ignored) {
                }
            }

            if(texturesNode.get("Cape") instanceof TextNode capeNode) {
                try {
                    textures.setCape(new URI(capeNode.asText()).toURL());
                } catch (Exception ignored) {
                }
            }
        }

        Set<ProfileProperty> properties = new HashSet<>();
        if(node.get("Properties") instanceof ArrayNode propertiesNode) {
            for(JsonNode propertyNode : propertiesNode) {
                String propertyName = propertyNode.get("Name") instanceof TextNode nameNode ? nameNode.asText() : null;
                String value = propertyNode.get("Value") instanceof TextNode valueNode ? valueNode.asText() : null;
                String signature = propertyNode.get("Signature") instanceof TextNode signatureNode ? signatureNode.asText() : null;

                if(propertyName != null && value != null) {
                    properties.add(new ProfileProperty(propertyName, value, signature));
                }
            }
        }
        profile.setProperties(properties);

        return profile;
    }

    private JsonNode owningPlayerToJson(OfflinePlayer owningPlayer) {
        ObjectNode node = objectMapper.createObjectNode();

        node.put("UUID", owningPlayer.getUniqueId().toString());

        return node;
    }

    private @Nullable OfflinePlayer jsonToOwningPlayer(JsonNode node) {
        UUID uuid = null;
        if(node.get("UUID") instanceof TextNode uuidNode) {
            try {
                uuid = UUID.fromString(uuidNode.asText());
            } catch (Exception ignored) {
            }
        }

        if(uuid == null) {
            return null;
        }

        return Bukkit.getOfflinePlayer(uuid);
    }
}