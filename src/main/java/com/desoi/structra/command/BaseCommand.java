package com.desoi.structra.command;

import org.bukkit.command.CommandSender;

public interface BaseCommand {

    boolean onCommand(CommandSender sender, String[] args);
}
