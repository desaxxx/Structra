package com.desoi.structra.util;

import com.desoi.structra.Structra;
import com.desoi.structra.model.Position;
import com.desoi.structra.service.Cache;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Util {

    public static final String PREFIX = "[Structra] ";

    public static void log(String... msg) {
        for (String s : msg) {
            Bukkit.getConsoleSender().sendMessage(HexUtil.parse(PREFIX + s));
        }
    }

    public static void tell(@NotNull CommandSender receiver, @NotNull String... msg) {
        for (String s : msg) {
            receiver.sendMessage(HexUtil.parse(PREFIX + s));
        }
    }



    private static final boolean DEBUG = true;
    public static void debug(String... msg) {
        if (DEBUG) {
            for (String s : msg) {
                log("[Debug] " + s);
            }
        }
    }


    public static void selectPosition(@NotNull CommandSender sender, @NotNull Position position, int pos) {
        if(!(pos == 1 || pos == 2)) return;

        if(pos == 1) {
            Cache.getSelections(sender).setPosition1(position);
        }else {
            Cache.getSelections(sender).setPosition2(position);
        }
        Util.tell(sender,"&aSet POSITION " + pos + " to location [" + position.getX() + "," + position.getY() + "," + position.getZ() + "]");
    }


    public static int parseInt(@NotNull String str, int def) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @NotNull
    public static List<String> savesFileNames() {
        try {
            File[] saveFiles = Structra.getSavesFolder().listFiles(l -> l.getName().endsWith(Structra.FILE_EXTENSION));
            if(saveFiles == null) return new ArrayList<>();
            return Arrays.stream(saveFiles).map(f -> f.getName().substring(0, f.getName().length() - Structra.FILE_EXTENSION.length())).toList();
        } catch (SecurityException e) {
            return new ArrayList<>();
        }
    }

    @NotNull
    public static List<String> historyFileNames() {
        File[] historyFiles = Structra.getHistoryFolder().listFiles(l -> l.getName().endsWith(Structra.FILE_EXTENSION));
        if (historyFiles == null) return new ArrayList<>();
        return Arrays.stream(historyFiles).map(f -> f.getName().substring(0, f.getName().length() - Structra.FILE_EXTENSION.length())).toList();
    }
}
