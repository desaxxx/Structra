package com.desoi.structra.command;

import com.desoi.structra.model.Position;
import com.desoi.structra.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@NullMarked
public class PosCommand implements SubCommand {

    @Override
    public String getName() {
        return "pos1";
    }

    @Override
    public Collection<String> aliases() {
        return List.of("pos2");
    }

    @Override
    public @Nullable String getPermission() {
        return "structra.select";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
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
                return;
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
    }

    @Override
    public Collection<String> suggest(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("<x>");
        } else if (args.length == 3) {
            return List.of("<y>");
        } else if (args.length == 4) {
            return List.of("<z>");
        } else if (args.length == 5) {
            return List.of("<world>");
        }
        return List.of();
    }
}
