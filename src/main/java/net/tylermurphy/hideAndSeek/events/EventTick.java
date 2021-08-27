package net.tylermurphy.hideAndSeek.events;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import net.tylermurphy.hideAndSeek.commands.Stop;
import net.tylermurphy.hideAndSeek.util.Packet;

public class EventTick {

	static int tick = 0;
	
	public static void onTick() {
		
		if(board == null) {
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard mainBoard = manager.getMainScoreboard();
			
			try { mainBoard.registerNewTeam("Seeker");} catch(Exception e) {}
			Seeker = mainBoard.getTeam("Seeker");
			Seeker.setColor(ChatColor.RED);
			if(nametagsVisible)
				Seeker.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
			else
				Seeker.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
			Seeker.setAllowFriendlyFire(false);
			
			try { mainBoard.registerNewTeam("Hider");} catch(Exception e) {}
			Hider = mainBoard.getTeam("Hider");
			Hider.setColor(ChatColor.GOLD);
			if(nametagsVisible)
				Hider.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
			else
				Hider.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
			Hider.setAllowFriendlyFire(false);
			
			try { mainBoard.registerNewTeam("Spectator");} catch(Exception e) {}
			Spectator = mainBoard.getTeam("Spectator");
			Spectator.setColor(ChatColor.GRAY);
			Spectator.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
			Spectator.setAllowFriendlyFire(false);
			
			board = mainBoard;
		}
		
		for(String entry : Hider.getEntries()) {
			if(!playerList.containsKey(entry)) {
				Hider.removeEntry(entry);
			}
		}
		
		for(String entry : Seeker.getEntries()) {
			if(!playerList.containsKey(entry)) {
				Seeker.removeEntry(entry);
			}
		}
		
		for(String entry : Spectator.getEntries()) {
			if(!playerList.containsKey(entry)) {
				Spectator.removeEntry(entry);
			}
		}

		if(status.equals("Starting")) {
			onStarting();
		} else if(status.equals("Playing")) {
			onPlaying();
		}
		
		tick ++;
		
		if(( status.equals("Starting") || status.equals("Playing") ) && Hider.getSize() < 1) {
			Bukkit.broadcastMessage(gameoverPrefix + "All hiders have been found.");
			Stop.onStop();
		}
		if(( status.equals("Starting") || status.equals("Playing") ) && Seeker.getSize() < 1) {
			Bukkit.broadcastMessage(abortPrefix + "All seekers have quit.");
			Stop.onStop();
		}
	}
	
	private static void onStarting() {
		for(String playerName : Seeker.getEntries()) {
			Player player = playerList.get(playerName);
			if(player != null) {
				player.teleport(new Location(Bukkit.getWorld("hideandseek_"+spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			}
		}
	}
	
	private static void onPlaying() {
		if(decreaseBorder) {
			World world = Bukkit.getWorld("world");
			WorldBorder border = world.getWorldBorder();
			border.setSize(border.getSize()-100,30);
			decreaseBorder = false;
		}
		if(!tauntPlayer.equals("")) {
			Player taunted = playerList.get(tauntPlayer);
			if(taunted != null) {
				Firework fw = (Firework) taunted.getLocation().getWorld().spawnEntity(taunted.getLocation(), EntityType.FIREWORK);
				FireworkMeta fwm = fw.getFireworkMeta();
				fwm.setPower(4);
		        fwm.addEffect(FireworkEffect.builder()
		        		.withColor(Color.BLUE)
		        		.withColor(Color.RED)
		        		.withColor(Color.YELLOW)
		        		.with(FireworkEffect.Type.STAR)
		        		.with(FireworkEffect.Type.BALL)
		        		.with(FireworkEffect.Type.BALL_LARGE)
		        		.flicker(true)
		        		.withTrail()
		        		.build());
		        fw.setFireworkMeta(fwm);
		        Bukkit.getServer().broadcastMessage(tauntPrefix + " Taunt has been activated");
			}
			tauntPlayer = "";
		}
		for(String playerName : Hider.getEntries()) {
			Player player = playerList.get(playerName);
			int distance = 100;
			for(String seekerName : Seeker.getEntries()) {
				Player seeker = playerList.get(seekerName);
				int temp = (int) player.getLocation().distance(seeker.getLocation());
				if(distance > temp) {
					distance = temp;
				}
				if(glowTime > 0) {
					Packet.setGlow(player, seeker, true);
				} else {
					Packet.setGlow(player, seeker, false);
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
	}
	
}
