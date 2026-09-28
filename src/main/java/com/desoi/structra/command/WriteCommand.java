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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;

@NullMarked
public class WriteCommand implements SubCommand {

    @Override
    public String getName() {
        return "write";
    }

    @Override
    public @Nullable String getPermission() {
        return "structra.write";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Position position1 = Cache.getSelections(sender).getPosition1();
        Position position2 = Cache.getSelections(sender).getPosition2();
        if(position1 == null || position2 == null) {
            Util.tell(sender, "&cYou have missing positions.");
            return;
        }
        String fileName = args[1];
        File file = new File(Structra.getInstance().getSavesFolder(), fileName + Structra.FILE_EXTENSION);
        if(file.exists()) {
            Util.tell(sender, "&cFile already exists, you can't overwrite it.");
            return;
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
            if(args.length < 6) {
                Util.tell(sender, "&cUsage: /structra write <fileName> <x> <y> <z> <world> [<batchSize>]");
                return;
            }
            x = Util.parseInt(args[2], 0);
            y = Util.parseInt(args[3], 0);
            z = Util.parseInt(args[4], 0);
            world = Bukkit.getWorld(args[5]);
            if(args.length > 6) {
                batchSize = Util.parseInt(args[6], batchSize);
            }
        }
        if(world == null) {
            Util.tell(sender, "&cWorld not found.");
            return;
        }
        Location originLocation = new Location(world, x, y, z);

        StructureWriter structureWriter = new StructureWriter(file, sender, position1, position2, originLocation, 0, 20, batchSize);
        structureWriter.createWriteTask().execute();
        Util.tell(sender, "&aWriting Structure...");
    }

    @Override
    public Collection<String> suggest(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("<fileName>");
        }
        if (sender instanceof Player) {
            if (args.length == 3) {
                return List.of("50","100","500","1000");
            } else if (args.length == 4) {
                return List.of("<x>");
            } else if (args.length == 5) {
                return List.of("<y>");
            } else if (args.length == 6) {
                return List.of("<z>");
            } else if (args.length == 7) {
                return List.of("<world>");
            }
        }else {
            if (args.length == 3) {
                return List.of("<x>");
            } else if (args.length == 4) {
                return List.of("<y>");
            } else if (args.length == 5) {
                return List.of("<z>");
            } else if (args.length == 6) {
                return List.of("<world>");
            } else if (args.length == 7) {
                return List.of("50","100","500","1000");
            }
        }

        return List.of();
    }
}
