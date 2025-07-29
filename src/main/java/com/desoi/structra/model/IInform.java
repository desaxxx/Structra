package com.desoi.structra.model;

import com.desoi.structra.util.Util;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.0-SNAPSHOT
 */
@ApiStatus.Experimental
public interface IInform {

    /**
     * Get the executor of task.
     * @return Executor
     * @since 1.0-SNAPSHOT
     */
    @NotNull CommandSender informer();

    /**
     * Check if task processes silently.
     * @return whether silent or not.
     * @since 1.0-SNAPSHOT
     */
    default boolean isSilent() {
        return false;
    }

    /**
     * Inform the executor.<br>
     * <b>NOTE:</b> Informing silently fails if {@link #isSilent()} is true.
     * @param messages Messages to inform
     * @since 1.0-SNAPSHOT
     */
    default void inform(String @NotNull ... messages) {
        if(isSilent()) return;
        Util.tell(informer(), messages);
    }

    /**
     * Inform the executor, ignore silent state of task.
     * @param messages Messages to inform
     * @since 1.0-SNAPSHOT
     */
    default void informIgnoreSilent(String @NotNull ... messages) {
        Util.tell(informer(), messages);
    }
}
