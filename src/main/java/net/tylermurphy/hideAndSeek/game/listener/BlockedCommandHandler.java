package net.tylermurphy.hideAndSeek.game.listener;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.game.util.Status;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static net.tylermurphy.hideAndSeek.configuration.Config.blockedCommands;
import static net.tylermurphy.hideAndSeek.configuration.Config.errorPrefix;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class BlockedCommandHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String[] array = message.split(" ");
        String[] temp = array[0].split(":");
        for(String handle : blockedCommands) {
            if (
                    array[0].substring(1).equalsIgnoreCase(handle) && Main.getInstance().getBoard().contains(player) ||
                            temp[temp.length-1].equalsIgnoreCase(handle) && Main.getInstance().getBoard().contains(player)
            ) {
                if (Main.getInstance().getGame().getStatus() == Status.STANDBY) return;
                player.sendMessage(errorPrefix + message("BLOCKED_COMMAND"));
                event.setCancelled(true);
                break;
            }
        }
    }

}
