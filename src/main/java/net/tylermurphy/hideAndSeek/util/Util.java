package net.tylermurphy.hideAndSeek.util;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.io.File;

import org.bukkit.Bukkit;
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
	
}