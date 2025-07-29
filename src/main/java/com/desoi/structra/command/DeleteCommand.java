package com.desoi.structra.command;

import com.desoi.structra.Structra;
import com.desoi.structra.util.Util;
import com.desoi.structra.util.Validate;
import org.bukkit.command.CommandSender;

import java.io.File;

public class DeleteCommand implements BaseCommand {
    static public final DeleteCommand INSTANCE = new DeleteCommand();

    private DeleteCommand() {}

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String fileName = args[1];
        if(!sender.hasPermission("structra.delete." + fileName) && !sender.hasPermission("structra.delete.*")) {
            Util.tell(sender, "&cYou don't have permission to delete this Structra.");
            return true;
        }
        File file = new File(Structra.getSavesFolder(), fileName + Structra.FILE_EXTENSION);
        if(!file.exists()) {
            Util.tell(sender, "&cFile doesn't exist.");
            return true;
        }

        boolean result = Validate.validateException(file::delete, "Failed to delete file.", true);
        if(result) {
            Util.tell(sender, "&aFile was deleted successfully!");
        }
        return true;
    }
}
