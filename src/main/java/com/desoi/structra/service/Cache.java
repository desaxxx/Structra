package com.desoi.structra.service;

import com.desoi.structra.model.Selections;
import com.desoi.structra.util.Validate;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    private static final Map<@NotNull CommandSender, @NotNull Selections> SELECTIONS_MAP = new HashMap<>();

    /**
     * Copy of the selections map.
     * @return Map of key:CommandSender, value:Selections
     */
    public static Map<CommandSender, Selections> getSelections() {
        return new HashMap<>(SELECTIONS_MAP);
    }

    /**
     * Get the Selections of the given key.
     * @param key Key of the map
     * @return Selections if found, else returns a new created value.
     */
    @NotNull
    public static Selections getSelections(CommandSender key) {
        Validate.notNull(key, "Selection key cannot be null.");
        return SELECTIONS_MAP.computeIfAbsent(key, k -> new Selections());
    }
}
