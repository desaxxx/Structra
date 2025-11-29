package com.desoi.structra.command;

import com.desoi.structra.model.Position;
import com.desoi.structra.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PosCommand implements BaseCommand {
    public static final PosCommand INSTANCE = new PosCommand();

    private PosCommand() {}

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission("structra.select")) {
            Util.tell(sender, "&cYou don't have permission to select a position.");
            return true;
        }
        int x, y, z;
        World world;
        if(args.length > 4) {
            x = Util.parseInt(args[1], 0);
            y = Util.parseInt(args[2], 0);
            z = Util.parseInt(args[3], 0);
            world = Bukkit.getWorld(args[4]);
        }else {
            if(!(sender instanceof Player player)) {
                Util.tell(sender, "&cUsage: /structra <pos1|pos2> <x> <y> <z> <world>");
                return true;
            }
            Location location = player.getLocation();
            Block targetBlock = player.getTargetBlockExact(10);
            if(targetBlock != null) location = targetBlock.getLocation();

            x = location.getBlockX();
            y = location.getBlockY();
            z = location.getBlockZ();
            world = location.getWorld();
        }

        Position position = new Position(x, y, z, world == null ? null : world.getName());
        Util.selectPosition(sender, position, args[0].equals("pos1") ? 1 : 2);
        return true;
    }
}
