package com.desoi.structra.command;

import com.desoi.structra.Structra;
import com.desoi.structra.history.HistoryFile;
import com.desoi.structra.loader.StructureLoader;
import com.desoi.structra.model.BlockTraversalOrder;
import com.desoi.structra.util.Util;
import org.bukkit.command.CommandSender;

import java.io.File;

public class PasteHistoryCommand implements BaseCommand {
    public static final PasteHistoryCommand INSTANCE = new PasteHistoryCommand();

    private PasteHistoryCommand() {}

    /*
     * PLAYER: /structra paste <fileName> [<batchSize>] [<x>] [<y>] [<z>] [<world>]
     * CONSOLE: /structra paste <fileName> <x> <y> <z> <world> [<batchSize>]
     */
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String fileName = args[1];
        if(!sender.hasPermission("structra.paste." + fileName) && !sender.hasPermission("structra.paste.*")) {
            Util.tell(sender, "&cYou don't have permission to paste this Structra.");
            return true;
        }
        File file = new File(Structra.getHistoryFolder(), fileName + Structra.FILE_EXTENSION);
        if(!file.exists()) {
            Util.tell(sender, "&cFile doesn't exist.");
            return true;
        }

        int batchSize = 50;
        if(args.length > 2) {
            batchSize = Util.parseInt(args[2], batchSize);
        }

        HistoryFile historyFile = new HistoryFile(file);
        StructureLoader structureLoader = new StructureLoader(historyFile, sender, 0, 20, batchSize, BlockTraversalOrder.DEFAULT);
        structureLoader.createPasteTask().execute();
        Util.tell(sender, "&aLoading History Structure...");
        return true;
    }
}
