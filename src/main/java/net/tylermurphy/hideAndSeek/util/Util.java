package net.tylermurphy.hideAndSeek.util;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.configuration.LocalizationString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class Util {
    
    public static void broadcastMessage(String message) {
    	for(Player player : Main.plugin.board.getPlayers()) {
    		player.sendMessage(message);
    	}
    }
    
    public static boolean isSetup() {
    	if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) return false;
		if(lobbyPosition.getBlockX() == 0 && lobbyPosition.getBlockY() == 0 && lobbyPosition.getBlockZ() == 0) return false;
		if(exitPosition.getBlockX() == 0 && exitPosition.getBlockY() == 0 && exitPosition.getBlockZ() == 0) return false;
		File destenation = new File(Main.root+File.separator+"hideandseek_"+spawnWorld);
		if(!destenation.exists()) return false;
		if(saveMinX == 0 || saveMinZ == 0 || saveMaxX == 0 || saveMaxZ == 0) return false;
		return true;
    }
    
    public static void sendDelayedMessage(String message, int gameId, int delay) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin, new Runnable() {
			public void run() {
				if(gameId == Main.plugin.gameId)
					Util.broadcastMessage(message);
			}
		}, delay);
	}

	public static void resetPlayer(Player player) {
		player.getInventory().clear();
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 1000000, 1, false, false));
		if (Main.plugin.board.isSeeker(player)) {
			ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD, 1);
			diamondSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
			ItemMeta diamondSwordMeta = diamondSword.getItemMeta();
			diamondSwordMeta.setDisplayName("Seeker Sword");
			diamondSwordMeta.setUnbreakable(true);
			diamondSword.setItemMeta(diamondSwordMeta);
			player.getInventory().addItem(diamondSword);

			ItemStack wackyStick = new ItemStack(Material.STICK, 1);
			wackyStick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
			ItemMeta wackyStickMeta = wackyStick.getItemMeta();
			wackyStickMeta.setDisplayName("Wacky Stick");
			wackyStick.setItemMeta(wackyStickMeta);
			player.getInventory().addItem(wackyStick);

			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 1, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1000000, 1, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 10, false, false));
		} else if (Main.plugin.board.isHider(player)) {
			ItemStack stoneSword = new ItemStack(Material.STONE_SWORD, 1);
			stoneSword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
			ItemMeta stoneSwordMeta = stoneSword.getItemMeta();
			stoneSwordMeta.setDisplayName("Hider Sword");
			stoneSwordMeta.setUnbreakable(true);
			stoneSword.setItemMeta(stoneSwordMeta);
			player.getInventory().addItem(stoneSword);

			ItemStack splashPotion = new ItemStack(Material.SPLASH_POTION, 1);
			PotionMeta splashPotionMeta = (PotionMeta) splashPotion.getItemMeta();
			splashPotionMeta.setBasePotionData(new PotionData(PotionType.REGEN));
			splashPotion.setItemMeta(splashPotionMeta);
			player.getInventory().addItem(splashPotion);

			ItemStack potion = new ItemStack(Material.POTION, 2);
			PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
			potionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
			potion.setItemMeta(potionMeta);
			player.getInventory().addItem(potion);

			if(glowEnabled) {
				ItemStack snowball = new ItemStack(Material.SNOWBALL, 1);
				ItemMeta snowballMeta = snowball.getItemMeta();
				snowballMeta.setDisplayName("Glow Powerup");
				List<String> snowballLore = new ArrayList<String>();
				snowballLore.add("Throw to make all seekers glow");
				snowballLore.add("Last 30s, all hiders can see it");
				snowballLore.add("Time stacks on multi use");
				snowballMeta.setLore(snowballLore);
				snowball.setItemMeta(snowballMeta);
				player.getInventory().addItem(snowball);
			}

			player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 1, false, false));
		}
	}
	
}