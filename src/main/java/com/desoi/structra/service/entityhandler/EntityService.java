package com.desoi.structra.service.entityhandler;

import com.desoi.structra.service.blockstate.*;
import com.desoi.structra.service.entity.ArmorStandHandler;
import com.desoi.structra.service.entity.BlockDisplayHandler;
import org.bukkit.block.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EntityService {

    private static final Map<EntityType, IEntityHandler<?>> handlers = new HashMap<>();

    static {
        handlers.put(EntityType.ARMOR_STAND, new ArmorStandHandler());
        handlers.put(EntityType.BLOCK_DISPLAY, new BlockDisplayHandler());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <E extends Entity> IEntityHandler<E> getHandler(EntityType type) {
        return (IEntityHandler<E>) handlers.get(type);
    }
}
