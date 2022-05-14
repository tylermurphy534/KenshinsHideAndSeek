package net.tylermurphy.hideAndSeek.game.listener;

import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Config.saveMaxZ;

public class MovementHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        if (!Main.getInstance().getBoard().contains(event.getPlayer())) return;
        if (!event.getPlayer().getWorld().getName().equals(Main.getInstance().getGame().getGameWorld())) return;
        if (event.getPlayer().hasPermission("hideandseek.leavebounds")) return;
        if (event.getTo() == null || event.getTo().getWorld() == null) return;
        if (!event.getTo().getWorld().getName().equals(Main.getInstance().getGame().getGameWorld())) return;
        if (event.getTo().getBlockX() < saveMinX || event.getTo().getBlockX() > saveMaxX || event.getTo().getBlockZ() < saveMinZ || event.getTo().getBlockZ() > saveMaxZ) {
            event.setCancelled(true);
        }
    }

}
