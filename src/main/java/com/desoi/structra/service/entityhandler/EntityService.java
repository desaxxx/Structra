package com.desoi.structra.service.entityhandler;

import com.desoi.structra.service.entity.ArmorStandHandler;
import com.desoi.structra.service.entity.BlockDisplayHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EntityService {

    private static final Map<EntityType, IEntityHandler<?>> handlers = new HashMap<>();

    static {
        handlers.put(EntityType.ARMOR_STAND, new ArmorStandHandler());
        tryPutting("BLOCK_DISPLAY", new BlockDisplayHandler());
    }

    private static void tryPutting(@NotNull String entityType, @NotNull IEntityHandler<?> handler) {
        try {
            EntityType enumValue = Enum.valueOf(EntityType.class, entityType);
            handlers.put(enumValue, handler);
        } catch (IllegalArgumentException ignored) {}
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <E extends Entity> IEntityHandler<E> getHandler(EntityType type) {
        return (IEntityHandler<E>) handlers.get(type);
    }

    @SuppressWarnings("unchecked")
    public static @Nullable <E extends Entity> IEntityHandler<E> getHandler(String type) {
        try {
            return (IEntityHandler<E>) handlers.get(EntityType.valueOf(type));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
