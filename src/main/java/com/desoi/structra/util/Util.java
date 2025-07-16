package com.desoi.structra.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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


    static public final Map<UUID, Vector> VECTORS_1 = new HashMap<>();
    static public final Map<UUID, Vector> VECTORS_2 = new HashMap<>();

    static public void selectVector(@NotNull Player player, @NotNull Vector vector, int pos) {
        if(!(pos == 1 || pos == 2)) return;

        if(pos == 1) {
            VECTORS_1.put(player.getUniqueId(), vector);
        }else {
            VECTORS_2.put(player.getUniqueId(), vector);
        }
        Util.tell(player,"&aSet POSITION " + pos + " to location [" + vector.getX() + "," + vector.getY() + "," + vector.getZ() + "]");
    }
}
