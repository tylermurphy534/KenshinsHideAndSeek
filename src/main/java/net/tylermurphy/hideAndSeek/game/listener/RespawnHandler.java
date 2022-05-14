package net.tylermurphy.hideAndSeek.game.listener;

import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RespawnHandler implements Listener {

    public static final Map<UUID, Location> temp_loc = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!Main.getInstance().getBoard().contains(player)) return;
        event.setKeepInventory(true);
        event.setDeathMessage("");
        temp_loc.put(player.getUniqueId(), player.getLocation());
        Main.getInstance().getLogger().severe("Player " + player.getName() + " died when not supposed to. Attempting to roll back death.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!Main.getInstance().getBoard().contains(player)) return;
        if (temp_loc.containsKey(player.getUniqueId())) {
            player.teleport(temp_loc.get(player.getUniqueId()));
            temp_loc.remove(player.getUniqueId());
        }
    }

}
