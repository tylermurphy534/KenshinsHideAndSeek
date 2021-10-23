package net.tylermurphy.hideAndSeek.events;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;
import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Util;

public class Worldborder {

	private final int temp;
	
	public Worldborder(int temp) {
		this.temp = temp;
	}
	
	public void schedule() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				decreaceWorldborder();
			}
		},20*60*worldborderDelay);
	}
	
	private void decreaceWorldborder() {
		if(temp != Main.plugin.gameId) return;
		if(currentWorldborderSize-100 > 100) {
			Util.broadcastMessage(worldborderPrefix + message("WORLDBORDER_DECREASING"));
			currentWorldborderSize -= 100;
			World world = Bukkit.getWorld("hideandseek_"+spawnWorld);
			WorldBorder border = world.getWorldBorder();
			border.setSize(border.getSize()-100,30);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					decreaceWorldborder();
				}
			},20*60*worldborderDelay);
		}
	}
	
	public static void resetWorldborder(String worldName) {
		if(worldborderEnabled) {
			World world = Bukkit.getWorld(worldName);
			WorldBorder border = world.getWorldBorder();
			border.setSize(worldborderSize);
			border.setCenter(worldborderPosition.getX(), worldborderPosition.getZ());
			currentWorldborderSize = worldborderSize;
		} else {
			World world = Bukkit.getWorld(worldName);
			WorldBorder border = world.getWorldBorder();
			border.setSize(30000000);
			border.setCenter(0, 0);
		}
	}
	
}
