package net.tylermurphy.hideAndSeek.manager;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
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
		
		if(board == null) return;
		
		Functions.checkTeams();
		
		for(Player player : playerList.values()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1000000, 127, false, false));
		}
		
		if(status.equals("Standby") || status.equals("Setup")) {
			onStandby();
		} else if(status.equals("Starting")) {
			onStarting();
		} else if(status.equals("Playing")) {
			onPlaying();
		}
		
		tick ++;
		
		if(Hider.getSize() < 1) {
			Bukkit.broadcastMessage(messagePrefix + "Game over! All hiders have been found.");
			Stop.onStop();
		}
		if(Seeker.getSize() < 1) {
			Bukkit.broadcastMessage(messagePrefix + "Game has ended as all seekers have quit.");
			Stop.onStop();
		}
	}
	
	private static void onStandby() {
		for(Player player : playerList.values()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1000000, 127, false, false));
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
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "worldborder add -100 30");
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
			int x = player.getLocation().getBlockX();
			int y = player.getLocation().getBlockY();
			int z = player.getLocation().getBlockZ();
			switch(tick%10) {
				case 0:
					if(distance < 30) Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("playsound minecraft:block.note_block.basedrum master %s %s %s %s .5 1",player.getName(),x,y,z));
					if(distance < 10) Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("playsound minecraft:block.note_block.bit master %s %s %s %s .3 1",player.getName(),x,y,z));
					break;
				case 3:
					if(distance < 30) Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("playsound minecraft:block.note_block.basedrum master %s %s %s %s 3.31",player.getName(),x,y,z));
					if(distance < 10) Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("playsound minecraft:block.note_block.bit master %s %s %s %s .3 1",player.getName(),x,y,z));
					break;
				case 6:
					if(distance < 10) Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("playsound minecraft:block.note_block.bit master %s %s %s %s .3 1",player.getName(),x,y,z));
					break;
				case 9:
					if(distance < 20) Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("playsound minecraft:block.note_block.bit master %s %s %s %s .3 1",player.getName(),x,y,z));
					break;
			}
		}
		if(tick%(20*30) == 0) {
			glowTime = Math.max(0, glowTime--);
		}
	}
	
}
