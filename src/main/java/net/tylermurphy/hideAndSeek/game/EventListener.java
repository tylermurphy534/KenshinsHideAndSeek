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

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Status;
import net.tylermurphy.hideAndSeek.util.Version;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import org.bukkit.inventory.ItemStack;
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

	public static final Map<UUID, Location> temp_loc = new HashMap<>();

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
			temp_loc.remove(player.getUniqueId());
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
					return;
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
						if (!Board.isPlayer(attacker)) event.setCancelled(true);
						if (Board.onSameTeam(player, attacker)) event.setCancelled(true);
						if (Board.isSpectator(player)) event.setCancelled(true);
					} else if(damager instanceof Arrow){
						ProjectileSource source = ((Arrow) damager).getShooter();
						if(source instanceof Player){
							attacker = (Player) source;
							if (!Board.isPlayer(attacker)) event.setCancelled(true);
							if (Board.onSameTeam(player, attacker)) event.setCancelled(true);
							if (Board.isSpectator(player)) event.setCancelled(true);
						}
					}
				}
				if(event.isCancelled()) return;
				if (player.getHealth() - event.getFinalDamage() < 0.5 || !pvpEnabled) {
					if (event instanceof EntityDamageByEntityEvent && !pvpEnabled) {
						Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
						if (damager instanceof Player) {
							Player atacker = (Player) damager;
							if(!Board.isSeeker(atacker)){
								event.setCancelled(true);
								return;
							}
						} else {
							event.setCancelled(true);
							return;
						}
					} else if(!pvpEnabled) {
						event.setCancelled(true);
						return;
					}
					if (spawnPosition == null) return;
					event.setCancelled(true);
					if(Version.atLeast("1.9")) {
						AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
						if (attribute != null) player.setHealth(attribute.getValue());
					} else {
						player.setHealth(player.getMaxHealth());
					}
					player.teleport(new Location(Bukkit.getWorld("hideandseek_" + spawnWorld), spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()));
					if(Version.atLeast("1.9")){
						XSound.ENTITY_PLAYER_DEATH.play(player, 1, 1);
					} else {
						XSound.ENTITY_PLAYER_HURT.play(player, 1, 1);
					}
					if (Board.isSeeker(player)) {
						Game.broadcastMessage(message("GAME_PLAYER_DEATH").addPlayer(player).toString());
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
		String[] temp = array[0].split(":");
		for(String handle : blockedCommands){
			if(
				array[0].substring(1).equalsIgnoreCase(handle) && Board.isPlayer(player) ||
				temp[temp.length-1].equalsIgnoreCase(handle) && Board.isPlayer(player)
			) {
				if(Game.status == Status.STANDBY) return;
				player.sendMessage(errorPrefix + message("BLOCKED_COMMAND"));
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!Board.isPlayer(event.getPlayer())) return;
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && blockedInteracts.contains(event.getClickedBlock().getType().name())){
			event.setCancelled(true);
			return;
		}
		ItemStack temp = event.getItem();
		if(temp == null) return;
		if(Game.status == Status.STANDBY)
			onPlayerInteractLobby(temp, event);
		if(Game.status == Status.PLAYING)
			onPlayerInteractGame(temp, event);
	}

	private void onPlayerInteractLobby(ItemStack temp, PlayerInteractEvent event){
		if (temp.getItemMeta().getDisplayName().equalsIgnoreCase(lobbyLeaveItem.getItemMeta().getDisplayName()) && temp.getType() == lobbyLeaveItem.getType()) {
			event.setCancelled(true);
			Game.leave(event.getPlayer());
		}

		if (temp.getItemMeta().getDisplayName().equalsIgnoreCase(lobbyStartItem.getItemMeta().getDisplayName()) && temp.getType() == lobbyStartItem.getType() && event.getPlayer().hasPermission("hideandseek.start")) {
			event.setCancelled(true);
			if (Game.isNotSetup()) {
				event.getPlayer().sendMessage(errorPrefix + message("GAME_SETUP"));
				return;
			}
			if (Game.status != Status.STANDBY) {
				event.getPlayer().sendMessage(errorPrefix + message("GAME_INPROGRESS"));
				return;
			}
			if (Board.size() < minPlayers) {
				event.getPlayer().sendMessage(errorPrefix + message("START_MIN_PLAYERS").addAmount(minPlayers));
				return;
			}
			Game.start();
		}
	}

	private void onPlayerInteractGame(ItemStack temp, PlayerInteractEvent event){
		if (temp.getItemMeta().getDisplayName().equalsIgnoreCase(glowPowerupItem.getItemMeta().getDisplayName()) && temp.getType() == glowPowerupItem.getType()) {
			if(!glowEnabled) return;
			Player player = event.getPlayer();
			if(Board.isHider(player)) {
				Game.glow.onProjectile();
				player.getInventory().remove(glowPowerupItem);
				assert XMaterial.SNOWBALL.parseMaterial() != null;
				player.getInventory().remove(XMaterial.SNOWBALL.parseMaterial());
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getWhoClicked() instanceof Player){
			Player player = (Player) event.getWhoClicked();
			if(Board.isPlayer(player) && Game.status == Status.STANDBY){
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemDrop(PlayerDropItemEvent event) {
		if(Board.isPlayer(event.getPlayer())){
			event.setCancelled(true);
		}
	}
}