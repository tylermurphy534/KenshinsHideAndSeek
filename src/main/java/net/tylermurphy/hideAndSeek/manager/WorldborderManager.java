package net.tylermurphy.hideAndSeek.manager;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.Main;

public class WorldborderManager {

	public static void schedule() {
		
		Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){

			public void run() {
				int temp = gameId;
				while(true) {
					try { Thread.sleep(1000*60*worldborderDelay); } catch (InterruptedException e) {}
					if(gameId != temp) break;
					if(currentWorldborderSize-100 > 100) {
						Bukkit.getServer().broadcastMessage(ChatColor.RED + "World Border> " + ChatColor.WHITE + "Worldborder decreacing by 100 blocks over the next 30s");
						currentWorldborderSize -= 100;
						decreaseBorder = true;
					} else {
						break;
					}
				}
			}
		});
	}
	
	public static void reset() {
		if(worldborderEnabled) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "worldborder set "+worldborderSize);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("worldborder center %s %s",worldborderPosition.getBlockX(),worldborderPosition.getBlockZ()));
			currentWorldborderSize = worldborderSize;
		} else {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "worldborder set 30000000");
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "worldborder center 0 0");
		}
	}
	
}
