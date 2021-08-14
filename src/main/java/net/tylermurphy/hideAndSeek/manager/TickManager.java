package net.tylermurphy.hideAndSeek.manager;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import net.tylermurphy.hideAndSeek.commands.Stop;

public class TickManager {

	static int tick = 0;
	
	public static void onTick() {
		
		if(board == null) return;
		
		checkTeams();
		
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
		tick %= 10;
		
		if(Hider.getSize() < 1) {
			Stop.onStop(false);
		}
		if(Seeker.getSize() < 1) {
			Stop.onStop(false);
		}
	}
	
	private static void checkTeams() {
		
		try { Hider.getSize(); }
		catch (Exception e) {
			board.registerNewTeam("Hider");
			Hider = board.getTeam("Hider");
			Hider.setColor(ChatColor.GOLD);
			Hider.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
			Hider.setAllowFriendlyFire(false);
		}
		
		try { Seeker.getSize(); }
		catch (Exception e) {
			board.registerNewTeam("Seeker");
			Seeker = board.getTeam("Seeker");
			Seeker.setColor(ChatColor.RED);
			Seeker.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
			Seeker.setAllowFriendlyFire(false);
		}
		
		try { Spectator.getSize(); }
		catch (Exception e) {
			board.registerNewTeam("Spectator");
			Spectator = board.getTeam("Spectator");
			Spectator.setColor(ChatColor.GRAY);
			Spectator.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
			Spectator.setAllowFriendlyFire(false);
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
				fwm.setPower(2);
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
			if(getPlayerData(player.getName(),"GiveStatus") > 0) {
				setPlayerData(player.getName(),"GiveStatus",0);
				player.getInventory().clear();
				for(PotionEffect effect : player.getActivePotionEffects()){
				    player.removePotionEffect(effect.getType());
				}
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
				}
			}
			if(getPlayerData(player.getName(),"Death") > 0) {
				setPlayerData(player.getName(),"Death",0);
				Seeker.addEntry(player.getName());
			}
		}
		for(String playerName : Seeker.getEntries()) {
			Player player = playerList.get(playerName);
			if(player != null) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2, false, false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 1, false, false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1000000, 1, false, false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1000000, 10, false, false));
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
			switch(tick) {
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
	}
	
}
