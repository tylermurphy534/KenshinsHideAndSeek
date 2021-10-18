package net.tylermurphy.hideAndSeek.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Functions;

import static net.tylermurphy.hideAndSeek.Store.*;

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
		if(temp != gameId) return;
		if(currentWorldborderSize-100 > 100) {
			Functions.broadcastMessage(worldborderPrefix + "Worldborder decreacing by 100 blocks over the next 30s");
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
	
}
