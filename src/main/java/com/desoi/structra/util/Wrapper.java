package com.desoi.structra.util;

import com.desoi.structra.Structra;
import org.jetbrains.annotations.NotNull;

public class Wrapper {

    private final Structra plugin;
    private final int version;

    public Wrapper(@NotNull Structra plugin) {
        this.plugin = plugin;
        VersionUtil.BukkitVersion bukkitVersion = VersionUtil.getVersion();
        // 26, 2, 0 -> 260200
        this.version = bukkitVersion.getMajor() * 1_0000 + bukkitVersion.getMinor() * 1_00 + bukkitVersion.getPatch();
    }

    public static Wrapper getInstance() {
        return Structra.getInstance().getWrapper();
    }

    public int getVersion() {
        return version;
    }
}
