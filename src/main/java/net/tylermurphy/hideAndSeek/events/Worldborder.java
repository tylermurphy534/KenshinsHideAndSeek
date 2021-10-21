package net.tylermurphy.hideAndSeek.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Util;

import static net.tylermurphy.hideAndSeek.Config.*;

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
			Util.broadcastMessage(worldborderPrefix + "Worldborder decreacing by 100 blocks over the next 30s");
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
