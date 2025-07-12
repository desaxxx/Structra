package com.desoi.structra;

import org.bukkit.Bukkit;

@SuppressWarnings("unused")
public class Util {

    static public void log(String... msg) {
        for (String s : msg) {
            Bukkit.getConsoleSender().sendMessage(HexUtil.parse(s));
        }
    }
}
