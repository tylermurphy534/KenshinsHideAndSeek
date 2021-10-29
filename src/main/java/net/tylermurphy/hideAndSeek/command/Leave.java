package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Util;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Leave implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!Util.isSetup()) {
			sender.sendMessage(errorPrefix + message("GAME_SETUP"));
			return;
		}
		Player player = Bukkit.getServer().getPlayer(sender.getName());
		if(player == null) {
			sender.sendMessage(errorPrefix + message("COMMAND_ERROR"));
			return;
		}
		if(!Main.plugin.board.isPlayer(player)) {
			sender.sendMessage(errorPrefix + message("GAME_NOT_INGAME"));
			return;
		}
		if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(messagePrefix + message("GAME_LEAVE").addPlayer(player));
		else Util.broadcastMessage(messagePrefix + message("GAME_LEAVE").addPlayer(player));
		Main.plugin.board.removeBoard(player);
		Main.plugin.board.remove(player);
		player.teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
		if(Main.plugin.status.equals("Standby")) {
			Main.plugin.board.reloadLobbyBoards();
		} else {
			Main.plugin.board.reloadGameBoards();
			Main.plugin.board.reloadBoardTeams();
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