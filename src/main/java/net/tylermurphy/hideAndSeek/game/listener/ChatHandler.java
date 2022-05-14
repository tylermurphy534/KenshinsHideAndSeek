package net.tylermurphy.hideAndSeek.game.listener;

import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().equals("fly")) {
            event.getPlayer().setAllowFlight(true);
            event.getPlayer().setFlying(true);
        }
        if (event.getMessage().equals("no fly")) {
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);
        }
        if (Main.getInstance().getBoard().isSeeker(event.getPlayer())) {
            event.setCancelled(true);
            Main.getInstance().getBoard().getSpectators().forEach(spectator -> spectator.sendMessage(ChatColor.GRAY + "[SPECTATOR] " + event.getPlayer().getName() + ": " + event.getMessage()));
        }
    }

}
