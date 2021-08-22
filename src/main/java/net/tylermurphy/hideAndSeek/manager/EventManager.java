package net.tylermurphy.hideAndSeek.manager;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Functions;

public class EventManager implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(status.equals("Playing") || status.equals("Starting")) {
			Spectator.addEntry(event.getPlayer().getName());
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
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		playerList.remove(event.getPlayer().getName());
		Hider.removeEntry(event.getPlayer().getName());
		Seeker.removeEntry(event.getPlayer().getName());
		Spectator.removeEntry(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			if(!status.equals("Playing")) {
				event.setCancelled(true);
				return;
			}
			Player player = (Player) event.getEntity();
			if(player.getHealth()-event.getDamage() < 0) {
				if(spawnPosition == null) return;
				event.setCancelled(true);
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				player.teleport(new Location(player.getWorld(), spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()));
				Functions.playSound(player, Sound.ENTITY_PLAYER_DEATH, 1, 1);
				Functions.resetPlayer(player);
				if(Hider.hasEntry(event.getEntity().getName())) {
					Bukkit.broadcastMessage(String.format(messagePrefix + "%s%s%s has died and become a seeker", ChatColor.GOLD, event.getEntity().getName(), ChatColor.WHITE));
				}
				if(Seeker.hasEntry(event.getEntity().getName())) {
					Bukkit.broadcastMessage(String.format(messagePrefix + "%s%s%s has been beat by a hider", ChatColor.RED, event.getEntity().getName(), ChatColor.WHITE));
				}
				Seeker.addEntry(player.getName());
			}
		}
		
	}
	
	@EventHandler
	public void onProjectile(ProjectileLaunchEvent event) {
		if(!status.equals("Playing")) return;
		if(event.getEntity() instanceof Snowball) {
			Snowball snowball = (Snowball) event.getEntity();
			if(snowball.getShooter() instanceof Player) {
				Player player = (Player) snowball.getShooter();
				if(Hider.hasEntry(player.getName())) {
					glowTime++;
					snowball.remove();
					player.getInventory().remove(Material.SNOWBALL);
					int temp = gameId;
					Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
						public void run() {
							if(temp != gameId) return;
							glowTime--;
						}
					}, 20 * 30);
				}
			}
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(false);
	}
	
	@EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if(event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN)
            event.setCancelled(true);
    }
}
