package com.desoi.structra.command;

import com.desoi.structra.HexUtil;
import com.desoi.structra.Structra;
import com.desoi.structra.Util;
import com.desoi.structra.model.Structure;
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

import java.io.File;
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
        /*
         * /structra <pos1|pos2>
         */
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
        /*
         * /structra save <fileName> [<batchSize>]
         */
        else if(args.length >= 2 && args[0].equals("save")) {
            Vector vector1 = VECTORS_1.get(player.getUniqueId());
            Vector vector2 = VECTORS_2.get(player.getUniqueId());
            if(vector1 == null || vector2 == null) {
                Util.tell(player, "&cYou have missing positions.");
                return true;
            }
            String fileName = args[1];
            int batchSize = 50;
            if(args.length >= 3) {
                batchSize = parseInt(args[2], batchSize);
            }
            Location originLocation = player.getLocation().clone();

            Structure structure = new Structure(player, vector1, vector2, 0, 20, batchSize);
            structure.save(new File(Structra.getInstance().getDataFolder(), fileName + ".structra"), originLocation);
            Util.tell(player, "&aSaving Structure:");
            Util.tell(player, "Min vector: [" + structure.getMinVector().getBlockX() + "," + structure.getMinVector().getBlockY() + "," + structure.getMinVector().getBlockZ() + "]");
            Util.tell(player, "Max vector: [" + structure.getMaxVector().getBlockX() + "," + structure.getMaxVector().getBlockY() + "," + structure.getMaxVector().getBlockZ() + "]");
            Util.tell(player, "Size: " + structure.getSize());
        }
        return true;
    }

    private int parseInt(@NotNull String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(args.length == 1) {
            return List.of("pos1","pos2","save");
        }
        return List.of();
    }
}
