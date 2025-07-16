package com.desoi.structra.listener;

import com.desoi.structra.Structra;
import com.desoi.structra.util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class BukkitListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if(!(action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK)) return;
        if(event.getItem() == null || !event.getItem().equals(Structra.SELECTOR_TOOL)) return;
        if(event.getClickedBlock() == null) return;

        event.setCancelled(true);
        Vector vector = event.getClickedBlock().getLocation().toVector();
        if(action == Action.LEFT_CLICK_BLOCK) {
            Util.selectVector(event.getPlayer(), vector, 1);
        }else {
            Util.selectVector(event.getPlayer(), vector, 2);
        }
    }
}
