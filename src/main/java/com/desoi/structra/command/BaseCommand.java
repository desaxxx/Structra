package com.desoi.structra.command;

import com.desoi.structra.Structra;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public abstract class BaseCommand implements BasicCommand {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    protected BaseCommand() {}

    protected void register(SubCommand subCommand) {
        subCommands.put(subCommand.getName(), subCommand);
        if (!subCommand.aliases().isEmpty()) {
            subCommand.aliases().forEach(alias -> subCommands.put(alias, subCommand));
        }
    }

    protected void unregister(SubCommand subCommand) {
        subCommands.remove(subCommand.getName());
        if (!subCommand.aliases().isEmpty()) {
            subCommand.aliases().forEach(subCommands::remove);
        }
    }

    protected void registerPermissions() {
        PluginManager pm = Bukkit.getPluginManager();

        for(SubCommand command : subCommands.values()) {
            if(command.getPermission() == null) {
                continue;
            }

            try {
                pm.addPermission(new Permission(command.getPermission()));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    protected @Nullable SubCommand getSubCommand(String name) {
        return subCommands.get(name);
    }

    protected Set<SubCommand> getSubCommands() {
        return Set.copyOf(subCommands.values());
    }

    protected Set<String> getSubCommandNames() {
        return Set.copyOf(subCommands.keySet());
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        String subCommandArg = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "";

        if(args.length == 0 || args.length == 1) {
            return subCommands.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(subCommandArg))
                    .filter(entry -> showTabComplete(source.getSender(), entry.getValue()))
                    .map(Map.Entry::getKey)
                    .toList();
        }

        SubCommand subCommand = subCommands.get(subCommandArg);
        if(!showTabComplete(source.getSender(), subCommand)) {
            return List.of();
        }

        return subCommand.suggest(source.getSender(), args);
    }

    protected boolean showTabComplete(CommandSender sender, @Nullable SubCommand subCommand) {
        if(subCommand == null) {
            return false;
        }

        if(subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission()) && subCommand.hideIfNoPermission()) {
            return false;
        }

        return true;
    }
}
