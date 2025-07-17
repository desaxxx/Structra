package com.desoi.structra.command;

import com.desoi.structra.Structra;
import com.desoi.structra.loader.StructureFile;
import com.desoi.structra.loader.StructureLoader;
import com.desoi.structra.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class PasteCommand implements BaseCommand {
    static public final PasteCommand INSTANCE = new PasteCommand();

    private PasteCommand() {}

    /*
     * PLAYER: /structra paste <fileName> [<batchSize>] [<x>] [<y>] [<z>] [<world>]
     * CONSOLE: /structra paste <fileName> <x> <y> <z> <world> [<batchSize>]
     */
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String fileName = args[1];
        if(!sender.hasPermission("structra.paste." + fileName)) {
            Util.tell(sender, "&cYou don't have permission to paste this Structra.");
            return true;
        }
        File file = new File(Structra.getSavesFolder(), fileName + Structra.FILE_EXTENSION);
        if(!file.exists()) {
            Util.tell(sender, "&cFile doesn't exist.");
            return true;
        }

        int batchSize = 50;
        int x, y, z;
        World world;
        if(sender instanceof Player player) {
            if(args.length > 2) {
                batchSize = Util.parseInt(args[2], batchSize);
            }
            x = player.getLocation().getBlockX();
            y = player.getLocation().getBlockY();
            z = player.getLocation().getBlockZ();
            world = player.getWorld();
            if(args.length > 6) {
                x = Util.parseInt(args[3], 0);
                y = Util.parseInt(args[4], 0);
                z = Util.parseInt(args[5], 0);
                world = Bukkit.getWorld(args[6]);
            }
        }else {
            if(args.length < 5) {
                Util.tell(sender, "&cUsage: /structra paste <fileName> <x> <y> <z> <world> [<batchSize>]");
                return true;
            }
            x = Util.parseInt(args[2], 0);
            y = Util.parseInt(args[3], 0);
            z = Util.parseInt(args[4], 0);
            world = Bukkit.getWorld(args[5]);
            if(args.length > 6) {
                batchSize = Util.parseInt(args[6], batchSize);
            }
        }
        Location originLocation = new Location(world, x, y, z);

        StructureFile structureFile = new StructureFile(file);
        StructureLoader structureLoader = new StructureLoader(structureFile, sender, 0, 20, batchSize, originLocation);
        structureLoader.execute();
        Util.tell(sender, "&aLoading Structure...");
        return true;
    }
}
