package net.tylermurphy.hideAndSeek.game.listener;

import com.cryptomorin.xseries.XMaterial;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.game.util.Status;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Config.glowPowerupItem;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class InteractHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Main.getInstance().getBoard().contains(event.getPlayer())) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && blockedInteracts.contains(event.getClickedBlock().getType().name())) {
            event.setCancelled(true);
            return;
        }
        ItemStack temp = event.getItem();
        if (temp == null) return;
        if (Main.getInstance().getGame().getStatus() == Status.STANDBY)
            onPlayerInteractLobby(temp, event);
        if (Main.getInstance().getGame().getStatus() == Status.PLAYING)
            onPlayerInteractGame(temp, event);
    }

    private void onPlayerInteractLobby(ItemStack temp, PlayerInteractEvent event) {
        if (temp.getItemMeta().getDisplayName().equalsIgnoreCase(lobbyLeaveItem.getItemMeta().getDisplayName()) && temp.getType() == lobbyLeaveItem.getType()) {
            event.setCancelled(true);
            Main.getInstance().getGame().leave(event.getPlayer());
        }

        if (temp.getItemMeta().getDisplayName().equalsIgnoreCase(lobbyStartItem.getItemMeta().getDisplayName()) && temp.getType() == lobbyStartItem.getType() && event.getPlayer().hasPermission("hideandseek.start")) {
            event.setCancelled(true);
            if (Main.getInstance().getGame().isNotSetup()) {
                event.getPlayer().sendMessage(errorPrefix + message("GAME_SETUP"));
                return;
            }
            if (Main.getInstance().getGame().getStatus() != Status.STANDBY) {
                event.getPlayer().sendMessage(errorPrefix + message("GAME_INPROGRESS"));
                return;
            }
            if (Main.getInstance().getBoard().size() < minPlayers) {
                event.getPlayer().sendMessage(errorPrefix + message("START_MIN_PLAYERS").addAmount(minPlayers));
                return;
            }
            Main.getInstance().getGame().start();
        }
    }

    private void onPlayerInteractGame(ItemStack temp, PlayerInteractEvent event) {
        if (temp.getItemMeta().getDisplayName().equalsIgnoreCase(glowPowerupItem.getItemMeta().getDisplayName()) && temp.getType() == glowPowerupItem.getType()) {
            if (!glowEnabled) return;
            Player player = event.getPlayer();
            if (Main.getInstance().getBoard().isHider(player)) {
                Main.getInstance().getGame().getGlow().onProjectile();
                player.getInventory().remove(glowPowerupItem);
                assert XMaterial.SNOWBALL.parseMaterial() != null;
                player.getInventory().remove(XMaterial.SNOWBALL.parseMaterial());
                event.setCancelled(true);
            }
        }
    }

}
