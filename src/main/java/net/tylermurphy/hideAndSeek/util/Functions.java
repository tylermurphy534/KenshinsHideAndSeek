package net.tylermurphy.hideAndSeek.util;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.SoundCategory;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;

import net.tylermurphy.hideAndSeek.Main;

public class Functions {
	
	private static ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	
	public static void resetPlayer(Player player) {
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
		}
	}
	
	public static void emptyOfflinePlayers() {
		
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
	
	public static void loadScoreboard() {
		
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
	
	public static void playSound(Player player, Sound sound, float volume, float pitch) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.NAMED_SOUND_EFFECT);
		packet.getSoundCategories().write(0, SoundCategory.MASTER);
		packet.getSoundEffects().write(0, sound);
		packet.getIntegers().write(0, (int)(player.getLocation().getX() * 8.0));
		packet.getIntegers().write(1, (int)(player.getLocation().getY() * 8.0));
		packet.getIntegers().write(2, (int)(player.getLocation().getZ() * 8.0));
		packet.getFloat().write(0, volume);
		packet.getFloat().write(1, pitch);
		try {
			protocolManager.sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static void setGlow(Player player, Player target, boolean glowing) {
	    PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
	    packet.getIntegers().write(0, target.getEntityId());
	    WrappedDataWatcher watcher = new WrappedDataWatcher();
	    Serializer serializer = Registry.get(Byte.class);
	    watcher.setEntity(target);
	    if(glowing) {
	    	watcher.setObject(0, serializer, (byte) (0x40));
	    } else {
	    	watcher.setObject(0, serializer, (byte) (0x0));
	    }
	    packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
	    try {
	    	protocolManager.sendServerPacket(player, packet);
	    } catch (InvocationTargetException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void scheduleTaunt() {
		Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){

			public void run() {
				int temp = gameId;
				while(true) {
					if(tauntPlayer != null && !tauntPlayer.equals("")) {
						try { Thread.sleep(1000); } catch (InterruptedException e) {}
						if(gameId != temp) break;
						continue;
					}
					try { Thread.sleep(1000*60); } catch (InterruptedException e) {}
					if(gameId != temp) break;
					if(Math.random() > .8) {
						Player taunted = null;
						int rand = (int) (Math.random()*Hider.getEntries().size());
						for(Player player : playerList.values()) {
							if(Hider.hasEntry(player.getName())) {
								rand--;
								if(rand==0) {
									taunted = player;
									break;
								}
							}
						}
						if(taunted != null) {
							taunted.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Oh no! You have been chosed to be taunted.");
							Bukkit.getServer().broadcastMessage(tauntPrefix + " A random hider will be taunted in the next 30s");
							try { Thread.sleep(1000*30); } catch (InterruptedException e) {}
							if(gameId != temp) break;
							tauntPlayer = taunted.getName();
						}
					}
				}
			}
		});
	}
	
	public static void scheduleWorldborder() {
		
		Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){

			public void run() {
				int temp = gameId;
				while(true) {
					try { Thread.sleep(1000*60*worldborderDelay); } catch (InterruptedException e) {}
					if(gameId != temp) break;
					if(currentWorldborderSize-100 > 100) {
						Bukkit.getServer().broadcastMessage(worldborderPrefix + "Worldborder decreacing by 100 blocks over the next 30s");
						currentWorldborderSize -= 100;
						decreaseBorder = true;
					} else {
						break;
					}
				}
			}
		});
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
	
}
