package net.tylermurphy.hideAndSeek.bukkit;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.command.Stop;
import net.tylermurphy.hideAndSeek.util.Functions;
import net.tylermurphy.hideAndSeek.util.Packet;

public class Tick {

	static int tick = 0;
	
	public static void onTick() {

		if(board == null) {
			Functions.loadScoreboard();
		}
		
		if(status.equals("Standby")) {
			tick = 0;
		}
		 
		if(status.equals("Playing")) {
			onPlaying();
		}
		
		if(( status.equals("Starting") || status.equals("Playing") ) && Hider.size() < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + "All hiders have been found.");
			else Functions.broadcastMessage(gameoverPrefix + "All hiders have been found.");
			Stop.onStop();
		}
		if(( status.equals("Starting") || status.equals("Playing") ) && Seeker.size() < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(abortPrefix + "All seekers have quit.");
			else Functions.broadcastMessage(abortPrefix + "All seekers have quit.");
			Stop.onStop();
		}
		
	}
	
	private static void onPlaying() {
		
		tick ++;
		
		for(String playerName : Hider) {
			Player player = playerList.get(playerName);
			int distance = 100;
			for(String seekerName : Seeker) {
				Player seeker = playerList.get(seekerName);
				int temp = (int) player.getLocation().distance(seeker.getLocation());
				if(distance > temp) {
					distance = temp;
				}
			}
			switch(tick%10) {
				case 0:
					if(distance < 30) Packet.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, .5f, 1f);
					if(distance < 10) Packet.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 3:
					if(distance < 30) Packet.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, .3f, 1f);
					if(distance < 10) Packet.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 6:
					if(distance < 10) Packet.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 9:
					if(distance < 20) Packet.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
			}
			
		}
		
		if(tick%20 == 0) {
			if(gameLength > 0) {
				timeLeft--;
				for(Player player : playerList.values()) {
					player.setLevel(timeLeft);
				}
				if(timeLeft < 1) {
					if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + "Seekers ran out of time. Hiders win!");
					else Functions.broadcastMessage(gameoverPrefix + "Seekers ran out of time. Hiders win!");
					Stop.onStop();
				}
			}
		}
	}
}