package com.desoi.structra.util;

import com.desoi.structra.Structra;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class Wrapper {

    private final Structra plugin;
    private final int version;

    public Wrapper(@NotNull Structra plugin) {
        this.plugin = plugin;
        this.version = fetchVersion();
    }

    public static Wrapper getInstance() {
        return Structra.getInstance().getWrapper();
    }

    private int fetchVersion() {
        String[] ver = Bukkit.getMinecraftVersion().split("\\.");
        if(ver.length < 2) {
            Util.log("{WARN}Could not fetch server version!");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return 0;
        }

        int first = 0;
        try { first = Integer.parseInt(ver[0]); } catch (NumberFormatException ignored) {}

        if(first >= 25) {
            return Integer.MAX_VALUE;
        }

        int major = 0;
        try { major = Integer.parseInt(ver[1]); } catch (NumberFormatException ignored) {}
        int minor = 0;
        if(ver.length > 2) {
            try { minor = Integer.parseInt(ver[2]); } catch (NumberFormatException ignored) {}
        }

        int version = major * 100 + minor;
        if(version < 1605) {
            Util.log(String.format("&cYou are using an unsupported server version '%s'!", String.join(".", ver)),
                    "&cPlease use v1.16.5 or newer.");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        return version;
    }

    public int getVersion() {
        return version;
    }
}
