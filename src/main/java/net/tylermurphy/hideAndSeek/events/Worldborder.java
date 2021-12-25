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
	private int delay;
	private boolean running;
	
	public Worldborder(int temp) {
		this.temp = temp;
	}
	
	public void schedule() {
		delay = 60*worldborderDelay;
		running = false;
		waitBorder();
	}

	private void waitBorder(){
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
			if(delay == 0) decreaceWorldborder();
			else {
				delay--; waitBorder();
			}
		}, 20);
	}
	
	private void decreaceWorldborder() {
		if(temp != Main.plugin.game.gameId) return;
		if(currentWorldborderSize-100 > 100) {
			running = true;
			Util.broadcastMessage(worldborderPrefix + message("WORLDBORDER_DECREASING"));
			currentWorldborderSize -= 100;
			World world = Bukkit.getWorld("hideandseek_"+spawnWorld);
			WorldBorder border = world.getWorldBorder();
			border.setSize(border.getSize()-100,30);
			schedule();
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

	public int getDelay(){
		return delay;
	}

	public boolean isRunning() {
		return running;
	}
	
}
