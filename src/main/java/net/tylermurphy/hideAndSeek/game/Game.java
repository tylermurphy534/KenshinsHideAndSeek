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

import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.game.events.Border;
import net.tylermurphy.hideAndSeek.game.events.Glow;
import net.tylermurphy.hideAndSeek.game.events.Taunt;
import net.tylermurphy.hideAndSeek.game.listener.RespawnHandler;
import net.tylermurphy.hideAndSeek.game.util.*;
import net.tylermurphy.hideAndSeek.world.WorldLoader;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class Game {

	private final Taunt taunt;
	private final Glow glow;
	private final Border worldBorder;
	private final WorldLoader worldLoader;

	private final Board board;

	private Status status;

	private int gameTick;
	private int lobbyTimer;
	private int startingTimer;
	private int gameTimer;
	private boolean hiderLeft;

	public Game(Board board){
		this.taunt = new Taunt();
		this.glow = new Glow();
		this.worldBorder = new Border();
		this.worldLoader = new WorldLoader(spawnWorld);

		this.status = Status.STANDBY;

		this.board = board;

		this.gameTick = 0;
		this.lobbyTimer = -1;
		this.startingTimer = -1;
		this.gameTimer = 0;
		this.hiderLeft = false;
	}

	public Status getStatus(){
		return status;
	}

	public int getTimeLeft(){
		return gameTimer;
	}

	public int getLobbyTime(){
		return lobbyTimer;
	}

	public Glow getGlow(){
		return glow;
	}

	public Border getBorder(){
		return worldBorder;
	}

	public Taunt getTaunt(){
		return taunt;
	}

	public WorldLoader getWorldLoader(){
		return worldLoader;
	}

	public void start() {
		try {
			Optional<Player> rand = board.getPlayers().stream().skip(new Random().nextInt(board.size())).findFirst();
			String seekerName = rand.get().getName();
			Player temp = Bukkit.getPlayer(seekerName);
			Player seeker = board.getPlayer(temp.getUniqueId());
			start(seeker);
		} catch (Exception e){
			Main.getInstance().getLogger().warning("Failed to select random seeker.");
		}
	}

	public void start(Player seeker) {
		if (mapSaveEnabled) worldLoader.rollback();
		board.reload();
		board.addSeeker(seeker);
		PlayerLoader.loadSeeker(seeker, getGameWorld());
		board.getPlayers().forEach(player -> {
			board.createGameBoard(player);
			if(board.isSeeker(player)) return;
			board.addHider(player);
			PlayerLoader.loadHider(player, getGameWorld());
		});
		worldBorder.resetWorldBorder(getGameWorld());
		if (gameLength > 0) gameTimer = gameLength;
		status = Status.STARTING;
		startingTimer = 30;
	}

	private void stop(WinType type) {
		status = Status.ENDING;
		List<UUID> players = board.getPlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList());
		if (type == WinType.HIDER_WIN) {
			List<UUID> winners = board.getHiders().stream().map(Entity::getUniqueId).collect(Collectors.toList());
			Main.getInstance().getDatabase().getGameData().addWins(board, players, winners, board.getHiderKills(), board.getHiderDeaths(), board.getSeekerKills(), board.getSeekerDeaths(), type);
		} else if (type == WinType.SEEKER_WIN) {
			List<UUID> winners = new ArrayList<>();
			winners.add(board.getFirstSeeker().getUniqueId());
			Main.getInstance().getDatabase().getGameData().addWins(board, players, winners, board.getHiderKills(), board.getHiderDeaths(), board.getSeekerKills(), board.getSeekerDeaths(), type);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), this::end, 5*20);
	}

	public void end() {
		board.getPlayers().forEach(PlayerLoader::unloadPlayer);
		worldBorder.resetWorldBorder(getGameWorld());
		board.getPlayers().forEach(player -> {
			if (leaveOnEnd) {
				board.removeBoard(player);
				board.remove(player);
				handleBungeeLeave(player);
			} else {
				player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(),lobbyPosition.getY(),lobbyPosition.getZ()));
				board.createLobbyBoard(player);
				board.addHider(player);
				PlayerLoader.joinPlayer(player);
			}
		});
		RespawnHandler.temp_loc.clear();
		if (mapSaveEnabled) worldLoader.unloadMap();
		board.reloadLobbyBoards();
		status = Status.ENDED;
	}

	public void join(Player player) {
		if (status != Status.STARTING && status != Status.PLAYING) {
			PlayerLoader.joinPlayer(player);
			board.addHider(player);
			board.createLobbyBoard(player);
			board.reloadLobbyBoards();
			if (announceMessagesToNonPlayers) Bukkit.broadcastMessage(messagePrefix + message("GAME_JOIN").addPlayer(player));
			else broadcastMessage(messagePrefix + message("GAME_JOIN").addPlayer(player));
		} else {
			PlayerLoader.loadSpectator(player, getGameWorld());
			board.addSpectator(player);
			board.createGameBoard(player);
			player.sendMessage(messagePrefix + message("GAME_JOIN_SPECTATOR"));
		}
	}

	public void leave(Player player) {
		PlayerLoader.unloadPlayer(player);
		if (announceMessagesToNonPlayers) Bukkit.broadcastMessage(messagePrefix + message("GAME_LEAVE").addPlayer(player));
		else broadcastMessage(messagePrefix + message("GAME_LEAVE").addPlayer(player));
		if (board.isHider(player) && status != Status.ENDING && status != Status.STANDBY) {
			hiderLeft = true;
		}
		board.removeBoard(player);
		board.remove(player);
		if (status == Status.STANDBY) {
			board.reloadLobbyBoards();
		} else {
			board.reloadGameBoards();
			board.reloadBoardTeams();
		}
		handleBungeeLeave(player);
	}

	private void handleBungeeLeave(Player player) {
		if (bungeeLeave) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF(leaveServer);
			player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
		} else {
			player.teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
		}
	}

	public void onTick() {
		if (isNotSetup()) return;
		if (status == Status.STANDBY) whileWaiting();
		else if (status == Status.STARTING) whileStarting();
		else if (status == Status.PLAYING) whilePlaying();
		gameTick++;
	}

	private void whileWaiting() {
		if (!lobbyCountdownEnabled) return;
		if (lobbyMin <= board.size()) {
			if (gameTimer == -1)
				lobbyTimer = countdown;
			if (board.size() >= changeCountdown)
				lobbyTimer = Math.min(lobbyTimer, 10);
			if (gameTick % 20 == 0) {
				lobbyTimer--;
				board.reloadLobbyBoards();
			}
			if (lobbyTimer == 0) {
				start();
			}
		} else {
			lobbyTimer = -1;
		}
	}

	private void whileStarting() {

		if(gameTick % 20 == 0) {
			if (startingTimer % 5 == 0 || startingTimer < 5) {
				String message;
				if (startingTimer == 0) {
					message = message("START").toString();
					status = Status.PLAYING;
					board.getPlayers().forEach(player -> PlayerLoader.resetPlayer(player, board));
				} else if (startingTimer == 1){
					message = message("START_COUNTDOWN_LAST").addAmount(startingTimer).toString();
				} else {
					message = message("START_COUNTDOWN").addAmount(startingTimer).toString();
				}
				board.getPlayers().forEach(player -> {
					if (countdownDisplay == CountdownDisplay.CHAT) {
						player.sendMessage(messagePrefix + message);
					} else if (countdownDisplay == CountdownDisplay.ACTIONBAR) {
						ActionBar.clearActionBar(player);
						ActionBar.sendActionBar(player, messagePrefix + message);
					} else if (countdownDisplay == CountdownDisplay.TITLE) {
						Titles.clearTitle(player);
						Titles.sendTitle(player, 10, 40, 10, " ", message);
					}
				});
			}
			startingTimer--;
		}
		checkWinConditions();
	}

	private void whilePlaying() {
		for(Player hider : board.getHiders()) {
			int distance = 100, temp = 100;
			for(Player seeker : board.getSeekers()) {
				try {
					temp = (int) hider.getLocation().distance(seeker.getLocation());
				} catch (Exception e) {
					//Players in different worlds, NOT OK!!!
				}
				if (distance > temp) {
					distance = temp;
				}
			}
			if (seekerPing) switch(gameTick %10) {
				case 0:
					if (distance < seekerPingLevel1) heartbeatSound.play(hider, seekerPingLeadingVolume, seekerPingPitch);
					if (distance < seekerPingLevel3) ringingSound.play(hider, seekerPingVolume, seekerPingPitch);
					break;
				case 3:
					if (distance < seekerPingLevel1) heartbeatSound.play(hider, seekerPingVolume, seekerPingPitch);
					if (distance < seekerPingLevel3) ringingSound.play(hider, seekerPingVolume, seekerPingPitch);
					break;
				case 6:
					if (distance < seekerPingLevel3) ringingSound.play(hider, seekerPingVolume, seekerPingPitch);
					break;
				case 9:
					if (distance < seekerPingLevel2) ringingSound.play(hider, seekerPingVolume, seekerPingPitch);
					break;
			}
		}
		if (gameTick %20 == 0) {
			if (gameLength > 0) {
				board.reloadGameBoards();
				gameTimer--;
			}
			if (worldBorderEnabled) worldBorder.update();
			if (tauntEnabled) taunt.update();
			if (glowEnabled || alwaysGlow) glow.update();
		}
		board.getSpectators().forEach(spectator -> spectator.setFlying(spectator.getAllowFlight()));
		checkWinConditions();
	}

	public void broadcastMessage(String message) {
		for(Player player : board.getPlayers()) {
			player.sendMessage(message);
		}
	}

	public boolean isNotSetup() {
		if (spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) return true;
		if (lobbyPosition.getBlockX() == 0 && lobbyPosition.getBlockY() == 0 && lobbyPosition.getBlockZ() == 0) return true;
		if (exitPosition.getBlockX() == 0 && exitPosition.getBlockY() == 0 && exitPosition.getBlockZ() == 0) return true;
		if (mapSaveEnabled) {
			File destination = new File(Main.getInstance().getWorldContainer() + File.separator + getGameWorld());
			if (!destination.exists()) return true;
		}
		return saveMinX == 0 || saveMinZ == 0 || saveMaxX == 0 || saveMaxZ == 0;
	}

	public String getGameWorld() {
		if (mapSaveEnabled) return "hideandseek_"+spawnWorld;
		else return spawnWorld;
	}

	private void checkWinConditions() {
		if (board.sizeHider() < 1) {
			if (hiderLeft) {
				if (announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameOverPrefix + message("GAME_GAMEOVER_HIDERS_QUIT"));
				else broadcastMessage(gameOverPrefix + message("GAME_GAMEOVER_HIDERS_QUIT"));
				stop(WinType.NONE);
			} else {
				if (announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameOverPrefix + message("GAME_GAMEOVER_HIDERS_FOUND"));
				else broadcastMessage(gameOverPrefix + message("GAME_GAMEOVER_HIDERS_FOUND"));
				stop(WinType.SEEKER_WIN);
			}
		} else if (board.sizeSeeker() < 1) {
			if (announceMessagesToNonPlayers) Bukkit.broadcastMessage(abortPrefix + message("GAME_GAMEOVER_SEEKERS_QUIT"));
			else broadcastMessage(abortPrefix + message("GAME_GAMEOVER_SEEKERS_QUIT"));
			stop(WinType.NONE);
		} else if (gameTimer < 1) {
			if (announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameOverPrefix + message("GAME_GAMEOVER_TIME"));
			else broadcastMessage(gameOverPrefix + message("GAME_GAMEOVER_TIME"));
			stop(WinType.HIDER_WIN);
		}
		hiderLeft = false;
	}

}