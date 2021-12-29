/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2020-2021. Tyler Murphy
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

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.configuration.Items;
import net.tylermurphy.hideAndSeek.database.Database;
import net.tylermurphy.hideAndSeek.util.Status;
import net.tylermurphy.hideAndSeek.util.WinType;
import net.tylermurphy.hideAndSeek.world.WorldLoader;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Packet;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;
import static net.tylermurphy.hideAndSeek.game.Game.broadcastMessage;

public class Game {

	public static Taunt taunt;
	public static Glow glow;
	public static Border worldBorder;
	public static WorldLoader worldLoader;
	public static int tick = 0;
	public static int countdownTime = -1;
	public static int gameId = 0;
	public static int timeLeft = 0;
	public static Status status = Status.STANDBY;

	static {
		worldLoader = new WorldLoader(spawnWorld);
	}

	public static void start(Player seeker){
		if(status == Status.STARTING || status == Status.PLAYING) return;
		if(worldLoader.getWorld() != null) {
			worldLoader.rollback();
		} else {
			worldLoader.loadMap();
		}
		Board.reload();
		for(Player temp : Board.getPlayers()) {
			if(temp.getName().equals(seeker.getName()))
				continue;
			Board.addHider(temp);
		}
		Board.addSeeker(seeker);
		currentWorldborderSize = worldborderSize;
		for(Player player : Board.getPlayers()) {
			player.getInventory().clear();
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(new Location(Bukkit.getWorld("hideandseek_"+spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			for(PotionEffect effect : player.getActivePotionEffects()){
				player.removePotionEffect(effect.getType());
			}
		}
		for(Player player : Board.getSeekers()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,1000000,127,false,false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1000000,127,false,false));
			player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "SEEKER", ChatColor.WHITE + message("SEEKERS_SUBTITLE").toString(), 10, 70, 20);
		}
		for(Player player : Board.getHiders()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,1000000,5,false,false));
			player.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "HIDER", ChatColor.WHITE + message("HIDERS_SUBTITLE").toString(), 10, 70, 20);
		}
		if(tauntEnabled)
			taunt = new Taunt();
		if (glowEnabled)
			glow = new Glow();
		worldBorder = new Border();
		worldBorder.resetWorldborder("hideandseek_"+spawnWorld);
		if(gameLength > 0)
			timeLeft = gameLength;
		for(Player player : Board.getPlayers())
			Board.createGameBoard(player);
		Board.reloadGameBoards();
		status = Status.STARTING;
		int temp = gameId;
		broadcastMessage(messagePrefix + message("START_COUNTDOWN").addAmount(30));
		sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(20), gameId, 20 * 10);
		sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(10), gameId, 20 * 20);
		sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(5), gameId, 20 * 25);
		sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(3), gameId, 20 * 27);
		sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(2), gameId, 20 * 28);
		sendDelayedMessage(messagePrefix + message("START_COUNTDOWN").addAmount(1), gameId, 20 * 29);
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, () -> {
			if(temp != gameId) return;
			broadcastMessage(messagePrefix + message("START"));
			for(Player player : Board.getPlayers()) resetPlayer(player);
			status = Status.PLAYING;
		}, 20 * 30);
	}

	public static void stop(WinType type){
		if(status == Status.STANDBY) return;
		tick = 0;
		countdownTime = -1;
		status = Status.STANDBY;
		gameId++;
		timeLeft = 0;
		List<UUID> players = Board.getPlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList());
		if(type == WinType.HIDER_WIN){
			List<UUID> winners = Board.getHiders().stream().map(Entity::getUniqueId).collect(Collectors.toList());
			Database.playerInfo.addWins(players, winners, type);
		} else if(type == WinType.SEEKER_WIN){
			List<UUID> winners = new ArrayList<>();
			winners.add(Board.getFirstSeeker().getUniqueId());
			Database.playerInfo.addWins(players, winners, type);
		}
		worldBorder.resetWorldborder("hideandseek_"+spawnWorld);
		for(Player player : Board.getPlayers()) {
			Board.createLobbyBoard(player);
			player.setGameMode(GameMode.ADVENTURE);
			Board.addHider(player);
			player.getInventory().clear();
			player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(),lobbyPosition.getY(),lobbyPosition.getZ()));
			for(PotionEffect effect : player.getActivePotionEffects()){
				player.removePotionEffect(effect.getType());
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 100));
			for(Player temp : Board.getPlayers()) {
				Packet.setGlow(player, temp, false);
			}
		}
		worldLoader.unloadMap();
		Board.reloadLobbyBoards();
	}

	public static boolean isNotSetup() {
		if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) return true;
		if(lobbyPosition.getBlockX() == 0 && lobbyPosition.getBlockY() == 0 && lobbyPosition.getBlockZ() == 0) return true;
		if(exitPosition.getBlockX() == 0 && exitPosition.getBlockY() == 0 && exitPosition.getBlockZ() == 0) return true;
		File destenation = new File(Main.root+File.separator+"hideandseek_"+spawnWorld);
		if(!destenation.exists()) return true;
		return saveMinX == 0 || saveMinZ == 0 || saveMaxX == 0 || saveMaxZ == 0;
	}

	public static void onTick() {
		if(isNotSetup()) return;
		if(status == Status.STANDBY) whileWaiting();
		else if(status == Status.STARTING) whileStarting();
		else if(status == Status.PLAYING) whilePlaying();
		tick++;
	}

	public static void resetWorldborder(String worldName){
		worldBorder.resetWorldborder(worldName);
	}

	public static void broadcastMessage(String message) {
		for(Player player : Board.getPlayers()) {
			player.sendMessage(message);
		}
	}

	public static void resetPlayer(Player player) {
		player.getInventory().clear();
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		if (Board.isSeeker(player)) {
			if(pvpEnabled)
				for(ItemStack item : Items.SEEKER_ITEMS)
					player.getInventory().addItem(item);
			for(PotionEffect effect : Items.SEEKER_EFFECTS)
				player.addPotionEffect(effect);
		} else if (Board.isHider(player)) {
			if(pvpEnabled)
				for(ItemStack item : Items.HIDER_ITEMS)
					player.getInventory().addItem(item);
			for(PotionEffect effect : Items.HIDER_EFFECTS)
				player.addPotionEffect(effect);
			if(glowEnabled) {
				ItemStack snowball = new ItemStack(Material.SNOWBALL, 1);
				ItemMeta snowballMeta = snowball.getItemMeta();
				assert snowballMeta != null;
				snowballMeta.setDisplayName("Glow Powerup");
				List<String> snowballLore = new ArrayList<>();
				snowballLore.add("Throw to make all seekers glow");
				snowballLore.add("Last 30s, all hiders can see it");
				snowballLore.add("Time stacks on multi use");
				snowballMeta.setLore(snowballLore);
				snowball.setItemMeta(snowballMeta);
				player.getInventory().addItem(snowball);
			}
		}
	}

	public static void join(Player player){
		if(Game.status == Status.STANDBY) {
			player.getInventory().clear();
			Board.addHider(player);
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(messagePrefix + message("GAME_JOIN").addPlayer(player));
			else Game.broadcastMessage(messagePrefix + message("GAME_JOIN").addPlayer(player));
			player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(),lobbyPosition.getY(),lobbyPosition.getZ()));
			player.setGameMode(GameMode.ADVENTURE);
			Board.createLobbyBoard(player);
			Board.reloadLobbyBoards();
		} else {
			Board.addSpectator(player);
			player.sendMessage(messagePrefix + message("GAME_JOIN_SPECTATOR"));
			player.setGameMode(GameMode.SPECTATOR);
			Board.createGameBoard(player);
			player.teleport(new Location(Bukkit.getWorld("hideandseek_"+spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			player.sendTitle(ChatColor.GRAY + "" + ChatColor.BOLD + "SPECTATING", ChatColor.WHITE + message("SPECTATOR_SUBTITLE").toString(), 10, 70, 20);
		}

		player.setFoodLevel(20);
		player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
	}

	public static void removeItems(Player player){
		for(ItemStack si : Items.SEEKER_ITEMS)
			for(ItemStack i : player.getInventory().getContents())
				if(si.isSimilar(i)) player.getInventory().remove(i);
		for(ItemStack hi : Items.HIDER_ITEMS)
			for(ItemStack i : player.getInventory().getContents())
				if(hi.isSimilar(i)) player.getInventory().remove(i);
	}

	private static void whileWaiting() {
		if(lobbyCountdownEnabled){
			if(lobbyMin <= Board.size()){
				if(countdownTime == -1)
					countdownTime = countdown;
				if(Board.size() >= changeCountdown)
					countdownTime = Math.min(countdownTime, 10);
				if(tick % 20 == 0)
					countdownTime--;
				if(countdownTime == 0){
					Optional<Player> rand = Board.getPlayers().stream().skip(new Random().nextInt(Board.size())).findFirst();
					if(!rand.isPresent()){
						Main.plugin.getLogger().warning("Failed to select random seeker.");
						return;
					}
					String seekerName = rand.get().getName();
					Player seeker = Board.getPlayer(seekerName);
					start(seeker);
				}
			} else {
				countdownTime = -1;
			}
		}
	}

	private static void whileStarting(){
		checkWinConditions();
	}
	
	private static void whilePlaying() {
		for(Player hider : Board.getHiders()) {
			int distance = 100, temp = 100;
			for(Player seeker : Board.getSeekers()) {
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
				Board.reloadGameBoards();
				timeLeft--;
			}
			if(worldborderEnabled) worldBorder.update();
			if(tauntEnabled) taunt.update();
			if (glowEnabled) glow.update();
		}
		checkWinConditions();
	}

	private static void checkWinConditions(){
		if(Board.sizeHider() < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_HIDERS_FOUND"));
			else broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_HIDERS_FOUND"));
			stop(WinType.SEEKER_WIN);
		} else if(Board.sizeSeeker() < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(abortPrefix + message("GAME_GAMEOVER_SEEKERS_QUIT"));
			else broadcastMessage(abortPrefix + message("GAME_GAMEOVER_SEEKERS_QUIT"));
			stop(WinType.NONE);
		} else if(timeLeft < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_TIME"));
			else broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_TIME"));
			stop(WinType.HIDER_WIN);
		}
	}

	private static void sendDelayedMessage(String message, int gameId, int delay) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, () -> {
			if(gameId == Game.gameId)
				broadcastMessage(message);
		}, delay);
	}

}

