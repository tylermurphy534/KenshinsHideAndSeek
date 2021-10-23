package net.tylermurphy.hideAndSeek.bukkit;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.tylermurphy.hideAndSeek.util.Packet;
import net.tylermurphy.hideAndSeek.util.Util;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.command.Start;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class EventListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.getPlayer().setLevel(0);
		Main.plugin.board.remove(event.getPlayer());
		if(!Util.isSetup()) return;
		if(event.getPlayer().getWorld().getName().equals("hideandseek_"+spawnWorld) || event.getPlayer().getWorld().getName().equals(lobbyWorld)){
			event.getPlayer().teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
			event.getPlayer().setGameMode(GameMode.ADVENTURE);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Main.plugin.board.remove(event.getPlayer());
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		Main.plugin.board.remove(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if(!Main.plugin.board.isPlayer(p)) return;
			if(!Main.plugin.status.equals("Playing")) {
				event.setCancelled(true);
				return;
			}
			Player attacker = null;
			if(event instanceof EntityDamageByEntityEvent) {
				Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
                if(damager instanceof Player) {
                	attacker = (Player) damager;
                	if(Main.plugin.board.onSameTeam(p, attacker)) event.setCancelled(true);
                	if(Main.plugin.board.isSpectator(p)) event.setCancelled(true);
                }
            }
			Player player = (Player) event.getEntity();
			if(player.getHealth()-event.getDamage() < 0) {
				if(spawnPosition == null) return;
				event.setCancelled(true);
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				player.teleport(new Location(Bukkit.getWorld("hideandseek_"+spawnWorld), spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()));
				Packet.playSound(player, Sound.ENTITY_PLAYER_DEATH, 1, 1);
				if(Main.plugin.board.isSeeker(player)) {
					Bukkit.broadcastMessage(message("GAME_PLAYER_DEATH").addPlayer(event.getEntity()).toString());
				}
				if(Main.plugin.board.isHider(player)) {
					if(attacker == null) {
						Util.broadcastMessage(message("GAME_PLAYER_FOUND").addPlayer(event.getEntity()).toString());
					} else {
						Util.broadcastMessage(message("GAME_PLAYER_FOUND_BY").addPlayer(event.getEntity()).addPlayer(attacker).toString());
					}
					Main.plugin.board.addSeeker(player);
				}
				Start.resetPlayer(player);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectile(ProjectileLaunchEvent event) {
		if(!Main.plugin.status.equals("Playing")) return;
		if(event.getEntity() instanceof Snowball) {
			Snowball snowball = (Snowball) event.getEntity();
			if(snowball.getShooter() instanceof Player) {
				Player player = (Player) snowball.getShooter();
				if(Main.plugin.board.isHider(player)) {
					Main.plugin.glow.onProjectilve();
					snowball.remove();
					player.getInventory().remove(Material.SNOWBALL);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if(event.getEntity() instanceof Player) {
			if(!Main.plugin.board.isPlayer((Player) event.getEntity())) return;
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if(event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN) {
        	if(event.getEntity() instanceof Player) {
        		if(!Main.plugin.board.isPlayer((Player) event.getEntity())) return;
    			event.setCancelled(true);
    		}
        }
    }
}
