package net.tylermurphy.hideAndSeek;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class Store {

	public static Map<String,Player> playerList = new HashMap<String,Player>();
	
	public static Scoreboard board;	
	public static Team Hider,Seeker,Spectator;
	
	public static String status = "Setup";
	
	public static String messagePrefix,errorPrefix,tauntPrefix,worldborderPrefix,abortPrefix,gameoverPrefix;
	
	public static Vector spawnPosition;
	public static String spawnWorld;
	
	public static Vector worldborderPosition;
	public static int worldborderSize,worldborderDelay,currentWorldborderSize;
	public static boolean worldborderEnabled = false, decreaseBorder = false;
	
	public static List<String> blockedCommands;
	
	public static boolean nametagsVisible;
	
	public static String tauntPlayer = "";
	
	public static int glowTime = 0;
	
	public static int gameId = 0;
	
	public static FileConfiguration getConfig() {
		return Main.plugin.getConfig();
	}
	
	public static void saveConfig() {
		Main.plugin.saveConfig();
	}
	
	public static void loadConfig() {
		
		Main.plugin.reloadConfig();
		
		getConfig().addDefault("spawn.x", 0);
		getConfig().addDefault("spawn.y", 0);
		getConfig().addDefault("spawn.z", 0);
		getConfig().addDefault("spawn.world", "world");
		getConfig().addDefault("worldBorder.x", 0);
		getConfig().addDefault("worldBorder.z", 0);
		getConfig().addDefault("worldBorder.delay", 10);
		getConfig().addDefault("worldBorder.size", 500);
		getConfig().addDefault("worldBorder.enabled", false);
		getConfig().addDefault("blockedCommands", Arrays.asList("tp","kill","gamemode","effect","clear"));
		getConfig().addDefault("prefix.default", "&9Hide and Seek > &f");
		getConfig().addDefault("prefix.error", "&cError > &f");
		getConfig().addDefault("prefix.taunt", "&eTaunt > &f");
		getConfig().addDefault("prefix.border", "&cWorld Border > &f");
		getConfig().addDefault("prefix.abort", "&cAbort > &f");
		getConfig().addDefault("prefix.gameover", "&aGame Over > &f");
		getConfig().addDefault("nametagsVisible", false);
		
		spawnPosition = new Vector(
				getConfig().getDouble("spawn.x"), 
				getConfig().getDouble("spawn.y"),
				getConfig().getDouble("spawn.z")
			);
		spawnWorld = getConfig().getString("spawn.world");
		
		worldborderPosition = new Vector(
				getConfig().getInt("worldBorder.x"), 
				0, 
				getConfig().getInt("worldBorder.z")
			);
		worldborderSize = getConfig().getInt("worldBorder.size");
		worldborderDelay = getConfig().getInt("worldBorder.delay");
		worldborderEnabled = getConfig().getBoolean("worldBorder.enabled");
		
		blockedCommands = getConfig().getStringList("blockedCommands");
		
		messagePrefix = getConfig().getString("prefix.default").replace("&", "§");
		errorPrefix = getConfig().getString("prefix.error").replace("&", "§");
		tauntPrefix = getConfig().getString("prefix.taunt").replace("&", "§");
		worldborderPrefix = getConfig().getString("prefix.border").replace("&", "§");
		abortPrefix = getConfig().getString("prefix.abort").replace("&", "§");
		gameoverPrefix = getConfig().getString("prefix.gameover").replace("&", "§");
		
		nametagsVisible = getConfig().getBoolean("nametagsVisible");
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		if(spawnPosition.getBlockX() != 0 || spawnPosition.getBlockY() != 0 || spawnPosition.getBlockZ() != 0) {
			status = "Standby";
		}
		
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