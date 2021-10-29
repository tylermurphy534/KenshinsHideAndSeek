package net.tylermurphy.hideAndSeek.configuration;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.Main;

public class Config {

	static ConfigManager manager;
	
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
		lobbyWorld;
	
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
		glowStackable;
	
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
		glowLength;
	
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
		glowEnabled = manager.getBoolean("glow.enabled");

		//Other
		nametagsVisible = manager.getBoolean("nametagsVisible");
		permissionsRequired = manager.getBoolean("permissionsRequired");
		minPlayers = Math.max(2, manager.getInt("minPlayers"));
		gameLength = manager.getInt("gameLength");
	}
	
	public static void addToConfig(String path, Object value) {
		manager.set(path, value);
	}

	public static void saveConfig() {
		manager.saveConfig();
	}
	
}