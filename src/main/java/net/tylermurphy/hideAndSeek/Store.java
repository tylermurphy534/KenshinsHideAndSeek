package net.tylermurphy.hideAndSeek;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
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
	
	public static String messagePrefix = String.format("%sHide and Seek > %s", ChatColor.BLUE, ChatColor.WHITE);
	public static String errorPrefix = String.format("%sError > %s", ChatColor.RED, ChatColor.WHITE);
	
	public static Vector spawnPosition;
	
	public static Vector worldborderPosition;
	public static int worldborderSize,worldborderDelay,currentWorldborderSize;
	public static boolean worldborderEnabled = false, decreaseBorder = false;
	
	public static String tauntPlayer = "";
	
	public static int glowTime = 0;
	
	public static int gameId = 0;
	
	public static FileConfiguration getConfig() {
		return Main.plugin.getConfig();
	}
	
	public static void saveConfig() {
		Main.plugin.saveConfig();
	}
	
}