package com.desoi.structra.command;

import com.desoi.structra.Structra;
import com.desoi.structra.model.Position;
import com.desoi.structra.service.Cache;
import com.desoi.structra.util.Util;
import com.desoi.structra.writer.StructureWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class WriteCommand implements BaseCommand {
    static public final WriteCommand INSTANCE = new WriteCommand();

    private WriteCommand() {}

    /*
     * PLAYER: /structra write <fileName> [<batchSize>] [<x>] [<y>] [<z>] [<world>]
     * CONSOLE: /structra write <fileName> <x> <y> <z> <world> [<batchSize>]
     */
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission("structra.write")) {
            Util.tell(sender, "&cYou don't have permission to write a Structra.");
            return true;
        }
        Position position1 = Cache.getSelections(sender).getPosition1();
        Position position2 = Cache.getSelections(sender).getPosition2();
        if(position1 == null || position2 == null) {
            Util.tell(sender, "&cYou have missing positions.");
            return true;
        }
        String fileName = args[1];
        File file = new File(Structra.getSavesFolder(), fileName + Structra.FILE_EXTENSION);
        if(file.exists()) {
            Util.tell(sender, "&cFile already exists, you can't overwrite it.");
            return true;
        }

        //
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
                Util.tell(sender, "&cUsage: /structra write <fileName> <x> <y> <z> <world> [<batchSize>]");
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

        StructureWriter structureWriter = new StructureWriter(file, sender, position1, position2, originLocation, 0, 20, batchSize);
        structureWriter.execute();
        Util.tell(sender, "&aWriting Structure...");
        return true;
    }
}
