package com.desoi.structra.util;

import com.desoi.structra.Structra;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class Wrapper {

    private final Structra plugin;

    public Wrapper(@NotNull Structra plugin) {
        this.plugin = plugin;
        version = fetchVersion();
    }

    public static Wrapper getInstance() {
        return Structra.getInstance().getWrapper();
    }

    @Getter
    private final int version;

    private int fetchVersion() {
        String[] ver = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        if(ver.length < 2) {
            Util.log("{WARN}Could not fetch server version!");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        int major = 0;
        try {
            major = Integer.parseInt(ver[1]);
        } catch (NumberFormatException ignored) {}
        int minor = 0;
        if(ver.length > 2) {
            try {
                minor = Integer.parseInt(ver[2]);
            } catch (NumberFormatException ignored) {
            }
        }

        int version = major * 100 + minor;
        if(version < 1605) {
            Util.log(String.format("&cYou are using an unsupported server version '%s'!", String.join(".", ver)),
                    "&cPlease use v1.16.5 or newer.");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        return version;
    }
}
