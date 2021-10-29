package net.tylermurphy.hideAndSeek.command;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.events.Glow;
import net.tylermurphy.hideAndSeek.events.Taunt;
import net.tylermurphy.hideAndSeek.events.Worldborder;
import net.tylermurphy.hideAndSeek.util.Util;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Start implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!Util.isSetup()) {
			sender.sendMessage(errorPrefix + message("GAME_SETUP"));
			return;
		}
		if(!Main.plugin.status.equals("Standby")) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if(!Main.plugin.board.isPlayer(sender)) {
			sender.sendMessage(errorPrefix + message("GAME_NOT_INGAME"));
			return;
		}
		if(Main.plugin.board.size() < minPlayers) {
			sender.sendMessage(errorPrefix + message("START_MIN_PLAYERS").addAmount(minPlayers));
			return;
		}
		if(Bukkit.getServer().getWorld("hideandseek_"+spawnWorld) != null) {
			Main.plugin.worldLoader.rollback();
		} else {
			Main.plugin.worldLoader.loadMap();
		}
		String seekerName;
		if(args.length < 1) {
			seekerName = Main.plugin.board.getPlayers().stream().skip(new Random().nextInt(Main.plugin.board.size())).findFirst().get().getName();
		} else {
			seekerName = args[0];
		}
		Player seeker = Main.plugin.board.getPlayer(seekerName);
		if(seeker == null) {
			sender.sendMessage(errorPrefix + message("START_INVALID_NAME").addPlayer(seekerName));
			return;
		}
		Main.plugin.board.reload();
		for(Player temp : Main.plugin.board.getPlayers()) {
			if(temp.getName().equals(seeker.getName()))
				continue;
			Main.plugin.board.addHider(temp);
		}
		Main.plugin.board.addSeeker(seeker);
		currentWorldborderSize = worldborderSize;
		for(Player player : Main.plugin.board.getPlayers()) {
			player.getInventory().clear();
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(new Location(Bukkit.getWorld("hideandseek_"+spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			for(PotionEffect effect : player.getActivePotionEffects()){
			    player.removePotionEffect(effect.getType());
			}
		}
		for(Player player : Main.plugin.board.getSeekers()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,1000000,127,false,false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1000000,127,false,false));
			player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "SEEKER", ChatColor.WHITE + message("SEEKERS_SUBTITLE").toString(), 10, 70, 20);
		}
		for(Player player : Main.plugin.board.getHiders()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,1000000,5,false,false));
			player.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "HIDER", ChatColor.WHITE + message("HIDERS_SUBTITLE").toString(), 10, 70, 20);
		}
		Worldborder.resetWorldborder("hideandseek_"+spawnWorld);
		for(Player player : Main.plugin.board.getPlayers()){
			Main.plugin.board.createGameBoard(player);
		}
		Main.plugin.board.reloadGameBoards();
		Main.plugin.status = "Starting";
		int temp = Main.plugin.gameId;
		Util.broadcastMessage(messagePrefix + message("START_COUNTDOWN").addAmount(30));
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(20), Main.plugin.gameId, 20 * 10);
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(10), Main.plugin.gameId, 20 * 20);
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(5), Main.plugin.gameId, 20 * 25);
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(3), Main.plugin.gameId, 20 * 27);
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(2), Main.plugin.gameId, 20 * 28);
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(1), Main.plugin.gameId, 20 * 29);
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				if(temp != Main.plugin.gameId) return;
				Util.broadcastMessage(messagePrefix + message("START"));
				Main.plugin.status = "Playing";
				for(Player player : Main.plugin.board.getPlayers()) {
					Util.resetPlayer(player);
				}
				Main.plugin.worldborder = null;
				Main.plugin.taunt = null;
				Main.plugin.glow = null;
				
				if(worldborderEnabled) {
					Main.plugin.worldborder = new Worldborder(Main.plugin.gameId);
					Main.plugin.worldborder.schedule();
				}

				if(tauntEnabled) {
					Main.plugin.taunt = new Taunt(Main.plugin.gameId);
					Main.plugin.taunt.schedule();
				}

				if (glowEnabled) {
					Main.plugin.glow = new Glow(Main.plugin.gameId);
				}
				
				if(gameLength > 0) {
					Main.plugin.timeLeft = gameLength;
				}
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
