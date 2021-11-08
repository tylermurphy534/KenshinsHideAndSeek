package net.tylermurphy.hideAndSeek.bukkit;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.command.Stop;
import net.tylermurphy.hideAndSeek.util.Packet;
import net.tylermurphy.hideAndSeek.util.Util;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Tick {

	static int tick = 0;
	
	public static void onTick() {
		
		if(Main.plugin.status.equals("Standby")) tick = 0;
		else if(Main.plugin.status.equals("Playing")) onPlaying();
		
		if(( Main.plugin.status.equals("Starting") || Main.plugin.status.equals("Playing") ) && Main.plugin.board.sizeHider() < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_HIDERS_FOUND"));
			else Util.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_HIDERS_FOUND"));
			Stop.onStop();
		}
		if(( Main.plugin.status.equals("Starting") || Main.plugin.status.equals("Playing") ) && Main.plugin.board.sizeSeeker() < 1) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(abortPrefix + message("GAME_GAMEOVER_SEEKERS_QUIT"));
			else Util.broadcastMessage(abortPrefix + message("GAME_GAMEOVER_SEEKERS_QUIT"));
			Stop.onStop();
		}
		
	}
	
	private static void onPlaying() {
		
		if(tick<1000000) tick++;
		else tick = 1;
		
		for(Player hider : Main.plugin.board.getHiders()) {
			int distance = 100, temp = 100;
			for(Player seeker : Main.plugin.board.getSeekers()) {
				try {
					temp = (int) hider.getLocation().distance(seeker.getLocation());
				} catch (Exception e){
					//Players in different worlds, NOT OK!!!
				}
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
				if(Main.plugin.timeLeft < 1) {
					if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_TIME"));
					else Util.broadcastMessage(gameoverPrefix + message("GAME_GAMEOVER_TIME"));
					Stop.onStop();
				}
			}
		}
	}
}