package net.tylermurphy.hideAndSeek.events;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
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
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Functions;

public class EventListener implements Listener {
	
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
			event.getPlayer().teleport(new Location(Bukkit.getWorld(spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
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
	public void onEntityDamage(EntityDamageEvent event) {
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
				player.teleport(new Location(Bukkit.getWorld(spawnWorld), spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()));
				Functions.playSound(player, Sound.ENTITY_PLAYER_DEATH, 1, 1);
				if(Hider.hasEntry(event.getEntity().getName())) {
					Bukkit.broadcastMessage(String.format(messagePrefix + "%s%s%s has died and became a seeker", ChatColor.GOLD, event.getEntity().getName(), ChatColor.WHITE));
				}
				if(Seeker.hasEntry(event.getEntity().getName())) {
					Bukkit.broadcastMessage(String.format(messagePrefix + "%s%s%s has been beat by a hider", ChatColor.RED, event.getEntity().getName(), ChatColor.WHITE));
				}
				Seeker.addEntry(player.getName());
				Functions.resetPlayer(player);
			}
		}
		
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof ArmorStand) {
			if(unbreakableArmorstands) {
				if(event.getDamager() instanceof Player) {
					Player player = (Player) event.getDamager();
					if(status.equals("Playing") || status.equals("Starting") || !player.hasPermission("hideandseek.blockbypass")) {
						System.out.println('t');
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(!interactableArmorstands) {
			if(event.getRightClicked() instanceof ArmorStand) {
				if(status.equals("Playing") || status.equals("Starting") || !event.getPlayer().hasPermission("hideandseek.blockbypass")) {
					event.setCancelled(true);
				}
			}
		}
		if(!interactableItemframes) {
			if(event.getRightClicked() instanceof ItemFrame) {
				if(status.equals("Playing") || status.equals("Starting") || !event.getPlayer().hasPermission("hideandseek.blockbypass")) {
					event.setCancelled(true);
				}
			}
		}
	}
		
	@EventHandler
	public void onPlayerInteractBlock(PlayerInteractEvent event) {
		if(!interactableDoors) {
			if(
					event.getClickedBlock().getType() == Material.ACACIA_DOOR ||
					event.getClickedBlock().getType() == Material.BIRCH_DOOR ||
					event.getClickedBlock().getType() == Material.CRIMSON_DOOR ||
					event.getClickedBlock().getType() == Material.DARK_OAK_DOOR ||
					event.getClickedBlock().getType() == Material.IRON_DOOR ||
					event.getClickedBlock().getType() == Material.JUNGLE_DOOR ||
					event.getClickedBlock().getType() == Material.OAK_DOOR ||
					event.getClickedBlock().getType() == Material.SPRUCE_DOOR ||
					event.getClickedBlock().getType() == Material.WARPED_DOOR
				) {
				if(status.equals("Playing") || status.equals("Starting") || !event.getPlayer().hasPermission("hideandseek.blockbypass")) {
					event.setCancelled(true);
				}
			}
		}
		if(!interactableTrapdoors) {
			if(
					event.getClickedBlock().getType() == Material.ACACIA_TRAPDOOR ||
					event.getClickedBlock().getType() == Material.BIRCH_TRAPDOOR ||
					event.getClickedBlock().getType() == Material.CRIMSON_TRAPDOOR ||
					event.getClickedBlock().getType() == Material.DARK_OAK_TRAPDOOR ||
					event.getClickedBlock().getType() == Material.IRON_TRAPDOOR ||
					event.getClickedBlock().getType() == Material.JUNGLE_TRAPDOOR ||
					event.getClickedBlock().getType() == Material.OAK_TRAPDOOR ||
					event.getClickedBlock().getType() == Material.SPRUCE_TRAPDOOR ||
					event.getClickedBlock().getType() == Material.WARPED_TRAPDOOR
				) {
				if(status.equals("Playing") || status.equals("Starting") || !event.getPlayer().hasPermission("hideandseek.blockbypass")) {
					event.setCancelled(true);
				}
			}
		}
		if(!interactableFencegate) {
			if(
					event.getClickedBlock().getType() == Material.ACACIA_FENCE_GATE ||
					event.getClickedBlock().getType() == Material.BIRCH_FENCE_GATE ||
					event.getClickedBlock().getType() == Material.CRIMSON_FENCE_GATE ||
					event.getClickedBlock().getType() == Material.DARK_OAK_FENCE_GATE ||
					event.getClickedBlock().getType() == Material.JUNGLE_FENCE_GATE ||
					event.getClickedBlock().getType() == Material.OAK_FENCE_GATE ||
					event.getClickedBlock().getType() == Material.SPRUCE_FENCE_GATE ||
					event.getClickedBlock().getType() == Material.WARPED_FENCE_GATE
				) {
				if(status.equals("Playing") || status.equals("Starting") || !event.getPlayer().hasPermission("hideandseek.blockbypass")) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		if(event.getEntity() instanceof ItemFrame) {
			if(unbreakableItemframes) {
				if(event.getRemover() instanceof Player) {
					Player player = (Player) event.getRemover();
					if(status.equals("Playing") || status.equals("Starting") || !player.hasPermission("hideandseek.blockbypass")) {
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
				
			}
		}
		if(event.getEntity() instanceof Painting) {
			if(unbreakableArmorstands) {
				if(event.getRemover() instanceof Player) {
					Player player = (Player) event.getRemover();
					if(status.equals("Playing") || status.equals("Starting") || !player.hasPermission("hideandseek.blockbypass")) {
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
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
		event.setCancelled(true);
	}
	
	@EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if(event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN)
            event.setCancelled(true);
    }
	
	@EventHandler
	public void onPlayerCommandPreProccess(PlayerCommandPreprocessEvent event) {
		if(status.equals("Setup") || status.equals("Standby")) return;
		String handle = event.getMessage().split(" ")[0].substring(1);
		for(String blocked : blockedCommands) {
			if(handle.equalsIgnoreCase(blocked) || handle.equalsIgnoreCase("minecraft:"+blocked)) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(errorPrefix + "This command is blocked during gameplay!");
				break;
			}
		}
	}
}
