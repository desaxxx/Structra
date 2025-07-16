package com.desoi.structra.command;

import com.desoi.structra.util.HexUtil;
import com.desoi.structra.Structra;
import com.desoi.structra.util.Util;
import com.desoi.structra.model.StructureLoader;
import com.desoi.structra.model.StructureWriter;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(HexUtil.parse("&cNuh uh!"));
            return true;
        }
        /*
         * /structra tool
         */
        if(args.length >= 1 && args[0].equals("tool")) {
            player.give(Structra.SELECTOR_TOOL);
            Util.tell(player, "&eHere your tool!");
        }
        /*
         * /structra <pos1|pos2>
         */
        else if(args.length >= 1 && (args[0].equals("pos1") || args[0].equals("pos2"))) {
            Block block = player.getTargetBlockExact(10);
            Location location = player.getLocation();
            if(block != null) {
                location = block.getLocation();
            }

            Vector vector = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Util.selectVector(player, vector, args[0].equals("pos1") ? 1 : 2);
        }
        /*
         * /structra save <fileName> [<batchSize>]
         */
        else if(args.length >= 2 && args[0].equals("save")) {
            Vector vector1 = Util.VECTORS_1.get(player.getUniqueId());
            Vector vector2 = Util.VECTORS_2.get(player.getUniqueId());
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

            StructureWriter structureWriter = new StructureWriter(player, vector1, vector2, 0, 20, batchSize);
            structureWriter.save(new File(Structra.getSavesFolder(), fileName + Structra.FILE_EXTENSION), originLocation);
            Util.tell(player, "&aSaving Structure:");
            Util.tell(player, "&eMin vector: [" + structureWriter.getMinVector().getBlockX() + "," + structureWriter.getMinVector().getBlockY() + "," + structureWriter.getMinVector().getBlockZ() + "]");
            Util.tell(player, "&eMax vector: [" + structureWriter.getMaxVector().getBlockX() + "," + structureWriter.getMaxVector().getBlockY() + "," + structureWriter.getMaxVector().getBlockZ() + "]");
            Util.tell(player, "&eSize: " + structureWriter.getSize());
        }
        /*
         * /structra load <fileName> [<batchSize>]
         */
        else if(args.length >= 2 && args[0].equals("load")) {
            String fileName = args[1];
            File file = new File(Structra.getSavesFolder(), fileName + Structra.FILE_EXTENSION);
            int batchSize = 50;
            if(args.length >= 3) {
                batchSize = parseInt(args[2], batchSize);
            }
            Location originLocation = player.getLocation().clone();

            StructureLoader structureLoader = new StructureLoader(sender, file, 0, 20, batchSize);
            structureLoader.load(originLocation);
            Util.tell(player, "&aLoading Structure:");
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
            return List.of("tool","pos1","pos2","save","load");
        } else if(args.length == 2 && args[0].equals("load")) {
            return savesFileNames();
        }
        return List.of();
    }

    @NotNull
    private List<String> savesFileNames() {
        File[] saveFiles = Structra.getSavesFolder().listFiles(l -> l.getName().endsWith(Structra.FILE_EXTENSION));
        if(saveFiles == null) return new ArrayList<>();
        return Arrays.stream(saveFiles).map(f -> f.getName().substring(0, f.getName().length() - Structra.FILE_EXTENSION.length())).toList();
    }
}
