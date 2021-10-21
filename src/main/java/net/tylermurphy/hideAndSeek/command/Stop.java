package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.Config.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.events.Worldborder;
import net.tylermurphy.hideAndSeek.util.Packet;
import net.tylermurphy.hideAndSeek.util.Util;

public class Stop implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!Util.isSetup()) {
			sender.sendMessage(errorPrefix + "Game is not setup. Run /hs setup to see what you needed to do");
			return;
		}
		if(Main.plugin.status.equals("Starting") || Main.plugin.status.equals("Playing")) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(abortPrefix + "Game has been force stopped.");
			else Util.broadcastMessage(abortPrefix + "Game has been force stopped.");
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
		if(Main.plugin.status.equals("Standby")) return;
		Main.plugin.status = "Standby";
		Main.plugin.gameId++;
		Main.plugin.timeLeft = 0;
		Worldborder.resetWorldborder("hideandseek_"+spawnWorld);
		for(Player player : Main.plugin.board.getPlayers()) {
			player.setGameMode(GameMode.ADVENTURE);
			player.setLevel(0);
			Main.plugin.board.addHider(player);
			player.getInventory().clear();
			player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(),lobbyPosition.getY(),lobbyPosition.getZ()));
			for(PotionEffect effect : player.getActivePotionEffects()){
			    player.removePotionEffect(effect.getType());
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 100));
			for(Player temp : Main.plugin.board.getPlayers()) {
				Packet.setGlow(player, temp, false);
			}
		}
		Util.unloadMap("hideandseek_"+spawnWorld);
		Main.plugin.board.reloadLobbyBoards();
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Stops the game";
	}

}
