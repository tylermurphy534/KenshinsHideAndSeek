

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.Main;

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
	
	public static List<String> blockedCommands;
	
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
		ConfigurationSection spawnConfig = getConfig().getConfigurationSection("spawn");
		if(spawnConfig == null) spawnConfig = getConfig().createSection("spawn");
		spawnPosition = new Vector(spawnConfig.getDouble("x", 0), spawnConfig.getDouble("y", 0), spawnConfig.getDouble("z", 0));
		getConfig().createSection("spawn", spawnConfig.getValues(true));
		ConfigurationSection worldBorderConfig = getConfig().getConfigurationSection("worldBorder");
		if(worldBorderConfig == null) worldBorderConfig = getConfig().createSection("worldBorder");
		worldborderPosition = new Vector(worldBorderConfig.getInt("x", 0), 0, worldBorderConfig.getInt("z", 0));
		worldborderSize = worldBorderConfig.getInt("x", 500);
		worldborderEnabled = worldBorderConfig.getBoolean("enabled", false);
		getConfig().createSection("worldBorder", worldBorderConfig.getValues(true));
		getConfig().addDefault("blockedCommands", new ArrayList<String>());
		blockedCommands = getConfig().getStringList("blockedCommands");
	}
	
}