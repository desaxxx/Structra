package com.desoi.structra.util;

import com.desoi.structra.Structra;
import com.desoi.structra.model.Position;
import com.desoi.structra.service.Cache;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

@SuppressWarnings("unused")
public class Util {

    static public final String PREFIX = "[Structra] ";

    static public void log(String... msg) {
        for (String s : msg) {
            Bukkit.getConsoleSender().sendMessage(HexUtil.parse(PREFIX + s));
        }
    }

    static public void tell(@NotNull CommandSender receiver, @NotNull String... msg) {
        for (String s : msg) {
            receiver.sendMessage(HexUtil.parse(PREFIX + s));
        }
    }



    static private final boolean DEBUG = true;
    static public void debug(String... msg) {
        if (DEBUG) {
            for (String s : msg) {
                log("[Debug] " + s);
            }
        }
    }


    static public void selectPosition(@NotNull CommandSender sender, @NotNull Position position, int pos) {
        if(!(pos == 1 || pos == 2)) return;

        if(pos == 1) {
            Cache.getSelections(sender).setPosition1(position);
        }else {
            Cache.getSelections(sender).setPosition2(position);
        }
        Util.tell(sender,"&aSet POSITION " + pos + " to location [" + position.getX() + "," + position.getY() + "," + position.getZ() + "]");
    }


    static public int parseInt(@NotNull String str, int def) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @NotNull
    static public List<String> savesFileNames() {
        File[] saveFiles = Structra.getSavesFolder().listFiles(l -> l.getName().endsWith(Structra.FILE_EXTENSION));
        if(saveFiles == null) return new ArrayList<>();
        return Arrays.stream(saveFiles).map(f -> f.getName().substring(0, f.getName().length() - Structra.FILE_EXTENSION.length())).toList();
    }
}
