package net.tylermurphy.hideAndSeek.game;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.events.Glow;
import net.tylermurphy.hideAndSeek.events.Taunt;
import net.tylermurphy.hideAndSeek.events.Worldborder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Packet;
import net.tylermurphy.hideAndSeek.util.Util;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;
import static net.tylermurphy.hideAndSeek.configuration.Config.*;

public class Game {

	public Taunt taunt;
	public Glow glow;
	public Worldborder worldborder;

	private int tick = 0;
	public  int countdownTime = -1;
	public int gameId = 0;
	public int timeLeft = 0;

	public void start(Player seeker){
		if(Main.plugin.status == Status.STARTING || Main.plugin.status == Status.PLAYING) return;
		if(Bukkit.getServer().getWorld("hideandseek_"+spawnWorld) != null) {
			Main.plugin.worldLoader.rollback();
		} else {
			Main.plugin.worldLoader.loadMap();
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
		Main.plugin.status = Status.STARTING;
		int temp = Main.plugin.game.gameId;
		Util.broadcastMessage(messagePrefix + message("START_COUNTDOWN").addAmount(30));
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(20), Main.plugin.game.gameId, 20 * 10);
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(10), Main.plugin.game.gameId, 20 * 20);
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(5), Main.plugin.game.gameId, 20 * 25);
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(3), Main.plugin.game.gameId, 20 * 27);
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(2), Main.plugin.game.gameId, 20 * 28);
		Util.sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(1), Main.plugin.game.gameId, 20 * 29);
		if(gameLength > 0) {
			timeLeft = gameLength;
		}
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, () -> {
			if(temp != Main.plugin.game.gameId) return;
			Util.broadcastMessage(messagePrefix + message("START"));

			for(Player player : Main.plugin.board.getPlayers()) {
				Util.resetPlayer(player);
			}

			if(worldborderEnabled) {
				worldborder = new Worldborder(Main.plugin.game.gameId);
				worldborder.schedule();
			}

			if(tauntEnabled) {
				taunt = new Taunt(Main.plugin.game.gameId);
				taunt.schedule();
			}

			if (glowEnabled) {
				glow = new Glow(Main.plugin.game.gameId);
			}

			Main.plugin.status = Status.PLAYING;
		}, 20 * 30);
	}

	public void stop(WinType type){
		if(Main.plugin.status == Status.STANDBY) return;
		tick = 0;
		countdownTime = -1;
		Main.plugin.status = Status.STANDBY;
		Main.plugin.game.gameId++;
		timeLeft = 0;
		List<UUID> players = Main.plugin.board.getPlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList());
		if(type == WinType.HIDER_WIN){
			List<UUID> winners = Main.plugin.board.getHiders().stream().map(Entity::getUniqueId).collect(Collectors.toList());
			Main.plugin.database.playerInfo.addWins(players, winners, type);
		} else if(type == WinType.SEEKER_WIN){
			List<UUID> winners = new ArrayList<>();
			winners.add(Main.plugin.board.getFirstSeeker().getUniqueId());
			Main.plugin.database.playerInfo.addWins(players, winners, type);
		}
		Worldborder.resetWorldborder("hideandseek_"+spawnWorld);
		for(Player player : Main.plugin.board.getPlayers()) {
			Main.plugin.board.createLobbyBoard(player);
			player.setGameMode(GameMode.ADVENTURE);
			Main.plugin.board.addHider(player);
			player.getInventory().clear();
			player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(),lobbyPosition.getY(),lobbyPosition.getZ()));
			for(PotionEffect effect : player.getActivePotionEffects()){
				player.removePotionEffect(effect.getType());
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 100));
			for(Player temp : Main.plugin.board.getPlayers()) {
				Packet.setGlow(player, temp, false);
			}
		}
		Main.plugin.worldLoader.unloadMap();
		Main.plugin.board.reloadLobbyBoards();
	}

	public void onTick() {

		if(!Util.isSetup()) return;

		if(Main.plugin.status == Status.STANDBY) whileWaiting();
		else if(Main.plugin.status == Status.PLAYING) whilePlaying();
		
		if(( Main.plugin.status == Status.STARTING || Main.plugin.status == Status.PLAYING ) && Main.plugin.board.sizeHider() < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_HIDERS_FOUND"));
			else Util.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_HIDERS_FOUND"));
			stop(WinType.SEEKER_WIN);
		}
		if(( Main.plugin.status == Status.STARTING || Main.plugin.status == Status.PLAYING ) && Main.plugin.board.sizeSeeker() < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(abortPrefix + message("GAME_GAMEOVER_SEEKERS_QUIT"));
			else Util.broadcastMessage(abortPrefix + message("GAME_GAMEOVER_SEEKERS_QUIT"));
			stop(WinType.NONE);
		}

		tick++;

	}

	private void whileWaiting() {
		if(lobbyCountdownEnabled){
			if(lobbyMin <= Main.plugin.board.size()){
				if(countdownTime == -1)
					countdownTime = countdown;
				if(Main.plugin.board.size() >= changeCountdown)
					countdownTime = Math.min(countdownTime, 10);
				if(tick % 20 == 0)
					countdownTime--;
				if(countdownTime == 0){
					String seekerName = Main.plugin.board.getPlayers().stream().skip(new Random().nextInt(Main.plugin.board.size())).findFirst().get().getName();
					Player seeker = Main.plugin.board.getPlayer(seekerName);
					start(seeker);
				}
			} else {
				countdownTime = -1;
			}
		}

	}
	
	private void whilePlaying() {
		
		for(Player hider : Main.plugin.board.getHiders()) {
			int distance = 100, temp = 100;
			for(Player seeker : Main.plugin.board.getSeekers()) {
				try {
					temp = (int) hider.getLocation().distance(seeker.getLocation());
				} catch (Exception e){
					//Players in different worlds, NOT OK!!!
				}
				if(distance > temp) {
					distance = temp;
				}
			}
			switch(tick%10) {
				case 0:
					if(distance < 30) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, .5f, 1f);
					if(distance < 10) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 3:
					if(distance < 30) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, .3f, 1f);
					if(distance < 10) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 6:
					if(distance < 10) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 9:
					if(distance < 20) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
			}

		}
		
		if(tick%20 == 0) {
			if(gameLength > 0) {
				Main.plugin.board.reloadGameBoards();
				timeLeft--;
				if(timeLeft < 1) {
					if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_TIME"));
					else Util.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_TIME"));
					stop(WinType.HIDER_WIN);
				}
			}
		}
	}
}