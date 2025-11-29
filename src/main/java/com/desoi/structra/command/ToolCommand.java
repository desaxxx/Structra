package com.desoi.structra.command;

import com.desoi.structra.Structra;
import com.desoi.structra.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToolCommand implements BaseCommand {
    public static final ToolCommand INSTANCE = new ToolCommand();

    private ToolCommand() {}

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission("structra.tool")) {
            Util.tell(sender, "&cYou don't have permission to get the too.");
            return true;
        }
        if(!(sender instanceof Player player)) {
            Util.tell(sender, "&cYou can't use this command on console.");
            return true;
        }
        player.getInventory().addItem(Structra.SELECTOR_TOOL);
        Util.tell(player, "&eHere your tool!");
        return true;
    }
}
