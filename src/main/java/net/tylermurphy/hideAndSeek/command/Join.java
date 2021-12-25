package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.game.Status;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Util;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Join implements ICommand {

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
		if(Main.plugin.board.isPlayer(player)){
			sender.sendMessage(errorPrefix + message("GAME_INGAME"));
			return;
		}

		join(player);
	}

	public static void join(Player player){
		if(Main.plugin.status == Status.STANDBY) {
			player.getInventory().clear();
			Main.plugin.board.addHider(player);
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(messagePrefix + message("GAME_JOIN").addPlayer(player));
			else Util.broadcastMessage(messagePrefix + message("GAME_JOIN").addPlayer(player));
			player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(),lobbyPosition.getY(),lobbyPosition.getZ()));
			player.setGameMode(GameMode.ADVENTURE);
			Main.plugin.board.createLobbyBoard(player);
			Main.plugin.board.reloadLobbyBoards();
		} else {
			Main.plugin.board.addSpectator(player);
			player.sendMessage(messagePrefix + message("GAME_JOIN_SPECTATOR"));
			player.setGameMode(GameMode.SPECTATOR);
			Main.plugin.board.createGameBoard(player);
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
