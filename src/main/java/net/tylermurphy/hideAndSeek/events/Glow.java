package net.tylermurphy.hideAndSeek.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Packet;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

public class Glow {

	private final int temp;
	private int glowTime;
	private boolean running;
	
	public Glow(int temp) {
		this.temp = temp;
		this.glowTime = 0;
	}
	
	public void onProjectilve() {
		if(glowStackable) glowTime += glowLength;
		else glowTime = glowLength;
		if(!running)
			startGlow();
	}
	
	private void startGlow() {
		running = true;
		for(Player hider : Main.plugin.board.getHiders()) {
			for(Player seeker : Main.plugin.board.getSeekers()) {
				Packet.setGlow(hider, seeker, true);
			}
		}
		waitGlow();
	}
	
	private void waitGlow() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if(temp != Main.plugin.gameId) return;
				glowTime--;
				glowTime = Math.max(glowTime, 0);
				if(glowTime == 0) {
					stopGlow();
				} else {
					waitGlow();
				}
			}
		}, 20);
	}
	
	private void stopGlow() {
		running = false;
		for(Player hider : Main.plugin.board.getHiders()) {
			for (Player seeker : Main.plugin.board.getSeekers()) {
				Packet.setGlow(hider, seeker, false);
			}
		}
	}

	public boolean isRunning() {
		return running;
	}
	
}
