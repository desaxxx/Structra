package com.desoi.structra.command;

import com.desoi.structra.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        /*
         * PLAYER: /structra tool
         */
        if(args.length >= 1 && args[0].equals("tool")) {
            return ToolCommand.INSTANCE.onCommand(sender, args);
        }
        /*
         * PLAYER: /structra <pos1|pos2> [<x>] [<y>] [<z>] [<world>]
         * CONSOLE: /structra <pos1|pos2> <x> <y> <z> <world>
         */
        else if(args.length >= 1 && (args[0].equals("pos1") || args[0].equals("pos2"))) {
            return PosCommand.INSTANCE.onCommand(sender, args);
        }
        /*
         * PLAYER: /structra write <fileName> [<batchSize>] [<x>] [<y>] [<z>] [<world>]
         * CONSOLE: /structra write <fileName> <x> <y> <z> <world> [<batchSize>]
         */
        else if(args.length >= 2 && args[0].equals("write")) {
            return WriteCommand.INSTANCE.onCommand(sender, args);
        }
        /*
         * PLAYER: /structra paste <fileName> [<batchSize>] [<x>] [<y>] [<z>] [<world>]
         * CONSOLE: /structra paste <fileName> <x> <y> <z> <world> [<batchSize>]
         */
        else if(args.length >= 2 && args[0].equals("paste")) {
            return PasteCommand.INSTANCE.onCommand(sender, args);
        }
        /*
         * /structra pasteHistory <fileName> [<batchSize>]
         */
        else if(args.length >= 2 && args[0].equals("pasteHistory")) {
            return PasteHistoryCommand.INSTANCE.onCommand(sender, args);
        }
        /*
         * /structra delete <fileName>
         */
        else if(args.length >= 2 && args[0].equals("delete")) {
            return DeleteCommand.INSTANCE.onCommand(sender, args);
        }
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(args.length == 1) {
            return List.of("tool", "pos1", "pos2", "write", "paste","delete","pasteHistory");
        } else if(args.length == 2 && List.of("paste","delete").contains(args[0])) {
            return Util.savesFileNames();
        } else if(args.length == 2 && "pasteHistory".equals(args[0])) {
            return Util.historyFileNames();
        }
        return List.of();
    }
}
