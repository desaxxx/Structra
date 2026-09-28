package com.desoi.structra.command;

import com.desoi.structra.Structra;
import com.desoi.structra.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@NullMarked
public class ToolCommand implements SubCommand {

    @Override
    public String getName() {
        return "tool";
    }

    @Override
    public @Nullable String getPermission() {
        return "structra.tool";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.getInventory().addItem(Structra.SELECTOR_TOOL);
        Util.tell(player, "&eHere your tool!");
    }

    @Override
    public Collection<String> suggest(CommandSender sender, String[] args) {
        return List.of();
    }
}
