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

	public static Map<String,Player> 
		playerList = new HashMap<String,Player>();
	
	public static Scoreboard 
		board;	
	
	public static Team 
		Hider,
		Seeker,
		Spectator;
	
	public static String 
		messagePrefix,
		errorPrefix,
		tauntPrefix,
		worldborderPrefix,
		abortPrefix,
		gameoverPrefix,
		warningPrefix,
		spawnWorld,
		status = "Standby",
		tauntPlayer = "";
	
	public static Vector 
		spawnPosition,
		worldborderPosition;
	
	public static List<String> 
		blockedCommands;
	
	public static boolean 
		nametagsVisible,
		permissionsRequired,
		unbreakableArmorstands,
		unbreakablePaintings,
		unbreakableItemframes,
		interactableArmorstands,
		interactableItemframes,
		interactableDoors,
		interactableTrapdoors,
		interactableFencegate,
		worldborderEnabled = false, 
		decreaseBorder = false;
	
	public static int 
		minPlayers,
		glowTime = 0,
		gameId = 0,
		worldborderSize,
		worldborderDelay,
		currentWorldborderSize;
	
	public static FileConfiguration getConfig() {
		return Main.plugin.getConfig();
	}
	
	public static void saveConfig() {
		Main.plugin.saveConfig();
	}
	
	public static void loadConfig() {
		
		Main.plugin.reloadConfig();
		
		//Default
		getConfig().addDefault("spawn.x", 0);
		getConfig().addDefault("spawn.y", 0);
		getConfig().addDefault("spawn.z", 0);
		getConfig().addDefault("spawn.world", "world");
		getConfig().addDefault("worldBorder.x", 0);
		getConfig().addDefault("worldBorder.z", 0);
		getConfig().addDefault("worldBorder.delay", 10);
		getConfig().addDefault("worldBorder.size", 500);
		getConfig().addDefault("worldBorder.enabled", false);
		getConfig().addDefault("blockedCommands", Arrays.asList("whisper","msg"));
		getConfig().addDefault("prefix.default", "&9Hide and Seek > &f");
		getConfig().addDefault("prefix.error", "&cError > &f");
		getConfig().addDefault("prefix.taunt", "&eTaunt > &f");
		getConfig().addDefault("prefix.border", "&cWorld Border > &f");
		getConfig().addDefault("prefix.abort", "&cAbort > &f");
		getConfig().addDefault("prefix.gameover", "&aGame Over > &f");
		getConfig().addDefault("prefix.warning", "&cWarning > &f");
		getConfig().addDefault("nametagsVisible", false);
		getConfig().addDefault("permissionsRequired", true);
		getConfig().addDefault("blockSettings.unbreakable.painting", false);
		getConfig().addDefault("blockSettings.unbreakable.armorstand", false);
		getConfig().addDefault("blockSettings.unbreakable.itemframe", false);
		getConfig().addDefault("blockSettings.interactable.armorstand", true);
		getConfig().addDefault("blockSettings.interactable.itemframe", true);
		getConfig().addDefault("blockSettings.interactable.door", true);
		getConfig().addDefault("blockSettings.interactable.trapdoor", true);
		getConfig().addDefault("blockSettings.interactable.fence", true);
		getConfig().addDefault("minPlayers", 2);
		
		//Spawn
		spawnPosition = new Vector(
				getConfig().getDouble("spawn.x"), 
				Math.max(0,Math.min(255,getConfig().getDouble("spawn.y"))),
				getConfig().getDouble("spawn.z")
			);
		spawnWorld = getConfig().getString("spawn.world");
		
		//World border
		worldborderPosition = new Vector(
				getConfig().getInt("worldBorder.x"), 
				0, 
				getConfig().getInt("worldBorder.z")
			);
		worldborderSize = Math.max(100,getConfig().getInt("worldBorder.size"));
		worldborderDelay = Math.max(1,getConfig().getInt("worldBorder.delay"));
		worldborderEnabled = getConfig().getBoolean("worldBorder.enabled");
		blockedCommands = getConfig().getStringList("blockedCommands");
		
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
		unbreakablePaintings = getConfig().getBoolean("blockSettings.unbreakable.painting");
		unbreakableArmorstands = getConfig().getBoolean("blockSettings.unbreakable.armorstand");
		unbreakableItemframes = getConfig().getBoolean("blockSettings.unbreakable.itemframe");
		interactableArmorstands = getConfig().getBoolean("blockSettings.interactable.armorstand");
		interactableItemframes = getConfig().getBoolean("blockSettings.interactable.itemframe");
		interactableDoors = getConfig().getBoolean("blockSettings.interactable.door");
		interactableTrapdoors = getConfig().getBoolean("blockSettings.interactable.trapdoor");
		interactableFencegate = getConfig().getBoolean("blockSettings.interactable.fence");
		minPlayers = Math.max(2,getConfig().getInt("minPlayers"));
		
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