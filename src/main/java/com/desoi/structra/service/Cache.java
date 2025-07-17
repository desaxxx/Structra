package com.desoi.structra.service;

import com.desoi.structra.model.Selections;
import com.desoi.structra.util.Validate;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    static private final Map<@NotNull CommandSender, @NotNull Selections> SELECTIONS_MAP = new HashMap<>();

    /**
     * Copy of the selections map.
     * @return Map of key:CommandSender, value:Selections
     */
    static public Map<CommandSender, Selections> getSelections() {
        return new HashMap<>(SELECTIONS_MAP);
    }

    /**
     * Get the Selections of the given key.
     * @param key Key of the map
     * @return Selections if found, else returns a new created value.
     */
    static public Selections getSelections(CommandSender key) {
        Validate.validate(key != null, "Selection key cannot be null.");
        return SELECTIONS_MAP.putIfAbsent(key, new Selections());
    }
}
