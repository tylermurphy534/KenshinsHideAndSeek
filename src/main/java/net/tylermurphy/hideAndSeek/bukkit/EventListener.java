package net.tylermurphy.hideAndSeek.bukkit;

import static net.tylermurphy.hideAndSeek.Store.*;

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
import org.bukkit.potion.PotionEffect;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Functions;
import net.tylermurphy.hideAndSeek.util.Packet;

public class EventListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.getPlayer().setLevel(0);
		if(!Functions.setup()) return;
		HiderTeam.removeEntry(event.getPlayer().getName());
		SeekerTeam.removeEntry(event.getPlayer().getName());
		SpectatorTeam.removeEntry(event.getPlayer().getName());
		if(status.equals("Playing") || status.equals("Starting")) {
			if(event.getPlayer().getWorld().getName().equals("hideandseek_"+spawnWorld)) {
				Spectator.add(event.getPlayer().getName());
				SpectatorTeam.addEntry(event.getPlayer().getName());
				event.getPlayer().sendMessage(messagePrefix + "You have joined mid game, and thus have been placed on the spectator team.");
				event.getPlayer().setGameMode(GameMode.SPECTATOR);
				event.getPlayer().getInventory().clear();
				for(PotionEffect effect : event.getPlayer().getActivePotionEffects()){
					event.getPlayer().removePotionEffect(effect.getType());
				}
				event.getPlayer().teleport(new Location(Bukkit.getWorld("hideandseek_"+spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			}
			if(event.getPlayer().getWorld().getName().equals(spawnWorld)) {
				event.getPlayer().teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
				event.getPlayer().setGameMode(GameMode.ADVENTURE);
			}
		} else if(status.equals("Setup") || status.equals("Standby")) {
			if(Functions.playerInProtectedWorld(event.getPlayer())){
				event.getPlayer().teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
				event.getPlayer().setGameMode(GameMode.ADVENTURE);
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if(!playerList.containsKey(event.getPlayer().getName())) return;
		playerList.remove(event.getPlayer().getName());
		Hider.remove(event.getPlayer().getName());
		HiderTeam.removeEntry(event.getPlayer().getName());
		Seeker.remove(event.getPlayer().getName());
		SeekerTeam.removeEntry(event.getPlayer().getName());
		Spectator.remove(event.getPlayer().getName());
		SpectatorTeam.removeEntry(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		if(!playerList.containsKey(event.getPlayer().getName())) return;
		playerList.remove(event.getPlayer().getName());
		Hider.remove(event.getPlayer().getName());
		HiderTeam.removeEntry(event.getPlayer().getName());
		Seeker.remove(event.getPlayer().getName());
		SeekerTeam.removeEntry(event.getPlayer().getName());
		Spectator.remove(event.getPlayer().getName());
		SpectatorTeam.removeEntry(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if(!playerList.containsKey(p.getName())) return;
			if(!status.equals("Playing")) {
				event.setCancelled(true);
				return;
			}
			Player attacker = null;
			if(event instanceof EntityDamageByEntityEvent) {
				Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
                if(damager instanceof Player) {
                	attacker = (Player) damager;
                	if(Hider.contains(attacker.getName()) && Hider.contains(p.getName())) event.setCancelled(true);
                	if(Seeker.contains(attacker.getName()) && Seeker.contains(p.getName())) event.setCancelled(true);
                	if(Spectator.contains(attacker.getName())) event.setCancelled(true);
                }
            }
			Player player = (Player) event.getEntity();
			if(player.getHealth()-event.getDamage() < 0) {
				if(spawnPosition == null) return;
				event.setCancelled(true);
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				player.teleport(new Location(Bukkit.getWorld("hideandseek_"+spawnWorld), spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()));
				Packet.playSound(player, Sound.ENTITY_PLAYER_DEATH, 1, 1);
				if(Seeker.contains(event.getEntity().getName())) {
					Bukkit.broadcastMessage(String.format(messagePrefix + "%s%s%s was killed", ChatColor.RED, event.getEntity().getName(), ChatColor.WHITE));
				}
				if(Hider.contains(event.getEntity().getName())) {
					if(attacker == null) {
						Functions.broadcastMessage(String.format(messagePrefix + "%s%s%s was found and became a seeker", ChatColor.GOLD, event.getEntity().getName(), ChatColor.WHITE));
					} else {
						Functions.broadcastMessage(String.format(messagePrefix + "%s%s%s was found by %s%s%s and became a seeker", ChatColor.GOLD, event.getEntity().getName(), ChatColor.WHITE, ChatColor.RED, attacker.getName(), ChatColor.WHITE));
					}
					Hider.remove(player.getName());
					Seeker.add(player.getName());
					SeekerTeam.addEntry(player.getName());
				}
				Functions.resetPlayer(player);
			}
		}
	}
	
	@EventHandler
	public void onProjectile(ProjectileLaunchEvent event) {
		if(!status.equals("Playing")) return;
		if(event.getEntity() instanceof Snowball) {
			Snowball snowball = (Snowball) event.getEntity();
			if(snowball.getShooter() instanceof Player) {
				if(!Functions.playerInProtectedWorld((Player) snowball.getShooter())) return;
				Player player = (Player) snowball.getShooter();
				if(Hider.contains(player.getName())) {
					Main.glow.onProjectilve();
					snowball.remove();
					player.getInventory().remove(Material.SNOWBALL);
				}
			}
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if(event.getEntity() instanceof Player) {
			if(!playerList.containsKey(event.getEntity().getName())) return;
			event.setCancelled(true);
		}
	}
	
	@EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if(event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN) {
        	if(event.getEntity() instanceof Player) {
        		if(!playerList.containsKey(event.getEntity().getName())) return;
    			event.setCancelled(true);
    		}
        }
    }
}
