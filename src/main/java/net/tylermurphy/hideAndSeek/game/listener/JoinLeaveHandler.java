package net.tylermurphy.hideAndSeek.game.listener;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.configuration.Items;
import net.tylermurphy.hideAndSeek.game.PlayerLoader;
import net.tylermurphy.hideAndSeek.game.util.Status;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Config.exitPosition;

public class JoinLeaveHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!Main.getInstance().getDatabase().getNameData().update(event.getPlayer().getUniqueId(), event.getPlayer().getName())){
            Main.getInstance().getLogger().warning("Failed to save name data for user: " + event.getPlayer().getName());
        }
        Main.getInstance().getBoard().remove(event.getPlayer());
        removeItems(event.getPlayer());
        if (Main.getInstance().getGame().isNotSetup()) return;
        if (autoJoin) {
            Main.getInstance().getGame().join(event.getPlayer());
        } else if (teleportToExit) {
            if (event.getPlayer().getWorld().getName().equals(Main.getInstance().getGame().getGameWorld()) || event.getPlayer().getWorld().getName().equals(lobbyWorld)) {
                event.getPlayer().teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
                event.getPlayer().setGameMode(GameMode.ADVENTURE);
            }
        } else {
            if (mapSaveEnabled && event.getPlayer().getWorld().getName().equals(Main.getInstance().getGame().getGameWorld())) {
                if (Main.getInstance().getGame().getStatus() != Status.STANDBY && Main.getInstance().getGame().getStatus() != Status.ENDING) {
                    Main.getInstance().getGame().join(event.getPlayer());
                } else {
                    event.getPlayer().teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
                    event.getPlayer().setGameMode(GameMode.ADVENTURE);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        handleLeave(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent event) {
        handleLeave(event.getPlayer());
    }

    private void handleLeave(Player player) {
        if(!Main.getInstance().getBoard().contains(player)) return;
        PlayerLoader.unloadPlayer(player);
        Main.getInstance().getBoard().remove(player);
        if(saveInventory) {
            ItemStack[] data = Main.getInstance().getDatabase().getInventoryData().getInventory(player.getUniqueId());
            player.getInventory().setContents(data);
        }
        if (Main.getInstance().getGame().getStatus() == Status.STANDBY) {
            Main.getInstance().getBoard().reloadLobbyBoards();
        } else {
            Main.getInstance().getBoard().reloadGameBoards();
        }
    }

    private void removeItems(Player player) {
        for(ItemStack si : Items.SEEKER_ITEMS)
            for(ItemStack i : player.getInventory().getContents())
                if (si.isSimilar(i)) player.getInventory().remove(i);
        for(ItemStack hi : Items.HIDER_ITEMS)
            for(ItemStack i : player.getInventory().getContents())
                if (hi.isSimilar(i)) player.getInventory().remove(i);
    }

}
