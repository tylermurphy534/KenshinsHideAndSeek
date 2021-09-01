package net.tylermurphy.hideAndSeek.command;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.events.Glow;
import net.tylermurphy.hideAndSeek.events.Taunt;
import net.tylermurphy.hideAndSeek.events.Worldborder;
import net.tylermurphy.hideAndSeek.util.Functions;
import net.tylermurphy.hideAndSeek.util.ICommand;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.io.File;
import java.util.ArrayList;
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
		Hider = new ArrayList<String>();
		Seeker = new ArrayList<String>();
		Spectator = new ArrayList<String>();
		for(Player temp : playerList.values()) {
			if(temp.getName().equals(seeker.getName()))
				continue;
			Hider.add(temp.getName());
			HiderTeam.addEntry(temp.getName());
		}
		Seeker.add(seeker.getName());
		SeekerTeam.addEntry(seeker.getName());
		
		for(Player player : playerList.values()) {
			player.getInventory().clear();
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(new Location(Bukkit.getWorld("hideandseek_"+spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			for(PotionEffect effect : player.getActivePotionEffects()){
			    player.removePotionEffect(effect.getType());
			}
		}
		for(String playerName : Seeker) {
			Player player = playerList.get(playerName);
			if(player != null) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,1000000,127,false,false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1000000,127,false,false));
			}
		}
		for(String playerName : Hider) {
			Player player = playerList.get(playerName);
			if(player != null) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,1000000,5,false,false));
			}
		}
		Functions.resetWorldborder("hideandseek_"+spawnWorld);
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
				Main.worldborder = null;
				Main.taunt = null;
				Main.glow = null;
				
				if(worldborderEnabled) {
					Main.worldborder = new Worldborder(gameId);
					Main.worldborder.schedule();
				}
				
				Main.taunt = new Taunt(gameId);
				Main.taunt.schedule();
				
				Main.glow = new Glow(gameId);
			}
		}, 20 * 30);
		
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
