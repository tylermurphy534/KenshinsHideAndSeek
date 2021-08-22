package net.tylermurphy.hideAndSeek.manager;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

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
			World world = Bukkit.getWorld("world");
			WorldBorder border = world.getWorldBorder();
			border.setSize(worldborderSize);
			border.setCenter(worldborderPosition.getX(), worldborderPosition.getZ());
			currentWorldborderSize = worldborderSize;
		} else {
			World world = Bukkit.getWorld("world");
			WorldBorder border = world.getWorldBorder();
			border.setSize(30000000);
			border.setCenter(0, 0);
		}
	}
	
}
