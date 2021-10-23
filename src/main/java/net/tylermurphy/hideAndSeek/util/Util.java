package net.tylermurphy.hideAndSeek.util;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
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
    
    public static void unloadMap(String mapname){
        if(Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(mapname), false)){
            Main.plugin.getLogger().info("Successfully unloaded " + mapname);
        }else{
            Main.plugin.getLogger().severe("COULD NOT UNLOAD " + mapname);
        }
    }

    public static void loadMap(String mapname){
        Bukkit.getServer().createWorld(new WorldCreator(mapname));
        Bukkit.getServer().getWorld("hideandseek_"+spawnWorld).setAutoSave(false);
    }
 
    public static void rollback(String mapname){
        unloadMap(mapname);
        loadMap(mapname);
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