package net.tylermurphy.hideAndSeek.command;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Util;

import static net.tylermurphy.hideAndSeek.Config.*;

public class Join implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!Util.isSetup()) {
			sender.sendMessage(errorPrefix + "Game is not setup. Run /hs setup to see what you needed to do");
			return;
		}
		Player player = Bukkit.getServer().getPlayer(sender.getName());
		if(player == null) {
			sender.sendMessage(errorPrefix + "An internal error has occured");
			return;
		}
		if(Main.plugin.board.isPlayer(player)){
			sender.sendMessage(errorPrefix + "You are already in the lobby/game");
			return;
		}
		
		if(Main.plugin.status.equals("Standby")) {
			Main.plugin.board.addHider(player);
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(messagePrefix + sender.getName() + " has joined the HideAndSeek lobby");
			else Util.broadcastMessage(messagePrefix + sender.getName() + " has joined the HideAndSeek lobby");
			player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(),lobbyPosition.getY(),lobbyPosition.getZ()));
			player.setGameMode(GameMode.ADVENTURE);
			Main.plugin.board.reloadLobbyBoards();
		} else {
			Main.plugin.board.addSeeker(player);
			player.sendMessage(messagePrefix + "You have joined mid game and became a spectator");
			player.setGameMode(GameMode.SPECTATOR);
			player.teleport(new Location(Bukkit.getWorld("hideandseek_"+spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
		}
		
		player.setFoodLevel(20);
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
	}

	public String getLabel() {
		return "join";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Joins the lobby if game is set to manual join/leave";
	}

}