class Glow {

	private int glowTime;
	private boolean running;

	public Glow() {
		this.glowTime = 0;
	}

	public void onProjectile() {
		if(glowStackable) glowTime += glowLength;
		else glowTime = glowLength;
		if(!running)
			startGlow();
	}

	private void startGlow() {
		running = true;
		for(Player hider : Board.getHiders()) {
			for(Player seeker : Board.getSeekers()) {
				Packet.setGlow(hider, seeker, true);
			}
		}
	}

	protected void update() {
		if(running) {
			glowTime--;
			glowTime = Math.max(glowTime, 0);
			if (glowTime == 0) {
				stopGlow();
			}
		}
	}

	private void stopGlow() {
		running = false;
		for(Player hider : Board.getHiders()) {
			for (Player seeker : Board.getSeekers()) {
				Packet.setGlow(hider, seeker, false);
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

}

class Taunt {

	private String tauntPlayer;
	private int delay;
	private boolean running;

	public Taunt() {
		this.delay = tauntDelay;
	}

	protected void update() {
		if(delay == 0) {
			if(running) launchTaunt();
			else if(tauntLast || Board.sizeHider() > 1) executeTaunt();
		} else {
			delay--;
			delay = Math.max(delay, 0);
		}
	}

	private void executeTaunt() {
		Optional<Player> rand = Board.getHiders().stream().skip(new Random().nextInt(Board.size())).findFirst();
		if(!rand.isPresent()){
			Main.plugin.getLogger().warning("Failed to select random seeker.");
			return;
		}
		Player taunted = rand.get();
		taunted.sendMessage(message("TAUNTED").toString());
		broadcastMessage(tauntPrefix + message("TAUNT"));
		tauntPlayer = taunted.getName();
		running = true;
		delay = 30;
	}

	private void launchTaunt(){
		Player taunted = Board.getPlayer(tauntPlayer);
		if(taunted != null) {
			if(!Board.isHider(taunted)){
				Main.plugin.getLogger().info("Taunted played died and is now seeker. Skipping taunt.");
				tauntPlayer = "";
				running = false;
				delay = tauntDelay;
				return;
			}
			World world = taunted.getLocation().getWorld();
			if(world == null){
				Main.plugin.getLogger().severe("Game world is null while trying to launch taunt.");
				tauntPlayer = "";
				running = false;
				delay = tauntDelay;
				return;
			}
			Firework fw = (Firework) world.spawnEntity(taunted.getLocation(), EntityType.FIREWORK);
			FireworkMeta fwm = fw.getFireworkMeta();
			fwm.setPower(4);
			fwm.addEffect(FireworkEffect.builder()
					.withColor(Color.BLUE)
					.withColor(Color.RED)
					.withColor(Color.YELLOW)
					.with(FireworkEffect.Type.STAR)
					.with(FireworkEffect.Type.BALL)
					.with(FireworkEffect.Type.BALL_LARGE)
					.flicker(true)
					.withTrail()
					.build());
			fw.setFireworkMeta(fwm);
			broadcastMessage(tauntPrefix + message("TAUNT_ACTIVATE"));
		}
		tauntPlayer = "";
		running = false;
		delay = tauntDelay;
	}

	public int getDelay(){
		return delay;
	}

	public boolean isRunning() {
		return running;
	}

}

class Border {

	private int delay;
	private boolean running;

	public Border() {
		delay = 60 * worldborderDelay;
	}

	void update(){
		if(delay == 30 && !running){
			broadcastMessage(worldborderPrefix + message("WORLDBORDER_WARN"));
		} else if(delay == 0){
			if(running){
				delay = 60 * worldborderDelay;
				running = false;
			}
			else decreaceWorldborder();
		}
		delay--;
	}

	private void decreaceWorldborder() {
		if(currentWorldborderSize-100 > 100) {
			running = true;
			broadcastMessage(worldborderPrefix + message("WORLDBORDER_DECREASING"));
			currentWorldborderSize -= 100;
			World world = Bukkit.getWorld("hideandseek_"+spawnWorld);
			assert world != null;
			org.bukkit.WorldBorder border = world.getWorldBorder();
			border.setSize(border.getSize()-100,30);
			delay = 30;
		}
	}

	public void resetWorldborder(String worldName) {
		World world = Bukkit.getWorld(worldName);
		assert world != null;
		org.bukkit.WorldBorder border = world.getWorldBorder();
		if(worldborderEnabled) {
			border.setSize(worldborderSize);
			border.setCenter(worldborderPosition.getX(), worldborderPosition.getZ());
			currentWorldborderSize = worldborderSize;
		} else {
			border.setSize(30000000);
			border.setCenter(0, 0);
		}
	}

	public int getDelay(){
		return delay;
	}

	public boolean isRunning() {
		return running;
	}

}