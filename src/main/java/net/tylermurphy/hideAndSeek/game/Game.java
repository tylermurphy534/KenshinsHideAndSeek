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

import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.configuration.Items;
import net.tylermurphy.hideAndSeek.database.Database;
import net.tylermurphy.hideAndSeek.util.*;
import net.tylermurphy.hideAndSeek.world.WorldLoader;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
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

	private static boolean hiderLeave = false;

	static {
		worldLoader = new WorldLoader(spawnWorld);
	}

	public static void start(){
		Optional<Player> rand = Board.getPlayers().stream().skip(new Random().nextInt(Board.size())).findFirst();
		if(!rand.isPresent()){
			Main.plugin.getLogger().warning("Failed to select random seeker.");
			return;
		}
		String seekerName = rand.get().getName();
		Player temp = Bukkit.getPlayer(seekerName);
		if(temp == null){
			Main.plugin.getLogger().warning("Failed to select random seeker.");
			return;
		}
		Player seeker = Board.getPlayer(temp.getUniqueId());
		if(seeker == null){
			Main.plugin.getLogger().warning("Failed to select random seeker.");
			return;
		}
		start(seeker);
	}

	public static void start(Player seeker){
		if(status == Status.STARTING || status == Status.PLAYING) return;
		if(mapSaveEnabled && worldLoader.getWorld() != null) {
			worldLoader.rollback();
		} else if(mapSaveEnabled) {
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
			player.teleport(new Location(Bukkit.getWorld(getGameWorld()), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			for(PotionEffect effect : player.getActivePotionEffects()){
				player.removePotionEffect(effect.getType());
			}
		}
		for(Player player : Board.getSeekers()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,1000000,127,false,false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1000000,127,false,false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,1000000,128,false,false));
			Titles.sendTitle(player, 10, 70, 20, ChatColor.WHITE + "" + message("SEEKER_TEAM_NAME"), ChatColor.WHITE + message("SEEKERS_SUBTITLE").toString());
		}
		for(Player player : Board.getHiders()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,1000000,5,false,false));
			Titles.sendTitle(player, 10, 70, 20, ChatColor.WHITE + "" + message("HIDER_TEAM_NAME"), ChatColor.WHITE + message("HIDERS_SUBTITLE").toString());
		}
		if(tauntEnabled)
			taunt = new Taunt();
		if (glowEnabled)
			glow = new Glow();
		worldBorder = new Border();
		worldBorder.resetWorldborder(getGameWorld());
		if(gameLength > 0)
			timeLeft = gameLength;
		for(Player player : Board.getPlayers())
			Board.createGameBoard(player);
		Board.reloadGameBoards();
		status = Status.STARTING;
		int temp = gameId;
		if(countdownDisplay != CountdownDisplay.TITLE) {
			sendHideCountdownMessage(messagePrefix + message("START_COUNTDOWN").addAmount(30), gameId, 0);
		}
		sendHideCountdownMessage(messagePrefix + message("START_COUNTDOWN").addAmount(20), gameId, 20 * 10);
		sendHideCountdownMessage(messagePrefix + message("START_COUNTDOWN").addAmount(10), gameId, 20 * 20);
		sendHideCountdownMessage(messagePrefix + message("START_COUNTDOWN").addAmount(5), gameId, 20 * 25);
		sendHideCountdownMessage(messagePrefix + message("START_COUNTDOWN").addAmount(3), gameId, 20 * 27);
		sendHideCountdownMessage(messagePrefix + message("START_COUNTDOWN").addAmount(2), gameId, 20 * 28);
		sendHideCountdownMessage(messagePrefix + message("START_COUNTDOWN").addAmount(1), gameId, 20 * 29);
		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, () -> {
			if(temp != gameId) return;
			sendHideCountdownMessage(messagePrefix + message("START"), gameId, 0);
			for(Player player : Board.getPlayers()) resetPlayer(player);
			status = Status.PLAYING;
		}, 20 * 30);
	}

	public static void stop(WinType type){
		if(status == Status.STANDBY || status == Status.ENDING) return;
		status = Status.ENDING;
		for(Player player : Board.getPlayers()) {
			player.getInventory().clear();
			for(PotionEffect effect : player.getActivePotionEffects()){
				player.removePotionEffect(effect.getType());
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 100));
			if(Version.atLeast("1.9")){
				for(Player temp : Board.getPlayers()) {
					Packet.setGlow(player, temp, false);
				}
			}
		}
		List<UUID> players = Board.getPlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList());
		if(type == WinType.HIDER_WIN){
			List<UUID> winners = Board.getHiders().stream().map(Entity::getUniqueId).collect(Collectors.toList());
			Database.playerInfo.addWins(players, winners, Board.getHiderKills(), Board.getHiderDeaths(), Board.getSeekerKills(), Board.getSeekerDeaths(), type);
		} else if(type == WinType.SEEKER_WIN){
			List<UUID> winners = new ArrayList<>();
			winners.add(Board.getFirstSeeker().getUniqueId());
			Database.playerInfo.addWins(players, winners, Board.getHiderKills(), Board.getHiderDeaths(), Board.getSeekerKills(), Board.getSeekerDeaths(), type);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, Game::end, 5*20);
	}

	public static void end(){
		if(status == Status.STANDBY) return;
		tick = 0;
		countdownTime = -1;
		status = Status.STANDBY;
		gameId++;
		timeLeft = 0;
		worldBorder.resetWorldborder(getGameWorld());
		for(Player player : Board.getPlayers()) {
			for(Player player2 : Board.getPlayers()){
				player.showPlayer(player2);
			}
			player.setAllowFlight(false);
			player.setFlying(false);
			if(Version.atLeast("1.9")){
				for(Player temp : Board.getPlayers()) {
					Packet.setGlow(player, temp, false);
				}
			}
			for(PotionEffect effect : player.getActivePotionEffects()){
				player.removePotionEffect(effect.getType());
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 100));
			if(leaveOnEnd){
				Board.removeBoard(player);
				Board.remove(player);
				player.getInventory().clear();
				if(bungeeLeave) {
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("Connect");
					out.writeUTF(leaveServer);
					player.sendPluginMessage(Main.plugin, "BungeeCord", out.toByteArray());
				} else {
					player.teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
				}
			} else {
				player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(),lobbyPosition.getY(),lobbyPosition.getZ()));
				Board.createLobbyBoard(player);
				player.setGameMode(GameMode.ADVENTURE);
				Board.addHider(player);
				player.getInventory().clear();
				if(lobbyStartItem != null && (!lobbyItemStartAdmin || player.isOp()))
					player.getInventory().setItem(lobbyItemStartPosition, lobbyStartItem);
				if(lobbyLeaveItem != null)
					player.getInventory().setItem(lobbyItemLeavePosition, lobbyLeaveItem);
				for(PotionEffect effect : player.getActivePotionEffects()){
					player.removePotionEffect(effect.getType());
				}
				player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 100));
			}
		}
		EventListener.temp_loc.clear();
		if(mapSaveEnabled) worldLoader.unloadMap();
		Board.reloadLobbyBoards();
	}

	public static void join(Player player){
		if(Game.status == Status.STANDBY || Game.status == Status.ENDING) {
			player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(),lobbyPosition.getY(),lobbyPosition.getZ()));
			player.getInventory().clear();
			if(lobbyStartItem != null && (!lobbyItemStartAdmin || player.hasPermission("hideandseek.start")))
				player.getInventory().setItem(lobbyItemStartPosition, lobbyStartItem);
			if(lobbyLeaveItem != null)
				player.getInventory().setItem(lobbyItemLeavePosition, lobbyLeaveItem);
			Board.addHider(player);
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(messagePrefix + message("GAME_JOIN").addPlayer(player));
			else Game.broadcastMessage(messagePrefix + message("GAME_JOIN").addPlayer(player));
			for(PotionEffect effect : player.getActivePotionEffects()){
				player.removePotionEffect(effect.getType());
			}
			player.setGameMode(GameMode.ADVENTURE);
			Board.createLobbyBoard(player);
			Board.reloadLobbyBoards();
		} else {
			Board.addSpectator(player);
			player.sendMessage(messagePrefix + message("GAME_JOIN_SPECTATOR"));
			player.setGameMode(GameMode.ADVENTURE);
			for(Player player2 : Board.getPlayers()){
				player2.hidePlayer(player);
			}
			Board.createGameBoard(player);
			player.teleport(new Location(Bukkit.getWorld(getGameWorld()), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			player.setAllowFlight(true);
			player.setFlying(true);
			player.setFallDistance(0.0F);
			Titles.sendTitle(player, 10, 70, 20, ChatColor.GRAY + "" + ChatColor.BOLD + "SPECTATING", ChatColor.WHITE + message("SPECTATOR_SUBTITLE").toString());
		}

		player.setFoodLevel(20);
		if(Version.atLeast("1.9")) {
			AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			if (attribute != null) player.setHealth(attribute.getValue());
		} else {
			player.setHealth(player.getMaxHealth());
		}
	}

	public static void leave(Player player){
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setFallDistance(0.0F);
		for(Player player2 : Board.getPlayers()){
			player2.showPlayer(player);
			player.showPlayer(player2);
		}
		if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(messagePrefix + message("GAME_LEAVE").addPlayer(player));
		else Game.broadcastMessage(messagePrefix + message("GAME_LEAVE").addPlayer(player));
		if(Board.isHider(player) && status != Status.ENDING && status != Status.STANDBY){
			hiderLeave = true;
		}
		Board.removeBoard(player);
		Board.remove(player);
		player.getInventory().clear();
		if(Game.status == Status.STANDBY) {
			Board.reloadLobbyBoards();
		} else {
			Board.reloadGameBoards();
			Board.reloadBoardTeams();
		}
		for(PotionEffect effect : player.getActivePotionEffects()){
			player.removePotionEffect(effect.getType());
		}
		if(bungeeLeave) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF(leaveServer);
			player.sendPluginMessage(Main.plugin, "BungeeCord", out.toByteArray());
		} else {
			player.teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
		}
	}

	public static void onTick() {
		if(isNotSetup()) return;
		if(status == Status.STANDBY) whileWaiting();
		else if(status == Status.STARTING) whileStarting();
		else if(status == Status.PLAYING) whilePlaying();
		tick++;
	}

	private static void whileWaiting() {
		if(!lobbyCountdownEnabled) return;
		if(lobbyMin <= Board.size()){
			if(countdownTime == -1)
				countdownTime = countdown;
			if(Board.size() >= changeCountdown)
				countdownTime = Math.min(countdownTime, 10);
			if(tick % 20 == 0) {
				countdownTime--;
				Board.reloadLobbyBoards();
			}
			if(countdownTime == 0){
				start();
			}
		} else {
			countdownTime = -1;
		}
	}

	private static void whileStarting(){
		for(Player spectator : Board.getSpectators()){
			spectator.setFlying(spectator.getAllowFlight());
		}
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
			if(seekerPing) switch(tick%10) {
				case 0:
					if(distance < seekerPingLevel1) heartbeatSound.play(hider, seekerPingLeadingVolume, seekerPingPitch);
					if(distance < seekerPingLevel3) ringingSound.play(hider, seekerPingVolume, seekerPingPitch);
					break;
				case 3:
					if(distance < seekerPingLevel1) heartbeatSound.play(hider, seekerPingVolume, seekerPingPitch);
					if(distance < seekerPingLevel3) ringingSound.play(hider, seekerPingVolume, seekerPingPitch);
					break;
				case 6:
					if(distance < seekerPingLevel3) ringingSound.play(hider, seekerPingVolume, seekerPingPitch);
					break;
				case 9:
					if(distance < seekerPingLevel2) ringingSound.play(hider, seekerPingVolume, seekerPingPitch);
					break;
			}
		}
		for(Player spectator : Board.getSpectators()){
			spectator.setFlying(spectator.getAllowFlight());
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

	public static void resetWorldborder(String worldName){
		worldBorder = new Border();
		worldBorder.resetWorldborder(worldName);
	}

	public static void broadcastMessage(String message) {
		for(Player player : Board.getPlayers()) {
			player.sendMessage(message);
		}
	}

	public static boolean isNotSetup() {
		if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) return true;
		if(lobbyPosition.getBlockX() == 0 && lobbyPosition.getBlockY() == 0 && lobbyPosition.getBlockZ() == 0) return true;
		if(exitPosition.getBlockX() == 0 && exitPosition.getBlockY() == 0 && exitPosition.getBlockZ() == 0) return true;
		if(mapSaveEnabled) {
			File destenation = new File(Main.root + File.separator + getGameWorld());
			if (!destenation.exists()) return true;
		}
		return saveMinX == 0 || saveMinZ == 0 || saveMaxX == 0 || saveMaxZ == 0;
	}

	public static String getGameWorld(){
		if(mapSaveEnabled) return "hideandseek_"+spawnWorld;
		else return spawnWorld;
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
				player.getInventory().addItem(glowPowerupItem);
			}
		}
	}

	public static void removeItems(Player player){
		for(ItemStack si : Items.SEEKER_ITEMS)
			for(ItemStack i : player.getInventory().getContents())
				if(si.isSimilar(i)) player.getInventory().remove(i);
		for(ItemStack hi : Items.HIDER_ITEMS)
			for(ItemStack i : player.getInventory().getContents())
				if(hi.isSimilar(i)) player.getInventory().remove(i);
	}

	private static void checkWinConditions(){
		if(Board.sizeHider() < 1) {
			if(hiderLeave){
				if (announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_HIDERS_QUIT"));
				else broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_HIDERS_QUIT"));
				stop(WinType.NONE);
			} else {
				if (announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_HIDERS_FOUND"));
				else broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_HIDERS_FOUND"));
				stop(WinType.SEEKER_WIN);
			}
		} else if(Board.sizeSeeker() < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(abortPrefix + message("GAME_GAMEOVER_SEEKERS_QUIT"));
			else broadcastMessage(abortPrefix + message("GAME_GAMEOVER_SEEKERS_QUIT"));
			stop(WinType.NONE);
		} else if(timeLeft < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_TIME"));
			else broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_TIME"));
			stop(WinType.HIDER_WIN);
		}
		hiderLeave = false;
	}

	private static void sendHideCountdownMessage(String message, int gameId, int delay) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, () -> {
			if(gameId == Game.gameId){
				for(Player player : Board.getPlayers()){
					if(countdownDisplay == CountdownDisplay.CHAT){
						player.sendMessage(message);
					} else if(countdownDisplay == CountdownDisplay.ACTIONBAR){
						ActionBar.clearActionBar(player);
						ActionBar.sendActionBar(player,message);
					} else if(countdownDisplay == CountdownDisplay.TITLE){
						Titles.clearTitle(player);
						Titles.sendTitle(player, 10, 40, 10, " ", message);
					}
				}
			}
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
		running = true;
	}

	private void sendPackets(){
		for(Player hider : Board.getHiders())
			for(Player seeker : Board.getSeekers())
				Packet.setGlow(hider, seeker, true);
	}

	protected void update() {
		if(running) {
			sendPackets();
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

	private UUID tauntPlayer;
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
		tauntPlayer = taunted.getUniqueId();
		running = true;
		delay = 30;
	}

	private void launchTaunt(){
		Player taunted = Board.getPlayer(tauntPlayer);
		if(taunted != null) {
			if(!Board.isHider(taunted)){
				Main.plugin.getLogger().info("Taunted played died and is now seeker. Skipping taunt.");
				tauntPlayer = null;
				running = false;
				delay = tauntDelay;
				return;
			}
			World world = taunted.getLocation().getWorld();
			if(world == null){
				Main.plugin.getLogger().severe("Game world is null while trying to launch taunt.");
				tauntPlayer = null;
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
		tauntPlayer = null;
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
		if(currentWorldborderSize == 100) return;
		int change = worldborderChange;
		if(currentWorldborderSize-worldborderChange < 100){
			change = currentWorldborderSize-100;
		}
		running = true;
		broadcastMessage(worldborderPrefix + message("WORLDBORDER_DECREASING").addAmount(change));
		currentWorldborderSize -= worldborderChange;
		World world = Bukkit.getWorld(Game.getGameWorld());
		assert world != null;
		org.bukkit.WorldBorder border = world.getWorldBorder();
		border.setSize(border.getSize()-change,30);
		delay = 30;
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