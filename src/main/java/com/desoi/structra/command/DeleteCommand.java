package com.desoi.structra.command;

import com.desoi.structra.Structra;
import com.desoi.structra.util.Util;
import com.desoi.structra.util.Validate;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;

@NullMarked
public class DeleteCommand implements SubCommand {

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public @Nullable String getPermission() {
        return "structra.delete";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String fileName = args[1];
        File file = new File(Structra.getInstance().getSavesFolder(), fileName + Structra.FILE_EXTENSION);
        if(!file.exists()) {
            Util.tell(sender, "&cFile doesn't exist.");
            return;
        }

        boolean result = Validate.validateException(file::delete, "Failed to delete file.", true);
        if(result) {
            Util.tell(sender, "&aFile was deleted successfully!");
        }
    }

    @Override
    public Collection<String> suggest(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Util.savesFileNames();
        }

        return List.of();
    }
}
