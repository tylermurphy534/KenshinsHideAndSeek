/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation, either version 3 of the License.
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

import net.tylermurphy.hideAndSeek.command.Join;
import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

import net.tylermurphy.hideAndSeek.util.Packet;
import org.bukkit.potion.PotionEffect;

import java.util.Objects;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class EventListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Board.remove(event.getPlayer());
		Game.removeItems(event.getPlayer());
		if(Game.isNotSetup()) return;
		if(autoJoin){
			Join.join(event.getPlayer());
		} else if(teleportToExit) {
			if (event.getPlayer().getWorld().getName().equals("hideandseek_" + spawnWorld) || event.getPlayer().getWorld().getName().equals(lobbyWorld)) {
				event.getPlayer().teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
				event.getPlayer().setGameMode(GameMode.ADVENTURE);
			}
		} else {
			if (event.getPlayer().getWorld().getName().equals("hideandseek_" + spawnWorld)) {
				event.getPlayer().teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
				event.getPlayer().setGameMode(GameMode.ADVENTURE);
			}
		}
	}
	
	@EventHandler
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
	
	@EventHandler
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

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		if(Board.isSeeker(event.getPlayer())){
			event.setCancelled(true);
			Board.getSpectators().forEach(spectator -> spectator.sendMessage(ChatColor.GRAY + "[SPECTATOR] " + event.getPlayer().getName() + ": " + event.getMessage()));
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event){
		if(!event.getPlayer().getWorld().getName().equals("hideandseek_" + spawnWorld)) return;
		if(event.getPlayer().hasPermission("hideandseek.leavebounds")) return;
		if(event.getTo() == null || event.getTo().getWorld() == null) return;
		if(!event.getTo().getWorld().getName().equals("hideandseek_" + spawnWorld)) return;
		if(event.getTo().getBlockX() < saveMinX || event.getTo().getBlockX() > saveMinX || event.getTo().getBlockZ() < saveMinZ || event.getTo().getBlockZ() > saveMaxZ){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		try {
			if (event.getEntity() instanceof Player) {
				Player p = (Player) event.getEntity();
				if (!Board.isPlayer(p)) return;
				if (Game.status != Status.PLAYING) {
					event.setCancelled(true);
					return;
				}
				Player attacker = null;
				if (event instanceof EntityDamageByEntityEvent) {
					Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
					if (damager instanceof Player) {
						attacker = (Player) damager;
						if (Board.onSameTeam(p, attacker)) event.setCancelled(true);
						if (Board.isSpectator(p)) event.setCancelled(true);
					}
				}
				Player player = (Player) event.getEntity();
				if (player.getHealth() - event.getDamage() < 0 || !pvpEnabled) {
					if (spawnPosition == null) return;
					event.setCancelled(true);
					player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
					player.teleport(new Location(Bukkit.getWorld("hideandseek_" + spawnWorld), spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()));
					Packet.playSound(player, Sound.ENTITY_PLAYER_DEATH, 1, 1);
					if (Board.isSeeker(player)) {
						Bukkit.broadcastMessage(message("GAME_PLAYER_DEATH").addPlayer(event.getEntity()).toString());
					}
					if (Board.isHider(player)) {
						if (attacker == null) {
							Game.broadcastMessage(message("GAME_PLAYER_FOUND").addPlayer(event.getEntity()).toString());
						} else {
							Game.broadcastMessage(message("GAME_PLAYER_FOUND_BY").addPlayer(event.getEntity()).addPlayer(attacker).toString());
						}
						Board.addSeeker(player);
					}
					Game.resetPlayer(player);
					Board.reloadBoardTeams();
				}
			}
		} catch (Exception e){
			//Has shown to cause problems, so ignore if exception
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
}
