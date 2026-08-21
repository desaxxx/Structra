package com.desoi.structra.util;

import com.desoi.structra.Structra;
import org.jetbrains.annotations.NotNull;

public class Wrapper {

    private final Structra plugin;

    public Wrapper(@NotNull Structra plugin) {
        this.plugin = plugin;
    }

    public static Wrapper getInstance() {
        return Structra.getInstance().getWrapper();
    }

    public int getVersion() {
        VersionUtil.BukkitVersion version = VersionUtil.getVersion();
        // 26, 2, 0 -> 260200
        return version.getMajor() * 1_0000 + version.getMinor() * 1_00 + version.getPatch();
    }
}
