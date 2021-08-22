package net.tylermurphy.hideAndSeek.util;

import static net.tylermurphy.hideAndSeek.Store.Hider;
import static net.tylermurphy.hideAndSeek.Store.Seeker;
import static net.tylermurphy.hideAndSeek.Store.Spectator;
import static net.tylermurphy.hideAndSeek.Store.board;
import static net.tylermurphy.hideAndSeek.Store.playerList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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
		Seeker.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		Seeker.setAllowFriendlyFire(false);
		
		try { mainBoard.registerNewTeam("Hider");} catch(Exception e) {}
		Hider = mainBoard.getTeam("Hider");
		Hider.setColor(ChatColor.GOLD);
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
	
}
