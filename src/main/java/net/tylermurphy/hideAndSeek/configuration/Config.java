/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
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

package net.tylermurphy.hideAndSeek.configuration;

import net.tylermurphy.hideAndSeek.util.Version;
import org.bukkit.util.Vector;

public class Config {

	private static ConfigManager manager;
	
	public static String 
		messagePrefix,
		errorPrefix,
		tauntPrefix,
		worldborderPrefix,
		abortPrefix,
		gameoverPrefix,
		warningPrefix,
		spawnWorld,
		exitWorld,
		lobbyWorld,
		locale;
	
	public static Vector
		spawnPosition,
		lobbyPosition,
		exitPosition,
		worldborderPosition;
	
	public static boolean 
		nametagsVisible,
		permissionsRequired,
		announceMessagesToNonPlayers,
		worldborderEnabled,
		tauntEnabled,
		tauntCountdown,
		tauntLast,
		glowEnabled,
		glowStackable,
		pvpEnabled,
		autoJoin,
		teleportToExit,
		lobbyCountdownEnabled,
		seekerPing;
	
	public static int 
		minPlayers,
		worldborderSize,
		worldborderDelay,
		currentWorldborderSize,
		gameLength,
		saveMinX,
		saveMinZ,
		saveMaxX,
		saveMaxZ,
		tauntDelay,
		glowLength,
		countdown,
		changeCountdown,
		lobbyMin,
		lobbyMax,
		seekerPingLevel1,
		seekerPingLevel2,
		seekerPingLevel3;
	
	public static void loadConfig() {

		manager = new ConfigManager("config.yml");
		manager.saveConfig();

		//Spawn
		spawnPosition = new Vector(
				manager.getDouble("spawns.game.x"),
				Math.max(0, Math.min(255, manager.getDouble("spawns.game.y"))),
				manager.getDouble("spawns.game.z")
		);
		spawnWorld = manager.getString("spawns.game.world");

		///Lobby
		lobbyPosition = new Vector(
				manager.getDouble("spawns.lobby.x"),
				Math.max(0, Math.min(255, manager.getDouble("spawns.lobby.y"))),
				manager.getDouble("spawns.lobby.z")
		);
		lobbyWorld = manager.getString("spawns.lobby.world");

		announceMessagesToNonPlayers = manager.getBoolean("announceMessagesToNonPlayers");

		exitPosition = new Vector(
				manager.getDouble("spawns.exit.x"),
				Math.max(0, Math.min(255, manager.getDouble("spawns.exit.y"))),
				manager.getDouble("spawns.exit.z")
		);
		exitWorld = manager.getString("spawns.exit.world");

		//World border
		worldborderPosition = new Vector(
				manager.getInt("worldBorder.x"),
				0,
				manager.getInt("worldBorder.z")
		);
		worldborderSize = Math.max(100, manager.getInt("worldBorder.size"));
		worldborderDelay = Math.max(1, manager.getInt("worldBorder.delay"));
		worldborderEnabled = manager.getBoolean("worldBorder.enabled");

		//Prefix
		char SYMBOLE = '\u00A7';
		String SYMBOLE_STRING = String.valueOf(SYMBOLE);

		messagePrefix = manager.getString("prefix.default").replace("&", SYMBOLE_STRING);
		errorPrefix = manager.getString("prefix.error").replace("&", SYMBOLE_STRING);
		tauntPrefix = manager.getString("prefix.taunt").replace("&", SYMBOLE_STRING);
		worldborderPrefix = manager.getString("prefix.border").replace("&", SYMBOLE_STRING);
		abortPrefix = manager.getString("prefix.abort").replace("&", SYMBOLE_STRING);
		gameoverPrefix = manager.getString("prefix.gameover").replace("&", SYMBOLE_STRING);
		warningPrefix = manager.getString("prefix.warning").replace("&", SYMBOLE_STRING);

		//Map Bounds
		saveMinX = manager.getInt("bounds.min.x");
		saveMinZ = manager.getInt("bounds.min.z");
		saveMaxX = manager.getInt("bounds.max.x");
		saveMaxZ = manager.getInt("bounds.max.z");

		//Taunt
		tauntEnabled = manager.getBoolean("taunt.enabled");
		tauntCountdown = manager.getBoolean("taunt.showCountdown");
		tauntDelay = Math.max(60,manager.getInt("taunt.delay"));
		tauntLast = manager.getBoolean("taunt.whenLastPerson");

		//Glow
		glowLength = Math.max(1,manager.getInt("glow.time"));
		glowStackable = manager.getBoolean("glow.stackable");
		glowEnabled = manager.getBoolean("glow.enabled") && Version.atLeast("1.9");

		//Lobby
		minPlayers = Math.max(2, manager.getInt("minPlayers"));
		countdown = Math.max(10,manager.getInt("lobby.countdown"));
		changeCountdown = Math.max(minPlayers,manager.getInt("lobby.changeCountdown"));
		lobbyMin = Math.max(minPlayers,manager.getInt("lobby.min"));
		lobbyMax = manager.getInt("lobby.max");
		lobbyCountdownEnabled = manager.getBoolean("lobby.enabled");

		//SeekerPing
		seekerPing = manager.getBoolean("seekerPing.enabled");
		seekerPingLevel1 = manager.getInt("seekerPing.distances.level1");
		seekerPingLevel2 = manager.getInt("seekerPing.distances.level2");
		seekerPingLevel3 = manager.getInt("seekerPing.distances.level3");

		//Other
		nametagsVisible = manager.getBoolean("nametagsVisible");
		permissionsRequired = manager.getBoolean("permissionsRequired");
		gameLength = manager.getInt("gameLength");
		pvpEnabled = manager.getBoolean("pvp");
		autoJoin = manager.getBoolean("autoJoin");
		teleportToExit = manager.getBoolean("teleportToExit");
		locale = manager.getString("locale", "local");
	}
	
	public static void addToConfig(String path, Object value) {
		manager.set(path, value);
	}

	public static void saveConfig() {
		manager.saveConfig();
	}
	
}