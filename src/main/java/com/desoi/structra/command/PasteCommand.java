package com.desoi.structra.command;

import com.desoi.structra.Structra;
import com.desoi.structra.loader.StructureFile;
import com.desoi.structra.loader.StructureLoader;
import com.desoi.structra.loader.StructurePasteTask;
import com.desoi.structra.model.BlockTraversalOrder;
import com.desoi.structra.model.Rotation;
import com.desoi.structra.model.StructraException;
import com.desoi.structra.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@NullMarked
public class PasteCommand implements SubCommand {

    @Override
    public String getName() {
        return "paste";
    }

    @Override
    public @Nullable String getPermission() {
        return "structra.paste";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String fileName = args[1];
        File file = new File(Structra.getInstance().getSavesFolder(), fileName + Structra.FILE_EXTENSION);
        if(!file.exists()) {
            Util.tell(sender, "&cFile doesn't exist.");
            return;
        }

        boolean skipHistory = Arrays.asList(args).contains("--skipHistory");
        Rotation rotation = parseRotation(args);

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
                Util.tell(sender, "&cUsage: /structra paste <fileName> <x> <y> <z> <world> [<batchSize>]");
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

        BlockTraversalOrder traversalOrder = BlockTraversalOrder.DEFAULT;
        StructureLoader structureLoader;
        StructurePasteTask pasteTask;
        try {
            StructureFile structureFile = new StructureFile(file);
            structureLoader = new StructureLoader(structureFile, sender, 0, 20, batchSize, originLocation, traversalOrder, rotation);
            pasteTask = structureLoader.createPasteTask();
        } catch (StructraException e) {
            Util.tellError(sender, e);
            return;
        }

        if(skipHistory) {
            pasteTask.execute();
        }else {
            structureLoader.saveHistory(pasteTask::execute);
        }
        Util.tell(sender, "&aLoading Structure...");
    }

    private Rotation parseRotation(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("--rotate=") || arg.startsWith("-r=")) {
                String val = arg.substring(arg.indexOf('=') + 1);
                return matchRotation(val);
            }

            if (arg.equalsIgnoreCase("--rotate") || arg.equalsIgnoreCase("-r")) {
                if (i + 1 < args.length) {
                    return matchRotation(args[i + 1]);
                }
            }
        }
        return Rotation.NONE;
    }
    private Rotation matchRotation(String value) {
        return switch (value.toUpperCase()) {
            case "90", "CW_90", "RIGHT" -> Rotation.CW_90;
            case "180", "CW_180", "BACK" -> Rotation.CW_180;
            case "270", "CW_270", "LEFT" -> Rotation.CW_270;
            default -> Rotation.NONE;
        };
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
        if (args.length > 7) {
            String prev = args[args.length-2];
            if (prev.equals("--rotate")) {
                return List.of("90","180","270","LEFT","RIGHT","BACK");
            } else {
                return List.of("--skipHistory","--rotate");
            }
        }

        return List.of();
    }
}
