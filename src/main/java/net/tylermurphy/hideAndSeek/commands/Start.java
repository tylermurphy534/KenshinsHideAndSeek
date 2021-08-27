package net.tylermurphy.hideAndSeek.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Functions;
import net.tylermurphy.hideAndSeek.util.ICommand;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.io.File;
import java.util.Random;

public class Start implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!status.equals("Standby")) {
			sender.sendMessage(errorPrefix + "Game is already in session");
			return;
		}
		if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) {
			sender.sendMessage(errorPrefix + "Please set spawn location first");
			return;
		}
		File destenation = new File(Main.root+File.separator+"hideandseek_"+spawnWorld);
		if(!destenation.exists()) {
			sender.sendMessage(errorPrefix + "Please set map save first");
			return;
		} else {
			if(Bukkit.getServer().getWorld("hideandseek_"+spawnWorld) != null) {
				Functions.rollback("hideandseek_"+spawnWorld);
			} else {
				Functions.loadMap("hideandseek_"+spawnWorld);
			}
		}
		if(playerList.size() < minPlayers) {
			sender.sendMessage(errorPrefix + "You must have at least "+minPlayers+" players to start");
			return;
		}
		String seekerName;
		if(args.length < 1) {
			seekerName = playerList.values().stream().skip(new Random().nextInt(playerList.values().size())).findFirst().get().getName();
		} else {
			seekerName = args[0];
		}
		Player seeker = playerList.get(seekerName);
		if(seeker == null) {
			sender.sendMessage(errorPrefix + "Invalid player: " + seekerName);
			return;
		}
		for(Player temp : playerList.values()) {
			Hider.addEntry(temp.getName());
		}
		Seeker.addEntry(seeker.getName());
		
		for(Player player : playerList.values()) {
			player.getInventory().clear();
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(new Location(Bukkit.getWorld("hideandseek_"+spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			for(PotionEffect effect : player.getActivePotionEffects()){
			    player.removePotionEffect(effect.getType());
			}
		}
		//Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("spawnpoint @a %s %s %s", spawnPosition.getBlockX(), spawnPosition.getBlockY(), spawnPosition.getBlockZ()));
		for(String playerName : Seeker.getEntries()) {
			Player player = playerList.get(playerName);
			if(player != null) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,1000000,127,false,false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1000000,127,false,false));
			}
		}
		for(String playerName : Hider.getEntries()) {
			Player player = playerList.get(playerName);
			if(player != null) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,1000000,5,false,false));
			}
		}
		Functions.resetWorldborder();
		status = "Starting";
		int temp = gameId;
		Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 30 seconds to hide!");
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 20 seconds to hide!");
			}
		}, 20 * 10);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 10 seconds to hide!");
			}
		}, 20 * 20);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 5 seconds to hide!");
			}
		}, 20 * 25);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 3 seconds to hide!");
			}
		}, 20 * 27);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 2 seconds to hide!");
			}
		}, 20 * 28);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 1 seconds to hide!");
			}
		}, 20 * 29);
		
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Attetion SEEKERS, its time to find the hiders!");
				status = "Playing";
				for(Player player : playerList.values()) {
					Functions.resetPlayer(player);
				}
			}
		}, 20 * 30);
		
		if(worldborderEnabled) {
			scheduleWorldborder();
		}
		scheduleTaunt();
		
	}
	
	private static void scheduleTaunt() {
		Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){

			public void run() {
				int temp = gameId;
				while(true) {
					if(tauntPlayer != null && !tauntPlayer.equals("")) {
						try { Thread.sleep(1000); } catch (InterruptedException e) {}
						if(gameId != temp) break;
						continue;
					}
					try { Thread.sleep(1000*60); } catch (InterruptedException e) {}
					if(gameId != temp) break;
					if(Math.random() > .8) {
						Player taunted = null;
						int rand = (int) (Math.random()*Hider.getEntries().size());
						for(Player player : playerList.values()) {
							if(Hider.hasEntry(player.getName())) {
								rand--;
								if(rand==0) {
									taunted = player;
									break;
								}
							}
						}
						if(taunted != null) {
							taunted.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Oh no! You have been chosed to be taunted.");
							Bukkit.getServer().broadcastMessage(tauntPrefix + " A random hider will be taunted in the next 30s");
							try { Thread.sleep(1000*30); } catch (InterruptedException e) {}
							if(gameId != temp) break;
							tauntPlayer = taunted.getName();
						}
					}
				}
			}
		});
	}
	
	private static void scheduleWorldborder() {
		
		Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){

			public void run() {
				int temp = gameId;
				while(true) {
					try { Thread.sleep(1000*60*worldborderDelay); } catch (InterruptedException e) {}
					if(gameId != temp) break;
					if(currentWorldborderSize-100 > 100) {
						Bukkit.getServer().broadcastMessage(worldborderPrefix + "Worldborder decreacing by 100 blocks over the next 30s");
						currentWorldborderSize -= 100;
						decreaseBorder = true;
					} else {
						break;
					}
				}
			}
		});
	}

	public String getLabel() {
		return "start";
	}
	
	public String getUsage() {
		return "<player>";
	}

	public String getDescription() {
		return "Starts the game either with a random seeker or chosen one";
	}

}
