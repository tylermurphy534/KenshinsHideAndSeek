package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class SetLobbyLocation implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(Game.status != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		Vector newLobbyPosition = new Vector();
		Player player = (Player) sender;
		if(player.getLocation().getBlockX() == 0 || player.getLocation().getBlockZ() == 0 || player.getLocation().getBlockY() == 0){
			sender.sendMessage(errorPrefix + message("NOT_AT_ZERO"));
			return;
		}
		newLobbyPosition.setX(player.getLocation().getBlockX());
		newLobbyPosition.setY(player.getLocation().getBlockY());
		newLobbyPosition.setZ(player.getLocation().getBlockZ());
		World world = player.getLocation().getWorld();
		if(world == null){
			throw new RuntimeException("Unable to get world: " + spawnWorld);
		}
		lobbyWorld = world.getName();
		lobbyPosition = newLobbyPosition;
		sender.sendMessage(messagePrefix + message("LOBBY_SPAWN"));
		addToConfig("spawns.lobby.x", lobbyPosition.getX());
		addToConfig("spawns.lobby.y", lobbyPosition.getY());
		addToConfig("spawns.lobby.z", lobbyPosition.getZ());
		addToConfig("spawns.lobby.world", player.getLocation().getWorld().getName());
		saveConfig();
	}

	public String getLabel() {
		return "setlobby";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Sets hide and seeks lobby location to current position";
	}

}
