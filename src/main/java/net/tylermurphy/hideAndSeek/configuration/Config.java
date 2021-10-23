package net.tylermurphy.hideAndSeek.configuration;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.Main;

public class Config {
	
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
		worldborderEnabled;
	
	public static int 
		minPlayers,
		worldborderSize,
		worldborderDelay,
		currentWorldborderSize,
		gameLength;
	
	public static FileConfiguration getConfig() {
		return Main.plugin.getConfig();
	}
	
	public static void saveConfig() {
		Main.plugin.saveConfig();
	}
	
	public static void loadConfig() {
		
		Main.plugin.reloadConfig();
		
		//Spawn
		spawnPosition = new Vector(
				getConfig().getDouble("spawns.game.x"), 
				Math.max(0,Math.min(255,getConfig().getDouble("spawns.game.y"))),
				getConfig().getDouble("spawns.game.z")
			);
		spawnWorld = getConfig().getString("spawns.game.world");
		
		///Lobby
		lobbyPosition = new Vector(
				getConfig().getDouble("spawns.lobby.x"), 
				Math.max(0,Math.min(255,getConfig().getDouble("spawns.lobby.y"))),
				getConfig().getDouble("spawns.lobby.z")
			);
		lobbyWorld = getConfig().getString("spawns.lobby.world");
		
		announceMessagesToNonPlayers = getConfig().getBoolean("announceMessagesToNonPlayers");
		
		exitPosition = new Vector(
				getConfig().getDouble("spawns.exit.x"), 
				Math.max(0,Math.min(255,getConfig().getDouble("spawns.exit.y"))),
				getConfig().getDouble("spawns.exit.z")
			);
		exitWorld = getConfig().getString("spawns.exit.world");
		
		//World border
		worldborderPosition = new Vector(
				getConfig().getInt("worldBorder.x"), 
				0, 
				getConfig().getInt("worldBorder.z")
			);
		worldborderSize = Math.max(100,getConfig().getInt("worldBorder.size"));
		worldborderDelay = Math.max(1,getConfig().getInt("worldBorder.delay"));
		worldborderEnabled = getConfig().getBoolean("worldBorder.enabled");
		
		//Prefix
		char SYMBOLE = '\u00A7';
		String SYMBOLE_STRING = new String(new char[] {SYMBOLE});
		
		messagePrefix = getConfig().getString("prefix.default").replace("&", SYMBOLE_STRING);
		errorPrefix = getConfig().getString("prefix.error").replace("&", SYMBOLE_STRING);
		tauntPrefix = getConfig().getString("prefix.taunt").replace("&", SYMBOLE_STRING);
		worldborderPrefix = getConfig().getString("prefix.border").replace("&", SYMBOLE_STRING);
		abortPrefix = getConfig().getString("prefix.abort").replace("&", SYMBOLE_STRING);
		gameoverPrefix = getConfig().getString("prefix.gameover").replace("&", SYMBOLE_STRING);
		warningPrefix = getConfig().getString("prefix.warning").replace("&", SYMBOLE_STRING);
		
		//Other
		nametagsVisible = getConfig().getBoolean("nametagsVisible");
		permissionsRequired = getConfig().getBoolean("permissionsRequired");
		minPlayers = Math.max(2,getConfig().getInt("minPlayers"));
		gameLength = getConfig().getInt("gameLength");
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
	}
	
	public static void addToSection(String sectionName, Map<String,Object> values) {
		ConfigurationSection section = getConfig().getConfigurationSection(sectionName);
		if(section == null) section = getConfig().createSection(sectionName);
		Map<String,Object> sectionValues = section.getValues(true);
		for(Entry<String, Object> entry : values.entrySet()) {
			sectionValues.put(entry.getKey(), entry.getValue());
		}
		getConfig().createSection(sectionName, sectionValues);
		saveConfig();
	}
	
}