package net.tylermurphy.hideAndSeek.commands;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.tylermurphy.hideAndSeek.util.Functions;
import net.tylermurphy.hideAndSeek.util.ICommand;

public class Stop implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(status.equals("Starting") || status.equals("Playing")) {
			Bukkit.broadcastMessage(messagePrefix + "Game has been force stopped.");
			onStop();
			
		} else {
			sender.sendMessage(errorPrefix + "There is no game in progress");
			return;
		}
	}

	public String getLabel() {
		return "stop";
	}
	
	public static void onStop() {
		if(status.equals("Standby") || status.equals("Setup")) return;
		status = "Standby";
		gameId++;
		for(Player player : playerList.values()) {
			player.setGameMode(GameMode.ADVENTURE);
			Hider.addEntry(player.getName());
			player.getInventory().clear();
			player.teleport(new Location(player.getWorld(), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			for(PotionEffect effect : player.getActivePotionEffects()){
			    player.removePotionEffect(effect.getType());
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 100));
			for(Player temp : playerList.values()) {
				Functions.setGlow(player, temp, false);
			}
		}
		Functions.resetWorldborder();
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Stops the game";
	}

}
