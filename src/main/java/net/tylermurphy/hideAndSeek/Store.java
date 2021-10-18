package net.tylermurphy.hideAndSeek;

import java.util.ArrayList;
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
	
	public static List<String>
		Hider = new ArrayList<String>(),
		Seeker = new ArrayList<String>(),
		Spectator = new ArrayList<String>(),
		Deaths = new ArrayList<String>();
	
	public static Scoreboard 
		board;	
	
	public static Team 
		HiderTeam,
		SeekerTeam,
		SpectatorTeam;
	
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
		status = "Standby";
	
	public static Vector
		spawnPosition,
		lobbyPosition,
		exitPosition,
		worldborderPosition;
	
	public static boolean 
		nametagsVisible,
		permissionsRequired,
		announceMessagesToNonPlayers,
		lobbyStarted = false,
		worldborderEnabled = false, 
		runningBackup = false;
	
	public static int 
		minPlayers,
		gameId = 0,
		worldborderSize,
		worldborderDelay,
		currentWorldborderSize,
		gameLength,
		timeLeft = 0;
	
	public static FileConfiguration getConfig() {
		return Main.plugin.getConfig();
	}
	
	public static void saveConfig() {
		Main.plugin.saveConfig();
	}
	
	public static void loadConfig() {
		
		Main.plugin.reloadConfig();
		
		//Default
		getConfig().addDefault("worldBorder.x", 0);
		getConfig().addDefault("worldBorder.z", 0);
		getConfig().addDefault("worldBorder.delay", 10);
		getConfig().addDefault("worldBorder.size", 500);
		getConfig().addDefault("worldBorder.enabled", false);
		getConfig().addDefault("prefix.default", "&9Hide and Seek > &f");
		getConfig().addDefault("prefix.error", "&cError > &f");
		getConfig().addDefault("prefix.taunt", "&eTaunt > &f");
		getConfig().addDefault("prefix.border", "&cWorld Border > &f");
		getConfig().addDefault("prefix.abort", "&cAbort > &f");
		getConfig().addDefault("prefix.gameover", "&aGame Over > &f");
		getConfig().addDefault("prefix.warning", "&cWarning > &f");
		getConfig().addDefault("nametagsVisible", false);
		getConfig().addDefault("permissionsRequired", true);
		getConfig().addDefault("announceMessagesToNonPlayers", false);
		getConfig().addDefault("spawns.lobby.x", 0);
		getConfig().addDefault("spawns.lobby.y", 0);
		getConfig().addDefault("spawns.lobby.z", 0);
		getConfig().addDefault("spawns.lobby.world", "world");
		getConfig().addDefault("spawns.exit.x", 0);
		getConfig().addDefault("spawns.exit.y", 0);
		getConfig().addDefault("spawns.exit.z", 0);
		getConfig().addDefault("spawns.exit.world", "world");
		getConfig().addDefault("spawns.game.x", 0);
		getConfig().addDefault("spawns.game.y", 0);
		getConfig().addDefault("spawns.game.z", 0);
		getConfig().addDefault("spawns.game.world", "world");
		getConfig().addDefault("minPlayers", 2);
		getConfig().addDefault("gameLength", 600);
		
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