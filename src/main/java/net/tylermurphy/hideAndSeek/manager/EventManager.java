package net.tylermurphy.hideAndSeek.manager;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import net.md_5.bungee.api.ChatColor;

public class EventManager implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(status.equals("Playing") || status.equals("Starting")) {
			Spectator.addEntry(event.getPlayer().getName());
			resetPlayerData(event.getPlayer().getName(), false);
			event.getPlayer().sendMessage(messagePrefix + "You have joined mid game, and thus have been placed on the spectator team.");
			event.getPlayer().setGameMode(GameMode.SPECTATOR);
			event.getPlayer().getInventory().clear();
			for(PotionEffect effect : event.getPlayer().getActivePotionEffects()){
				event.getPlayer().removePotionEffect(effect.getType());
			}
			event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
		} else if(status.equals("Setup") || status.equals("Standby")) {
			Hider.addEntry(event.getPlayer().getName());
		}
		playerList.put(event.getPlayer().getName(), event.getPlayer());
		if(board == null) BoardManager.loadScoreboard();
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		playerList.remove(event.getPlayer().getName());
		Hider.removeEntry(event.getPlayer().getName());
		Seeker.removeEntry(event.getPlayer().getName());
		Spectator.removeEntry(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if(status.equals("Playing")) {
			if(Hider.hasEntry(event.getEntity().getName())) {
				Bukkit.getServer().broadcastMessage(String.format(messagePrefix + "%s%s has died", ChatColor.GOLD, event.getEntity().getName()));
			}
			if(Seeker.hasEntry(event.getEntity().getName())) {
				Bukkit.getServer().broadcastMessage(String.format(messagePrefix + "%s%s has died", ChatColor.RED, event.getEntity().getName()));
			}
			
			setPlayerData(event.getEntity().getName(), "Death", 1);
			setPlayerData(event.getEntity().getName(), "GiveStatus", 1);
		}
	}
	
}
