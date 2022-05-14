package net.tylermurphy.hideAndSeek.game.listener;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.game.util.Status;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!Main.getInstance().getBoard().contains((Player) event.getEntity())) return;
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) {
            if (event.getEntity() instanceof Player) {
                if (!Main.getInstance().getBoard().contains((Player) event.getEntity())) return;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (Main.getInstance().getBoard().contains(player) && Main.getInstance().getGame().getStatus() == Status.STANDBY) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (Main.getInstance().getBoard().contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

}
