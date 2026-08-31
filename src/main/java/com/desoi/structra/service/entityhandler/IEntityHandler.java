package com.desoi.structra.service.entityhandler;

import com.desoi.structra.util.Wrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface IEntityHandler<E extends Entity> {

    default int minSupportedVersion() {
        return 11605;
    }

    default boolean isSupported() {
        return Wrapper.getInstance().getVersion() >= minSupportedVersion();
    }

    //
    ObjectMapper objectMapper = new ObjectMapper();

    @NotNull
    default String name() {
        return this.getClass().getSimpleName();
    }

    void save(@NotNull E entity, @NotNull ObjectNode node);

    void spawnAndLoad(Location location, ObjectNode node);
}
