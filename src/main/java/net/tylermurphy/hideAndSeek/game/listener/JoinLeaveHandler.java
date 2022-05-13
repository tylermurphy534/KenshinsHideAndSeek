package net.tylermurphy.hideAndSeek.game.listener;

import net.tylermurphy.hideAndSeek.game.Board;
import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
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
import org.bukkit.potion.PotionEffect;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Config.exitPosition;

public class JoinLeaveHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Board.remove(event.getPlayer());
        Game.removeItems(event.getPlayer());
        if (Game.isNotSetup()) return;
        if (autoJoin){
            Game.join(event.getPlayer());
        } else if(teleportToExit) {
            if (event.getPlayer().getWorld().getName().equals(Game.getGameWorld()) || event.getPlayer().getWorld().getName().equals(lobbyWorld)) {
                event.getPlayer().teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
                event.getPlayer().setGameMode(GameMode.ADVENTURE);
            }
        } else {
            if (mapSaveEnabled && event.getPlayer().getWorld().getName().equals(Game.getGameWorld())) {
                if(Game.status != Status.STANDBY && Game.status != Status.ENDING){
                    Game.join(event.getPlayer());
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
        Board.remove(player);
        if(Game.status == Status.STANDBY) {
            Board.reloadLobbyBoards();
        } else {
            Board.reloadGameBoards();
        }
        for(PotionEffect effect : player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }
        Game.removeItems(player);
    }

}
