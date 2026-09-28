package com.desoi.structra.command;

import com.desoi.structra.util.Util;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * @since 1.0-SNAPSHOT
 */
public class MainCommand extends BaseCommand {

    /**
     * Structra command syntax.
     *
     * <p><b>Selection</b></p>
     * <ul>
     *   <li><code>/structra tool</code></li>
     *   <li><code>/structra pos1 [x y z world]</code> (player)</li>
     *   <li><code>/structra pos1 x y z world</code> (console)</li>
     *   <li><code>/structra pos2 [x y z world]</code> (player)</li>
     *   <li><code>/structra pos2 x y z world</code> (console)</li>
     * </ul>
     *
     * <p><b>Write</b></p>
     * <ul>
     *   <li><code>/structra write &lt;file&gt; [batchSize] [x y z world]</code> (player)</li>
     *   <li><code>/structra write &lt;file&gt; x y z world [batchSize]</code> (console)</li>
     * </ul>
     *
     * <p><b>Paste</b></p>
     * <ul>
     *   <li><code>/structra paste &lt;file&gt; &lt;batchSize&gt; [x y z world]</code> (player)</li>
     *   <li><code>/structra paste &lt;file&gt; x y z world [batchSize]</code> (console)</li>
     * </ul>
     *
     * <p>Additional paste flags:</p>
     * <ul>
     *   <li><code>--skipHistory</code></li>
     *   <li><code>--rotate &lt;90|180|270&gt;</code></li>
     * </ul>
     *
     * <p><b>History</b></p>
     * <ul>
     *   <li><code>/structra pasteHistory &lt;file&gt; [batchSize]</code></li>
     * </ul>
     *
     * <p><b>Delete</b></p>
     * <ul>
     *   <li><code>/structra delete &lt;file&gt;</code></li>
     * </ul>
     */
    public MainCommand() {
        register(new ToolCommand());
        register(new PosCommand());
        register(new WriteCommand());
        register(new PasteCommand());
        register(new PasteHistoryCommand());
        register(new DeleteCommand());

        registerPermissions();
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();
        if(args.length == 0) {
            sender.sendMessage("Invalid command.");
            return;
        }

        SubCommand subCommand = getSubCommand(args[0].toLowerCase(Locale.ROOT));
        if(subCommand == null) {
            sender.sendMessage("Invalid command.");
            return;
        }

        if(subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        if(subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage("You do not have permission to use this command.");
            return;
        }

        subCommand.execute(sender, args);
    }
}
