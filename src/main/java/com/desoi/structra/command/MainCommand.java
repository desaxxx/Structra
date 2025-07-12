package com.desoi.structra.command;

import com.desoi.structra.HexUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainCommand implements CommandExecutor, TabCompleter {

    static private final Map<UUID, Vector> VECTORS_1 = new HashMap<>();
    static private final Map<UUID, Vector> VECTORS_2 = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(HexUtil.parse("&cNuh uh!"));
            return true;
        }
        if(args.length >= 1 && (args[0].equals("pos1") || args[0].equals("pos2"))) {
            Block block = player.getTargetBlockExact(10);
            Location location = player.getLocation();
            if(block != null) {
                location = block.getLocation();
            }

            Vector vector = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            if(args[0].equals("pos1")) {
                VECTORS_1.put(player.getUniqueId(), vector);
            }else {
                VECTORS_2.put(player.getUniqueId(), vector);
            }
            player.sendMessage(HexUtil.parse("&aSet POSITION " + args[0].replace("pos","") + " to location [" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + "]"));
        }
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }
}
