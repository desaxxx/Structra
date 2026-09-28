package com.desoi.structra.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

@NullMarked
public interface SubCommand {

    String getName();
    default Collection<String> aliases() {
        return List.of();
    }
    @Nullable String getPermission();
    default boolean isPlayerOnly() {
        return false;
    }
    default boolean hideIfNoPermission() {
        return true;
    }

    void execute(CommandSender sender, String[] args);
    Collection<String> suggest(CommandSender sender, String[] args);

    default Collection<String> suggestPlayerNames(String arg) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg.toLowerCase(Locale.ROOT)))
                .toList();
    }
}
