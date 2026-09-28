package com.desoi.structra.command;

import com.desoi.structra.Structra;
import com.desoi.structra.history.HistoryFile;
import com.desoi.structra.loader.StructureLoader;
import com.desoi.structra.model.BlockTraversalOrder;
import com.desoi.structra.model.StructraException;
import com.desoi.structra.util.Util;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;

@NullMarked
public class PasteHistoryCommand implements SubCommand {

    @Override
    public String getName() {
        return "pasteHistory";
    }

    @Override
    public @Nullable String getPermission() {
        return "structra.paste";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String fileName = args[1];
        File file = new File(Structra.getInstance().getHistoryFolder(), fileName + Structra.FILE_EXTENSION);
        if(!file.exists()) {
            Util.tell(sender, "&cFile doesn't exist.");
            return;
        }

        int batchSize = 50;
        if(args.length > 2) {
            batchSize = Util.parseInt(args[2], batchSize);
        }

        try {
            HistoryFile historyFile = new HistoryFile(file);
            StructureLoader structureLoader = new StructureLoader(historyFile, sender, 0, 20, batchSize, BlockTraversalOrder.DEFAULT);
            structureLoader.createPasteTask().execute();
        } catch (StructraException e) {
            Util.tellError(sender, e);
            return;
        }

        Util.tell(sender, "&aLoading History Structure...");
    }

    @Override
    public Collection<String> suggest(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Util.historyFileNames();
        } else if (args.length == 3) {
            return List.of("50", "100", "500", "1000");
        }

        return List.of();
    }
}
