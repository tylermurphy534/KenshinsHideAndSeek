package net.tylermurphy.hideAndSeek.util;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.configuration.Items;
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
		if (Main.plugin.board.isSeeker(player)) {
			if(pvpEnabled)
				for(ItemStack item : Items.SEEKER_ITEMS)
					player.getInventory().addItem(item);
			for(PotionEffect effect : Items.SEEKER_EFFECTS)
				player.addPotionEffect(effect);
		} else if (Main.plugin.board.isHider(player)) {
			if(pvpEnabled)
				for(ItemStack item : Items.HIDER_ITEMS)
					player.getInventory().addItem(item);
			for(PotionEffect effect : Items.HIDER_EFFECTS)
				player.addPotionEffect(effect);
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
		}
	}

	public static void removeItems(Player player){
		for(ItemStack si : Items.SEEKER_ITEMS)
			for(ItemStack i : player.getInventory().getContents())
				if(si.isSimilar(i)) player.getInventory().remove(i);
		for(ItemStack hi : Items.HIDER_ITEMS)
			for(ItemStack i : player.getInventory().getContents())
				if(hi.isSimilar(i)) player.getInventory().remove(i);
	}
}