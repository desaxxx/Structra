package com.desoi.structra;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Util {

    static public void log(String... msg) {
        for (String s : msg) {
            Bukkit.getConsoleSender().sendMessage(HexUtil.parse(s));
        }
    }

    static public void tell(@NotNull CommandSender receiver, @NotNull String... msg) {
        for (String s : msg) {
            receiver.sendMessage(HexUtil.parse(s));
        }
    }
}
