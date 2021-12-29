/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation version 3.
 *
 * Kenshins Hide and Seek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.tylermurphy.hideAndSeek.game;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.*;

import net.tylermurphy.hideAndSeek.util.Packet;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class EventListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Board.remove(event.getPlayer());
		Game.removeItems(event.getPlayer());
		if(Game.isNotSetup()) return;
		if(autoJoin){
			Game.join(event.getPlayer());
		} else if(teleportToExit) {
			if (event.getPlayer().getWorld().getName().equals("hideandseek_" + spawnWorld) || event.getPlayer().getWorld().getName().equals(lobbyWorld)) {
				event.getPlayer().teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
				event.getPlayer().setGameMode(GameMode.ADVENTURE);
			}
		} else {
			if (event.getPlayer().getWorld().getName().equals("hideandseek_" + spawnWorld)) {
				if(Game.status != Status.STANDBY){
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
		Board.remove(event.getPlayer());
		if(Game.status == Status.STANDBY) {
			Board.reloadLobbyBoards();
		} else {
			Board.reloadGameBoards();
		}
		for(PotionEffect effect : event.getPlayer().getActivePotionEffects()){
			event.getPlayer().removePotionEffect(effect.getType());
		}
		Game.removeItems(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKick(PlayerKickEvent event) {
		Board.remove(event.getPlayer());
		if(Game.status == Status.STANDBY) {
			Board.reloadLobbyBoards();
		} else {
			Board.reloadGameBoards();
		}
		for(PotionEffect effect : event.getPlayer().getActivePotionEffects()){
			event.getPlayer().removePotionEffect(effect.getType());
		}
		Game.removeItems(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event){
		if(Board.isSeeker(event.getPlayer())){
			event.setCancelled(true);
			Board.getSpectators().forEach(spectator -> spectator.sendMessage(ChatColor.GRAY + "[SPECTATOR] " + event.getPlayer().getName() + ": " + event.getMessage()));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent event){
		if(!event.getPlayer().getWorld().getName().equals("hideandseek_" + spawnWorld)) return;
		if(event.getPlayer().hasPermission("hideandseek.leavebounds")) return;
		if(event.getTo() == null || event.getTo().getWorld() == null) return;
		if(!event.getTo().getWorld().getName().equals("hideandseek_" + spawnWorld)) return;
		if(event.getTo().getBlockX() < saveMinX || event.getTo().getBlockX() > saveMaxX || event.getTo().getBlockZ() < saveMinZ || event.getTo().getBlockZ() > saveMaxZ){
			event.setCancelled(true);
		}
	}

	Map<UUID, Location> temp_loc = new HashMap<>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		if(!Board.isPlayer(player)) return;
		event.setKeepInventory(true);
		event.setDeathMessage("");
		temp_loc.put(player.getUniqueId(), player.getLocation());
		Main.plugin.getLogger().severe("Player "+player.getName() + " died when not supposed to. Attempting to roll back death.");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(!Board.isPlayer(player)) return;
		if(temp_loc.containsKey(player.getUniqueId())){
			player.teleport(temp_loc.get(player.getUniqueId()));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		try {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				if (!Board.isPlayer(player)) {
					if (event instanceof EntityDamageByEntityEvent) {
						Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
						if (damager instanceof Player) {
							if(Board.isPlayer(damager)){
								event.setCancelled(true);
								return;
							}
						}
					}
				}
				if (Game.status != Status.PLAYING) {
					event.setCancelled(true);
					return;
				}
				Player attacker = null;
				if (event instanceof EntityDamageByEntityEvent) {
					Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
					if (damager instanceof Player) {
						attacker = (Player) damager;
						if (Board.onSameTeam(player, attacker)) event.setCancelled(true);
						if (Board.isSpectator(player)) event.setCancelled(true);
					} else if(damager instanceof Arrow){
						ProjectileSource source = ((Arrow) damager).getShooter();
						if(source instanceof Player){
							attacker = (Player) source;
							if (Board.onSameTeam(player, attacker)) event.setCancelled(true);
							if (Board.isSpectator(player)) event.setCancelled(true);
						}
					}
				}
				if (player.getHealth() - event.getDamage() < 0 || !pvpEnabled) {
					if (spawnPosition == null) return;
					event.setCancelled(true);
					AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
					if(attribute != null)
						player.setHealth(attribute.getValue());
					player.teleport(new Location(Bukkit.getWorld("hideandseek_" + spawnWorld), spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()));
					Packet.playSound(player, Sound.ENTITY_PLAYER_DEATH, 1, 1);
					if (Board.isSeeker(player)) {
						Bukkit.broadcastMessage(message("GAME_PLAYER_DEATH").addPlayer(player).toString());
					}
					if (Board.isHider(player)) {
						if (attacker == null) {
							Game.broadcastMessage(message("GAME_PLAYER_FOUND").addPlayer(player).toString());
						} else {
							Game.broadcastMessage(message("GAME_PLAYER_FOUND_BY").addPlayer(player).addPlayer(attacker).toString());
						}
						Board.addSeeker(player);
					}
					Game.resetPlayer(player);
					Board.reloadBoardTeams();
				}
			}
		} catch (Exception e){
			Main.plugin.getLogger().severe("Entity Damage Event Error: " + e.getMessage());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectile(ProjectileLaunchEvent event) {
		if(Game.status != Status.PLAYING) return;
		if(event.getEntity() instanceof Snowball) {
			if(!glowEnabled) return;
			Snowball snowball = (Snowball) event.getEntity();
			if(snowball.getShooter() instanceof Player) {
				Player player = (Player) snowball.getShooter();
				if(Board.isHider(player)) {
					Game.glow.onProjectile();
					snowball.remove();
					player.getInventory().remove(Material.SNOWBALL);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if(event.getEntity() instanceof Player) {
			if(!Board.isPlayer((Player) event.getEntity())) return;
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if(event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN) {
        	if(event.getEntity() instanceof Player) {
        		if(!Board.isPlayer((Player) event.getEntity())) return;
    			event.setCancelled(true);
    		}
        }
    }

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		String[] array = message.split(" ");
		if(array[0].equalsIgnoreCase("/kill")){
			if(Board.isPlayer(player)){
				Main.plugin.getLogger().info("Blocking "+player.getName()+ "from running /kill with anyone associated in the lobby");
				event.setCancelled(true);
			} else if(array.length > 1){
				for(int i=1; i<array.length; i++){
					if(Board.isPlayer(array[i])){
						Main.plugin.getLogger().info("Blocking "+player.getName()+ "from running /kill with anyone associated in the lobby");
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}
}
