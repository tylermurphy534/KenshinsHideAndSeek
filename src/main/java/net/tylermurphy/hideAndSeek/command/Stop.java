package net.tylermurphy.hideAndSeek.command;

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
import net.tylermurphy.hideAndSeek.util.Packet;

public class Stop implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(status.equals("Starting") || status.equals("Playing")) {
			Bukkit.broadcastMessage(abortPrefix + "Game has been force stopped.");
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
		if(status.equals("Standby")) return;
		status = "Standby";
		gameId++;
		Functions.resetWorldborder("hideandseek_"+spawnWorld);
		for(Player player : playerList.values()) {
			player.setGameMode(GameMode.ADVENTURE);
			Hider.add(player.getName());
			HiderTeam.addEntry(player.getName());
			player.getInventory().clear();
			player.teleport(new Location(Bukkit.getWorld(spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			for(PotionEffect effect : player.getActivePotionEffects()){
			    player.removePotionEffect(effect.getType());
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 100));
			for(Player temp : playerList.values()) {
				Packet.setGlow(player, temp, false);
			}
		}
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Stops the game";
	}

}
