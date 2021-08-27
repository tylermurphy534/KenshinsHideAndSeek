package net.tylermurphy.hideAndSeek.util;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import net.tylermurphy.hideAndSeek.Main;

public class Functions {
	
	public static void resetPlayer(Player player) {
		player.getInventory().clear();
		for(PotionEffect effect : player.getActivePotionEffects()){
		    player.removePotionEffect(effect.getType());
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 1000000, 1, false, false));
		if(Seeker.getEntries().contains(player.getName())){
			ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD,1);
			diamondSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
			ItemMeta diamondSwordMeta = diamondSword.getItemMeta();
			diamondSwordMeta.setDisplayName("Seeker Sword");
			diamondSwordMeta.setUnbreakable(true);
			diamondSword.setItemMeta(diamondSwordMeta);
			player.getInventory().addItem(diamondSword);
			
			ItemStack wackyStick = new ItemStack(Material.STICK,1);
			wackyStick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
			ItemMeta wackyStickMeta = wackyStick.getItemMeta();
			wackyStickMeta.setDisplayName("Wacky Stick");
			wackyStick.setItemMeta(wackyStickMeta);
			player.getInventory().addItem(wackyStick);
			
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 1, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1000000, 1, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 10, false, false));
		}
		else if(Hider.getEntries().contains(player.getName())){
			ItemStack stoneSword = new ItemStack(Material.STONE_SWORD,1);
			stoneSword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
			ItemMeta stoneSwordMeta = stoneSword.getItemMeta();
			stoneSwordMeta.setDisplayName("Hider Sword");
			stoneSwordMeta.setUnbreakable(true);
			stoneSword.setItemMeta(stoneSwordMeta);
			player.getInventory().addItem(stoneSword);
			
			ItemStack splashPotion = new ItemStack(Material.SPLASH_POTION,1);
			PotionMeta splashPotionMeta = (PotionMeta) splashPotion.getItemMeta();
			splashPotionMeta.setBasePotionData(new PotionData(PotionType.REGEN));
			splashPotion.setItemMeta(splashPotionMeta);
			player.getInventory().addItem(splashPotion);
			
			ItemStack potion = new ItemStack(Material.POTION,2);
			PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
			potionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
			potion.setItemMeta(potionMeta);
			player.getInventory().addItem(potion);
			
			ItemStack snowball = new ItemStack(Material.SNOWBALL,1);
			ItemMeta snowballMeta = snowball.getItemMeta();
			snowballMeta.setDisplayName("Glow Powerup");
			List<String> snowballLore = new ArrayList<String>();
			snowballLore.add("Throw to make all seekers glow");
			snowballLore.add("Last 30s, all hiders can see it");
			snowballLore.add("Time stacks on multi use");
			snowballMeta.setLore(snowballLore);
			snowball.setItemMeta(snowballMeta);
			player.getInventory().addItem(snowball);
			
			player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 1, false, false));
		}
	}
	
	public static void resetWorldborder() {
		if(worldborderEnabled) {
			World world = Bukkit.getWorld("world");
			WorldBorder border = world.getWorldBorder();
			border.setSize(worldborderSize);
			border.setCenter(worldborderPosition.getX(), worldborderPosition.getZ());
			currentWorldborderSize = worldborderSize;
		} else {
			World world = Bukkit.getWorld("world");
			WorldBorder border = world.getWorldBorder();
			border.setSize(30000000);
			border.setCenter(0, 0);
		}
	}
	
	public static void copyFileStructure(File source, File target){
	    try {
	        ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
	        if(!ignore.contains(source.getName())) {
	            if(source.isDirectory()) {
	                if(!target.exists())
	                    if (!target.mkdirs())
	                        throw new IOException("Couldn't create world directory!");
	                String files[] = source.list();
	                for (String file : files) {
	                    File srcFile = new File(source, file);
	                    File destFile = new File(target, file);
	                    copyFileStructure(srcFile, destFile);
	                }
	            } else {
	                InputStream in = new FileInputStream(source);
	                OutputStream out = new FileOutputStream(target);
	                byte[] buffer = new byte[1024];
	                int length;
	                while ((length = in.read(buffer)) > 0)
	                    out.write(buffer, 0, length);
	                in.close();
	                out.close();
	            }
	        }
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public static void unloadMap(String mapname){
        if(Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(mapname), false)){
            Main.plugin.getLogger().info("Successfully unloaded " + mapname);
        }else{
            Main.plugin.getLogger().severe("COULD NOT UNLOAD " + mapname);
        }
    }

    public static void loadMap(String mapname){
        Bukkit.getServer().createWorld(new WorldCreator(mapname));
        Bukkit.getServer().getWorld("hideandseek_"+spawnWorld).setAutoSave(false);
    }
 
    public static void rollback(String mapname){
        unloadMap(mapname);
        loadMap(mapname);
    }
	
}
