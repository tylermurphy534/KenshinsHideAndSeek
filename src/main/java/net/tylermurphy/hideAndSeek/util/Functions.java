package net.tylermurphy.hideAndSeek.util;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import net.tylermurphy.hideAndSeek.Main;

public class Functions {
	
	public static void resetPlayer(Player player) {
		player.getInventory().clear();
		for(PotionEffect effect : player.getActivePotionEffects()){
		    player.removePotionEffect(effect.getType());
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 1000000, 1, false, false));
		if(Seeker.contains(player.getName())){
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
		else if(Hider.contains(player.getName())){
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
	
	public static void resetWorldborder(String worldName) {
		if(worldborderEnabled) {
			World world = Bukkit.getWorld(worldName);
			WorldBorder border = world.getWorldBorder();
			border.setSize(worldborderSize);
			border.setCenter(worldborderPosition.getX(), worldborderPosition.getZ());
			currentWorldborderSize = worldborderSize;
		} else {
			World world = Bukkit.getWorld(worldName);
			WorldBorder border = world.getWorldBorder();
			border.setSize(30000000);
			border.setCenter(0, 0);
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
    
    public static void loadScoreboard() {

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard mainBoard = manager.getMainScoreboard();
		
		try { mainBoard.registerNewTeam("Seeker");} catch(Exception e) {}
		SeekerTeam = mainBoard.getTeam("Seeker");
		SeekerTeam.setColor(ChatColor.RED);
		if(nametagsVisible)
			SeekerTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
		else
			SeekerTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		SeekerTeam.setAllowFriendlyFire(false);
		
		try { mainBoard.registerNewTeam("Hider");} catch(Exception e) {}
		HiderTeam = mainBoard.getTeam("Hider");
		HiderTeam.setColor(ChatColor.GOLD);
		if(nametagsVisible)
			HiderTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
		else
			HiderTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		HiderTeam.setAllowFriendlyFire(false);
		
		try { mainBoard.registerNewTeam("Spectator");} catch(Exception e) {}
		SpectatorTeam = mainBoard.getTeam("Spectator");
		SpectatorTeam.setColor(ChatColor.GRAY);
		SpectatorTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		SpectatorTeam.setAllowFriendlyFire(false);
		
		board = mainBoard;
    }
    
    public static boolean playerInProtectedWorld(Player p) {
    	return p.getWorld().getName().equals("hideandseek_"+spawnWorld) || p.getWorld().getName().equals(spawnWorld);
    }
	
}