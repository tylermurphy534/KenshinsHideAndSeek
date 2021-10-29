package net.tylermurphy.hideAndSeek.util;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.io.*;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.configuration.LocalizationString;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;

public class Util {
    
    public static void broadcastMessage(String message) {
    	for(Player player : Main.plugin.board.getPlayers()) {
    		player.sendMessage(message);
    	}
    }
    
    public static boolean isSetup() {
    	if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) return false;
		if(lobbyPosition.getBlockX() == 0 && lobbyPosition.getBlockY() == 0 && lobbyPosition.getBlockZ() == 0) return false;
		if(exitPosition.getBlockX() == 0 && exitPosition.getBlockY() == 0 && exitPosition.getBlockZ() == 0) return false;
		File destenation = new File(Main.root+File.separator+"hideandseek_"+spawnWorld);
		if(!destenation.exists()) return false;
		if(saveMinX == 0 || saveMinZ == 0 || saveMaxX == 0 || saveMaxZ == 0) return false;
		return true;
    }
    
    public static void sendDelayedMessage(String message, int gameId, int delay) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, new Runnable() {
			public void run() {
				if(gameId == Main.plugin.gameId)
					Util.broadcastMessage(message);
			}
		}, delay);
	}

	public YamlConfiguration loadDefaultConfig(String name) {

		YamlConfiguration defaultConfig = null;

		InputStream deafult_stream = null;
		InputStreamReader default_stream_reader = null;
		try {
			deafult_stream = Class.class.getResourceAsStream(name + ".yml");
			default_stream_reader = new InputStreamReader(deafult_stream);
			defaultConfig = YamlConfiguration.loadConfiguration(default_stream_reader);
		} catch (Exception e) {
			// No Issue Here
		} finally {
			try {
				deafult_stream.close();
				default_stream_reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return defaultConfig;
	}
	
}