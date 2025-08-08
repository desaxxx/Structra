package com.desoi.structra.listener;

import com.desoi.structra.Structra;
import com.desoi.structra.model.Position;
import com.desoi.structra.util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BukkitListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if(!(action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK)) return;
        if(event.getItem() == null || !event.getItem().equals(Structra.SELECTOR_TOOL)) return;
        if(!event.getPlayer().hasPermission("structra.select")) return;
        if(event.getClickedBlock() == null) return;

        event.setCancelled(true);
        Position position = Position.fromLocation(event.getClickedBlock().getLocation(), true);
        if(action == Action.LEFT_CLICK_BLOCK) {
            Util.selectPosition(event.getPlayer(), position, 1);
        }else {
            Util.selectPosition(event.getPlayer(), position, 2);
        }
    }
}
