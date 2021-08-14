package net.tylermurphy.hideAndSeek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class Store {

	public static Map<String,Player> playerList = new HashMap<String,Player>();
	public static List<String> loadedPlayers = new ArrayList<String>();
	public static Scoreboard board;
	public static Team Hider,Seeker,Spectator;
	public static String status = "Setup";
	public static String messagePrefix = String.format("%sHide and Seek > %s", ChatColor.BLUE, ChatColor.WHITE);
	public static String errorPrefix = String.format("%sError > %s", ChatColor.RED, ChatColor.WHITE);
	public static Vector spawnPosition,worldborderPosition;
	public static int worldborderSize,worldborderDelay,currentWorldborderSize;
	public static boolean worldborderEnabled = false, decreaseBorder = false;
	public static String tauntPlayer = "";
	public static HashMap<String,HashMap<String,Integer>> playerData = new HashMap<String,HashMap<String,Integer>>();
	public static int startTaskId;
	public static int gameId = 0;
	
	public static FileConfiguration getConfig() {
		return Main.plugin.getConfig();
	}
	
	public static void saveConfig() {
		Main.plugin.saveConfig();
	}
	
	public static int getPlayerData(String playerName, String key) {
		HashMap<String,Integer> data = playerData.get(playerName);
		if(data == null) {
			return 0;
		}
		if(data.get(key) == null) {
			return 0;
		}
		return data.get(key);
	}
	
	public static void setPlayerData(String playerName, String key, int value) {
		HashMap<String,Integer> data = playerData.get(playerName);
		if(data == null) {
			data = new HashMap<String,Integer>();
		}
		data.put(key, value);
		playerData.put(playerName, data);
	}
	
	public static void resetPlayerData(String playerName, boolean giveItems) {
		HashMap<String,Integer> data = new HashMap<String,Integer>();
		data.put("Death", 0);
		data.put("GiveStatus", (giveItems) ? 1 : 0);
		playerData.put(playerName, data);
	}
}
