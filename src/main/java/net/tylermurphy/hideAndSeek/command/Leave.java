package net.tylermurphy.hideAndSeek.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Util;

import static net.tylermurphy.hideAndSeek.Config.*;

public class Leave implements ICommand {

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
		if(!Main.plugin.board.isPlayer(player)) {
			sender.sendMessage(errorPrefix + "You are currently not in the lobby");
			return;
		}
		if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(messagePrefix + sender.getName() + " has left the HideAndSeek lobby");
		else Util.broadcastMessage(messagePrefix + sender.getName() + " has left the HideAndSeek lobby");
		Main.plugin.board.remove(player);
		player.teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
		Main.plugin.board.removeBoard(player);
		if(Main.plugin.status.equals("Standby")) {
			Main.plugin.board.reloadLobbyBoards();
		} else {
			Main.plugin.board.reloadGameBoards();
		}
	}

	public String getLabel() {
		return "leave";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Leaves the lobby if game is set to manual join/leave";
	}

}