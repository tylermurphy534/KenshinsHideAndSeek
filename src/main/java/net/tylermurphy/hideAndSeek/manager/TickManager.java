package net.tylermurphy.hideAndSeek.manager;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.tylermurphy.hideAndSeek.commands.Stop;
import net.tylermurphy.hideAndSeek.util.Functions;

public class TickManager {

	static int tick = 0;
	
	public static void onTick() {
		
		if(board == null) {
			Functions.loadScoreboard();
		}
		
		Functions.emptyOfflinePlayers();

		if(status.equals("Starting")) {
			onStarting();
		} else if(status.equals("Playing")) {
			onPlaying();
		}
		
		tick ++;
		
		if(( status.equals("Starting") || status.equals("Playing") ) && Hider.getSize() < 1) {
			Bukkit.broadcastMessage(messagePrefix + "Game over! All hiders have been found.");
			Stop.onStop();
		}
		if(( status.equals("Starting") || status.equals("Playing") ) && Seeker.getSize() < 1) {
			Bukkit.broadcastMessage(messagePrefix + "Game has ended as all seekers have quit.");
			Stop.onStop();
		}
	}
	
	private static void onStarting() {
		for(String playerName : Seeker.getEntries()) {
			Player player = playerList.get(playerName);
			if(player != null) {
				player.teleport(new Location(player.getWorld(), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
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
		        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "Taunt >" + ChatColor.WHITE + " Taunt has been activated");
			}
			tauntPlayer = "";
		}
		for(Player player : playerList.values()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 1000000, 1, false, false));
		}
		for(String playerName : Seeker.getEntries()) {
			Player player = playerList.get(playerName);
			if(player != null) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2, false, false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 1, false, false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1000000, 1, false, false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 10, false, false));
			}
			if(glowTime > 0) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 1, false, false));
			} else {
				player.removePotionEffect(PotionEffectType.GLOWING);
			}
		}
		for(String playerName : Hider.getEntries()) {
			Player player = playerList.get(playerName);
			if(player != null) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 1, false, false));
			}
			int distance = 100;
			for(String seekerName : Seeker.getEntries()) {
				Player seeker = playerList.get(seekerName);
				int temp = (int) player.getLocation().distance(seeker.getLocation());
				if(distance > temp) {
					distance = temp;
				}
			}
			switch(tick%10) {
				case 0:
					if(distance < 30) Functions.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, .5f, 1f);
					if(distance < 10) Functions.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 3:
					if(distance < 30) Functions.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, .3f, 1f);
					if(distance < 10) Functions.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 6:
					if(distance < 10) Functions.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
				case 9:
					if(distance < 20) Functions.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, .3f, 1f);
					break;
			}
		}
	}
	
}
