package com.desoi.structra.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class VersionUtil {

    private static final BukkitVersion version = new BukkitVersion();

    public static BukkitVersion getVersion() {
        return version;
    }

    private VersionUtil() {
        throw new UnsupportedOperationException("Cannot instantiate VersionUtil");
    }



    public static class BukkitVersion implements Comparable<BukkitVersion> {

        private final int major;
        private final int minor;
        private final int patch;
        private BukkitVersion() {
            String versionString = Bukkit.getBukkitVersion().split("-")[0];

            String[] parts = versionString.split("\\.", 3);
            int major = 0;
            int minor = 0;
            int patch = 0;
            try {
                major = Integer.parseInt(parts[0]);
            }catch (NumberFormatException ignored) {}
            if(parts.length > 1) {
                try {
                    minor = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ignored) {}
            }
            if(parts.length > 2) {
                try {
                    patch = Integer.parseInt(parts[2]);
                } catch (NumberFormatException ignored) {}
            }

            this.major = major;
            this.minor = minor;
            this.patch = patch;
        }

        public int getMajor() {
            return major;
        }

        public int getMinor() {
            return minor;
        }

        public int getPatch() {
            return patch;
        }

        public boolean isAtLeast(int major, int minor, int patch) {
            return this.major >= major && this.minor >= minor && this.patch >= patch;
        }

        public boolean isAtLeast(int major, int minor) {
            return this.major >= major && this.minor >= minor;
        }

        public boolean isAtLeast(int major) {
            return this.major >= major;
        }


        @Override
        public int compareTo(@NotNull BukkitVersion o) {
            int result = Integer.compare(major, o.major);
            if (result != 0) return result;

            result = Integer.compare(minor, o.minor);
            if (result != 0) return result;

            return Integer.compare(patch, o.patch);
        }
    }
}
