package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.game.Board;
import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Join implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(Game.isNotSetup()) {
			sender.sendMessage(errorPrefix + message("GAME_SETUP"));
			return;
		}
		Player player = Bukkit.getServer().getPlayer(sender.getName());
		if(player == null) {
			sender.sendMessage(errorPrefix + message("COMMAND_ERROR"));
			return;
		}
		if(Board.isPlayer(player)){
			sender.sendMessage(errorPrefix + message("GAME_INGAME"));
			return;
		}

		join(player);
	}

	public static void join(Player player){
		if(Game.status == Status.STANDBY) {
			player.getInventory().clear();
			Board.addHider(player);
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(messagePrefix + message("GAME_JOIN").addPlayer(player));
			else Game.broadcastMessage(messagePrefix + message("GAME_JOIN").addPlayer(player));
			player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(),lobbyPosition.getY(),lobbyPosition.getZ()));
			player.setGameMode(GameMode.ADVENTURE);
			Board.createLobbyBoard(player);
			Board.reloadLobbyBoards();
		} else {
			Board.addSpectator(player);
			player.sendMessage(messagePrefix + message("GAME_JOIN_SPECTATOR"));
			player.setGameMode(GameMode.SPECTATOR);
			Board.createGameBoard(player);
			player.teleport(new Location(Bukkit.getWorld("hideandseek_"+spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
		}

		player.setFoodLevel(20);
		player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
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
