package net.tylermurphy.hideAndSeek.bukkit;

import static net.tylermurphy.hideAndSeek.Config.*;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.command.Stop;
import net.tylermurphy.hideAndSeek.util.Packet;
import net.tylermurphy.hideAndSeek.util.Util;

public class Tick {

	static int tick = 0;
	
	public static void onTick() {
		
		if(Main.plugin.status.equals("Standby")) tick = 0;
		else if(Main.plugin.status.equals("Playing")) onPlaying();
		
		if(( Main.plugin.status.equals("Starting") || Main.plugin.status.equals("Playing") ) && Main.plugin.board.sizeHider() < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + "All hiders have been found.");
			else Util.broadcastMessage(gameoverPrefix + "All hiders have been found.");
			Stop.onStop();
		}
		if(( Main.plugin.status.equals("Starting") || Main.plugin.status.equals("Playing") ) && Main.plugin.board.sizeSeeker() < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(abortPrefix + "All seekers have quit.");
			else Util.broadcastMessage(abortPrefix + "All seekers have quit.");
			Stop.onStop();
		}
		
	}
	
	private static void onPlaying() {
		
		if(tick<1000000) tick++;
		else tick = 1;
		
		for(Player hider : Main.plugin.board.getHiders()) {
			int distance = 100;
			for(Player seeker : Main.plugin.board.getSeekers()) {
				int temp = (int) hider.getLocation().distance(seeker.getLocation());
				if(distance > temp) {
					distance = temp;
				}
			}
			switch(tick%10) {
				case 0:
					if(distance < 30) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, .5f, 1f);
					if(distance < 10) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 3:
					if(distance < 30) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, .3f, 1f);
					if(distance < 10) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 6:
					if(distance < 10) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 9:
					if(distance < 20) Packet.playSound(hider, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
			}
			
		}
		
		if(tick%20 == 0) {
			if(gameLength > 0) {
				Main.plugin.board.reloadGameBoards();
				Main.plugin.timeLeft--;
				for(Player player : Main.plugin.board.getPlayers()) {
					player.setLevel(Main.plugin.timeLeft);
				}
				if(Main.plugin.timeLeft < 1) {
					if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + "Seekers ran out of time. Hiders win!");
					else Util.broadcastMessage(gameoverPrefix + "Seekers ran out of time. Hiders win!");
					Stop.onStop();
				}
			}
		}
	}
}