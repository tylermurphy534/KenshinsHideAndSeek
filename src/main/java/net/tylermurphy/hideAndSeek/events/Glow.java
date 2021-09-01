package net.tylermurphy.hideAndSeek.events;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Packet;

public class Glow {

	private final int temp;
	private int glowTime;
	private boolean running;
	
	public Glow(int temp) {
		this.temp = temp;
		this.glowTime = 0;
	}
	
	public void onProjectilve() {
		glowTime++;
		if(!running)
			startGlow();
	}
	
	private void startGlow() {
		running = true;
		for(String hiderName : Hider) {
			Player hider = playerList.get(hiderName);
			if(hider == null) continue;
			for(String seekerName : Seeker) {
				Player seeker = playerList.get(seekerName);
				if(seeker == null) continue;
				Packet.setGlow(hider, seeker, true);
			}
		}
		waitGlow();
	}
	
	private void waitGlow() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				if(temp != gameId) return;
				glowTime--;
				glowTime = Math.max(glowTime, 0);
				if(glowTime == 0) {
					stopGlow();
				} else {
					waitGlow();
				}
			}
		}, 20*30);
	}
	
	private void stopGlow() {
		for(String hiderName : Hider) {
			Player hider = playerList.get(hiderName);
			if(hider == null) continue;
			for(String seekerName : Seeker) {
				Player seeker = playerList.get(seekerName);
				if(seeker == null) continue;
				Packet.setGlow(hider, seeker, false);
			}
		}
	}
	
}
