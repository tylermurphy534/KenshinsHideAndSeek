package net.tylermurphy.hideAndSeek.command;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

import net.tylermurphy.hideAndSeek.game.Status;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.events.Glow;
import net.tylermurphy.hideAndSeek.events.Taunt;
import net.tylermurphy.hideAndSeek.events.Worldborder;
import net.tylermurphy.hideAndSeek.util.Util;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Start implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!Util.isSetup()) {
			sender.sendMessage(errorPrefix + message("GAME_SETUP"));
			return;
		}
		if(Main.plugin.status != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if(!Main.plugin.board.isPlayer(sender)) {
			sender.sendMessage(errorPrefix + message("GAME_NOT_INGAME"));
			return;
		}
		if(Main.plugin.board.size() < minPlayers) {
			sender.sendMessage(errorPrefix + message("START_MIN_PLAYERS").addAmount(minPlayers));
			return;
		}
		String seekerName;
		if(args.length < 1) {
			seekerName = Main.plugin.board.getPlayers().stream().skip(new Random().nextInt(Main.plugin.board.size())).findFirst().get().getName();
		} else {
			seekerName = args[0];
		}
		Player seeker = Main.plugin.board.getPlayer(seekerName);
		if(seeker == null) {
			sender.sendMessage(errorPrefix + message("START_INVALID_NAME").addPlayer(seekerName));
			return;
		}
		Main.plugin.game.start(seeker);
	}
	
	public String getLabel() {
		return "start";
	}
	
	public String getUsage() {
		return "<player>";
	}

	public String getDescription() {
		return "Starts the game either with a random seeker or chosen one";
	}

}
